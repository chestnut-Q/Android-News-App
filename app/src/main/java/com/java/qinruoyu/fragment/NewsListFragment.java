package com.java.qinruoyu.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.java.qinruoyu.News;
import com.java.qinruoyu.NewsManager;
import com.java.qinruoyu.OfflineNewsManager;
import com.java.qinruoyu.R;
import com.java.qinruoyu.activity.CategoryManagementActivity;
import com.java.qinruoyu.activity.NewsDetailActivity;
import com.java.qinruoyu.activity.SearchActivity;
import com.java.qinruoyu.adapter.CategoryAdapter;
import com.java.qinruoyu.adapter.NewsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class NewsListFragment extends Fragment {

    private String mParam1;
    private String mParam2;
    private RecyclerView mRvCategory;
    private RecyclerView mRvNews;
    private CategoryAdapter mCategoryAdapter;
    private NewsAdapter mNewsAdapter;
    private ArrayList<News> mNewsList = new ArrayList<>();
    private String mCurrentCategory;
    private NewsManager mNewsManager;
    private FloatingActionButton mBtnSearch;
    private String mCategoryArrangement;
    private ArrayList<String> mCategoryList = new ArrayList<>();
    private ImageButton mIbCatManager;
    private final int mSize = 15;
    public static RefreshLayout refreshLayout;
    private OfflineNewsManager mOfflineNewsManager;
    private static long timeD, lastClickTime;

    public NewsListFragment() {
        // Required empty public constructor
    }

    public static NewsListFragment newInstance() {
        NewsListFragment fragment = new NewsListFragment();
        return fragment;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("NewsListFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        mCurrentCategory = "全部";
        mCategoryArrangement = "娱乐,军事,教育,文化,健康,财经,体育,汽车,科技,社会#";
        mOfflineNewsManager = OfflineNewsManager.getInstance(getContext());

        mRvNews = view.findViewById(R.id.rv_news_list);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                System.out.println(today);
                mNewsManager = NewsManager.getInstance();
                Log.d("onCreateView", mNewsManager.getCurrentCategory());
                ArrayList<News> newsTmp = mNewsManager.getNews(mSize, null, today, null, mCurrentCategory, false, false);
                mNewsList = new ArrayList<>();
                if (newsTmp != null)
                    mNewsList.addAll(newsTmp);
                System.out.println("现在有" + mNewsList.size() + "新闻可以展示");

                // 新闻列表
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager layoutManagerNews = new LinearLayoutManager(getContext());
                        mRvNews.setLayoutManager(layoutManagerNews);
                        mNewsAdapter = new NewsAdapter(mNewsList, getContext(), new NewsAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int pos) {

                                // 防止连续点击
                                if (isFastDoubleClick())
                                    return;

                                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                                News newsDetail = mNewsList.get(pos);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("news", newsDetail);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }, mOfflineNewsManager.getHistoryList());
                        mRvNews.setAdapter(mNewsAdapter);
                        mRvNews.setItemViewCacheSize(100);
                    }
                });
            }
        }).start();

        // 分类列表
        mRvCategory = view.findViewById(R.id.rv_category);
        mRvCategory.setHasFixedSize(true);
        LinearLayoutManager layoutManagerCategory = new LinearLayoutManager(getContext());
        layoutManagerCategory.setOrientation(RecyclerView.HORIZONTAL);
        mRvCategory.setLayoutManager(layoutManagerCategory);
        String[] categoryString = mCategoryArrangement.split("#")[0].split(",");
        mCategoryList = new ArrayList<>(Arrays.asList("全部"));
        mCategoryList.addAll(Arrays.asList(categoryString));
        mCategoryAdapter = new CategoryAdapter(getContext(), mCategoryList, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, String category) {

                // 防止连续点击
                view.setClickable(false);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setClickable(true);
                    }
                }, 500);

                Toast.makeText(getContext(), category, Toast.LENGTH_SHORT).show();
                updateNewsView(category);
            }
        });
        mRvCategory.setAdapter(mCategoryAdapter);
        mRvCategory.setItemViewCacheSize(5);
        mBtnSearch = view.findViewById(R.id.btn_search);

        // 下滑刷新
        refreshLayout = view.findViewById(R.id.item_refresh_layout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                                ArrayList<News> newsTmp = mNewsManager.getNews(15, null,
                                        today, null, mCurrentCategory, true, false);
                                if (newsTmp != null) {
                                    if (mNewsList == null) {
                                        mNewsList = new ArrayList<>();
                                    }
                                    mNewsList.clear();
                                    mNewsList.addAll(newsTmp);
                                    mNewsAdapter.updateNews(newsTmp);
                                    refreshlayout.finishRefresh();
                                    mNewsAdapter.notifyDataSetChanged();
                                    Toast.makeText(getActivity().getApplicationContext(), "刷新完成", Toast.LENGTH_SHORT).show();
                                } else {
                                    refreshlayout.finishRefresh();
                                    Toast.makeText(getActivity().getApplicationContext(), "刷新失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }, 2000);
            }
        });

        // 上拉加载
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshlayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                                ArrayList<News> newsTmp = mNewsManager.getNews(15, null,
                                        today, null, mCurrentCategory, true, false);
                                if (newsTmp != null) {
                                    if (mNewsList == null) {
                                        mNewsList = new ArrayList<>();
                                    }
                                    mNewsList.addAll(newsTmp);
                                    mNewsAdapter.addNews(newsTmp);
                                    mNewsAdapter.notifyDataSetChanged();
                                    refreshlayout.finishLoadMore();
                                    Toast.makeText(getActivity().getApplicationContext(), "新返回" + newsTmp.size() + "条新闻", Toast.LENGTH_SHORT).show();

                                } else {
                                    refreshlayout.finishLoadMore();
                                    Toast.makeText(getActivity().getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }, 2000);
            }
        });

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        mIbCatManager = view.findViewById(R.id.ib_category_manager);
        mIbCatManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCategoryAdapter != null && mCategoryAdapter.getItemCount() != 0) {
                    mRvCategory.smoothScrollToPosition(0);
                }
                Intent intent = new Intent(getActivity(), CategoryManagementActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("cat_arrange", mCategoryArrangement);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0) {
            // 标签管理
            if (resultCode == RESULT_OK) {
                mCategoryArrangement = data.getExtras().getString("arrangement");
                mCategoryList = new ArrayList<>(Arrays.asList("全部"));
                mCategoryList.addAll(Arrays.asList(mCategoryArrangement.split("#")[0].split(",")));
                mCategoryAdapter.setCategoryList(mCategoryList);
                mCategoryAdapter.notifyDataSetChanged();
                updateNewsView("全部");
            }
        }
    }

    private void updateNewsView(String category) {
        if (!mCurrentCategory.equals(category)) {
            mCurrentCategory = category;
            mCategoryAdapter.select(mCategoryAdapter.mFirstCategoty);
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
            mNewsList = mNewsManager.getNews(mSize, null, today, null, category, false, false);
            if (mNewsList == null) {
                Log.w("getNews", category + "刷新失败");
                return;
            }
            Log.i("getNews", category + "现在有" + mNewsList.size() + "条新闻可以展示");
            if (mNewsAdapter != null) {
                mNewsAdapter.setNewsList(mNewsList);
                mNewsAdapter.notifyDataSetChanged();
                if (mNewsAdapter.getItemCount() != 0)
                    mRvNews.smoothScrollToPosition(0);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNewsAdapter != null) {
            mNewsAdapter.setHistoryList(mOfflineNewsManager.getHistoryList());
            mNewsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mNewsAdapter != null) {
                mNewsAdapter.setHistoryList(mOfflineNewsManager.getHistoryList());
                mNewsAdapter.notifyDataSetChanged();
            }
        }
    }
}