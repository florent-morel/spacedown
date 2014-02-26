package org.spacedown.engine.game;

import org.spacedown.database.SpacedownContentProvider.Schema;

import android.content.ContentResolver;
import android.database.Cursor;


public class Player {

	private long id;

	private String name;

	private boolean active;


	public Player() {
		super();
	}


	public Player(long internalId) {
		super();
		this.id = internalId;
		this.initPlayer();
	}

	public void initPlayer() {
		this.active = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean equals(Object o) {
		boolean toReturn = false;
		Player other = (Player) o;
		if (this.getId() == other.getId()) {
			toReturn = true;
		}
		return toReturn;
	}

	public String toString() {
		StringBuilder playerString = new StringBuilder(this.getName());
		playerString.append("(");
		playerString.append(this.getId());
		playerString.append(")");

		return playerString.toString();
	}

	public static Player build(Cursor cursor, ContentResolver contentResolver) {
		Player out = new Player();

		out.id = cursor.getLong(cursor.getColumnIndex(Schema.COL_PLAYER_ID));
		
		out.name = cursor.getString(cursor.getColumnIndex(Schema.COL_NAME));

		out.active = Schema.VAL_ACTIVE == cursor.getInt(cursor.getColumnIndex(Schema.COL_ACTIVE));

		return out;
	}

}
