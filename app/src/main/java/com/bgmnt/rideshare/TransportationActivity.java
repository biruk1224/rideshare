package com.bgmnt.rideshare;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.bgmnt.rideshare.Adapters.transportationadapter;

import java.util.Objects;

public class TransportationActivity extends AppCompatActivity {
    transportationadapter recyclerAdapter;
    RecyclerView recyclerView;
    String[] name = {"Zayride", "Feres", "Ride", "Taxiye", "Seregela", "Lole", "Shuufare", "Michu", "Ilift"};
    int[] logo = {R.drawable.zayride, R.drawable.feres, R.drawable.ride, R.drawable.taxiye, R.drawable.seregela, R.drawable.lole, R.drawable.shuufare, R.drawable.michu, R.drawable.ilift};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportation);

        recyclerView = findViewById(R.id.transport);
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        transportationadapter transportationadapterVar = new transportationadapter(this,logo,name);
        recyclerAdapter = transportationadapterVar;
        recyclerView.setAdapter(transportationadapterVar);



    }
}