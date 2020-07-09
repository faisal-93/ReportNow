package com.peacebuster.reportnow.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.peacebuster.reportnow.R;
import com.peacebuster.reportnow.app.AppConfig;
import com.peacebuster.reportnow.app.AppController;
import com.peacebuster.reportnow.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Polin on 22-Nov-16.
 */

public class WritePost extends AppCompatActivity {



    private static final String TAG = WritePost.class.getSimpleName();
    Toolbar postTool;
    //Button post;
    EditText et_description, et_heading;
    ImageView postImage, location,category,post;
    private SQLiteHandler db;
    //Image request code
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    //Bitmap to get image from gallery
    private Bitmap bitmap=null;
    TextView loc,cate;
    private String location_grp;
    private String location_child;
    private String cat;
    private ProgressBar proBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bar_post);
        postTool = (Toolbar) findViewById(R.id.toolbar_post);

        post = (ImageView) findViewById(R.id.post);
        et_description = (EditText) findViewById(R.id.et_description);
        postImage = (ImageView) findViewById(R.id.post_image);
        et_heading = (EditText) findViewById(R.id.et_heading);
        location = (ImageView) findViewById(R.id.location_add_button);
        category = (ImageView) findViewById(R.id.category_add_button);
        loc = (TextView) findViewById(R.id.tv_location);
        cate = (TextView) findViewById(R.id.tv_category);
        proBar = (ProgressBar)findViewById(R.id.post_progress_bar);

        postTool.setTitle("Write Post");

        setSupportActionBar(postTool);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        postTool.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        postTool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = new SQLiteHandler(getApplicationContext());
                HashMap<String, String> user = db.getUserDetails();
                String uid = user.get("uid");
                String username = user.get("name");
                String user_image = user.get("image_url");
                String heading = et_heading.getText().toString().trim();
                String description = et_description.getText().toString().trim();


                createPost(uid, username, user_image, heading, description, location_grp, location_child, cat);

            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent location = new Intent(WritePost.this,Location.class);
                startActivityForResult(location,1);
               // finish();
            }
        });

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent category = new Intent(WritePost.this,Category.class);
                startActivityForResult(category,2);
                // finish();
            }
        });

    }




    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(WritePost.this);
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
            }
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }

        if(requestCode==1){
            location_grp=data.getStringExtra("LOCATION_GROUP");
            location_child=data.getStringExtra("LOCATION_CHILD");
            loc.setText("at "+location_child +", "+location_grp);

        }
        if(requestCode==2){

            cat = data.getStringExtra("product");
            cate.setText(cat);
        }
        if(requestCode==3){
            String location=data.getStringExtra("BACKPRESSED_1");
            loc.setText(""+location);
            //finish();
        }
        if(requestCode==4){
            String category=data.getStringExtra("BACKPRESSED_2");
            cate.setText(""+category);
           // finish();
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

            } catch (IOException e) {
                e.printStackTrace();
            }
            postImage.setImageBitmap(bitmap);
        }
    }

    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        postImage.setImageBitmap(bitmap);

    }


    private void createPost(final String pUid, final String pName, final String pImage, final String pHeading, final String pDesc,
                                final String plocGrp, final String plocChild, final String pCategory) {
        // Tag used to cancel the request
        String tag_string_req = "req_post";
        proBar.setVisibility(View.VISIBLE);
        post.setVisibility(View.INVISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_POST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "WritePost Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully updated in MySQL
                        // Now update the user in sqlite

                        JSONObject postObj = jObj.getJSONObject("post");
                        String uid = postObj.getString("uid");
                        String name = postObj.getString("name");
                        String heading = postObj.getString("heading");
                        String imageUrl = postObj.getString("image_url");

                        proBar.setVisibility(View.INVISIBLE);
                        post.setVisibility(View.VISIBLE);

                        Intent intent = new Intent(WritePost.this, Newsfeed.class);
                        startActivity(intent);
                        finish();

                        Toast.makeText(getApplicationContext(), "You post has been successfully posted!", Toast.LENGTH_LONG).show();

                    }

                    else {

                        // Error occurred in updating data. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Log.e(TAG, "WritePost Error: "+errorMsg);
                        Toast.makeText(getApplicationContext(),
                                "Error occured during post updating.", Toast.LENGTH_LONG).show();
                    }
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

                proBar.setVisibility(View.INVISIBLE);
                post.setVisibility(View.VISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                String image;
                if(bitmap==null){
                    image = "";
                }
                else{
                    //Converting Bitmap to String
                    image = getStringImage(bitmap);
                }

                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", pUid);
                params.put("user_name", pName);
                params.put("user_image", pImage);
                params.put("heading", pHeading);
                params.put("description", pDesc);
                params.put("image_url", image);
                params.put("location", plocChild+","+plocGrp);
                params.put("category", pCategory);



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
        finish();
    }
}
