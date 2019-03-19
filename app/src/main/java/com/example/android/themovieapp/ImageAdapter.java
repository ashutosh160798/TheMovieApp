package com.example.android.themovieapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


class ImageAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final ArrayList<String> img;

    public ImageAdapter(@NonNull Context context, int resource, ArrayList<String> mDataList) {
        super(context, resource);
        img = mDataList;
        mContext = context;
    }


    @Override
    public int getCount() {
        return img.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView poster;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            poster = (ImageView) inflater.inflate(R.layout.grid_item, viewGroup, false);
        } else {
            poster = (ImageView) view;
        }

        Picasso.with(mContext).load(img.get(i)).resize(185, 275).into(poster);

        return poster;
    }
}
