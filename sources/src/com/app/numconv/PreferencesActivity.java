package com.app.numconv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.LayoutInflater;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		ListPreference systemsAction = (ListPreference) findPreference("systems_action");
		systemsAction.setSummary(getResources().getStringArray(R.array.pref_systems_action_values)[
				Integer.valueOf(preferences.getString("systems_action", "1"))]);
		systemsAction.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary(getResources()
						.getStringArray(R.array.pref_systems_action_values)[Integer.valueOf(
								(String) newValue)]);
				Toast.makeText(PreferencesActivity.this, R.string.pref_alert, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		
		OnPreferenceChangeListener changeWithToast = new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Toast.makeText(PreferencesActivity.this, R.string.pref_alert, Toast.LENGTH_SHORT).show();
				return true;
			}
		};
		
		CheckBoxPreference useAppKeyboard = (CheckBoxPreference) findPreference("use_app_keyboard");
		CheckBoxPreference hotsFix = (CheckBoxPreference) findPreference("hots_fix");
		useAppKeyboard.setOnPreferenceChangeListener(changeWithToast);
		hotsFix.setOnPreferenceChangeListener(changeWithToast);
		
		Preference aboutPreference = findPreference("about");
		aboutPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);
				builder.setTitle(R.string.pref_about);
				builder.setView(LayoutInflater.from(PreferencesActivity.this)
						.inflate(R.layout.about_dialog, null));
				builder.show();
				return false;
			}
		});
		
	}
	
	public static void setDefaultValues(Context context) {
		PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
	}
}
