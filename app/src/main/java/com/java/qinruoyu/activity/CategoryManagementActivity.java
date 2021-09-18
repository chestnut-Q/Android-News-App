package com.java.qinruoyu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.java.qinruoyu.adapter.DragAdapter;
import com.java.qinruoyu.adapter.OtherAdapter;
import com.java.qinruoyu.R;
import com.java.qinruoyu.view.DragGridView;
import com.java.qinruoyu.view.MyGridView;

import java.util.ArrayList;
import java.util.List;

public class CategoryManagementActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private MyGridView mOtherGv;
    private DragGridView mUserGv;
    private Button mBtnFinish;
    private List<String> mUserList = new ArrayList<>();
    private List<String> mOtherList = new ArrayList<>();
    private OtherAdapter mOtherAdapter;
    private DragAdapter mUserAdapter;
    private String mOriginalArrangement;
    public static long lastClickTime, timeD;
    private final String[] ALL_CATEGORY = {"娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);
        Bundle bundle = getIntent().getExtras();
        mOriginalArrangement = bundle.getString("cat_arrange");
        initView();
    }

    public void initView() {
        mUserGv = findViewById(R.id.userGridView);
        mOtherGv = findViewById(R.id.otherGridView);
        mBtnFinish = findViewById(R.id.btn_finish_cat_arrange);
        mBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                StringBuffer categoryBuffer = new StringBuffer();
                for (int i = 0; i < mUserList.size(); i++) {
                    for (int j = 0; j < ALL_CATEGORY.length; j++) {
                        if (mUserList.get(i).equals(ALL_CATEGORY[j])) {
                            if (i == 0) {
                                categoryBuffer.append(ALL_CATEGORY[j]);
                            } else {
                                categoryBuffer.append("," + ALL_CATEGORY[j]);
                            }
                        }
                    }
                }
                categoryBuffer.append("#");
                for (int i = 0; i < mOtherList.size(); i++) {
                    for (int j = 0; j < ALL_CATEGORY.length; j++) {
                        if (mOtherList.get(i).equals(ALL_CATEGORY[j])) {
                            if (i == 0) {
                                categoryBuffer.append(ALL_CATEGORY[j]);
                            } else {
                                categoryBuffer.append("," + ALL_CATEGORY[j]);
                            }
                        }
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putString("arrangement", categoryBuffer.toString());
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        String[] userAndOther = mOriginalArrangement.split("#");
        if (userAndOther.length == 1) {
            if (mOriginalArrangement.substring(0, 1).equals("#")) {
                // mUserList为空
                for (String item : userAndOther[0].split(",")) {
                    mOtherList.add(item);
                }
            } else {
                // mOtherList为空
                for (String item : userAndOther[0].split(",")) {
                    mUserList.add(item);
                }
            }
        } else {
            for (String item : userAndOther[0].split(",")) {
                mUserList.add(item);
            }
            for (String item : userAndOther[1].split(",")) {
                mOtherList.add(item);
            }
        }

        mUserAdapter = new DragAdapter(this, mUserList, true);
        mOtherAdapter = new OtherAdapter(this, mOtherList, false);
        mUserGv.setAdapter(mUserAdapter);
        mOtherGv.setAdapter(mOtherAdapter);
        mUserGv.setOnItemClickListener(this);
        mOtherGv.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("arrangement", mOriginalArrangement);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * 获取点击的Item的对应View，
     * 因为点击的Item已经有了自己归属的父容器MyGridView，所有我们要是有一个ImageView来代替Item移动
     *
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     *
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     * 用于存放我们移动的View
     */
    private ViewGroup getMoveViewGroup() {
        //window中最顶层的view
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 点击ITEM移动动画
     *
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param moveChannel
     * @param clickGridView
     */
    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final String moveChannel,
                          final GridView clickGridView, final boolean isUser) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // 判断点击的是DragGrid还是OtherGridView
                if (isUser) {
                    mOtherAdapter.setVisible(true);
                    mOtherAdapter.notifyDataSetChanged();
                    mUserAdapter.remove();
                } else {
                    mUserAdapter.setVisible(true);
                    mUserAdapter.notifyDataSetChanged();
                    mOtherAdapter.remove();
                }
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        // 防止连续点击
        if (isFastDoubleClick())
            return;

        switch (parent.getId()) {
            case R.id.userGridView:
                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final String channel = ((DragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                    mOtherAdapter.setVisible(false);
                    //添加到最后一个
                    mOtherAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                mOtherGv.getChildAt(mOtherGv.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, channel, mUserGv, true);
                                mUserAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }

                break;
            case R.id.otherGridView:
                final ImageView moveImageView2 = getView(view);
                if (moveImageView2 != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final String channel = ((OtherAdapter) parent.getAdapter()).getItem(position);
                    mUserAdapter.setVisible(false);
                    //添加到最后一个
                    mUserAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                mUserGv.getChildAt(mUserGv.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView2, startLocation, endLocation, channel, mOtherGv, false);
                                mOtherAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }

    public static boolean isFastDoubleClick() {
        timeD = System.currentTimeMillis() - lastClickTime;
        if (timeD >= 0 && timeD <= 1000) {
            return true;
        } else {
            lastClickTime = System.currentTimeMillis();
            return false;
        }
    }
}

