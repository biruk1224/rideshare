package com.bgmnt.rideshare;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import com.bgmnt.rideshare.Model.User;
import com.bgmnt.rideshare.Network.Connection_listener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class CreatorActivity extends AppCompatActivity {
    private static final String BUTTONSTATE = "BUTTONSTATE";

 //   TextView ShowCreated;
    TextInputEditText Destination;
    private AlertDialog.Builder builder;
    Button chat;
    Connection_listener connection_listener = new Connection_listener();
    Button create;
    private DatabaseReference db;
    private FirebaseUser fa;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference groupcreatedtime;
    Intent intent;
    Location lastlocation;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    TextInputEditText noofpeople;
    private Preference_Manager preference_manager;
    private ProgressBar progressBar;
    private String uid;

    private String username;


    public interface Locationcall {
        void oncallback(boolean z);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator);
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        this.create = findViewById(R.id.create);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        this.Destination = findViewById(R.id.destination);
        this.noofpeople = findViewById(R.id.no_of_people);
       this.progressBar = findViewById(R.id.progressBar3);

        this.chat = findViewById(R.id.chat);
        Log.d("cycle", "ON CREATE");
        preference_manager = new Preference_Manager(getApplicationContext());
        locationRequest = LocationRequest.create().setInterval(1000L).setFastestInterval(1000L).setPriority(100);
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (getApplicationContext() == null) {
                        stop_connection();
                    } else {
                        lastlocation = location;
                        if (preference_manager.getBoolean("session")) {
                            Log.d("cycle", "ON LOCATION");
                            startLocationUpdates(location);
                        }
                    }
                }
            }
        };


        if (savedInstanceState != null && !savedInstanceState.getString(BUTTONSTATE).equals("Create")) {
            this.create.setBackgroundColor(Color.RED);
            this.create.setText("Delete");
            this.chat.setVisibility(View.VISIBLE);
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        this.fa = currentUser;
        if (currentUser != null) {
             username = currentUser.getDisplayName();
            this.uid = this.fa.getUid();
        }
        this.builder = new AlertDialog.Builder(this);
        this.db = FirebaseDatabase.getInstance().getReference("Creater Available");
        this.groupcreatedtime = FirebaseDatabase.getInstance().getReference("MESSAGES_DATABASE").child("Group Created").child(this.uid);
        this.create.setOnClickListener(view -> Create(view));

        this.chat.setOnClickListener(view -> groupActivity(view));
    }

    public void Create(final View view) {
        String charSequence = ((Button) view).getText().toString();
        connectcreater();
        update_location();
        if (charSequence.equals("Create")) {
            String trim = this.Destination.getText().toString().trim();
            String trim2 = this.noofpeople.getText().toString().trim();
            if (TextUtils.isEmpty(trim)) {
                Toast.makeText(this, "Please set your destination", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(trim2)) {
                Toast.makeText(this, "Please fill No of people", Toast.LENGTH_SHORT).show();
                return;
            } else if (Integer.valueOf(trim2).intValue() > 12) {
                Toast.makeText(this, "Number of people must be less than 12", Toast.LENGTH_SHORT).show();
                return;
            } else if (checkLocationPermission()) {
                return;
            } else {
                if (!checkGPSStatus(this) && !isLocationEnabled(this)) {
                    Enforce_gps();
                    return;
                } else if (TextUtils.isEmpty(trim) || TextUtils.isEmpty(trim2)) {
                    return;
                } else {
                    this.preference_manager.putBoolean("joiner", false);
                    this.preference_manager.putString("uid", this.uid);
                    this.preference_manager.putString("creator_name", this.fa.getDisplayName());
                    this.preference_manager.putBoolean("session", true);
                    sendinfo();
                    this.groupcreatedtime.child("Created_time").setValue(Long.valueOf(System.currentTimeMillis()));
                    DatabaseReference tokenref = FirebaseDatabase.getInstance().getReference("MESSAGES_DATABASE").child("Group Tokens").child(uid);
                    new Handler().postDelayed(() -> Created(view), 2000L);
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                private static final String TAG = "d";

                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                        return;
                                    }
                                    String token = task.getResult();
                                    tokenref.child(username).setValue(token);
                                }
                            });

                    return;
                }
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.are_you_sure_want_you_to_delete_this);
        builder.setMessage(R.string.this_will_remove_the_room_that_you_have_created_recently);
        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> Delete(dialogInterface, i));
        builder.setNegativeButton(R.string.no, (dialog, id) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    public void Created(View view) {
        Snackbar.make(view, "You are know visible for people who want to join", BaseTransientBottomBar.LENGTH_LONG).setAction("Action", (View.OnClickListener) null).show();
        savecreate();
        progressBar.setVisibility(View.INVISIBLE);

    }

    public void Delete(DialogInterface dialogInterface, int i) {
        stop_connection();
        remove_creation();
        this.create.setBackgroundColor(getResources().getColor(R.color.rideshare));
        this.create.setText("Create");
        this.chat.setVisibility(View.INVISIBLE);
        dialogInterface.dismiss();
        Toast.makeText(this, "You have successfully deleted the created room", Toast.LENGTH_SHORT).show();
    }

    public  void groupActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), GroupchatActivity.class);
        this.intent = intent;
        startActivity(intent);
    }


    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString(BUTTONSTATE, this.create.getText().toString());
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (this.create.getText().toString().equalsIgnoreCase("create")) {
            super.onBackPressed();
            return;
        }
        builder.setTitle(R.string.yes_or_no);
        builder.setMessage(R.string.do_you_want_to_run_this_on_background);

        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            Log.d("cycle", "ON BACKPRESS");
            if (create.getText().toString().equalsIgnoreCase("Delete")) {
                connectcreater();
                update_location();
            }
            dialogInterface.dismiss();
            CreatorActivity.super.onBackPressed();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> OnBackPressed(dialogInterface, i));
        builder.show();
    }


    public void OnBackPressed(DialogInterface dialogInterface, int i) {
        stop_connection();
        remove_creation();
        dialogInterface.dismiss();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }


    public void startLocationUpdates(Location location) {
        if (location != null) {
            DatabaseReference child = db.child("Locations").child(fa.getUid());
            DatabaseReference child2 = db.child("Status").child(fa.getUid()).child(Objects.requireNonNull(fa.getDisplayName()));
            DatabaseReference child3 = db.child("Locations").child(fa.getUid());
            //sendinfo();
            new GeoFire(child).setLocation(this.fa.getDisplayName(), new GeoLocation(location.getLatitude(), location.getLongitude()));
            child3.onDisconnect().setValue(null);
            child2.onDisconnect().setValue(null);
        }
    }


    public void update_location() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                startLocationUpdates(location);
            }
        });
    }

    private void remove_creation() {
        DatabaseReference child = this.db.child("Locations");
        DatabaseReference child2 = this.db.child("Status").child(this.fa.getUid());
        DatabaseReference child3 = this.db.child("Locations").child(this.fa.getUid());
        DatabaseReference child4 = FirebaseDatabase.getInstance().getReference("MESSAGES_DATABASE").child("Group Messages").child(this.uid);
        DatabaseReference child5 = FirebaseDatabase.getInstance().getReference("MESSAGES_DATABASE").child("Group Tokens").child(this.uid);
        this.preference_manager.putBoolean("session", false);
        new GeoFire(child).removeLocation(this.uid);
        if (child2 != null) {
            child2.setValue(null);
        }
        if (child3 != null) {
            child3.setValue(null);
        }
        DatabaseReference databaseReference = this.groupcreatedtime;
        if (databaseReference != null) {
            databaseReference.setValue(null);
        }
        if (child4 != null) {
            child4.setValue(null);
        }
        if (child5 != null) {
            child5.setValue(null);
        }
        Log.d("cycle", "Remove CALLED");
    }

    private void sendinfo() {
        User user = new User();
        Editable text = Destination.getText();
        Objects.requireNonNull(text);
        user.setDestination(text.toString());
        user.setNoofpeople(Objects.requireNonNull(noofpeople.getText()).toString());
        user.setName(fa.getDisplayName());
        user.setUid(fa.getUid());
        user.setCreated_time(System.currentTimeMillis());
        db.child("Status").child(fa.getUid()).child(Objects.requireNonNull(fa.getDisplayName())).setValue(user);
       // progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onRestart() {
        Log.d("Activity cycle re", "on called");
        super.onRestart();
    }



    public void connectcreater() {
        checkLocationPermission();
        Log.d("cycle", "Conncection Created");
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        this.fusedLocationClient = fusedLocationProviderClient;
        fusedLocationProviderClient.requestLocationUpdates(this.locationRequest, this.locationCallback, null);
    }


    public void stop_connection() {
        remove_creation();
        FusedLocationProviderClient fusedLocationProviderClient = this.fusedLocationClient;
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(this.locationCallback);
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.ACCESS_FINE_LOCATION")) {

                new AlertDialog.Builder(this).setTitle("Give Permission").setMessage("This feature won't work if you don't give the location permission").setPositiveButton("OK", (dialogInterface, i) -> locationPermission()).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);
            }
            return true;
        }
        return false;
    }

    public void locationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 1) {
            return;
        }
        if (iArr.length > 0 && iArr[0] == 0) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
                return;
            }
            Log.d("cycle", "PERMMISION");
            update_location();
            return;
        }
        Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
    }

    private void savecreate() {
        this.create.setBackgroundColor(Color.RED);
        this.create.setText("Delete");
        this.chat.setVisibility(View.VISIBLE);

    }


    @Override
    public void onPause() {
        Log.d("cycle", "ON PAUSE");
        super.onPause();
    }


    @Override
    public void onResume() {
      //  isCreated();
        Log.d("cycle", "ON RESUME");
        super.onResume();
    }


    @Override

    public void onStart() {
        Log.d("cycle", "ON START");

        existingstatus(this::checkCreate);
        //isCreated();
        registerReceiver(this.connection_listener, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        super.onStart();
    }

    public void checkCreate(boolean z) {
        if (z) {
            stop_connection();
            remove_creation();
            return;
        }
        savecreate();
        connectcreater();
    }


    @Override
    public void onStop() {
        Log.d("cycle", "ON STOP ");
        unregisterReceiver(this.connection_listener);
        super.onStop();
    }

    private void existingstatus(final Locationcall locationcall) {
        this.groupcreatedtime.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!dataSnapshot.child("Created_time").exists()) {
                        return;
                    }
                    if (Long.parseLong(Objects.requireNonNull(dataSnapshot.child("Created_time").getValue()).toString()) < System.currentTimeMillis() - 600000) {
                        locationcall.oncallback(true);
                        Log.d("new session", "created");
                        return;
                    }
                    locationcall.oncallback(false);
                    return;
                }
                locationcall.oncallback(true);
            }
        });
    }

    public static boolean checkGPSStatus(Context context) {
        return ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled("gps");
    }

    private void Enforce_gps() {
        if (!isFinishing()) {

            this.builder.setTitle("GPS Needed").setMessage("This Feature Won't work without GPS,Please enable your GPS").setPositiveButton("Okay", this::Enforce_gps).create().show();
        }
    }

    public void Enforce_gps(DialogInterface dialogInterface, int i) {
        finish();
        dialogInterface.dismiss();
    }


//    private void isCreated(){
//        DatabaseReference child2 = db.child("Status").child(fa.getUid()).child(Objects.requireNonNull(fa.getDisplayName()));
//        child2.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    Toast.makeText(CreatorActivity.this, "status exists "  + snapshot.getValue(), Toast.LENGTH_SHORT).show();
//                    for(DataSnapshot status: snapshot.getChildren()){
//                        if(status.child("noofpeople").exists()){
//
//                           String Noofpeople = status.child("noofpeople").getValue().toString();
//                            Toast.makeText(CreatorActivity.this, "no " + Noofpeople, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }



    private boolean isLocationEnabled(Context context) {
        return LocationManagerCompat.isLocationEnabled((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
    }


}