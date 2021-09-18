package com.java.qinruoyu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.java.qinruoyu.fragment.NewsListFragment;
import com.java.qinruoyu.R;
import com.java.qinruoyu.fragment.HomeFragment;

public class MainScreenActivity extends AppCompatActivity {

    private NewsListFragment mNewsListFragment;
    private HomeFragment mHomeFragment;
    private ImageView mIvNews;
    private ImageView mIvMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Log.d("Main Screen", "onCreate");

        mHomeFragment = HomeFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_main_screen, mHomeFragment, "home").commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().hide(mHomeFragment).commitAllowingStateLoss();
        mNewsListFragment = NewsListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_main_screen, mNewsListFragment, "news").commitAllowingStateLoss();

        mIvNews = findViewById(R.id.iv_news_main_screen);
        mIvMine = findViewById(R.id.iv_mine_main_screen);
        mIvNews.setSelected(true);
        mIvNews.setEnabled(false);
        mIvMine.setSelected(false);
        mIvMine.setEnabled(true);

        mIvNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIvNews.setSelected(true);
                mIvNews.setEnabled(false);
                mIvMine.setSelected(false);
                mIvMine.setEnabled(true);
                getSupportFragmentManager().beginTransaction().hide(mHomeFragment).show(mNewsListFragment).addToBackStack(null).commitAllowingStateLoss();
            }
        });

        mIvMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIvMine.setSelected(true);
                mIvMine.setEnabled(false);
                mIvNews.setSelected(false);
                mIvNews.setEnabled(true);
                getSupportFragmentManager().beginTransaction().hide(mNewsListFragment).show(mHomeFragment).addToBackStack(null).commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (mHomeFragment.isHidden()) {
                mIvMine.setSelected(true);
                mIvMine.setEnabled(false);
                mIvNews.setSelected(false);
                mIvNews.setEnabled(true);
            } else {
                mIvNews.setSelected(true);
                mIvNews.setEnabled(false);
                mIvMine.setSelected(false);
                mIvMine.setEnabled(true);
            }
        }
        super.onBackPressed();
    }
}