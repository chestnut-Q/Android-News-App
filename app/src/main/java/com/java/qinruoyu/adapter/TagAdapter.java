package com.java.qinruoyu.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.qinruoyu.R;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.GridViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;
    private final String[] mCategoryList =  {"娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};

    public TagAdapter(Context context, OnItemClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GridViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_grid_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        holder.textView.setText(mCategoryList[position]);
        holder.textView.setSelected(true);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(holder.getBindingAdapterPosition());
//                view.setBackground(R.drawable.bg_search);
                if (view.isSelected()) {
                    ((TextView) view).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    view.setSelected(false);
                } else {
                    ((TextView) view).getPaint().setFlags(((TextView) view).getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    view.setSelected(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCategoryList.length;
    }

    class GridViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_item);
        }
    }

    public interface OnItemClickListener {
        void onClick(int pos);
    }
}
