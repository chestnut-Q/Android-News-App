package com.java.qinruoyu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.java.qinruoyu.DatePickDialog;
import com.java.qinruoyu.R;
import com.java.qinruoyu.adapter.TagAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView mRvTag;
    private final String[] ALL_CATEGORY = {"娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};
    private boolean[] mCategoryChosen = {true, true, true, true, true, true, true, true, true, true};
    private Button mBtnSearchOutside;
    private String query = null;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    protected String mStartTime = "";
    protected String mEndTime = "";
    private DatePickDialog mDatePickDialog;
    private Button mDateSeter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initSearchView();
        setDateView();

        mRvTag = findViewById(R.id.rv_tag_in_search);
        mRvTag.setLayoutManager(new GridLayoutManager(SearchActivity.this, 4));
        mRvTag.setAdapter(new TagAdapter(SearchActivity.this, new TagAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                mCategoryChosen[pos] = !mCategoryChosen[pos];
            }
        }));

        mBtnSearchOutside = findViewById(R.id.btn_search_outside);
        mBtnSearchOutside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callSearch();
            }
        });
    }

    public void initSearchView() {
        final SearchView searchView = findViewById(R.id.sv_search);
//        searchView.setIconified(false);//设置searchView处于展开状态
        View v = searchView.findViewById(R.id.search_plate);
        if (v != null) {
            v.setBackground(null);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;
                return true;
            }
        });
    }

    public void callSearch() {
        //Do searching
        Log.i("query", "" + query);
        Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
        intent.putExtra("query", query);
        ArrayList<String> stringArrayList = new ArrayList<>();
        int catCounter = 0;
        for (int i = 0; i < ALL_CATEGORY.length; i++) {
            if (mCategoryChosen[i] == true) {
                stringArrayList.add(ALL_CATEGORY[i]);
                catCounter++;
            }
        }
        if (catCounter == 10) {
            intent.putExtra("category", "全部");
        } else {
            intent.putExtra("category", String.join(",", stringArrayList));
        }
        // 传递时间参数
        intent.putExtra("startTime", mStartTime);
        intent.putExtra("endTime", mEndTime);
        startActivity(intent);
        finish();
    }

    public void setDateView() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
        mDateSeter = findViewById(R.id.btn_calendar_news_list);
        mDateSeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStartTime.isEmpty())
                    mStartTime = today;
                if (mEndTime.isEmpty())
                    mEndTime = today;
                mDatePickDialog = new DatePickDialog(SearchActivity.this, mStartTime, mEndTime);
                mDatePickDialog.datePickerDialog(mDateSeter);
            }
        });
    }

    public void setStartTime(String startTime) {
        this.mStartTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.mEndTime = endTime;
    }
}