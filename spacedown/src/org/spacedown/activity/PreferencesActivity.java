package org.spacedown.activity;

import org.spacedown.R;
import org.spacedown.SpacedownApp;
import org.spacedown.engine.Constants;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreferencesActivity extends PreferenceActivity implements OnPreferenceClickListener {

	/**
	 * Reference to Application object
	 */
	private SpacedownApp app;
	
	private Resources mResources;

	private Preference mBackupPreference;

	private Preference mRestorePreference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = ((SpacedownApp) getApplication());
		mResources = getResources();
		addPreferencesFromResource(R.xml.preferences);

		// Set summary of some preferences to their actual values
		// and register a change listener to set again the summary in case of
		// change
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// External storage directory
		ListPreference timerPref = (ListPreference) findPreference(mResources.getString(R.string.prefs_timer_val_key));
		timerPref.setSummary(String.format(mResources.getString(R.string.prefs_timer_val_summary),
				prefs.getString(mResources.getString(R.string.prefs_timer_val_key), Constants.TIMER_DEFAULT)));
		timerPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// Set summary with the directory value
				preference.setSummary(String.format(mResources.getString(R.string.prefs_timer_val_summary), newValue));
				return true;
			}
		});

		mBackupPreference = getPreferenceManager().findPreference(getString(R.string.prefs_backup));
		mBackupPreference.setOnPreferenceClickListener(this);
		
		mRestorePreference = getPreferenceManager().findPreference(getString(R.string.prefs_restore));
		mRestorePreference.setOnPreferenceClickListener(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {

		if(preference.getKey().equals(getString(R.string.prefs_backup))) {
			app.backupDatabase();
	        return true;
	    } else if(preference.getKey().equals(getString(R.string.prefs_restore))) {
			app.restoreDatabase(this);
	        return true;
	    }
	    return false;
	}

}
