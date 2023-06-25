package com.bgmnt.rideshare;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bgmnt.rideshare.Adapters.Calc_Adapter;
import com.bgmnt.rideshare.Adapters.Calc_item;
import com.bgmnt.rideshare.Network.Connection_listener;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FarecalculationActivity extends AppCompatActivity {
    AutoCompleteTextView Noofpeople;

    Button CalculatePrice;
    private int noofpeople;

    TextView textView1;
    EditText etDestination;
    EditText etSource;
    String sType;
    TextView textView;
    double lat1 = 0.0d;
    double long1 = 0.0d;
    double lat2 = 0.0d;
    double long2 = 0.0d;
    int flag = 0;

    private Double Distance;

    private ArrayList<Calc_item> mCompaniesName;
    
    private Calc_Adapter mAdapter;

    private String clickedCompaniesName;
    String[] items ={"1","2","3","4","5","6","7","8","9","10","11","12"};

    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapter;

    Map<String,Map.Entry<Integer,Integer>> mapmini;

    Map<String,Map.Entry<Integer,Integer>> mapmvan;

    Connection_listener connection_listener = new Connection_listener();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farecalculation);


        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);




        initList();
        mapmini = new HashMap<>();
        mapmvan = new HashMap<>();

        mapmini.put("Ride",new AbstractMap.SimpleEntry<>(90,17));
        mapmini.put("Feres",new AbstractMap.SimpleEntry<>(70,20));
        mapmini.put("Taxiye",new AbstractMap.SimpleEntry<>(50,15));
        mapmini.put("Seregela",new AbstractMap.SimpleEntry<>(0,0));
        mapmini.put("Shuufare",new AbstractMap.SimpleEntry<>(0,0));
        mapmini.put("Lole", new AbstractMap.SimpleEntry<>(0,0));
        mapmini.put("Ilift", new AbstractMap.SimpleEntry<>(0,0));


        mapmvan.put("Ride",new AbstractMap.SimpleEntry<>(60,20));
        mapmvan.put("Feres",new AbstractMap.SimpleEntry<>(70,22));
        mapmvan.put("Taxiye",new AbstractMap.SimpleEntry<>(50,17));
        mapmvan.put("Seregela",new AbstractMap.SimpleEntry<>(0,0));
        mapmvan.put("Shuufare",new AbstractMap.SimpleEntry<>(0,0));
        mapmvan.put("Lole", new AbstractMap.SimpleEntry<>(0,0));
        mapmvan.put("Ilift", new AbstractMap.SimpleEntry<>(0,0));











        Noofpeople = findViewById(R.id.autoComplete_txt);
        Spinner spinnerCompanies = findViewById(R.id.spinner_countries);

        mAdapter = new Calc_Adapter(this, mCompaniesName);
        spinnerCompanies.setAdapter(mAdapter);


        spinnerCompanies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Calc_item clickedItem = (Calc_item) parent.getItemAtPosition(position);
                clickedCompaniesName = clickedItem.getCountryName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CalculatePrice = findViewById(R.id.cal);

        autoCompleteTextView = findViewById(R.id.autoComplete_txt);

        adapter = new ArrayAdapter<String>(this,R.layout.list_item,items);

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
           String item =  adapterView.getItemAtPosition(i).toString();
        });


        etSource = (EditText) findViewById(R.id.et_source);
        etDestination = (EditText) findViewById(R.id.et_destination);
        textView = findViewById(R.id.textView);
        textView1 = findViewById(R.id.textView2);


        Places.initialize(getApplicationContext(), "AIzaSyDaNbUhAGhLLJa1slPF5htBSX3z9iYUwG0");
        this.etSource.setFocusable(false);

        this.etSource.setOnClickListener(v -> {
            FarecalculationActivity.this.sType = "source";
            List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(FarecalculationActivity.this);
            FarecalculationActivity.this.startActivityForResult(intent, 100);
        });
        this.etDestination.setFocusable(false);

        this.etDestination.setOnClickListener(v -> {
            FarecalculationActivity.this.sType = "destination";
            List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(FarecalculationActivity.this);
            FarecalculationActivity.this.startActivityForResult(intent, 100);
        });
        this.textView.setText("It's around 20.0 Km");

    CalculatePrice.setOnClickListener(view -> calculate_price());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == -1) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            if (this.sType.equals("source")) {
                this.flag++;
                this.etSource.setText(place.getAddress());
                String sSource = String.valueOf(place.getLatLng());
                String[] split = sSource.replaceAll("lat/lng: ", "").replace("(", "").replace(")", "").split(",");
                this.lat1 = Double.parseDouble(split[0]);
                this.long1 = Double.parseDouble(split[1]);
            } else {
                this.flag++;
                this.etDestination.setText(place.getAddress());
                String sDestination = String.valueOf(place.getLatLng());
                String[] split2 = sDestination.replaceAll("lat/lng: ", "").replace("(", "").replace(")", "").split(",");
                this.lat2 = Double.parseDouble(split2[0]);
                this.long2 = Double.parseDouble(split2[1]);
            }
            if (this.flag >= 2) {
               String []distance =  distance(this.lat1, this.long1, this.lat2, this.long2).split(" ");
               Distance = Double.parseDouble(distance[0]);
            }
        } else if (requestCode == 2) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String distance(double lat1, double long1, double lat2, double long2) {

        double longDiff = long1 - long2;
        double distance = (Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))) + (Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(longDiff)));
        this.textView.setText(String.format("%02f Kilometers", Double.valueOf(60.0d * rad2deg(Math.acos(distance)) * 1.1515d * 1.609344d)));
        String a = String.format("%02f Kilometers", Double.valueOf(60.0d * rad2deg(Math.acos(distance)) * 1.1515d * 1.609344d));
        Toast.makeText(this,String.valueOf(distance) + " " + a,Toast.LENGTH_SHORT).show();


return  a;
    }

    private double rad2deg(double distance)
    {
        return (180.0d * distance) / 3.141592653589793d;
    }

    private double deg2rad(double lat1) {
        return (3.141592653589793d * lat1) / 180.0d;
    }

    private void initList() {
        mCompaniesName = new ArrayList<>();
        mCompaniesName.add(new Calc_item("Ride", R.drawable.ride));
        mCompaniesName.add(new Calc_item("Seregela", R.drawable.seregela));
        mCompaniesName.add(new Calc_item("Shuufare", R.drawable.shuufare));
        mCompaniesName.add(new Calc_item("Feres", R.drawable.feres));
        mCompaniesName.add(new Calc_item("Zayride", R.drawable.zayride));
        mCompaniesName.add(new Calc_item("Lole", R.drawable.lole));
        mCompaniesName.add(new Calc_item("Taxiye", R.drawable.taxiye));
        mCompaniesName.add(new Calc_item("Ilift", R.drawable.ilift));

    }

    private void calculate_price(){
        if(TextUtils.isEmpty(Noofpeople.getText())){
            Toast.makeText(this, "Please provide number of people", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(clickedCompaniesName)){
            Toast.makeText(this,"Please provide the ride companies",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(etSource.getText())){
            Toast.makeText(this,"Please provide the Source Location",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(etDestination.getText())){
            Toast.makeText(this,"Please provide the Destination Location",Toast.LENGTH_SHORT).show();
            return;
        }
        noofpeople = Integer.parseInt(Noofpeople.getText().toString());

            int price = calculate_price_by_companies(clickedCompaniesName);

            textView1.setText("Price => " + price + "Birr " + "each");





    }

    int calculate_price_by_companies(String RideCompanies){
        int price = 0;

        if (noofpeople <= 4){
            Map.Entry<Integer,Integer> pair= mapmini.get(RideCompanies);
            if(pair!=null){
                int intialfee = pair.getKey();
                int priceperkilo = pair.getValue();
                price = (int) ((intialfee + (priceperkilo*Distance))/noofpeople);
            }
        }

        else if (noofpeople > 4 && noofpeople <=7) {
            Map.Entry<Integer,Integer> pair= mapmvan.get(RideCompanies);

            if(pair!=null){
                int intialfee = pair.getKey();
                int priceperkilo = pair.getValue();
                price = (int) ((intialfee + (priceperkilo*Distance))/noofpeople);
            }
        }
        else    {
            Map.Entry<Integer,Integer> pair= mapmvan.get(RideCompanies);
            int intialfee = pair.getKey();
            int priceperkilo = pair.getValue();
            price = (int) ((intialfee + (priceperkilo*Distance))/noofpeople);

            }



        return price;



    }

    @Override
    protected void onStart() {
        registerReceiver(connection_listener, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        super.onStart();
    }

    @Override
    protected void onStop() {
        registerReceiver(connection_listener, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        super.onStop();
    }
}