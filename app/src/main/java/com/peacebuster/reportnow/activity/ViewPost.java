package com.peacebuster.reportnow.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.peacebuster.reportnow.R;
import com.peacebuster.reportnow.helper.CircleTransform;
import com.peacebuster.reportnow.model.PostData;
import com.squareup.picasso.Picasso;

public class ViewPost extends AppCompatActivity {

    private TextView vp_name;
    private TextView vp_heading;
    private TextView vp_description;
    private TextView vp_time;
    private TextView vp_loc_cate;
    private ImageView vp_user_image;
    private ImageView vp_post_image;
    PostData post;
    private Toolbar tool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_viewpost);

        post = (PostData)getIntent().getSerializableExtra("OBJ");
        tool= (Toolbar)findViewById(R.id.toolbar_viewpost);
        tool.setTitle("Top Post");

        setSupportActionBar(tool);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        tool.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        tool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        initView();
    }

    private void initView(){

        vp_name = (TextView)findViewById(R.id.vp_name);
        vp_heading = (TextView)findViewById(R.id.vp_heading);
        vp_description = (TextView)findViewById(R.id.vp_description);
        vp_time = (TextView)findViewById(R.id.vp_time);
        vp_loc_cate = (TextView)findViewById(R.id.vp_location_category);
        vp_post_image = (ImageView)findViewById(R.id.vp_post_image);
        vp_user_image = (ImageView)findViewById(R.id.vp_user_image);

        vp_name.setText(post.getName());
        vp_heading.setText(post.getHeading());
        vp_description.setText(post.getDescription());
        vp_time.setText(post.getCreated_at());
        vp_loc_cate.setText(post.getCategory()+" : "+post.getLocation());

        Picasso.with(ViewPost.this).load(post.getImage_url()).into(vp_post_image);

        Glide.with(this).load(post.getUserImage())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(vp_user_image);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
