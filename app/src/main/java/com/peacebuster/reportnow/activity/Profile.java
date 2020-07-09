package com.peacebuster.reportnow.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.peacebuster.reportnow.R;
import com.peacebuster.reportnow.adapter.GridViewAdapter;
import com.peacebuster.reportnow.app.AppConfig;
import com.peacebuster.reportnow.app.AppController;
import com.peacebuster.reportnow.helper.CircleTransform;
import com.peacebuster.reportnow.helper.SQLiteHandler;
import com.peacebuster.reportnow.model.PostData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.srain.cube.views.GridViewWithHeaderAndFooter;



/**
 * Created by Polin on 20-Nov-16.
 */

public class Profile extends AppCompatActivity {

    private static final String TAG = Profile.class.getSimpleName();
    ImageView changeImage;
    ImageView update;
    EditText userName, user_email,change_password;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    private GridViewWithHeaderAndFooter gridView;
    private ArrayAdapter<PostData> adapter;
    private SQLiteHandler db;
    private Toolbar profileToolbar;
    private Bitmap thumbnail;
    private ProgressBar proBar;
    private String uid;
    ArrayList<PostData> data = new ArrayList<PostData>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_view);
        gridView = (GridViewWithHeaderAndFooter) findViewById(R.id.grid_view);
        setGridViewHeaderAndFooter();


        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        String username = user.get("name");
        String email = user.get("email");
        String password = user.get("password");
        String url = user.get("image_url");

        userName.setText(username);
        user_email.setText(email);
        change_password.setText(password);

        // Loading profile image
        Glide.with(this).load(url)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(changeImage);

        getUserPost(uid);

    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    private void setGridViewHeaderAndFooter() {
        LayoutInflater layoutInflater = LayoutInflater.from(Profile.this);

        View headerView = layoutInflater.inflate(R.layout.bar, null, false);


        changeImage = (ImageView) headerView.findViewById(R.id.change_pro_img);
        userName = (EditText) headerView.findViewById(R.id.edit_name);
        user_email = (EditText) headerView.findViewById(R.id.email_add);
        change_password =(EditText) headerView.findViewById(R.id.password_change);
        profileToolbar = (Toolbar) headerView.findViewById(R.id.toolbar_profile);

        proBar = (ProgressBar) profileToolbar.findViewById(R.id.toolbar_progress_bar);
        update = (ImageView) profileToolbar.findViewById(R.id.update);

        profileToolbar.setTitle("Profile");
        setSupportActionBar(profileToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        profileToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        profileToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                Intent intent = new Intent(Profile.this, Newsfeed.class);
                startActivity(intent);
                finish();
            }
        });

        changeImage.setOnClickListener(new View.OnClickListener() {
                                           public void onClick(View view) {selectImage();
                                           }
                                       }
        );

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uName = userName.getText().toString().trim();
                String uEmail = user_email.getText().toString().trim();
                String uPwd = change_password.getText().toString().trim();

                if (!uName.isEmpty() && !uEmail.isEmpty() && !uPwd.isEmpty()) {

                    updateInfo(uid, uName, uEmail, uPwd);
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Please fill all the fields.", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        gridView.addHeaderView(headerView);

    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {

                    cameraIntent();

                } else if (items[item].equals("Choose from Gallery")) {

                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE){

                onSelectFromGalleryResult(data);
                Glide.with(this).load(data.getData())
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(this))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(changeImage);
            }
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        thumbnail = null;
        if (data != null) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        Uri uri = getImageUri(this, thumbnail);
        Glide.with(this).load(uri)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(changeImage);

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void getUserPost(String uid){
        String tag_string_req = "req_test";
        String url = new AppConfig().UrlGetUserPost(uid);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "WritePost Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray posts = jObj.getJSONArray("result");

                    for(int i=0;i<posts.length();i++){
                        JSONObject jo = posts.getJSONObject(i);
                        PostData pd = new PostData();

                        pd.setName(jo.getString("user_name"));
                        pd.setUserImage(jo.getString("user_image"));
                        pd.setHeading(jo.getString("heading"));
                        pd.setDescription(jo.getString("description"));
                        pd.setLocation(jo.getString("location"));
                        pd.setCategory(jo.getString("category"));
                        pd.setImage_url(jo.getString("image_url"));
                        pd.setCreated_at(jo.getString("created_at"));

                        Log.d("show", "URL: "+pd.getImage_url());

                        data.add(pd);
                    }
                    adapter = new GridViewAdapter(Profile.this, R.layout.grid_item, data);
                    gridView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "WritePost Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Connection Error!", Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void updateInfo(final String uid, final String name, final String email,
                            final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_update";

        update.setVisibility(View.INVISIBLE);
        proBar.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully updated in MySQL
                        // Now update the user in sqlite

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String pwd = user.getString("password");
                        String imageUrl = user.getString("url");

                        // Updating row in users table
                        db.updateInfo(name, email, pwd, imageUrl);

                        Toast.makeText(getApplicationContext(), "Profile successfully updated!", Toast.LENGTH_LONG).show();

                        proBar.setVisibility(View.INVISIBLE);
                        update.setVisibility(View.VISIBLE);

                        Intent intent = new Intent(Profile.this, Newsfeed.class);
                        startActivity(intent);
                        finish();
                    }

                    else {

                        // Error occurred in updating data. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Log.e(TAG, "Update Error: "+errorMsg);
                        Toast.makeText(getApplicationContext(),
                                "Error occured during update.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Connection Error!", Toast.LENGTH_LONG).show();

                proBar.setVisibility(View.INVISIBLE);
                update.setVisibility(View.VISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                String image;
                if(thumbnail==null){
                    image = "";
                }
                else{
                    //Converting Bitmap to String
                    image = getStringImage(thumbnail);
                }

                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", uid);
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("image", image);



                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Profile.this, Newsfeed.class);
        startActivity(intent);
        finish();
    }
}