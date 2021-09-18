package com.java.qinruoyu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.java.qinruoyu.News;
import com.java.qinruoyu.NewsManager;
import com.java.qinruoyu.OfflineNewsManager;
import com.java.qinruoyu.R;
import com.java.qinruoyu.adapter.NewsAdapter;
import com.java.qinruoyu.fragment.NewsListFragment;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SearchResultActivity extends AppCompatActivity {

    private NewsManager mNewsManager;
    private String category = "全部";
    private String query;
    private String startTime, endTime;
    private ArrayList<News> mNewsList;
    private NewsAdapter mNewsAdapter;
    private RecyclerView mRvNewsSearched;
    private LinearLayoutManager mLinearLayoutManager;
    private OfflineNewsManager mOfflineNewsManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mNewsManager = NewsManager.getInstance();
        mOfflineNewsManager = OfflineNewsManager.getInstance(getApplicationContext());
        Intent intent = getIntent();

        query = intent.getStringExtra("query");
        category = intent.getStringExtra("category");
        Log.d("SearchResultActivity", "category:" + category);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        if (startTime.isEmpty())
            startTime = "";
        if (endTime.isEmpty())
            endTime = today;
        mNewsList = mNewsManager.getNews(15, startTime, endTime, query, category, false, false);

        mNewsAdapter = new NewsAdapter(mNewsList, SearchResultActivity.this, new NewsAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                if (isFastDoubleClick())
                    return;
                Intent intent = new Intent(getApplicationContext(), NewsDetailActivity.class);
                intent.putExtra("news", mNewsList.get(pos));
                startActivity(intent);
            }
        }, mOfflineNewsManager.getHistoryList());

        RefreshLayout refreshLayout = findViewById(R.id.srl_search_result);
        // 下滑刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<News> newsTmp = mNewsManager.getNews(15, startTime, endTime, query, category,
                                        true, false);
                                if (newsTmp != null) {
                                    mNewsList.clear();
                                    mNewsList.addAll(newsTmp);
                                    mNewsAdapter.updateNews(newsTmp);
                                    mNewsAdapter.notifyDataSetChanged();
                                    refreshlayout.finishRefresh();
                                    Toast.makeText(getApplicationContext(), "刷新完成", Toast.LENGTH_SHORT).show();
                                } else {
                                    refreshlayout.finishRefresh();
                                    Toast.makeText(getApplicationContext(), "刷新失败", Toast.LENGTH_SHORT).show();
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                                ArrayList<News> newsTmp = mNewsManager.getNews(15, startTime, endTime, query, category,
                                        true, false);
                                if (newsTmp != null) {
                                    mNewsList.addAll(newsTmp);
                                    mNewsAdapter.addNews(newsTmp);
                                    mNewsAdapter.notifyDataSetChanged();
                                    refreshlayout.finishLoadMore();
                                    Toast.makeText(getApplicationContext(), "新返回" + newsTmp.size() + "条新闻", Toast.LENGTH_SHORT).show();

                                } else {
                                    refreshlayout.finishLoadMore();
                                    Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }, 2000);
            }
        });

        mRvNewsSearched = findViewById(R.id.rv_search_result);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRvNewsSearched.setLayoutManager(mLinearLayoutManager);
        mRvNewsSearched.setAdapter(mNewsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNewsAdapter != null)
            mNewsAdapter.notifyDataSetChanged();
    }
}