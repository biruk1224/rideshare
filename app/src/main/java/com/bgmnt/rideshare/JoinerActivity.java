package com.bgmnt.rideshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bgmnt.rideshare.Adapters.RecyclerAdapter;
import com.bgmnt.rideshare.Model.User;
import com.bgmnt.rideshare.Network.Connection_listener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.bgmnt.rideshare.databinding.ActivityJoinerBinding;
import com.google.android.gms.measurement.api.AppMeasurementSdk;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JoinerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityJoinerBinding binding;
    private DatabaseReference Createrref;
    private DatabaseReference Joinerref;
    private AlertDialog.Builder builder;
    private LatLng creator_location;
    private DatabaseReference db;
    private ArrayList<User> joinerArrayList;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    private Preference_Manager preference_manager;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private String username;
    Connection_listener connection_listener = new Connection_listener();
    boolean[] map = {false};
    LocationCallback locationCallback1 = new LocationCallback() {
        @Override // com.google.android.gms.location.LocationCallback
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                lastLocation = location;
                GeoFire geoFire = new GeoFire(Joinerref);
                if (preference_manager.getBoolean("Not_joined")) {
                    geoFire.setLocation(username, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    creator_location = new LatLng(location.getLatitude(), location.getLongitude());
                    if (!map[0]) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(creator_location, 10.0f));
                        map[0] = true;
                    }
                    getcreateraround();
                }
            }
        }
    };
    List<Marker> markers = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityJoinerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);

        supportActionBar.setDisplayHomeAsUpEnabled(true);
        Preference_Manager preference_manager = new Preference_Manager(getApplicationContext());
        this.preference_manager = preference_manager;
        preference_manager.putBoolean("Not_joined", true);
        this.swipeRefreshLayout = findViewById(R.id.swipe);
        this.builder = new AlertDialog.Builder(this);

        this.swipeRefreshLayout.setOnRefreshListener(() -> waitForit());

        binding.fab1.setOnClickListener(view ->gotoCreator(view));
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        this.recyclerView =  findViewById(R.id.recyclerview);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        this.joinerArrayList = new ArrayList<>();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        this.db = reference;
        this.Joinerref = reference.child("Joiner Available");
        this.Createrref = this.db.child("Creater Available").child("Locations");
        this.username = firebaseAuth.getCurrentUser().getDisplayName();
        checkLocationPermission();
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient((Activity) this);
        createLocationRequest();
        if (!checkGPSStatus(this) && !isLocationEnabled(this)) {
            Enforce_gps();
        }
        if (Isempty()) {
            Toast.makeText(this, "Swipe to refresh", Toast.LENGTH_SHORT).show();
        }




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void waitForit() {

        new Handler().postDelayed(() -> checkcreator(), 2000L);
    }

    public void checkcreator() {
        getandadd();
        if (Isempty()) {
            Snackbar.make(this.swipeRefreshLayout, "No one is here to join at the moment please try to create and wait.", BaseTransientBottomBar.LENGTH_LONG).setAction("Action", null).show();
        }
        this.swipeRefreshLayout.setRefreshing(false);
    }

    public void gotoCreator(View view) {
        startActivity(new Intent(getApplicationContext(), CreatorActivity.class));
    }






    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        createLocationRequest();
        if (Build.VERSION.SDK_INT > 21) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                this.mFusedLocationClient.requestLocationUpdates(this.locationRequest, this.locationCallback1, Looper.myLooper());
                this.mMap.setMyLocationEnabled(true);
                return;
            }
            checkLocationPermission();
        }
    }






    protected void createLocationRequest() {
        LocationRequest create = LocationRequest.create();
        this.locationRequest = create;
        create.setInterval(1000L);
        this.locationRequest.setFastestInterval(1000L);
        this.locationRequest.setPriority(100);
    }


    public void calcdistance(final String str, final String str2) {
        DatabaseReference child = this.Createrref.child(str2).child(str);
        Log.d(FirebaseAnalytics.Param.SUCCESS, "d" + this.Createrref.child(str2).child(str).getRef());
        child.addValueEventListener(new ValueEventListener() {

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(FirebaseAnalytics.Param.SUCCESS, "yes");
                    List list = (List) dataSnapshot.child("l").getValue();
                    double d = 0.0d;
                    assert list != null;
                    double parseDouble = list.get(0) != null ? Double.parseDouble(list.get(0).toString()) : 0.0d;
                    if (list.get(1) != null) {
                        d = Double.parseDouble(list.get(1).toString());
                    }
                    LatLng latLng = new LatLng(parseDouble, d);
                    Location location = new Location("");
                    location.setLatitude(lastLocation.getLatitude());
                    location.setLongitude(lastLocation.getLongitude());
                    Location location2 = new Location("");
                    location2.setLatitude(latLng.latitude);
                    location2.setLongitude(latLng.longitude);
                    String format = String.format("%.0f", location.distanceTo(location2));
                    DatabaseReference parent = dataSnapshot.getRef().getParent();
                    Objects.requireNonNull(parent);
                    DatabaseReference parent2 = parent.getParent();
                    Objects.requireNonNull(parent2);
                    DatabaseReference parent3 = parent2.getParent();
                    Objects.requireNonNull(parent3);
                    DatabaseReference child2 = parent3.child("Status").child(str2).child(str).child("distance");
                    DatabaseReference child3 = child2.child("Distance" + username);
                    child3.setValue(format + "m");
                }
            }
        });
    }


    public void getcreateraround() {
        this.Createrref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                    if (dataSnapshot2.exists()) {
                        DatabaseReference databaseReference = Createrref;
                        String key = dataSnapshot2.getKey();
                        Objects.requireNonNull(key);
                        GeoQuery queryAtLocation = new GeoFire(databaseReference.child(key)).queryAtLocation(new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), 3.0d);
                        queryAtLocation.removeAllListeners();
                        queryAtLocation.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {


                            @Override
                            public void onDataChanged(DataSnapshot dataSnapshot3, GeoLocation geoLocation) {
                            }

                            @Override
                            public void onGeoQueryError(DatabaseError databaseError) {
                            }

                            @Override
                            public void onDataEntered(DataSnapshot dataSnapshot3, GeoLocation geoLocation) {
                                calcdistance(dataSnapshot3.getKey(), dataSnapshot2.getKey());
                                for (Marker marker : markers) {
                                    if (marker.getTag() != null && dataSnapshot3.getKey() != null && marker.getTag().equals(dataSnapshot3.getKey())) {
                                        return;
                                    }
                                }
                                Marker addMarker = mMap.addMarker(new MarkerOptions().icon(bitmapDescriptortovector(getApplicationContext(), R.drawable.ic_baseline_person_pin_24)).position(new LatLng(geoLocation.latitude, geoLocation.longitude)).title(dataSnapshot3.getKey()));
                                addMarker.setTag(dataSnapshot3.getKey());
                                markers.add(addMarker);
                            }

                            @Override
                            public void onDataExited(DataSnapshot dataSnapshot3) {
                                for (Marker marker : markers) {
                                    if (marker.getTag() != null && dataSnapshot3.getKey() != null && marker.getTag().equals(dataSnapshot3.getKey())) {
                                        marker.remove();
                                        markers.remove(marker);
                                        return;
                                    }
                                }
                            }

                            @Override
                            public void onDataMoved(DataSnapshot dataSnapshot3, GeoLocation geoLocation) {
                                for (Marker marker : markers) {
                                    if (marker.getTag() != null && dataSnapshot3.getKey() != null && marker.getTag().equals(dataSnapshot3.getKey())) {
                                        marker.setPosition(new LatLng(geoLocation.latitude, geoLocation.longitude));
                                        return;
                                    }
                                }
                                Marker addMarker = mMap.addMarker(new MarkerOptions().icon(bitmapDescriptortovector(getApplicationContext(), R.drawable.ic_baseline_person_pin_24)).position(new LatLng(geoLocation.latitude, geoLocation.longitude)).title(dataSnapshot3.getKey()));
                                addMarker.setTag(dataSnapshot3.getKey());
                                markers.add(addMarker);
                            }

                            @Override
                            public void onGeoQueryReady() {
                                getandadd();
                            }
                        });
                    }
                }
            }
        });
    }


    public void getandadd() {
        db.child("Creater Available").child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClearALL();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                            User user = new User();
                            if (dataSnapshot2.exists()) {
                                DataSnapshot child = dataSnapshot3.child("distance");
                                if (child.child("Distance" + username).exists()) {
                                    StringBuilder sb = new StringBuilder();
                                    DataSnapshot child2 = dataSnapshot3.child("distance");
                                    Object value = child2.child("Distance" + username).getValue();
                                    Objects.requireNonNull(value);
                                    sb.append(value);
                                    sb.append(" from you");
                                    user.setDistance(sb.toString());
                                    if (dataSnapshot3.child(AppMeasurementSdk.ConditionalUserProperty.NAME).exists()) {
                                        Object value2 = dataSnapshot3.child(AppMeasurementSdk.ConditionalUserProperty.NAME).getValue();
                                        Objects.requireNonNull(value2);
                                        user.setName(value2.toString().toUpperCase());
                                    }
                                    if (dataSnapshot3.child(FirebaseAnalytics.Param.DESTINATION).exists()) {
                                        StringBuilder sb2 = new StringBuilder();
                                        sb2.append("To ");
                                        Object value3 = dataSnapshot3.child(FirebaseAnalytics.Param.DESTINATION).getValue();
                                        Objects.requireNonNull(value3);
                                        sb2.append(value3);
                                        user.setDestination(sb2.toString());
                                    }
                                    if (dataSnapshot3.child("uid").exists()) {
                                        Object value4 = dataSnapshot3.child("uid").getValue();
                                        Objects.requireNonNull(value4);
                                        user.setUid(value4.toString());
                                        if (dataSnapshot3.child("noofpeople").exists()) {
                                            StringBuilder sb3 = new StringBuilder();
                                            Object value5 = dataSnapshot3.child("noofpeople").getValue();
                                            Objects.requireNonNull(value5);
                                            sb3.append(value5);
                                            sb3.append(" People");
                                            user.setNoofpeople(sb3.toString());
                                        }
                                    }
                                    joinerArrayList.add(user);
                                }
                            }
                        }
                        recyclerAdapter = new RecyclerAdapter(getApplicationContext(), joinerArrayList);
                        recyclerView.setAdapter(recyclerAdapter);
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }


    public void ClearALL() {
        ArrayList<User> arrayList = this.joinerArrayList;
        if (arrayList != null) {
            arrayList.clear();
        }
        RecyclerAdapter recyclerAdapter = this.recyclerAdapter;
        if (recyclerAdapter != null) {
            recyclerAdapter.notifyDataSetChanged();
        }
        this.joinerArrayList = new ArrayList<>();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.ACCESS_FINE_LOCATION")) {
                new AlertDialog.Builder(this).setTitle("Give permission").setMessage("This feature don't work if you don't give a permission").setPositiveButton("OK", (dialogInterface, i) -> checkLocationPermission()).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);
            }
        }
    }




    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 1) {
            if (iArr.length > 0 && iArr[0] == 0) {
                if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
                    return;
                }
                mFusedLocationClient.requestLocationUpdates(this.locationRequest, this.locationCallback1, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
                return;
            }
            Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        this.Joinerref.child(this.username).setValue(null);
        FusedLocationProviderClient fusedLocationProviderClient = this.mFusedLocationClient;
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(this.locationCallback1);
        }
        this.preference_manager.putBoolean("Not_joined", false);
        super.onBackPressed();
    }


    @Override
    public void onStop() {
        this.preference_manager.putBoolean("Not_joined", false);
        unregisterReceiver(this.connection_listener);
        super.onStop();
    }


    @Override
    public void onStart() {
        registerReceiver(this.connection_listener, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        super.onStart();
    }


    @Override
    public void onDestroy() {
        DatabaseReference child = this.Joinerref.child(this.username);
        this.preference_manager.putBoolean("Not_joined", false);
        child.setValue(null);
        FusedLocationProviderClient fusedLocationProviderClient = this.mFusedLocationClient;
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(this.locationCallback1);
        }
        super.onDestroy();
    }

    private boolean Isempty() {
        getandadd();
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getApplicationContext(),joinerArrayList);
        this.recyclerAdapter = recyclerAdapter;
        return recyclerAdapter.getItemCount() == 0;
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    public BitmapDescriptor bitmapDescriptortovector(Context context, int i) {
        Drawable drawable = ContextCompat.getDrawable(context, i);
        assert drawable != null;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        drawable.draw(new Canvas(createBitmap));
        return BitmapDescriptorFactory.fromBitmap(createBitmap);
    }

    public static boolean checkGPSStatus(Context context) {
        return ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled("gps");
    }

    private boolean isLocationEnabled(Context context) {
        return LocationManagerCompat.isLocationEnabled((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
    }

    private void Enforce_gps() {
        if (!isFinishing()) {
            this.builder.setTitle("GPS Needed").setMessage("This Feature Won't work without GPS,Please enable your GPS or Location").
                    setPositiveButton("Okay",  (dialog, which) -> dialog.dismiss()).create().show();
        }
    }
}