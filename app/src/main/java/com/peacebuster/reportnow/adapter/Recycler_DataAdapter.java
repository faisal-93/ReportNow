package com.peacebuster.reportnow.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.peacebuster.reportnow.R;
import com.peacebuster.reportnow.activity.ViewPost;
import com.peacebuster.reportnow.model.PostData;

import java.util.ArrayList;

public class Recycler_DataAdapter extends RecyclerView.Adapter<Recycler_DataAdapter.ViewHolder> {
    private ArrayList<PostData> pData;
    private Context context;


    public Recycler_DataAdapter(Context context, ArrayList<PostData> pData) {
        this.pData = pData;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        viewHolder.tv_description.setText(pData.get(i).getHeading());
        viewHolder.tv_time.setText(pData.get(i).getCreated_at());


        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();  // deprecated
        int height = display.getHeight();
        Glide.with(context)
                .load(pData.get(i).getImage_url())
                .centerCrop()
                .crossFade()
                .override(width, height/3)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(viewHolder.img);
        if (i > 0) {

            viewHolder.tv_description.setTextSize(15);
            Glide.with(context)
                    .load(pData.get(i).getImage_url())
                    .centerCrop()
                    .crossFade()
                    .override(width / 2, height/6)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(viewHolder.img);
        }

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostData obj = new PostData();
                obj.setName(pData.get(i).getName());
                obj.setUserImage(pData.get(i).getUserImage());
                obj.setHeading(pData.get(i).getHeading());
                obj.setDescription(pData.get(i).getDescription());
                obj.setImage_url(pData.get(i).getImage_url());
                obj.setCreated_at(pData.get(i).getCreated_at());
                obj.setLocation(pData.get(i).getLocation());
                obj.setCategory(pData.get(i).getCategory());

                Intent intent = new Intent(context, ViewPost.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("OBJ",obj);
                intent.putExtras(mBundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_description;
        private ImageView img;
        private TextView tv_time;

        public ViewHolder(View view) {
            super(view);

            tv_description = (TextView) view.findViewById(R.id.tv_heading);
            img = (ImageView) view.findViewById(R.id.img);
            tv_time = (TextView) view.findViewById(R.id.tv_time);

            context = view.getContext();
        }
    }

}