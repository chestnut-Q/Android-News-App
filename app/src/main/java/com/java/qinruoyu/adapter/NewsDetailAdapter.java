package com.java.qinruoyu.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.java.qinruoyu.R;

import java.util.ArrayList;


public class NewsDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mImageList;
    private String[] mContents;
    private int screenWidth;

    public NewsDetailAdapter(Context context, ArrayList<String> imageList, String[] contents, int screenWidth) {
        this.mContext = context;
        this.mImageList = imageList;
        this.mContents = contents;
        this.screenWidth = screenWidth;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new LinearViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_news_detail_image, parent, false));
        } else {
            return new LinearViewHolder2(LayoutInflater.from(mContext).inflate(R.layout.layout_news_detail_content, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int[] type = getItemView(position);
        if (type[0] == 1) {
            ((LinearViewHolder) holder).imageView.setMaxWidth((int) (screenWidth * 0.8));
            ((LinearViewHolder) holder).imageView.setMaxHeight(screenWidth * 5);
            String url = mImageList.get(type[1]);
            Log.d("NewsDetailAdapter", "image: " + url);
            Glide.with(mContext).load(url).apply(new RequestOptions().error(R.drawable.load_fail3)).into(((LinearViewHolder) holder).imageView);
        } else {
            ((LinearViewHolder2) holder).textView.setText(mContents[type[1]]);
        }
    }

    private int[] getItemView(int position) {
        int[] type = new int[2];
        int min = Math.min(mContents.length, mImageList.size());
        if (position >= 2 * min) {
            if (mContents.length < mImageList.size()) {
                type[0] = 1;
            } else {
                type[0] = 0;
            }
            type[1] = position - min;
        } else {
            type[0] = position % 2;
            type[1] = position / 2;
        }
        return type;
    }

    @Override
    public int getItemViewType(int position) {
        return getItemView(position)[0];
    }

    @Override
    public int getItemCount() {
        return mContents.length + mImageList.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image_news_detail);
        }
    }

    class LinearViewHolder2 extends RecyclerView.ViewHolder {

        private TextView textView;

        public LinearViewHolder2(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_content_news_detail);
        }
    }
}

//            GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
//                    .addHeader("User-Agent",
//                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36 Edge/15.15063")
//                    .build());

//            ViewGroup.LayoutParams lp = ((LinearViewHolder) holder).imageView.getLayoutParams();
//            lp.width = screenWidth;
//            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            ((LinearViewHolder) holder).imageView.setLayoutParams(lp);