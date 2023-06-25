package com.bgmnt.rideshare;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bgmnt.rideshare.Model.User;
import com.bgmnt.rideshare.Network.Connection_listener;
import com.bgmnt.rideshare.databinding.ActivityFi4stBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;



public class Fi4st extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityFi4stBinding binding;
    private Preference_Manager preference_manager;

    private Configuration configuration;

    private LanguageManager languageManager;
    private Intent intent;

    private String Imgurl;

    private ImageView imageView;

    DatabaseReference db;

    Connection_listener connection_listener = new Connection_listener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFi4stBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preference_manager = new Preference_Manager(getApplicationContext());



        setSupportActionBar(binding.appBarFi4st.toolbar);
        languageManager = new LanguageManager(this);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        imageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);

        Preference_Manager preferenceManager = new Preference_Manager(this);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_create, R.id.nav_join, R.id.nav_transportation, R.id.nav_chat, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_fi4st);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        String capitalization = capitalization(this.preference_manager.getString("username"));
        update_profile();


        imageView.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), UserprofileActivity.class)));

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.Hello)).setText(getString(R.string.hello) + " " + capitalization + "!");

        navigationView.setNavigationItemSelectedListener(item -> {


            if (item.getItemId() == R.id.nav_create) {
                intent = new Intent(getApplicationContext(), CreatorActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.nav_join) {
                intent = new Intent(getApplicationContext(), JoinerActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.nav_transportation) {
                intent = new Intent(getApplicationContext(), TransportationActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.nav_chat) {
                intent = new Intent(getApplicationContext(), GroupchatActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.nav_calculate) {
                intent = new Intent(getApplicationContext(), FarecalculationActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.nav_aboutus) {
                intent = new Intent(getApplicationContext(), AboutusActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.nav_share) {
                share();
                return true;
            } else {


                return true;

            }

        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        int i = item.getItemId();


        if (i == R.id.action_logout) {
            mAuth.signOut();
            Intent in = new Intent(getApplicationContext(), LoginActivity.class);
            preference_manager.clear();

            startActivity(in);
            finish();

        }
        if (i == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fi4st, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_fi4st);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private String capitalization(String str) {
        String upperCase = str.substring(0, 1).toUpperCase();
        String substring = str.substring(1);
        return upperCase + substring;
    }

    private void share() {
        // Create the text message with a string.
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Download RideShare and Make your life easier!!!");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.bgmnt.rideshare");
        sendIntent.setType("text/plain");

// Try to invoke the intent.
        try {
            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            // Define what your app should do if no activity can handle the intent.
        }

    }

    private void update_profile() {
        Imgurl = preference_manager.getString("profilePicture");

        if (Imgurl != null)
            Picasso.get().load(Imgurl).placeholder(R.drawable.baseline_account_circle_24).into(imageView);
        else {
            db.child("profilePicture").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Picasso.get().load(snapshot.getValue().toString()).placeholder(R.drawable.baseline_account_circle_24).into(imageView);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    @Override
    protected void onStart() {

        updateLanguageConfiguration();

        super.onStart();
    }

    @Override
    protected void onResume() {
        updateLanguageConfiguration();

        super.onResume();
    }

    @Override
    protected void onRestart() {
        updateLanguageConfiguration();
        update_profile();
        super.onRestart();
    }



    private String getLang() {
        Preference_Manager preference_manager = new Preference_Manager(this);
        String code = preference_manager.getString("LANG");
        if (code == null) {
            code = "en";
        }
        return code;


    }


    private void updateLanguageConfiguration() {
        String selectedOption = getLang();
        // Apply the language configuration based on the selected option
        if (selectedOption.equals("en")) {
            languageManager.setLocale("en");
        } else if (selectedOption.equals("am")) {
            languageManager.setLocale("am");
        } else if (selectedOption.equals("om")) {
            languageManager.setLocale("om");
        }

    }
}