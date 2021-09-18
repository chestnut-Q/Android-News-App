package com.java.qinruoyu;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class NewsManager {

    private static NewsManager Instance = null;
    private static String mCurrentCategory;
    private final String allCategory = "娱乐,军事,教育,文化,健康,财经,体育,汽车,科技,社会";
    private int newNewsCounter;
    private int mPageCounter;

    private NewsManager() {
        mCurrentCategory = "全部";
        newNewsCounter = 0;
        mPageCounter = 1;
    }

    public static NewsManager getInstance() {
        if (Instance == null) {
            Instance = new NewsManager();
        }
        return Instance;
    }

    public static String getCurrentCategory() {
        return mCurrentCategory;
    }

    public int getNewNewsCounter() {
        return newNewsCounter;
    }

    public ArrayList<News> getNews(int size, final String startDate, final String endDate, final String words,
                                   final String category, boolean refresh, boolean reset) {
        Log.i("NewsManager", "size=" + size + " start=" + startDate + " end=" + endDate + " word=" + words
         + " category=" + category);
        try {
            if (!mCurrentCategory.equals(category)) {
                mPageCounter = 1;
                mCurrentCategory = category;
            } else if (refresh) {
                mPageCounter++;
            } else {
                mPageCounter = 1;
            }
            newNewsCounter = 0;
            NewsArrayFromAPI newsData = new NewsArrayFromAPI();
            Log.d("getNews", endDate);
            ArrayList<News> newsList = newsData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(size), startDate,
                    endDate, words, category.equals("全部") ? null : category, Integer.toString(mPageCounter)).get();
            return newsList;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
