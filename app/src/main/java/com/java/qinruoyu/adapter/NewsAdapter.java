package com.java.qinruoyu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.java.qinruoyu.News;
import com.java.qinruoyu.NewsManager;
import com.java.qinruoyu.R;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<News> mNewsList;
    private Context mContext;
    private NewsManager mNewsManager;
    private OnItemClickListener mListener;
    private ArrayList<String> mImageList;
    private ArrayList<String> mHistoryList;

    public NewsAdapter(ArrayList<News> newsList, Context context, OnItemClickListener listener, ArrayList<String> historyList) {
        this.mNewsList = newsList;
        this.mContext = context;
        this.mNewsManager = NewsManager.getInstance();
        this.mListener = listener;
        this.mHistoryList = historyList;
    }

    public void updateNews(ArrayList<News> news) {
        this.mNewsList = news;
    }

    public void addNews(ArrayList<News> news) {
        this.mNewsList.addAll(news);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new NoPicViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_nopic_news_item, parent, false));
        else if (viewType == 1)
            return new OnePicViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_onepic_news_item, parent, false));
        else
            return new MultiPicViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_multipic_news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            ((NoPicViewHolder) holder).mTvTitle.setText(mNewsList.get(position).getTitle().replace((char) 12288 + "", ""));
            String tmp = mNewsList.get(position).getContent().replace((char) 12288 + "", "").replace("\n", "");
            if (tmp.length() > 90)
                tmp = tmp.substring(0, 90) + "…";
            ((NoPicViewHolder) holder).mTvAbstract.setText(tmp);

            // 浏览变色
            if (mHistoryList.contains(mNewsList.get(position).getNewsID())){
                ((NoPicViewHolder) holder).mTvTitle.setTextColor(0xFFCCCCCC);
                ((NoPicViewHolder) holder).mTvAbstract.setTextColor(0xFFCCCCCC);
            } else {
                ((NoPicViewHolder) holder).mTvTitle.setTextColor(0xFF000000);
                ((NoPicViewHolder) holder).mTvAbstract.setTextColor(0xFF5d5d5d);
            }

        } else if (holder.getItemViewType() == 1) {
            ((OnePicViewHolder) holder).mTvTitle.setText(mNewsList.get(position).getTitle().replace((char) 12288 + "", ""));
            String tmp = mNewsList.get(position).getContent().replace((char) 12288 + "", "").replace("\n", "");
            if (tmp.length() > 40)
                tmp = tmp.substring(0, 40) + "…";
            ((OnePicViewHolder) holder).mTvAbstract.setText(tmp);
            ((OnePicViewHolder) holder).mTvAbstract.setMinHeight(30);

            // 浏览变色
            if (mHistoryList.contains(mNewsList.get(position).getNewsID())){
                ((OnePicViewHolder) holder).mTvTitle.setTextColor(0xFFCCCCCC);
                ((OnePicViewHolder) holder).mTvAbstract.setTextColor(0xFFCCCCCC);
            } else {
                ((OnePicViewHolder) holder).mTvTitle.setTextColor(0xFF000000);
                ((OnePicViewHolder) holder).mTvAbstract.setTextColor(0xFF5d5d5d);
            }

            String url = mImageList.get(0);
            Glide.with(mContext).load(url).apply(new RequestOptions().error(R.drawable.load_fail3)).into(((OnePicViewHolder) holder).mIvPic);
        } else {
            ((MultiPicViewHolder) holder).mTvTitle.setText(mNewsList.get(position).getTitle().replace((char) 12288 + "", ""));
            String url1 = mImageList.get(0);
            Glide.with(mContext).load(url1).apply(new RequestOptions().error(R.drawable.load_fail3)).into(((MultiPicViewHolder) holder).mIvPic1);
            String url2 = mImageList.get(1);
            Glide.with(mContext).load(url2).apply(new RequestOptions().error(R.drawable.load_fail3)).into(((MultiPicViewHolder) holder).mIvPic2);
            String url3 = mImageList.get(2);
            Glide.with(mContext).load(url3).apply(new RequestOptions().error(R.drawable.load_fail3)).into(((MultiPicViewHolder) holder).mIvPic3);

            // 浏览变色
            if (mHistoryList.contains(mNewsList.get(position).getNewsID())){
                ((MultiPicViewHolder) holder).mTvTitle.setTextColor(0xFFCCCCCC);
            } else {
                ((MultiPicViewHolder) holder).mTvTitle.setTextColor(0xFF000000);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(holder.getBindingAdapterPosition());
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        News news = mNewsList.get(position);
        String[] images = news.getImage();
        mImageList = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            if (!images[i].equals("") && images[i].contains("http")) {
                mImageList.add(images[i]);
            }
        }
        if (mImageList.size() == 0) {
            return 0;
        } else if (mImageList.size() <= 2) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public ArrayList<News> getNewsList() {
        return mNewsList;
    }

    public void setNewsList(ArrayList<News> newsList) {
        this.mNewsList = newsList;
    }

    public void setHistoryList(ArrayList<String> historyList) {
        this.mHistoryList = historyList;
    }

    class NoPicViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle;
        private TextView mTvAbstract;

        public NoPicViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title_news_item);
            mTvAbstract = itemView.findViewById(R.id.tv_abstract_news_item);
        }
    }

    class OnePicViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle;
        private TextView mTvAbstract;
        private ImageView mIvPic;

        public OnePicViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title_news_item);
            mTvAbstract = itemView.findViewById(R.id.tv_abstract_news_item);
            mIvPic = itemView.findViewById(R.id.iv_one_pic);
        }
    }

    class MultiPicViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle;
        private ImageView mIvPic1;
        private ImageView mIvPic2;
        private ImageView mIvPic3;

        public MultiPicViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title_news_item);
            mIvPic1 = itemView.findViewById(R.id.iv_pic1);
            mIvPic2 = itemView.findViewById(R.id.iv_pic2);
            mIvPic3 = itemView.findViewById(R.id.iv_pic3);
        }
    }

    public interface OnItemClickListener {
        void onClick(int pos);
    }
}
