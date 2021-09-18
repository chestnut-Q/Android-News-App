package com.java.qinruoyu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.qinruoyu.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;
    private static CategoryAdapter Instance = null;
    private ArrayList<String> mCategoryList;
    private View mLastCategory;
    public View mFirstCategoty;

    public CategoryAdapter(Context context, ArrayList<String> categoryList, OnItemClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mCategoryList = categoryList;
    }

    public void setCategoryList(ArrayList<String> mCategoryList) {
        this.mCategoryList = mCategoryList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).textView.setText(mCategoryList.get(holder.getBindingAdapterPosition()));
        if (position == 0) {
            mFirstCategoty = holder.itemView;
            select(mFirstCategoty);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(view, mCategoryList.get(holder.getBindingAdapterPosition()));
                select(holder.itemView);
            }
        });
    }


    public void select(View itemView) {
        if (mLastCategory != null) {
            mLastCategory.setBackgroundColor(0xFFFFFFFF);
        }
        itemView.setBackgroundColor(0xFFBBBBBB);
        mLastCategory = itemView;
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_category);
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, String category);
    }

}
