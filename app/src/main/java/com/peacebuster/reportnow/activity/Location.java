package com.peacebuster.reportnow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;

import com.peacebuster.reportnow.R;
import com.peacebuster.reportnow.adapter.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Polin on 30-Nov-16.
 */

public class Location extends AppCompatActivity{

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_listview);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);


        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(Location.this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {


            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub


                String location_grp = (String)listAdapter.getGroup(groupPosition);
                String location_child = (String)listAdapter.getChild(groupPosition,childPosition);
                Intent intent=new Intent();
                intent.putExtra("LOCATION_GROUP",location_grp);
                intent.putExtra("LOCATION_CHILD",location_child);
                setResult(1,intent);

                finish();
                return false;
            }
        });
    }

    /*
    * Preparing the list data
    */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Dhaka");
        listDataHeader.add("Chittagong");
        listDataHeader.add("Sylhet");
        listDataHeader.add("Khulna");
        listDataHeader.add("Barisal");
        listDataHeader.add("Rajshahi");
        listDataHeader.add("Rangpur");


        // Adding child data
        List<String> dhaka = new ArrayList<String>();
        dhaka.add("Mirpur");
        dhaka.add("Uttara");
        dhaka.add("Dhanmondi");
        dhaka.add("Mohammadpur");
        dhaka.add("Gulshan");
        dhaka.add("Elephant Road");
        dhaka.add("Savar");
        dhaka.add("Basundhara");
        dhaka.add("Badda");
        dhaka.add("Motijheel");
        dhaka.add("Tejgaon");
        dhaka.add("Jatrabari");
        dhaka.add("Banani");
        dhaka.add("Khilgaon");
        dhaka.add("Paltan");
        dhaka.add("Rampura");
        dhaka.add("Baridhara");
        dhaka.add("Malibag");
        dhaka.add("Kawranbazar");
        dhaka.add("Wari");
        dhaka.add("Khilkhet");
        dhaka.add("Purbachal");
        dhaka.add("Mogbazar");
        dhaka.add("Lalbag");
        dhaka.add("Bangshal");
        dhaka.add("Mohakhali");
        dhaka.add("Tongi");
        dhaka.add("Cantonment");
        dhaka.add("Banglamotor");
        dhaka.add("Keraniganj");
        dhaka.add("Sutrapur");
        dhaka.add("Demra");
        dhaka.add("Ramna");
        dhaka.add("New Market");
        dhaka.add("Basabo");
        dhaka.add("Kamrangirchar");
        dhaka.add("Chaukbazar");
        dhaka.add("Kafrul");
        dhaka.add("Hazaribagh");
        dhaka.add("Kotowali");
        dhaka.add("Dhamrai");
        dhaka.add("Shajahanpur");
        dhaka.add("Mohakhali DOHS");
        dhaka.add("Mirpur DOHS");
        dhaka.add("Nawabganj");
        dhaka.add("Dohar");
        dhaka.add("Banani DOHS");

        List<String> ctg = new ArrayList<String>();
        ctg.add("Agrabad");
        ctg.add("Chawkbazar");
        ctg.add("Halishahar");
        ctg.add("Chandgaon");
        ctg.add("Kotwali");
        ctg.add("Khulshi");
        ctg.add("Baizid");
        ctg.add("Nasirabad");
        ctg.add("Muradpur");
        ctg.add("Panchlaish");
        ctg.add("Cornelhat");
        ctg.add("Pahartali");
        ctg.add("Hathazari");
        ctg.add("Anderkilla");
        ctg.add("Lalkhan Bazar");
        ctg.add("CDA Avenue");
        ctg.add("Bandar");
        ctg.add("Sholashahar");
        ctg.add("Patenga");
        ctg.add("Bakoliya");
        ctg.add("Double Morning");
        ctg.add("Jamalkhan");
        ctg.add("Sitakunda");
        ctg.add("Raozan");
        ctg.add("Patiya");
        ctg.add("Karnafuly");
        ctg.add("Mirsharai");
        ctg.add("Fatikchari");
        ctg.add("Boalkhali");
        ctg.add("Rangunia");
        ctg.add("Anwara");
        ctg.add("Satkania");
        ctg.add("Lohagara");
        ctg.add("Chandanaish");
        ctg.add("Banskhali");
        ctg.add("Sandwip");

        List<String> syl = new ArrayList<String>();
        syl.add("Zinda Bazar");
        syl.add("Amber Khana");
        syl.add("Bandar");
        syl.add("Uposhohor");
        syl.add("South Surma");
        syl.add("Shibgonj");
        syl.add("Akhalia");
        syl.add("Gopalganj");
        syl.add("Shahporan");
        syl.add("Subid Bazar");
        syl.add("Kumar para");
        syl.add("Baghbari");
        syl.add("Bishwanath");
        syl.add("Beanibazar");
        syl.add("Pathan Tula");
        syl.add("Lama Bazar");
        syl.add("Majortila");
        syl.add("Shahi Eidgah");
        syl.add("Subhani Ghat");
        syl.add("Osmani Nagar");
        syl.add("Nayasarak");
        syl.add("Chouhatta");
        syl.add("Fenchuganj");
        syl.add("Dargah Mahalla");
        syl.add("Balaganj");
        syl.add("Companiganj");
        syl.add("Bimanbondor");
        syl.add("Jaintapur");
        syl.add("Kanaighat");
        syl.add("Gowainghat");
        syl.add("Nehari Para");
        syl.add("Zakiganj");

        List<String> khulna = new ArrayList<String>();
        khulna.add("Sonadanga");
        khulna.add("Khalishpur");
        khulna.add("Khan Jahan Ali");
        khulna.add("Rupsa");
        khulna.add("Daulatpur");
        khulna.add("Kotwali");
        khulna.add("Gollamari");
        khulna.add("Boyra Bazar");
        khulna.add("Phultala");
        khulna.add("Dumuria");
        khulna.add("Lobon Chora");
        khulna.add("Batighata");
        khulna.add("Koyla Ghat");
        khulna.add("Rayermohol");
        khulna.add("Koyra");
        khulna.add("Dighalia");
        khulna.add("Decope");
        khulna.add("Paikgacha");
        khulna.add("Terokhada");
        khulna.add("Pabla");

        List<String> barisal = new ArrayList<String>();
        barisal.add("Sadar Road");
        barisal.add("Nattullabad");
        barisal.add("Rupatali");
        barisal.add("Notun Bazar");
        barisal.add("Nobogram Road");
        barisal.add("Amtala");
        barisal.add("Banglabazar");
        barisal.add("Chawk Bazar");
        barisal.add("City Gate Barisal(Gorierpar)");
        barisal.add("Kashipur Bazar");
        barisal.add("Puran Bazar");
        barisal.add("Chand Mari");
        barisal.add("Launch Ghat");
        barisal.add("Uttar Alekanda");
        barisal.add("Nazirmoholla");
        barisal.add("Beltola Feri Ghat");
        barisal.add("Kalizira");

        List<String> rajshahi = new ArrayList<String>();
        rajshahi.add("Shaheb Bazar");
        rajshahi.add("Uposahar");
        rajshahi.add("Kazla");
        rajshahi.add("Laksimipur");
        rajshahi.add("Nawdapara");
        rajshahi.add("Motihar");
        rajshahi.add("Rajpara");
        rajshahi.add("Kadirgani");
        rajshahi.add("Shiroil");
        rajshahi.add("Boalia");
        rajshahi.add("Baharampur");
        rajshahi.add("Padma Residential Area");
        rajshahi.add("Hatemkha");
        rajshahi.add("Rani Nagar");
        rajshahi.add("Kazihata");
        rajshahi.add("Keshabpur");
        rajshahi.add("Chhota Banagram");
        rajshahi.add("Ramchandrapur");
        rajshahi.add("Hossainiganj");
        rajshahi.add("Bosepara");

        List<String> rangpur = new ArrayList<String>();
        rangpur.add("Jahaj Company More");
        rangpur.add("Dhap");
        rangpur.add("Shapla Chottor");
        rangpur.add("Lalbag");
        rangpur.add("Pourobazar");
        rangpur.add("Shatmatha Chottor");
        rangpur.add("Kachari Bazaar");
        rangpur.add("Modern More");
        rangpur.add("Bodorganj");
        rangpur.add("Mahigonj");
        rangpur.add("Paglapir");
        rangpur.add("Shathibari");
        rangpur.add("Mithapukur");
        rangpur.add("Tajhat");
        rangpur.add("Vinno Jogot");
        rangpur.add("College Para");
        rangpur.add("Shatrasta Mor");
        rangpur.add("Alamdangha");


        listDataChild.put(listDataHeader.get(0), dhaka); // Header, Child data
        listDataChild.put(listDataHeader.get(1), ctg);
        listDataChild.put(listDataHeader.get(2), syl);
        listDataChild.put(listDataHeader.get(3), khulna);
        listDataChild.put(listDataHeader.get(4), barisal);
        listDataChild.put(listDataHeader.get(5), rajshahi);
        listDataChild.put(listDataHeader.get(6), rangpur);
    }

    @Override
    public void onBackPressed() {

        String s = "Select Location";
        Intent i = new Intent();
        i.putExtra("BACKPRESSED_1",s);
        setResult(3,i);
        finish();
    }
}


