package org.spacedown.database;

import org.spacedown.database.SpacedownContentProvider.Schema;
import org.spacedown.engine.Constants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	/**
	 * Singleton instance.
	 */
	private static DBHelper sInstance;
	
	private static final String CREATE_TABLE = "CREATE TABLE ";

	private static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";

	/**
	 * SQL for creating table CARDS
	 * @since 1
	 */
	public static final String SQL_CREATE_TABLE_CARDS = ""
		+ CREATE_TABLE + Schema.TABLE_CARDS + " ("
		+ Schema.COL_ID + " integer primary key autoincrement,"
		+ Schema.COL_NAME + " text,"
		+ Schema.COL_CATEGORY + " text,"
		+ Schema.COL_URL + " text,"
		+ Schema.COL_ACTIVE + " integer not null default 1,"
		+ Schema.COL_DESCRIPTION + " text,"
		+ Schema.COL_LAST_PLAYED + " int"
		+ ")";

	/**
	 * SQL for creating table CARDS
	 * @since 1
	 */
	public static final String SQL_CREATE_TABLE_PLAYERS = ""
		+ CREATE_TABLE + Schema.TABLE_PLAYERS + " ("
		+ Schema.COL_ID + " integer primary key autoincrement,"
		+ Schema.COL_NAME + " text,"
		+ Schema.COL_ACTIVE + " integer not null default 0"
		+ ")";

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
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DROP_TABLE_IF_EXISTS + Schema.TABLE_CARDS);
		db.execSQL(SQL_CREATE_TABLE_CARDS);
		db.execSQL(DROP_TABLE_IF_EXISTS + Schema.TABLE_PLAYERS);
		db.execSQL(SQL_CREATE_TABLE_PLAYERS);
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DBHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL(DROP_TABLE_IF_EXISTS + Schema.TABLE_CARDS);
		onCreate(db);
	}

	public void dropTable(SQLiteDatabase db, String table) {
		db.execSQL(DROP_TABLE_IF_EXISTS + table);
		onCreate(db);
	}
	
}
