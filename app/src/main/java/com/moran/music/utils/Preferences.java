package com.moran.music.utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.CheckBox;

import com.moran.music.R;

/**
 * Created by Denis on 04/02/2016.
 */
public class Preferences extends PreferenceActivity {
   SharedPreferences prefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
   /* @Override
    public void onStart(){
        super.onStart();
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        final Preference feedback = (Preference)findPreference("feedback");
        final Preference terms = (Preference)findPreference("terms");
        final Preference privacy = (Preference)findPreference("privacy");
        final Preference credits = (Preference)findPreference("credits");
        final CheckBoxPreference nigtly = (CheckBoxPreference)findPreference("nigtly");
    }
*/
}
