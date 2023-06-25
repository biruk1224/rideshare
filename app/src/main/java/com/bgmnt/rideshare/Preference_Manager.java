package com.bgmnt.rideshare;


import android.content.Context;
import android.content.SharedPreferences;

public class Preference_Manager {
    private final SharedPreferences sharedPreferences;

    public Preference_Manager(Context context) {
        this.sharedPreferences = context.getSharedPreferences("App_Preference", 0);
    }

    public void putBoolean(String str, Boolean bool) {
        SharedPreferences.Editor edit = this.sharedPreferences.edit();
        edit.putBoolean(str, bool.booleanValue());
        edit.apply();
    }

    public Boolean getBoolean(String str) {
        return Boolean.valueOf(this.sharedPreferences.getBoolean(str, false));
    }

    public void putString(String str, String str2) {
        SharedPreferences.Editor edit = this.sharedPreferences.edit();
        edit.putString(str, str2);
        edit.apply();
    }

    public String getString(String str) {
        return this.sharedPreferences.getString(str, null);
    }

    public void clear() {
        SharedPreferences.Editor edit = this.sharedPreferences.edit();
        edit.clear();
        edit.apply();
    }
}