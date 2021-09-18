package com.java.qinruoyu;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class OfflineNewsManager {

    private static OfflineNewsManager instance = null;
    private Context mContext;
    private ArrayList<String> mHistoryList = new ArrayList<>();
    private ArrayList<String> mCollectionList = new ArrayList<>();
    private final String FILENAME = "data.txt";

    private OfflineNewsManager(Context context) {
        this.mContext = context;
    }

    public static OfflineNewsManager getInstance(Context context) {
        if (instance == null) {
            instance = new OfflineNewsManager(context);
        }
        return instance;
    }

    public void saveHistoryNews(News news, boolean isCollected) {
        mCollectionList.removeIf(s -> news.getNewsID().equals(s));
        if (isCollected) {
            mCollectionList.add(news.getNewsID());
        }
        mHistoryList.removeIf(s -> news.getNewsID().equals(s));
        mHistoryList.add(news.getNewsID());
        Log.d("list num", String.valueOf(mHistoryList.size()));
        Log.d("list num", mHistoryList.toString());

        JSONObject newsObject = new JSONObject();
        JSONObject allObject = new JSONObject();
        File dir = mContext.getFilesDir();
        File file = new File(dir, FILENAME);
        if (file.exists()) {
            try {
                BufferedReader reader = null;
                InputStream in = mContext.openFileInput(FILENAME);
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonString = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                allObject = new JSONObject(jsonString.toString());
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            newsObject.put("title", news.getTitle());
            newsObject.put("publisher", news.getPublisher());
            newsObject.put("publishTime", news.getPublishTime());
            newsObject.put("content", news.getContent());
            newsObject.put("newsID", news.getNewsID());
            newsObject.put("video", news.getVideo());
            newsObject.put("image", Arrays.toString(news.getImage()));
            Log.d("saveHistoryNews", "image=" + Arrays.toString(news.getImage()));
            allObject.put(news.getNewsID(), newsObject);
            String[] histories = new String[mHistoryList.size()];
            for (int i = 0; i < mHistoryList.size(); i++){
                histories[i] = mHistoryList.get(i);
            }
            allObject.put("History", String.join(",", histories));
            String[] collections = new String[mCollectionList.size()];
            for (int i = 0; i < mCollectionList.size(); i++){
                collections[i] = mCollectionList.get(i);
            }
            allObject.put("Collection", String.join(",", collections));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(allObject.toString());
            writer.close();
            Log.i("File", "文件存储成功 " + mContext.getFilesDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<News> getCollectionNews() {
        ArrayList<News> newsList = new ArrayList<>();
        JSONObject allObject = new JSONObject();
        File dir = mContext.getFilesDir();
        File file = new File(dir, FILENAME);
        if (file.exists()) {
            try {
                BufferedReader reader = null;
                InputStream in = mContext.openFileInput(FILENAME);
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonString = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                allObject = new JSONObject(jsonString.toString());
                for (String key : mCollectionList) {
                    JSONObject newsObject = allObject.getJSONObject(key);
                    String image = newsObject.getString("image");
                    String[] images = image.split(",");
                    for (int j = 0; j < images.length; j++) {
                        images[j] = images[j]
                                .replace("\\", "")
                                .replace("[", "")
                                .replace("]", "")
                                .trim();
                    }
                    News news = new News(images, newsObject.getString("publishTime"), new String[]{""},
                            newsObject.getString("video"), newsObject.getString("title"), newsObject.getString("content"),
                            newsObject.getString("newsID"), new String[]{""}, newsObject.getString("publisher"));
                    newsList.add(news);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Collections.reverse(newsList);
        return newsList;
    }

    public ArrayList<News> getHistoryNews() {
        ArrayList<News> newsList = new ArrayList<>();
        JSONObject allObject = new JSONObject();
        File dir = mContext.getFilesDir();
        File file = new File(dir, FILENAME);
        if (file.exists()) {
            try {
                BufferedReader reader = null;
                InputStream in = mContext.openFileInput(FILENAME);
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonString = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                allObject = new JSONObject(jsonString.toString());
                Log.d("allObject", allObject.toString());
                for (String key : mHistoryList) {
                    Log.d("getHistoryNews", key);
                    JSONObject newsObject = allObject.getJSONObject(key);

                    String image = newsObject.getString("image");
                    Log.d("getHistoryNews", "image=" + image);
                    String[] images = image.split(",");
                    for (int j = 0; j < images.length; j++) {
                        images[j] = images[j]
                                .replace("\\", "")
                                .replace("[", "")
                                .replace("]", "")
                                .trim();
                    }
                    News news = new News(images, newsObject.getString("publishTime"), new String[]{""},
                            newsObject.getString("video"), newsObject.getString("title"), newsObject.getString("content"),
                            newsObject.getString("newsID"), new String[]{""}, newsObject.getString("publisher"));
                    newsList.add(news);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Collections.reverse(newsList);
        return newsList;
    }

    public void loadList() {
        // 启动时调用
        mHistoryList = new ArrayList<>();
        mCollectionList = new ArrayList<>();
        JSONObject allObject = new JSONObject();
        File dir = mContext.getFilesDir();
        File file = new File(dir, FILENAME);
        if (file.exists()) {
            try {
                BufferedReader reader = null;
                InputStream in = mContext.openFileInput(FILENAME);
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonString = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                allObject = new JSONObject(jsonString.toString());
                String[] collections = allObject.getString("Collection").split(",");
                mCollectionList.clear();
                mCollectionList.addAll(Arrays.asList(collections));
                String[] histories = allObject.getString("History").split(",");
                mHistoryList.clear();
                mHistoryList.addAll(Arrays.asList(histories));
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> getHistoryList() {
        return mHistoryList;
    }

    public ArrayList<String> getCollectionList() {
        return mCollectionList;
    }

    public void setHistoryList(ArrayList<String> historyList) {
        this.mHistoryList = historyList;
    }

    public void setCollectionList(ArrayList<String> collectionList) {
        this.mCollectionList = collectionList;
    }
}
