package com.peacebuster.reportnow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.peacebuster.reportnow.R;
import com.peacebuster.reportnow.app.AppConfig;
import com.peacebuster.reportnow.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Summery extends AppCompatActivity {
    Button sm_location;
    Spinner sp_from, sp_to;
    ArrayAdapter<String> sp_adapter_for_list;
    ArrayAdapter sp_adapter_for_string_array;
    String item[];
    List<String> list;
    private String yearFrom="";
    private String yearTo="";
    private int sp_from_position;
    String loc="";
    BarData data;
    ArrayList<String> xAxis = null;
    ArrayList<BarDataSet> dataSets = null;
    BarChart chart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summery);

        sm_location = (Button) findViewById(R.id.sm_location);
        sp_from = (Spinner) findViewById(R.id.sm_sp_from);
        sp_to = (Spinner) findViewById(R.id.sm_sp_to);

        chart = (BarChart) findViewById(R.id.chart);

        sm_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent location = new Intent(Summery.this,Location.class);
                startActivityForResult(location,1);
            }
        });

        searchByYear();
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
                filterPost(loc, yearFrom, yearTo);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){
            String location_grp=data.getStringExtra("LOCATION_GROUP");
            String location_child=data.getStringExtra("LOCATION_CHILD");
            loc = location_child +","+location_grp;
            sm_location.setText(loc);
            filterPost(loc, yearFrom, yearTo);

        }

        if(resultCode==3){
            String value=data.getStringExtra("BACKPRESSED_1");
            sm_location.setText(value);
            loc = "";
            filterPost(loc, yearFrom, yearTo);
        }
//        if(resultCode==4){
//            String value=data.getStringExtra("BACKPRESSED_2");
//            category.setText(value);
//            getAllPost("", "", yearFrom, yearTo);
//            // finish();
//        }

    }


    private void filterPost(String location, String from_year, String to_year){
        String from_date = from_year+"-01-01";
        String to_date = to_year+"-12-31";
        String tag_string_req = "req_test";
        String url = new AppConfig().FilterPost(location, from_date, to_date);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "WritePost Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray posts = jObj.getJSONArray("result");
                    String category, count;
                    ArrayList<BarEntry> valueSet = new ArrayList<>();
                    xAxis = new ArrayList<>();

                    for(int i=0;i<posts.length();i++){
                        JSONObject jo = posts.getJSONObject(i);
                        category = jo.getString("category");
                        count = jo.getString(category);

                        BarEntry entry = new BarEntry(Float.parseFloat(count), i);
                        valueSet.add(entry);

                        xAxis.add(category);

                        //checkCategory(category, count);

                        Log.d("show", category+": "+count);
                    }

                    BarDataSet barDataSet1 = new BarDataSet(valueSet, "Category");
                    barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
                    dataSets = new ArrayList<>();
                    dataSets.add(barDataSet1);

                    data = new BarData(xAxis, dataSets);
                    chart.setData(data);
                    chart.setDescription("My Chart");
                    chart.animateXY(2000, 2000);
                    chart.invalidate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "WritePost Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Connection Error!", Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

}
