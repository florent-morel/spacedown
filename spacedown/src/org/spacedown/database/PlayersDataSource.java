package org.spacedown.database;

import java.util.ArrayList;
import java.util.List;

import org.spacedown.database.SpacedownContentProvider.Schema;
import org.spacedown.engine.game.Player;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class PlayersDataSource {

	private static final String TAG = PlayersDataSource.class.getSimpleName();

	/**
	 * ContentResolver to interact with content provider
	 */
	private ContentResolver contentResolver;

	public PlayersDataSource(Context c) {
		contentResolver = c.getContentResolver();
	}

	public long createPlayer(Player player) {
		// Create entry in PLAYERS table
		ContentValues values = new ContentValues();
		values.put(Schema.COL_NAME, player.getName());
		values.put(Schema.COL_ACTIVE, player.isActive());

		Uri uri = contentResolver.insert(SpacedownContentProvider.CONTENT_URI_PLAYERS, values);
		long playerId = ContentUris.parseId(uri);
		return playerId;
	}

	public int updatePlayer(Player player) {

		int nbRow = 0;

		if (player != null) {
			long id = player.getId();
			ContentValues values = new ContentValues();
			values.put(Schema.COL_NAME, player.getName());
			values.put(Schema.COL_ACTIVE, player.isActive());

			Uri playerUri = ContentUris
					.withAppendedId(SpacedownContentProvider.CONTENT_URI_PLAYERS, id);
			nbRow = contentResolver.update(playerUri, values, null, null);
		}
		return nbRow;
	
	}

	public Player getPlayer(long playerId) {
		Player player = null;
		Uri cardUri = ContentUris.withAppendedId(SpacedownContentProvider.CONTENT_URI_CARDS, playerId);
		Cursor cursor = contentResolver.query(cardUri, null, null, null, null);
		if (cursor.moveToFirst()) {
			player = Player.build(cursor, contentResolver);
		}
		return player;
	}

	public void deletePlayer(Player player) {
		this.deletePlayer(player.getId());
	}

	public void deletePlayer(long playerId) {
		Log.v(TAG, "Deleting player with id '" + playerId);
		contentResolver.delete(ContentUris.withAppendedId(SpacedownContentProvider.CONTENT_URI_PLAYERS, playerId),
				null, null);
	}

	public List<Player> getAllPLayers(Boolean isActive) {
		List<Player> players = new ArrayList<Player>();

		Cursor cursor = contentResolver.query(SpacedownContentProvider.CONTENT_URI_PLAYERS, null,
				Schema.COL_ACTIVE + " = '" + isActive + "'", null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Player card = Player.build(cursor, contentResolver);
			players.add(card);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return players;
	}

}
