package org.spacedown.activity;

import org.spacedown.R;
import org.spacedown.SpacedownApp;
import org.spacedown.engine.Constants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class PreferencesActivity extends PreferenceActivity implements OnPreferenceClickListener {

	/**
	 * Reference to Application object
	 */
	private SpacedownApp app;
	
	private Resources mResources;

	private Preference mBackupPreference;

	private Preference mRestorePreference;

	private Preference mAboutPreference;

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
		
		mAboutPreference = getPreferenceManager().findPreference(getString(R.string.prefs_about_key));
		mAboutPreference.setOnPreferenceClickListener(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {

		if(preference.getKey().equals(getString(R.string.prefs_backup))) {
			app.backupDatabase();
	        return true;
	    } else if(preference.getKey().equals(getString(R.string.prefs_restore))) {
			app.restoreDatabase(this);
	        return true;
	    } else if(preference.getKey().equals(getString(R.string.prefs_about_key))) {
			this.showAboutDialog();
	        return true;
	    }
	    return false;
	}


	/**
	 * About dialog
	 */
	private void showAboutDialog() {

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.about_dialog, null);

		TextView buildDate = (TextView) layout.findViewById(R.id.build_date);
		buildDate.setText(String.format(mResources.getString(R.string.build_date),
				mResources.getString(R.string.app_build_date)));

		TextView versionView = (TextView) layout.findViewById(R.id.version);
		versionView.setText(getString(R.string.main_app_title) + Constants.SPACE + getString(R.string.ver)
				+ SpacedownApp.getVersionName(this));

		TextView messageView = (TextView) layout.findViewById(R.id.message);

		String aboutStr = getString(R.string.about_dialog_message);
		// adding links to "about" text
		final SpannableString s = new SpannableString(aboutStr);
		Linkify.addLinks(s, Linkify.ALL);

		messageView.setText(s + getString(R.string.acknowledgements));

		messageView.setMovementMethod(LinkMovementMethod.getInstance());

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.about);
		// builder.setIcon(R.drawable.icon);
		builder.setView(layout);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});

		builder.setCancelable(true);

		AlertDialog alert = builder.create();
		alert.show();

	}
}
