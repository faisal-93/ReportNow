package com.peacebuster.reportnow.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.peacebuster.reportnow.R;
import com.peacebuster.reportnow.app.AppConfig;
import com.peacebuster.reportnow.app.AppController;
import com.peacebuster.reportnow.adapter.Recycler_DataAdapter;
import com.peacebuster.reportnow.helper.CircleTransform;
import com.peacebuster.reportnow.helper.SQLiteHandler;
import com.peacebuster.reportnow.helper.SessionManager;
import com.peacebuster.reportnow.model.PostData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Newsfeed extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1234;
    private  static final String TAG = Newsfeed.class.getSimpleName();
    private SQLiteHandler db;
    private SessionManager session;
    private TextView userName, txt;
    private Button location;
    private Button category;
    private ImageView proImage, changeImage;
    private View navHeader;
    private String loc="";
    private String cat="";
    private String yearFrom="";
    private String yearTo="";
    private int sp_from_position;

    DrawerLayout drawer;
    Recycler_DataAdapter adapter;
    ArrayList<PostData> data = new ArrayList<PostData>();
    RecyclerView recyclerView;
    Spinner sp_from, sp_to;
    ArrayAdapter<String> sp_adapter_for_list;
    ArrayAdapter sp_adapter_for_string_array;
    String item[];
    List<String> list;
    Context ctx;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ctx = this;
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FloatingActionButton newPost = (FloatingActionButton) findViewById(R.id.fab);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        location = (Button)findViewById(R.id.nf_location);
        category = (Button)findViewById(R.id.nf_catagory);
        navHeader = navigationView.getHeaderView(0);
        userName = (TextView) navHeader.findViewById(R.id.userName);
        proImage = (ImageView) navHeader.findViewById(R.id.profileImage);
        sp_from = (Spinner) findViewById(R.id.sp_from);
        sp_to = (Spinner) findViewById(R.id.sp_to);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        loadNavHeader();

        initViews();
        getAllPost(loc, cat, yearFrom, yearTo);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Newsfeed.this, WritePost.class);
                startActivity(intent);
            }
        });
        proImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Newsfeed.this, Profile.class);
                startActivity(intent);
                finish();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent location = new Intent(Newsfeed.this,Location.class);
                startActivityForResult(location,1);
                // finish();
            }
        });

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent category = new Intent(Newsfeed.this,Category.class);
                startActivityForResult(category,2);
                // finish();
            }
        });
        searchByYear();

    }
    private void initViews() {

        // Create a grid layout with two columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        // Create a custom SpanSizeLookup where the first item spans both columns
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 1;
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){
            String location_grp=data.getStringExtra("LOCATION_GROUP");
            String location_child=data.getStringExtra("LOCATION_CHILD");
            loc = location_child +","+location_grp;
            location.setText(loc);
            getAllPost(loc, cat, yearFrom, yearTo);

        }
        if(requestCode==2){

            cat = data.getStringExtra("product");
            category.setText(cat);
            getAllPost(loc, cat, yearFrom, yearTo);
        }
        if(resultCode==3){
            String value=data.getStringExtra("BACKPRESSED_1");
            location.setText(value);
            loc = "";
            getAllPost(loc, "", yearFrom, yearTo);

            //finish();
        }
        if(resultCode==4){
            String value=data.getStringExtra("BACKPRESSED_2");
            category.setText(value);
            cat = "";
            getAllPost("", cat, yearFrom, yearTo);
            // finish();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.newsfeed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.notification) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_call) {
            makeCall();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(ctx, Summery.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            logoutUser();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadNavHeader() {

        HashMap<String, String> user = db.getUserDetails();
        userName.setText(user.get("name"));
        String url = user.get("image_url");

        // Loading profile image
        Glide.with(this).load(url)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(proImage);

    }

    private void getAllPost(String location, String category, String from_year, String to_year){
        String tag_string_req = "req_all_post";
        String from_date = from_year+"-01-01";
        String to_date = to_year+"-12-31";
        String url = new AppConfig().UrlGetAllPost(location, category, from_date, to_date);
        data.clear();


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

                        Log.d("show", pd.getLocation());

                        data.add(pd);
                    }
                    adapter = new Recycler_DataAdapter(getApplicationContext(), data);
                    recyclerView.setAdapter(adapter);

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

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(Newsfeed.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void makeCall() {
        final CharSequence[] items = {"01966-939223", "01925-187123", "01515-601295",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Newsfeed.this);
        builder.setTitle("Emergency Call");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("01966-939223")) {
                    makeCallIntent("01966939223");

                } else if (items[item].equals("01925-187123")) {

                    makeCallIntent("01925187123");

                } else if (items[item].equals("01515-601295")) {

                    makeCallIntent("01515601295");

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private String makeCallIntent(String number) {
        if (ContextCompat.checkSelfPermission(Newsfeed.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Newsfeed.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:+88" + number));
            ctx.startActivity(callIntent);
        }

        return number;
    }

    //for spinner
    private void searchByYear() {
        sp_adapter_for_string_array = ArrayAdapter.createFromResource(this, R.array.year,
                android.R.layout.simple_spinner_item);
        sp_adapter_for_string_array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_from.setAdapter(sp_adapter_for_string_array);
        sp_to.setAdapter(sp_adapter_for_string_array);
        change_spItemByItemclick();
    }

    private void change_spItemByItemclick() {
        item = getResources().getStringArray(R.array.year);
        list = new ArrayList<String>();
        sp_adapter_for_list = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        sp_adapter_for_list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                list.clear();
                sp_from_position = position;

                for (int i = sp_from_position; i < sp_adapter_for_string_array.getCount(); i++) {
                    list.add(item[i]);
                }
                sp_to.setAdapter(sp_adapter_for_list);
                yearFrom = sp_from.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearTo = sp_to.getSelectedItem().toString();
                getAllPost(loc, cat, yearFrom, yearTo);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
