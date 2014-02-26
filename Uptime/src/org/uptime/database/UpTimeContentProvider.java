package org.uptime.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class UpTimeContentProvider extends ContentProvider {

	private static final String TAG = UpTimeContentProvider.class.getSimpleName();

	public static final String ROOT_PACKAGE = "org.uptime";
	
	private DBHelper dbHelper;

	/**
	 * Authority for Uris
	 */
	public static final String AUTHORITY = ROOT_PACKAGE + ".provider";

	/**
	 * Uri for cards
	 */
	public static final Uri CONTENT_URI_CARDS = Uri.parse("content://" + AUTHORITY + "/" + Schema.TABLE_CARDS);

	/**
	 * Uri for players
	 */
	public static final Uri CONTENT_URI_PLAYERS = Uri.parse("content://" + AUTHORITY + "/" + Schema.TABLE_PLAYERS);

	/**
	 * Uri Matcher
	 */
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		uriMatcher.addURI(AUTHORITY, Schema.TABLE_CARDS, Schema.URI_CODE_CARDS);
		uriMatcher.addURI(AUTHORITY, Schema.TABLE_CARDS + "/active", Schema.URI_CODE_CARD_ACTIVE);
		uriMatcher.addURI(AUTHORITY, Schema.TABLE_CARDS + "/#", Schema.URI_CODE_CARD_ID);
		uriMatcher.addURI(AUTHORITY, Schema.TABLE_PLAYERS, Schema.URI_CODE_PLAYERS);
		uriMatcher.addURI(AUTHORITY, Schema.TABLE_PLAYERS + "/active", Schema.URI_CODE_PLAYER_ACTIVE);
		uriMatcher.addURI(AUTHORITY, Schema.TABLE_PLAYERS + "/#", Schema.URI_CODE_PLAYER_ID);

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.v(TAG, "delete(), uri=" + uri);

		int count;
		// Select which data type to delete
		switch (uriMatcher.match(uri)) {
		case Schema.URI_CODE_CARDS:
			count = dbHelper.getWritableDatabase().delete(Schema.TABLE_CARDS, selection, selectionArgs);
			break;
		case Schema.URI_CODE_CARD_ID:
			// the URI matches a specific card, delete all related entities
			String cardId = Long.toString(ContentUris.parseId(uri));
			count = dbHelper.getWritableDatabase().delete(Schema.TABLE_CARDS, Schema.COL_ID + " = ?",
					new String[] { cardId });
			break;
		case Schema.URI_CODE_PLAYERS:
			count = dbHelper.getWritableDatabase().delete(Schema.TABLE_PLAYERS, selection, selectionArgs);
			break;
		case Schema.URI_CODE_PLAYER_ID:
			// the URI matches a specific card, delete all related entities
			String playerId = Long.toString(ContentUris.parseId(uri));
			count = dbHelper.getWritableDatabase().delete(Schema.TABLE_PLAYERS, Schema.COL_PLAYER_ID + " = ?",
					new String[] { playerId });
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		Log.v(TAG, "getType(), uri=" + uri);

		// Select which type to return
		switch (uriMatcher.match(uri)) {
		case Schema.URI_CODE_CARDS:
			return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + ROOT_PACKAGE + "." + Schema.TABLE_CARDS;
		case Schema.URI_CODE_PLAYERS:
			return ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + ROOT_PACKAGE + "." + Schema.TABLE_PLAYERS;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.v(TAG, "insert(), uri=" + uri + ", values=" + values.toString());

		// Select which data type to insert
		switch (uriMatcher.match(uri)) {
		case Schema.URI_CODE_CARDS:
			if (values.containsKey(Schema.COL_NAME)) {
				long rowId = dbHelper.getWritableDatabase().insert(Schema.TABLE_CARDS, null, values);
				if (rowId > 0) {
					Uri trackUri = ContentUris.withAppendedId(CONTENT_URI_CARDS, rowId);
					getContext().getContentResolver().notifyChange(trackUri, null);
					return trackUri;
				}
			} else {
				throw new IllegalArgumentException("values should provide " + Schema.COL_NAME);
			}
			break;
		case Schema.URI_CODE_PLAYERS:
			if (values.containsKey(Schema.COL_NAME)) {
				long rowId = dbHelper.getWritableDatabase().insert(Schema.TABLE_PLAYERS, null, values);
				if (rowId > 0) {
					Uri trackUri = ContentUris.withAppendedId(CONTENT_URI_PLAYERS, rowId);
					getContext().getContentResolver().notifyChange(trackUri, null);
					return trackUri;
				}
			} else {
				throw new IllegalArgumentException("values should provide " + Schema.COL_NAME);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		return null;
	}

	@Override
	public boolean onCreate() {
		dbHelper = DBHelper.getInstance(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selectionIn, String[] selectionArgsIn, String sortOrder) {
		Log.v(TAG, "query(), uri=" + uri);

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String selection = selectionIn;
		String[] selectionArgs = selectionArgsIn;

		String groupBy = null;
		String limit = null;

		// Select which datatype was requested
		switch (uriMatcher.match(uri)) {
		case Schema.URI_CODE_CARDS:
			qb.setTables(Schema.TABLE_CARDS);
			break;
		case Schema.URI_CODE_CARD_ID:
			if (selectionIn != null || selectionArgsIn != null) {
				// Any selection/selectionArgs will be ignored
				throw new UnsupportedOperationException();
			}
			String cardId = uri.getLastPathSegment();
			qb.setTables(Schema.TABLE_CARDS);
			selection = Schema.TABLE_CARDS + "." + Schema.COL_ID + " = ?";
			selectionArgs = new String[] { cardId };
			break;
		case Schema.URI_CODE_CARD_ACTIVE:
			if (selectionIn != null || selectionArgsIn != null) {
				// Any selection/selectionArgs will be ignored
				throw new UnsupportedOperationException();
			}
			qb.setTables(Schema.TABLE_CARDS);
			selection = Schema.COL_ACTIVE + " = ?";
			selectionArgs = new String[] { Integer.toString(Schema.VAL_ACTIVE) };
			break;
		case Schema.URI_CODE_PLAYERS:
			qb.setTables(Schema.TABLE_PLAYERS);
			break;
		case Schema.URI_CODE_PLAYER_ID:
			if (selectionIn != null || selectionArgsIn != null) {
				// Any selection/selectionArgs will be ignored
				throw new UnsupportedOperationException();
			}
			String playerId = uri.getLastPathSegment();
			qb.setTables(Schema.TABLE_PLAYERS);
			selection = Schema.TABLE_PLAYERS + "." + Schema.COL_PLAYER_ID + " = ?";
			selectionArgs = new String[] { playerId };
			break;
		case Schema.URI_CODE_PLAYER_ACTIVE:
			if (selectionIn != null || selectionArgsIn != null) {
				// Any selection/selectionArgs will be ignored
				throw new UnsupportedOperationException();
			}
			qb.setTables(Schema.TABLE_PLAYERS);
			selection = Schema.COL_ACTIVE + " = ?";
			selectionArgs = new String[] { Integer.toString(Schema.VAL_ACTIVE) };
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		Cursor c = qb.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, groupBy, null,
				sortOrder, limit);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selectionIn, String[] selectionArgsIn) {
		Log.v(TAG, "update(), uri=" + uri);

		String table;
		String selection = selectionIn;
		String[] selectionArgs = selectionArgsIn;

		switch (uriMatcher.match(uri)) {
		case Schema.URI_CODE_CARD_ID:
			if (selectionIn != null || selectionArgsIn != null) {
				// Any selection/selectionArgs will be ignored
				throw new UnsupportedOperationException();
			}
			table = Schema.TABLE_CARDS;
			String cardId = uri.getLastPathSegment();
			selection = Schema.COL_ID + " = ?";
			selectionArgs = new String[] { cardId };
			break;
		case Schema.URI_CODE_CARD_ACTIVE:
			if (selectionIn != null || selectionArgsIn != null) {
				// Any selection/selectionArgs will be ignored
				throw new UnsupportedOperationException();
			}
			table = Schema.TABLE_CARDS;
			selection = Schema.COL_ACTIVE + " = ?";
			selectionArgs = new String[] { Integer.toString(Schema.VAL_ACTIVE) };
			break;
		case Schema.URI_CODE_CARDS:
			// Dangerous: Will update all the cards, but necessary for instance
			// to switch all the cards to inactive
			table = Schema.TABLE_CARDS;
			break;
		case Schema.URI_CODE_PLAYER_ID:
			if (selectionIn != null || selectionArgsIn != null) {
				// Any selection/selectionArgs will be ignored
				throw new UnsupportedOperationException();
			}
			table = Schema.TABLE_PLAYERS;
			String playerId = uri.getLastPathSegment();
			selection = Schema.COL_ID + " = ?";
			selectionArgs = new String[] { playerId };
			break;
		case Schema.URI_CODE_PLAYER_ACTIVE:
			if (selectionIn != null || selectionArgsIn != null) {
				// Any selection/selectionArgs will be ignored
				throw new UnsupportedOperationException();
			}
			table = Schema.TABLE_PLAYERS;
			selection = Schema.COL_ACTIVE + " = ?";
			selectionArgs = new String[] { Integer.toString(Schema.VAL_ACTIVE) };
			break;
		case Schema.URI_CODE_PLAYERS:
			// Dangerous: Will update all the cards, but necessary for instance
			// to switch all the cards to inactive
			table = Schema.TABLE_PLAYERS;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		int rows = dbHelper.getWritableDatabase().update(table, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return rows;

	}

	/**
	 * Represents Data Schema.
	 */
	public static final class Schema {
		public static final String TABLE_CARDS = "cards";
		public static final String TABLE_PLAYERS = "players";
		public static final String COL_CATEGORY = "category";
		public static final String COL_URL = "url";
		public static final String COL_NAME = "name";

		public static final String COL_ID = "_id";
		public static final String COL_PLAYER_ID = "_id";
		public static final String COL_DESCRIPTION = "description";
		public static final String COL_ACTIVE = "active";
		public static final String COL_COLOR = "color";

		public static final String COL_NOTES = "notes";

		// Codes for UriMatcher
		public static final int URI_CODE_CARDS = 3;
		public static final int URI_CODE_CARD_ID = 4;
		public static final int URI_CODE_CARD_ACTIVE = 5;
		public static final int URI_CODE_PLAYERS = 6;
		public static final int URI_CODE_PLAYER_ID = 7;
		public static final int URI_CODE_PLAYER_ACTIVE = 8;

		public static final int VAL_ACTIVE = 1;
		public static final int VAL_INACTIVE = 0;
	}

}
