package com.java.qinruoyu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.java.qinruoyu.News;
import com.java.qinruoyu.OfflineNewsManager;
import com.java.qinruoyu.R;
import com.java.qinruoyu.adapter.NewsDetailAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import cn.jzvd.JZVideoPlayerStandard;

public class NewsDetailActivity extends AppCompatActivity {

    private TextView mTvTitle, mTvPublisher;
    private News mNews;
    private RecyclerView mRvImageAndContent;
    private NewsDetailAdapter mNewsDetailAdapter;
    private FloatingActionButton mBtnCollection;
    private OfflineNewsManager mOfflineNewsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        mOfflineNewsManager = OfflineNewsManager.getInstance(getApplicationContext());

        Intent intent = getIntent();
        mNews = (News) intent.getSerializableExtra("news");
        mTvTitle = findViewById(R.id.tv_title_news_detail);
        mTvTitle.setText(mNews.getTitle());
        mTvPublisher = findViewById(R.id.tv_publisher_news_detail);
        String publish = mNews.getPublisher() + " " + mNews.getPublishTime();
        mTvPublisher.setText(publish);

        if (mNews.getVideo() != null && !mNews.getVideo().equals("")) {
            String url = mNews.getVideo();
            findViewById(R.id.page_video_layout).setVisibility(View.VISIBLE);
            JZVideoPlayerStandard jzVideoPlayerStandard = findViewById(R.id.video_player);
            jzVideoPlayerStandard.setUp(url, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL);
            Glide.with(this).load(url).into(jzVideoPlayerStandard.thumbImageView);
        }

        // 获取屏幕宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        int screenWidth = size.x;

        String[] contents = mNews.getContent().split("\n");
        String[] images = mNews.getImage();
        ArrayList<String> imageList = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            if (!images[i].equals("") && images[i].contains("http")) {
                imageList.add(images[i]);
            }
        }
        mRvImageAndContent = findViewById(R.id.rv_image_and_content);
        mRvImageAndContent.setLayoutManager(new LinearLayoutManager(NewsDetailActivity.this));
        mNewsDetailAdapter = new NewsDetailAdapter(NewsDetailActivity.this, imageList, contents, screenWidth);
        mRvImageAndContent.setAdapter(mNewsDetailAdapter);
        Log.d("NewsDetailActivity", "image=" + imageList.size() + " content=" + contents.length);

        mBtnCollection = findViewById(R.id.btn_star);
        mBtnCollection.setSelected(mOfflineNewsManager.getCollectionList().contains(mNews.getNewsID()));
        mBtnCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBtnCollection.isSelected()){
                    mBtnCollection.setSelected(false);
                    Toast.makeText(getApplicationContext(), "取消收藏", Toast.LENGTH_SHORT).show();
                }else {
                    mBtnCollection.setSelected(true);
                    Toast.makeText(getApplicationContext(), "收藏成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (JZVideoPlayerStandard.backPress()) {
            saveNews();
            finish();
            return;
        }
        saveNews();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayerStandard.releaseAllVideos();
    }

    private void saveNews() {
        if (mBtnCollection != null && mBtnCollection.isSelected()){
            mOfflineNewsManager.saveHistoryNews(mNews, true);
        }else {
            mOfflineNewsManager.saveHistoryNews(mNews, false);
        }

    }
}