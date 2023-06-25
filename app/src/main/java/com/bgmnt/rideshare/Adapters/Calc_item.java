package com.bgmnt.rideshare.Adapters;


import android.widget.ArrayAdapter;



public class Calc_item {
    private String mCompaniesName;
    private int mFlagImage;


    public Calc_item(String companiesName, int flagImage) {



        mCompaniesName = companiesName;
        mFlagImage = flagImage;
    }

    public String getCountryName() {
        return mCompaniesName;
    }

    public int getFlagImage() {
        return mFlagImage;
    }
}