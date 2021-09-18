package com.java.qinruoyu.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.java.qinruoyu.News;
import com.java.qinruoyu.OfflineNewsManager;
import com.java.qinruoyu.R;
import com.java.qinruoyu.activity.NewsDetailActivity;
import com.java.qinruoyu.adapter.NewsAdapter;

import java.io.File;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private Button mBtnCollection, mBtnHistory;
    private RecyclerView mRvHomeList;
    private ImageButton mBtnClear;
    private final String FILENAME = "data.txt";
    private NewsAdapter mNewsAdapter;
    private OfflineNewsManager mOfflineNewsManager;
    private ArrayList<News> mNewsList;
    private static long timeD, lastClickTime;

    public static boolean isFastDoubleClick() {
        timeD = System.currentTimeMillis() - lastClickTime;
        if (timeD >= 0 && timeD <= 1000) {
            return true;
        } else {
            lastClickTime = System.currentTimeMillis();
            return false;
        }
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateView();
        }
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mOfflineNewsManager = OfflineNewsManager.getInstance(getContext());
        mNewsList = mOfflineNewsManager.getCollectionNews();
        mRvHomeList = view.findViewById(R.id.rv_home_list);
        LinearLayoutManager layoutManagerNews = new LinearLayoutManager(getContext());
        mRvHomeList.setLayoutManager(layoutManagerNews);
        mNewsAdapter = new NewsAdapter(mNewsList, getContext(), new NewsAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos) {

                // 防止连续点击
                if (isFastDoubleClick())
                    return;

                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                News newsDetail = mNewsAdapter.getNewsList().get(pos);
                Bundle bundle = new Bundle();
                bundle.putSerializable("news", newsDetail);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        }, new ArrayList<>());
        mRvHomeList.setAdapter(mNewsAdapter);
        mRvHomeList.setItemViewCacheSize(100);

        mBtnCollection = view.findViewById(R.id.btn_collection);
        mBtnCollection.setSelected(true);
        mBtnHistory = view.findViewById(R.id.btn_history);
        mBtnCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBtnCollection.isSelected()) {
                    mBtnCollection.setSelected(true);
                    mBtnHistory.setSelected(false);
                    updateView();
                }
            }
        });
        mBtnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBtnHistory.isSelected()) {
                    mBtnHistory.setSelected(true);
                    mBtnCollection.setSelected(false);
                    updateView();
                }
            }
        });

        mBtnClear = view.findViewById(R.id.btn_clear);
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("清除历史和收藏记录");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mOfflineNewsManager.setHistoryList(new ArrayList<>());
                        mOfflineNewsManager.setCollectionList(new ArrayList<>());
                        File dir = getContext().getFilesDir();
                        File file = new File(dir, FILENAME);
                        if (file.exists()) {
                            if (file.delete()) {
                                Toast.makeText(getContext(), "清除成功", Toast.LENGTH_SHORT).show();
                            }

//                                Toast.makeText(getContext(), "清除失败", Toast.LENGTH_SHORT).show();
                        }
//                            Toast.makeText(getContext(), "文件不存在", Toast.LENGTH_SHORT).show();
                        updateView();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.setIcon(R.drawable.ic_baseline_cleaning_services_24);
                builder.show();
            }
        });

        return view;
    }

    private void updateView() {
        if (mBtnCollection.isSelected()) {
            mNewsList = mOfflineNewsManager.getCollectionNews();
        } else {
            mNewsList = mOfflineNewsManager.getHistoryNews();
        }
        mNewsAdapter.setNewsList(mNewsList);
        mNewsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        updateView();
    }
}