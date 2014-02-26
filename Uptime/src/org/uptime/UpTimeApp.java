package org.uptime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.List;

import org.uptime.database.CardsDataSource;
import org.uptime.database.DBHelper;
import org.uptime.database.UpTimeContentProvider.Schema;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Card;

import utils.AppLog;
import utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;
//import android.os.Vibrator;

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

	private Resources mResources;

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

	private String importDatabaseFileName;

	private Handler mainHandler = new Handler();

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

		mResources = getResources();

		// set application external storage folder
		appDir = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SLASH + Constants.PATH_APP_NAME;

		// database helper
		dbHelper = DBHelper.getInstance(this);

		// SQLiteDatabase
		try {
			db = dbHelper.getWritableDatabase();
//			dropColumn();
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

	}
	
//	private void dropColumn() {
//		CardsDataSource ds = new CardsDataSource(getApplicationContext());
//	    db.execSQL(DBHelper.SQL_CREATE_TABLE_PLAYERS);
//		
//		List<Card> inactiveCards = ds.getAllCardsOld(db, false);
//	    String tableName = Schema.TABLE_CARDS;
//	    
//	    db.execSQL("ALTER TABLE " + tableName + " RENAME TO " + tableName + "_old;");
//
//	    // Creating the table on its new format (no redundant columns)
//	    db.execSQL(DBHelper.SQL_CREATE_TABLE_CARDS);
//
//	    String columns = Schema.COL_ID + ","
//	    		+ Schema.COL_NAME;
//	    
//	    // Populating the table with the data
//	    db.execSQL("INSERT INTO " + tableName + "(" + columns + ") SELECT "
//	            + columns + " FROM " + tableName + "_old;");
	    // Update inactive cards
//	    for (Card card : inactiveCards) {
//			ds.updateCard(card);
//		}
//	    db.execSQL("DROP TABLE " + tableName + "_old;");
//	}

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

	/**
	 * Copy application database to sd card
	 */
	public void backupDatabase() {

		try {

			File data = Environment.getDataDirectory();

			if (this.getExternalStorageWriteable()) {

				String currentDBPath = this.getDataDir() + Constants.SLASH + Constants.DATABASE_FILE;

				String dateStr = (String) DateFormat.format("yyyyMMdd_kkmmss", new Date());

				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(this.getAppDir() + Constants.SLASH + Constants.PATH_BACKUP + Constants.SLASH
						+ dateStr + Constants.UNDERSCORE + Constants.DATABASE_FILE);

				if (currentDB.exists()) {

					FileInputStream fis = new FileInputStream(currentDB);
					FileOutputStream fos = new FileOutputStream(backupDB);

					FileChannel src = fis.getChannel();
					FileChannel dst = fos.getChannel();
					dst.transferFrom(src, 0, src.size());

					src.close();
					dst.close();
					fis.close();
					fos.close();

					Toast.makeText(this,
							getString(R.string.backup_completed) + Constants.SPACE + backupDB.getPath(),
							Toast.LENGTH_LONG).show();

				} else {
					Toast.makeText(
							this,
							String.format(mResources.getString(R.string.backup_error),
									mResources.getString(R.string.backup_error_source_not_found)), Toast.LENGTH_LONG)
							.show();
				}

			}
		}

		catch (Exception e) {

			Log.e(Constants.TAG, e.getMessage());

			Toast.makeText(this,
					getString(R.string.backup_error) + Constants.SPACE + e.getMessage(), Toast.LENGTH_LONG).show();

		}

	}

	/**
	 * Restoring database from previously saved copy
	 */
	public void restoreDatabase(Activity activity) {

		// show "select a file" dialog
		File importFolder = new File(this.getAppDir() + Constants.SLASH + Constants.PATH_BACKUP + Constants.SLASH);
		final String importFiles[] = importFolder.list();

		if (importFiles == null || importFiles.length == 0) {
			Toast.makeText(this, R.string.source_folder_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		importDatabaseFileName = importFiles[0];

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.select_file);
		builder.setSingleChoiceItems(importFiles, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

				importDatabaseFileName = importFiles[whichButton];
				mainHandler.post(restoreDatabaseRunnable);

				dialog.dismiss();

			}
		});

		AlertDialog alert = builder.create();
		alert.show();

	}

	/**
	 * Runnable performing restoration of the application database from external
	 * source
	 */
	private Runnable restoreDatabaseRunnable = new Runnable() {
		@Override
		public void run() {

			String backupPath = UpTimeApp.this.getAppDir() + Constants.SLASH + Constants.PATH_BACKUP + Constants.SLASH;
			try {
				// open database in readonly mode
				SQLiteDatabase db = SQLiteDatabase.openDatabase(backupPath + importDatabaseFileName, null,
						SQLiteDatabase.OPEN_READONLY);

				// check version compatibility
				// only same version of the db can be restored
				if (UpTimeApp.this.getDatabase().getVersion() != db.getVersion()) {
					Toast.makeText(UpTimeApp.this, getString(R.string.restore_db_version_conflict),
							Toast.LENGTH_LONG).show();
					return;
				}

				db.close();

			} catch (SQLiteException e) {

				Toast.makeText(UpTimeApp.this,
						String.format(mResources.getString(R.string.restore_file_error), e.getMessage()),
						Toast.LENGTH_LONG).show();

				return;
			}

			// closing current db
			UpTimeApp.this.getDatabase().close();

			try {

				File data = Environment.getDataDirectory();

				if (UpTimeApp.this.getExternalStorageWriteable()) {

					String restoreDBPath = backupPath + importDatabaseFileName;

					File restoreDB = new File(restoreDBPath);
					// File currentDB = new File(app.getDataDir(),
					// Constants.DATABASE_FILE);
					File currentDB = new File(data, UpTimeApp.this.getDataDir() + Constants.SLASH + Constants.DATABASE_FILE);

					FileInputStream fis = new FileInputStream(restoreDB);
					FileOutputStream fos = new FileOutputStream(currentDB);

					FileChannel src = fis.getChannel();
					FileChannel dst = fos.getChannel();

					dst.transferFrom(src, 0, src.size());

					src.close();
					dst.close();
					fis.close();
					fos.close();

					UpTimeApp.this.setDatabase();

					Toast.makeText(UpTimeApp.this, getString(R.string.restore_completed),
							Toast.LENGTH_SHORT).show();

				}

			} catch (Exception e) {

				Log.e(Constants.TAG, e.getMessage());

				UpTimeApp.this.setDatabase();

				Toast.makeText(UpTimeApp.this, getString(R.string.restore_error) + ": " + e.getMessage(),
						Toast.LENGTH_LONG).show();

			}

		}
	};

}
