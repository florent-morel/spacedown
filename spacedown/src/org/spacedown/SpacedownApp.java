package org.spacedown;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.spacedown.database.CardsDataSource;
import org.spacedown.database.DBHelper;
import org.spacedown.database.SpacedownContentProvider.Schema;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Card;

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
import android.database.Cursor;
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
 * This file is part of spacedown.
 * 
 * spacedown is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * spacedown is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * spacedown. If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author florent
 * 
 */
public class SpacedownApp extends Application {

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
		appDir = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SLASH
				+ Constants.PATH_APP_NAME;

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

		dataDir = Environment.getDataDirectory().getAbsolutePath() + "/org.spacedown/databases";
		// TODO: NEW FEATURE: app database file in external memory
		// dataDir = appDir + Constants.SLASH + Constants.PATH_DATABASE;

		// create all folders required by the application on external storage
		if (getExternalStorageAvailable() && getExternalStorageWriteable()) {
			createFolderStructure();
		} else {
			Toast.makeText(this, R.string.memory_card_not_available, Toast.LENGTH_SHORT).show();
		}

	}

	private void dropColumn() {
		CardsDataSource ds = new CardsDataSource(getApplicationContext());

		// List<Card> inactiveCards = ds.getAllCardsOld(db, false);
		String tableName = Schema.TABLE_CARDS;
		Cursor query = db.query(tableName, null, null, null, null, null, null);

		query.moveToFirst();
//		StringBuilder builder = new StringBuilder();
		List<String> listIds = new ArrayList<String>();
		String ids = "18,43,56,76,81,115,131,149,175,189,193,199,202,203,205,237,261,266,267,270,272,276,285,292,319,321,325,344,348,370,382,386,388,396,425,435,436,437,438,447,458,469,487,527,536,542,570,582,589,593,599,609,611,613,620,640,643,666,674,682,691,752,760,797,809,829,833,844,855,926,935,941,955,956,957,990,997,999,1003";

		StringTokenizer st = new StringTokenizer(ids, ",");
		while (st.hasMoreTokens()) {
			listIds.add(st.nextToken());
		}

		List<Card> inactiveCards = new ArrayList<Card>();
		while (!query.isAfterLast()) {
			Card card = Card.build(query, getContentResolver());
			if (listIds.contains(String.valueOf(card.getId()))) {
				card.setActiveInDB(false);
			} else {
				card.setActiveInDB(true);
			}
			inactiveCards.add(card);
			query.moveToNext();
		}

//		db.execSQL("ALTER TABLE " + tableName + " RENAME TO " + tableName + "_old;");

		// Creating the table on its new format (no redundant columns)
//		db.execSQL(DBHelper.SQL_CREATE_TABLE_CARDS);

		// db.execSQL("alter table "+tableName+" add column "+Schema.COL_DESCRIPTION+" text;");

//		String columns = Schema.COL_ID + "," + Schema.COL_NAME;

//		Log.v(tableName, card.toString());
		// query = db.query(tableName + "_old", null, null, null, null, null,
		// null);
		//
		// query.moveToFirst();
		// builder = new StringBuilder();
		//
		// while (!query.isAfterLast()) {
		// builder.append(query.getString(1));
		// query.moveToNext();
		// }
		//
		// Log.v(tableName + "_old", builder.toString());

		// Populating the table with the data
		// db.execSQL("INSERT INTO " + tableName + "(" + columns + ") SELECT " +
		// columns + " FROM " + tableName + "_old;");
		// Update inactive cards
		for (Card card : inactiveCards) {
			ds.updateCard(card);
		}
		// db.execSQL("DROP TABLE " + tableName + "_old;");
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

					Toast.makeText(this, getString(R.string.backup_completed) + Constants.SPACE + backupDB.getPath(),
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

			Toast.makeText(this, getString(R.string.backup_error) + Constants.SPACE + e.getMessage(), Toast.LENGTH_LONG)
					.show();

		}

	}

	/**
	 * Restoring database from previously saved copy
	 */
	public void restoreDatabase(Activity activity) {

		// show "select a file" dialog
		String path = this.getAppDir() + Constants.SLASH + Constants.PATH_BACKUP + Constants.SLASH;
		File importFolder = new File(path);
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

			String backupPath = SpacedownApp.this.getAppDir() + Constants.SLASH + Constants.PATH_BACKUP
					+ Constants.SLASH;
			try {
				// open database in readonly mode
				SQLiteDatabase db = SQLiteDatabase.openDatabase(backupPath + importDatabaseFileName, null,
						SQLiteDatabase.OPEN_READONLY);

				// check version compatibility
				// only same version of the db can be restored
				if (SpacedownApp.this.getDatabase().getVersion() != db.getVersion()) {
					Toast.makeText(SpacedownApp.this, getString(R.string.restore_db_version_conflict),
							Toast.LENGTH_LONG).show();
					return;
				}

				db.close();

			} catch (SQLiteException e) {

				Toast.makeText(SpacedownApp.this,
						String.format(mResources.getString(R.string.restore_file_error), e.getMessage()),
						Toast.LENGTH_LONG).show();

				return;
			}

			// closing current db
			SpacedownApp.this.getDatabase().close();

			try {

				File data = Environment.getDataDirectory();

				if (SpacedownApp.this.getExternalStorageWriteable()) {

					String restoreDBPath = backupPath + importDatabaseFileName;

					File restoreDB = new File(restoreDBPath);
					// File currentDB = new File(app.getDataDir(),
					// Constants.DATABASE_FILE);
					File currentDB = new File(data, SpacedownApp.this.getDataDir() + Constants.SLASH
							+ Constants.DATABASE_FILE);

					FileInputStream fis = new FileInputStream(restoreDB);
					FileOutputStream fos = new FileOutputStream(currentDB);

					FileChannel src = fis.getChannel();
					FileChannel dst = fos.getChannel();

					dst.transferFrom(src, 0, src.size());

					src.close();
					dst.close();
					fis.close();
					fos.close();

					SpacedownApp.this.setDatabase();

					Toast.makeText(SpacedownApp.this, getString(R.string.restore_completed), Toast.LENGTH_SHORT).show();

				}

			} catch (Exception e) {

				Log.e(Constants.TAG, e.getMessage());

				SpacedownApp.this.setDatabase();

				Toast.makeText(SpacedownApp.this, getString(R.string.restore_error) + ": " + e.getMessage(),
						Toast.LENGTH_LONG).show();

			}

		}
	};

}
