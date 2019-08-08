package com.packag.eventmonitor.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {
    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setData(String name,String usename) {
        prefs.edit().putString(name, usename).commit();
    }

    public String getData(String name) {
        String data = prefs.getString(name,"");
        return data;
    }
    public void removeData(String name){
        prefs.edit().remove(name).commit();
    }
}
