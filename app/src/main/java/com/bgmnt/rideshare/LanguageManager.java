package com.bgmnt.rideshare;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.Locale;

public class LanguageManager {


    private Context context;
    private SharedPreferences sharedPreferences;

    String SelectedItem = "English";
    public LanguageManager(Context mcontext){

        context=mcontext;

    }

    public Configuration setLocale(String code){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(code);
        resources.updateConfiguration(configuration, metrics);
        return configuration;

    }



    private String getLang() {
        Preference_Manager preference_manager = new Preference_Manager(context);
        String code = preference_manager.getString("LANG");
        if (code == null) {
            code = "en";
        }
        return code;


    }
    public void updateLanguageConfiguration() {
        String selectedOption = getLang();
        // Apply the language configuration based on the selected option
        if (selectedOption.equals("en")) {
           setLocale("en");
        } else if (selectedOption.equals("am")) {
            setLocale("am");
        } else if (selectedOption.equals("om")) {
            setLocale("om");
        }

    }









}
