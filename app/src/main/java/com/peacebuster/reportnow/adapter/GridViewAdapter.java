package com.peacebuster.reportnow.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.peacebuster.reportnow.R;
import com.peacebuster.reportnow.activity.ViewPost;
import com.peacebuster.reportnow.model.PostData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Polin on 10-Nov-16.
 */

public class GridViewAdapter extends ArrayAdapter<PostData> {

    private Activity activity;
    private ArrayList<PostData> pData = new ArrayList<PostData>();
    Context ctx;

    public GridViewAdapter(Activity context, int resource, ArrayList<PostData> pData) {
        super(context, resource, pData);
        this.activity = context;
        this.pData = pData;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        Bundle bundle = new Bundle();
        bundle.putString("back", "back");
        //set drawable to imageview
        holder.heading.setText(pData.get(position).getHeading());
        holder.time.setText(pData.get(position).getCreated_at());

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();  // deprecated
        int height = display.getHeight();
        Glide.with(activity)
                .load(pData.get(position).getImage_url())
                .centerCrop()
                .crossFade()
                .override(width, height/3)
                .into(holder.imageView);


        //Picasso.with(activity).load(pData.get(position).getImage_url()).resize(width, height).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostData obj = new PostData();
                obj.setName(pData.get(position).getName());
                obj.setUserImage(pData.get(position).getUserImage());
                obj.setHeading(pData.get(position).getHeading());
                obj.setDescription(pData.get(position).getDescription());
                obj.setImage_url(pData.get(position).getImage_url());
                obj.setCreated_at(pData.get(position).getCreated_at());
                obj.setLocation(pData.get(position).getLocation());
                obj.setCategory(pData.get(position).getCategory());

                Intent intent = new Intent(activity, ViewPost.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("OBJ",obj);
                intent.putExtras(mBundle);
                getContext().startActivity(intent);
            }
        });

        return convertView;


    }

    private class ViewHolder {

        private TextView heading;
        private TextView time;
        private ImageView imageView;

        public ViewHolder(View v) {

            heading = (TextView) v.findViewById(R.id.gi_heading);
            time = (TextView) v.findViewById(R.id.gi_time);
            imageView = (ImageView) v.findViewById(R.id.gi_img);
        }
    }
}