package com.example.fazlulhoque.iiucbustracking.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Md Azad on 3/2/2018.
 */

public class IIUCBusTrackerPreference {
    Context context;
    SharedPreferences masterPreference;

    String userID;
    boolean loggedIn;


    public IIUCBusTrackerPreference(Context context) {
    this.context= context;

    masterPreference=context.getSharedPreferences("com.example.fazlulhoque.iiucbustracking",Context.MODE_PRIVATE);
    }


    public String getUserID() {
        return masterPreference.getString("userID","none");
    }

    public void setUserID(String userID) {
        SharedPreferences.Editor editor= masterPreference.edit();
        editor.putString("userID",userID);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return masterPreference.getBoolean("loggedIn",false);
    }

    public void setLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor editor= masterPreference.edit();
        editor.putBoolean("loggedIn",loggedIn);
        editor.apply();
    }
}
