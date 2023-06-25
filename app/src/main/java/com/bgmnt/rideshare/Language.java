package com.bgmnt.rideshare;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Objects;


public class Language extends AppCompatActivity {
    RadioButton rbEnglish;
    RadioButton rbAmharic;
    RadioGroup rgLanguage;




    RadioButton rbOromia;
    TextView tvOutput;
    TextView tvSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        this.tvSelect = findViewById(R.id.tv_select);
        this.rgLanguage = findViewById(R.id.rg_language);
        this.rbEnglish = findViewById(R.id.rb_english);
        this.rbAmharic = findViewById(R.id.rb_amharic);
        this.rbOromia = findViewById(R.id.rb_oromo);
        this.tvOutput = findViewById(R.id.tv_output);

        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);



      String selectedOption = getLang();

        if (selectedOption.equals("en")) {
            rbEnglish.setChecked(true);
        } else if (selectedOption.equals("am")) {
            rbAmharic.setChecked(true);
        } else if (selectedOption.equals("om")) {
            rbOromia.setChecked(true);

        }


        this.rgLanguage.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rb_english) {

                setLocale("en");
            } else if (i == R.id.rb_amharic) {

                setLocale("am");
            }
            else if(i==R.id.rb_oromo){
                setLocale("om");
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void setLocale(String language) {

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        // Save the language preference
        setLang(language);

        // Restart the activity
        restartActivities();

    }


    public void setLang(String code) {
        Preference_Manager preference_manager = new Preference_Manager(this);
        preference_manager.putString("LANG", code);
    }


    private String getLang(){
        Preference_Manager preference_manager = new Preference_Manager(this);
        String code = preference_manager.getString("LANG");
        if(code==null){
            code="en";
        }

        return code;


    }
    private void restartActivities() {
        // Restart MainActivity
        Intent intent = new Intent(this, Language.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Restart other activities as needed
        Intent intent3 = new Intent(this,CreatorActivity.class);
        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

         Intent intent2 = new Intent(this, Fi4st.class);
         intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

    }
}
