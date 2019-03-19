package com.example.android.themovieapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ashu on 27-06-2018.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<TrailerObject> mDataList;
    private final TrailerItemClickListener mListener;

    public TrailerAdapter(Context mContext, ArrayList<TrailerObject> mDataList, TrailerItemClickListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
        this.mDataList = mDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String id = mDataList.get(position).getKey();
        String thumbnailURL = "http://img.youtube.com/vi/".concat(id).concat("/hqdefault.jpg");
        Picasso.with(mContext).load(thumbnailURL).placeholder(R.drawable.thumbnail).into(holder.trailerImage);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public interface TrailerItemClickListener {
        void onTrailerItemClick(String key);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView trailerImage;

        public ViewHolder(View itemView) {
            super(itemView);
            trailerImage = itemView.findViewById(R.id.trailerImage);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            String key = mDataList.get(getAdapterPosition()).getKey();
            mListener.onTrailerItemClick(key);

        }
    }

}
