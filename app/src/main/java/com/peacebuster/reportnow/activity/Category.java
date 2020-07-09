package com.peacebuster.reportnow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.peacebuster.reportnow.R;

/**
 * Created by Polin on 02-Dec-16.
 */

public class Category extends AppCompatActivity {

    ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_listview);

        list = (ListView) findViewById(R.id.listview);



        // storing string resources into Array
        String[] category_names = getResources().getStringArray(R.array.category_names);

        // Binding resources Array to ListAdapter
       list.setAdapter(new ArrayAdapter<String>(Category.this, R.layout.category_listitem, R.id.category_name, category_names));

     // ListView lv = getListView();




        // listening to single list item on click
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TextView tv = (TextView) view.findViewById(R.id.category_name);

                // selected item
                String product = tv.getText().toString();

                // Launching new Activity on selecting single List Item
                Intent i = new Intent();
                // sending data to new activity
                i.putExtra("product", product);
                setResult(2,i);

                finish();

            }
        });
    }


    @Override
    public void onBackPressed() {

        String s = "Select Category";
        Intent i = new Intent();
        i.putExtra("BACKPRESSED_2",s);
        setResult(4,i);
        finish();
    }
}
