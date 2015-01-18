package com.app.numconv;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;


import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppActivity {
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new AppPreferenceFragment()).commit();
    }

    public static class AppPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Application.getContext());

            Preference colorTheme = findPreference(getResources().getString(R.string.pref_id_color_theme));
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colorTheme.setSummary(getColorThemeName(preferences));
                colorTheme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        preference.setSummary(getColorThemeName((String) newValue));
                        return true;
                    }
                });
            } else {
                colorTheme.setEnabled(false);
                colorTheme.setSummary(R.string.only_in_android5);
            }
        }

        private int getColorThemeName(SharedPreferences preferences) {
            String name = preferences.getString(getResources().getString(R.string.pref_id_color_theme), "Blue");
            return getColorThemeName(name);
        }

        private int getColorThemeName(String name) {
            switch (name) {
                case "Black":
                    return R.string.pref_color_black;
                case "Blue":
                    return R.string.pref_color_blue;
                case "Green":
                    return R.string.pref_color_green;
                case "Red":
                    return R.string.pref_color_red;
                case "White":
                    return R.string.pref_color_white;
            }

            return R.string.pref_color_blue;
        }
    }
}
