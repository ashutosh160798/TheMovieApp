package com.example.android.themovieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ashu on 27-06-2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<ReviewsObject> mDataList;
    private String reviewText;
    private String author;
    private final ReviewsItemClickListener mListener;

    public ReviewsAdapter(Context mContext, ArrayList<ReviewsObject> mDataList, ReviewsItemClickListener mListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mListener = mListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        author = mDataList.get(position).getAuthor();
        reviewText = mDataList.get(position).getContent();
        holder.content.setText(reviewText);
        holder.author.setText(author);

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    interface ReviewsItemClickListener {
        void onReviewClick(String url);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView author;
        final TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.authorText);
            content = itemView.findViewById(R.id.reviewText);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            String reviewUrl = mDataList.get(getAdapterPosition()).getUrl();
            mListener.onReviewClick(reviewUrl);
        }
    }
}
