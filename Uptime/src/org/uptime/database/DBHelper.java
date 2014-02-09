package org.uptime.database;

import org.uptime.engine.Constants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	/**
	 * Singleton instance.
	 */
	private static DBHelper sInstance;

	public static final String TABLE_CARDS = "cards";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_CATEGORY = "category";
	public static final String COLUMN_URL = "url";
	public static final String COLUMN_ACTIVE = "active";

	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + TABLE_CARDS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NAME + " text not null, " + COLUMN_CATEGORY + " text, "
			+ COLUMN_URL + " text, " + COLUMN_ACTIVE + " text" + ");";

	public static DBHelper getInstance(Context context) {

		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null) {
			sInstance = new DBHelper(context.getApplicationContext());
		}
		return sInstance;
	}

	private DBHelper(Context context) {
		super(context, Constants.DATABASE_FILE, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DBHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
		onCreate(db);
	}

	public void dropTable(SQLiteDatabase db, String table) {
		db.execSQL("DROP TABLE IF EXISTS " + table);
		onCreate(db);
	}

}