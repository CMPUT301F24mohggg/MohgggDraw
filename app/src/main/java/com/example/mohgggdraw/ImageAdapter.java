package com.example.mohgggdraw;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private final Context context;
    private List<String> imageUrls;

    public ImageAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(300, 400)); // Adjust size
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        // Load image using Glide
        Glide.with(context)
                .load(imageUrls.get(position))
                .into(imageView);

        return imageView;
    }

    public void updateImages(List<String> newImageUrls) {
        this.imageUrls = newImageUrls;
        notifyDataSetChanged();
    }
}