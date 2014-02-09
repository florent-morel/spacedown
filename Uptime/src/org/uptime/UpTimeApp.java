package org.uptime;

import java.lang.Thread.UncaughtExceptionHandler;

import org.uptime.database.DBHelper;
import org.uptime.engine.Constants;

import utils.AppLog;
import utils.Utils;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
//import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * 
 * This file is part of Uptime.
 * 
 * Uptime is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Uptime is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Uptime.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author florent
 *
 */
public class UpTimeApp extends Application {

	/**
	 * Android shared preferences
	 */
	private SharedPreferences preferences;

	/**
	 * application directory
	 */
	private String appDir;

	private String dataDir;

	/**
	 * is external storage writable
	 */
	private boolean externalStorageWriteable = false;

	/**
	 * is external storage available, ex: SD card
	 */
	private boolean externalStorageAvailable = false;

	/**
	 * database object
	 */
	private SQLiteDatabase db;
	
	private DBHelper dbHelper;

	// uncaught exception handler variable
	private UncaughtExceptionHandler defaultUncaughtExceptionHandler;

	// handler listener
	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {

			AppLog.e(getApplicationContext(), "!!! Uncaught exception !!!");

			// remove all notifications
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancelAll();

			// re-throw critical exception further to the os
			defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
		}
	};

	@Override
	public void onCreate() {

		super.onCreate();

		// setup handler for uncaught exception
		this.defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

		// accessing preferences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		// set application external storage folder
		appDir = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SLASH + Constants.PATH_APP_NAME;

		// database helper
		dbHelper = DBHelper.getInstance(this);

		// SQLiteDatabase
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException e) {
			Toast.makeText(this, R.string.memory_card_not_available, Toast.LENGTH_SHORT).show();
			android.os.Process.killProcess(android.os.Process.myPid());
		}

		setExternalStorageState();

		dataDir = Environment.getDataDirectory().getAbsolutePath() + "/org.uptime/databases";
		// TODO: NEW FEATURE: app database file in external memory
//		 dataDir = appDir + Constants.SLASH + Constants.PATH_DATABASE;

		// create all folders required by the application on external storage
		if (getExternalStorageAvailable() && getExternalStorageWriteable()) {
			createFolderStructure();
		} else {
			Toast.makeText(this, R.string.memory_card_not_available, Toast.LENGTH_SHORT).show();
		}

		// display density
		// density = getContext().getResources().getDisplayMetrics().density;

		// adding famous waypoints only once
		// if (!getPreferences().contains("famous_waypoints")) {
		//
		// Waypoints.insertFamousWaypoints(db);
		//
		// // switch flag of famous locations added to true
		// SharedPreferences.Editor editor = getPreferences().edit();
		// editor.putInt("famous_waypoints", 1);
		// editor.commit();
		//
		// }

		// reference to vibrator service
		// this.vibrator = (Vibrator)
		// getSystemService(Context.VIBRATOR_SERVICE);

		// AppLog.d(this,
		// "=================== app: onCreate ===================");

	}

	/**
	 * Checking if external storage is available and writable
	 */
	private void setExternalStorageState() {

		// checking access to SD card
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			externalStorageAvailable = externalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			externalStorageAvailable = true;
			externalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need to know is we can neither read nor write
			externalStorageAvailable = externalStorageWriteable = false;
		}

	}

	/**
	 * Create application folders
	 */
	private void createFolderStructure() {
		Utils.createFolder(getAppDir());
		Utils.createFolder(getAppDir() + Constants.SLASH + Constants.PATH_DATABASE);
		Utils.createFolder(getAppDir() + Constants.SLASH + Constants.PATH_BACKUP);
		Utils.createFolder(getAppDir() + Constants.SLASH + Constants.PATH_DEBUG);
		Utils.createFolder(getAppDir() + Constants.SLASH + Constants.PATH_LOGS);
	}

	public SQLiteDatabase getDatabase() {
		return db;
	}

	/**
	 * Get application version name
	 * 
	 * @param context
	 */
	public static String getVersionName(Context context) {

		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 
	 */
	public void setDatabase() {
		dbHelper = DBHelper.getInstance(this);
		db = dbHelper.getWritableDatabase();
	}

	public boolean getExternalStorageAvailable() {
		return externalStorageAvailable;
	}

	public boolean getExternalStorageWriteable() {
		return externalStorageWriteable;
	}

	public SharedPreferences getPreferences() {
		return preferences;
	}

	public String getAppDir() {
		return appDir;
	}

	public String getDataDir() {
		return dataDir;
	}

	public DBHelper getDbHelper() {
		return dbHelper;
	}

}
