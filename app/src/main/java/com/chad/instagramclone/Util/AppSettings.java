package com.chad.instagramclone.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.chad.instagramclone.Constants.Constants;

public class AppSettings {

    private SharedPreferences sharedPreferences;
    private int theme;

    public AppSettings(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt(Constants.THEME_KEY, 0);
    }


    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.THEME_KEY, theme);
        editor.apply();
    }
}
