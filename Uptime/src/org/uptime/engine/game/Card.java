package org.uptime.engine.game;

import org.uptime.database.UpTimeContentProvider.Schema;

import android.content.ContentResolver;
import android.database.Cursor;

public class Card {

	private long id;

	/**
	 * Non active cards will not be used when building card lists for a game.
	 */
	private boolean active;

	private String nameToFind;

	private String category;

	private String url;

	private String description;

	private boolean isFound;

	public Card() {
		super();
	}

	public Card(Integer id, String nameToFind, String category) {
		super();
		this.id = id;
		this.nameToFind = nameToFind;
		this.category = category;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNameToFind(String nameToFind) {
		this.nameToFind = nameToFind;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isFound() {
		return isFound;
	}

	public void setFound(boolean isFound) {
		this.isFound = isFound;
	}

	public String getNameToFind() {
		return nameToFind;
	}

	public long getId() {
		return id;
	}

	public String getCategory() {
		return category;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActiveInDB() {
		return active;
	}

	public void setActiveInDB(boolean isActiveInDB) {
		this.active = isActiveInDB;
	}

	public static Card build(Cursor cursor, ContentResolver contentResolver) {
		Card out = new Card();

		out.id = cursor.getLong(cursor.getColumnIndex(Schema.COL_ID));
		
		out.nameToFind = cursor.getString(cursor.getColumnIndex(Schema.COL_NAME));
		
		out.category = cursor.getString(cursor.getColumnIndex(Schema.COL_CATEGORY));
		
		out.url = cursor.getString(cursor.getColumnIndex(Schema.COL_URL));

		out.active = Schema.VAL_ACTIVE == cursor.getInt(cursor.getColumnIndex(Schema.COL_ACTIVE));
		
		out.description = cursor.getString(cursor.getColumnIndex(Schema.COL_DESCRIPTION));

		return out;
	}

	public static Card buildOld(Cursor cursor, ContentResolver contentResolver) {
		Card out = new Card();

		out.id = cursor.getLong(cursor.getColumnIndex(Schema.COL_ID));
		
		out.nameToFind = cursor.getString(cursor.getColumnIndex(Schema.COL_NAME));
		
		out.category = cursor.getString(cursor.getColumnIndex(Schema.COL_CATEGORY));
		
		out.url = cursor.getString(cursor.getColumnIndex(Schema.COL_URL));

//		out.active = Schema.VAL_ACTIVE == cursor.getInt(cursor.getColumnIndex(Schema.COL_ACTIVE));
		out.active = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(Schema.COL_ACTIVE)));
		
//		out.description = cursor.getString(cursor.getColumnIndex(Schema.COL_DESCRIPTION));

		return out;
	}

	@Override
	public String toString() {
		return "Card [id=" + id + ", active=" + active + ", nameToFind=" + nameToFind + ", category=" + category
				+ ", url=" + url + ", description=" + description + ", isFound=" + isFound + "]";
	}

}
