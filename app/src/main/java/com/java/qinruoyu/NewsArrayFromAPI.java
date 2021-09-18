package com.java.qinruoyu;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class NewsArrayFromAPI extends AsyncTask<String, Void, ArrayList<News>> {

    @Override
    protected ArrayList<News> doInBackground(String... strings) {
        try {
            System.out.println("Json");
            String sizeUtf8 = (strings[0] == null) ? "" : URLEncoder.encode(strings[0], "utf-8");
            String startDateUtf8 = (strings[1] == null) ? "" : URLEncoder.encode(strings[1], "utf-8");
            String endDateUtf8 = (strings[2] == null) ? "" : URLEncoder.encode(strings[2], "utf-8");
            String wordsUtf8 = (strings[3] == null) ? "" : URLEncoder.encode(strings[3], "utf-8");
            String categoriesUtf8 = (strings[4] == null) ? "" : URLEncoder.encode(strings[4], "utf-8");
            String pageUtf8 = (strings[5] == null) ? "" : URLEncoder.encode(strings[5], "utf-8");
            String url = "size=" + sizeUtf8 // 删去了接口
                    + "&startDate=" + startDateUtf8 + "&endDate=" + endDateUtf8 + "&words=" + wordsUtf8
                    + "&categories=" + categoriesUtf8 + "&page=" + pageUtf8;
            Log.d("doInBackground", "url=" + url);
            // image是list
//            url = "https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2020-01-01&endDate=2021-09-07&words=python";
            // video
//            url = "https://api2.newsminer.net/svc/news/queryNewsList?words=%E5%88%9B%E6%84%8F%E4%BA%A7%E5%93%81%E5%BC%80%E5%8F%91&size=15&startDate=2021-09-01&endDate=2021-09-02";

            String json = getHttpResponse(url);
            if (json == null) {
                return null;
            }
            JSONObject jsonObj = new JSONObject(json);
            return getNewsArray(jsonObj);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<News> getNewsArray(JSONObject json) {
        ArrayList<News> newsArray = new ArrayList<>();

        if (json != null) {
            try {
                if (Integer.parseInt(json.getString("pageSize")) == 0) {
                    return null;
                }
                JSONArray newsJsonArray = json.getJSONArray("data");
                for (int i = 0; i < newsJsonArray.length(); i++) {
                    JSONObject newsObject = newsJsonArray.getJSONObject(i);
                    String[] images = {""};
                    try {
                        String image = newsObject.getString("image");
                        images = image.split(",");
                    } catch (JSONException e) {
                        Log.d("getNewsArray", "解析错误");
                        continue;
//                        StringBuilder stringBuilder = new StringBuilder();
//                        Object[] a = (Object[]) newsObject.get("image");
//                        for (Object b : a) {
//                            stringBuilder.append(b.toString() + "###");
//                        }
//                        images = stringBuilder.toString().split("###");
                    }

                    for (int j = 0; j < images.length; j++) {
                        images[j] = images[j]
                                .replace("\\", "")
                                .replace("[", "")
                                .replace("]", "")
                                .replace("\"", "")
                                .trim();
                        Log.d("error image", j + " " + images[j]);
                    }

                    StringBuffer keywordsBuffer = new StringBuffer();
                    JSONArray keywordArray = newsObject.getJSONArray("keywords");
                    for (int j = 0; j < keywordArray.length(); j++) {
                        JSONObject keywordObject = keywordArray.getJSONObject(j);
                        keywordsBuffer.append(keywordObject.getString("word"));
                        if (j != keywordArray.length() - 1) {
                            keywordsBuffer.append("###");
                        }
                    }
                    String[] keywords = keywordsBuffer.toString().split("###");

                    String publishTime = newsObject.getString("publishTime");
                    String video = newsObject.getString("video");
                    String title = newsObject.getString("title");
                    String content = newsObject.getString("content");
                    String newsID = newsObject.getString("newsID");
                    String publisher = newsObject.getString("publisher");

                    JSONArray organizationArray = newsObject.getJSONArray("organizations");
                    StringBuffer organizationsBuffer = new StringBuffer();
                    for (int j = 0; j < organizationArray.length(); j++) {
                        JSONObject organizationObject = organizationArray.getJSONObject(j);
                        organizationsBuffer.append(organizationObject.getString("mention"));
                        if (j != organizationArray.length() - 1) {
                            organizationsBuffer.append("#");
                        }
                    }
                    String[] organizations = organizationsBuffer.toString().split("#");
                    newsArray.add(new News(images, publishTime, keywords, video, title,
                            content, newsID, organizations, publisher));
                }
                return newsArray;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getHttpResponse(String allConfigUrl) {
        BufferedReader in = null;
        StringBuffer result = null;
        try {
            URL url = new URL(allConfigUrl);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setConnectTimeout(2000); // save 500
            connection.setReadTimeout(2000);
            System.out.println("Before connect");
            connection.connect();
            System.out.println("After connect");

            result = new StringBuffer();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            return result.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return null;

    }
}
