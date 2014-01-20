package database;

import java.util.ArrayList;
import java.util.List;

import org.uptime.engine.game.Card;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CardsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DBHelper dbHelper;
	private String[] allColumns = { DBHelper.COLUMN_ID, DBHelper.COLUMN_NAME, DBHelper.COLUMN_CATEGORY, DBHelper.COLUMN_URL, DBHelper.COLUMN_ACTIVE };

	public CardsDataSource(Context context) {
		dbHelper = new DBHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Card createCard(Card card) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.COLUMN_NAME, card.getNameToFind());
		values.put(DBHelper.COLUMN_CATEGORY, card.getCategory());
		values.put(DBHelper.COLUMN_URL, card.getUrl());
		values.put(DBHelper.COLUMN_ACTIVE, Boolean.valueOf(card.isActiveInDB()).toString());
		
		long insertId = database.insert(DBHelper.TABLE_CARDS, null, values);
		Card newCard = getCard(insertId);
		return newCard;
	}

	public Card updateCard(Card card) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.COLUMN_NAME, card.getNameToFind());
		values.put(DBHelper.COLUMN_CATEGORY, card.getCategory());
		values.put(DBHelper.COLUMN_URL, card.getUrl());
		values.put(DBHelper.COLUMN_ACTIVE, Boolean.valueOf(card.isActiveInDB()).toString());
		
		Integer id = card.getId();
		database.update(DBHelper.TABLE_CARDS, values, DBHelper.COLUMN_ID + "=" + id, null);
		Card newCard = getCard(id);
		return newCard;
	}

	public Card getCard(long insertId) {
		Cursor cursor = database.query(DBHelper.TABLE_CARDS, allColumns, DBHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Card card = cursorToCard(cursor);
		cursor.close();
		return card;
	}

	public void deleteCard(Card card) {
		long id = card.getId();
		System.out.println("Card deleted with id: " + id);
		database.delete(DBHelper.TABLE_CARDS, DBHelper.COLUMN_ID + " = " + id, null);
	}

	public List<Card> getAllCards() {
		List<Card> cards = new ArrayList<Card>();

		Cursor cursor = database.query(DBHelper.TABLE_CARDS, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Card card = cursorToCard(cursor);
			cards.add(card);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return cards;
	}

	private Card cursorToCard(Cursor cursor) {
		Card card = new Card();
		card.setId(cursor.getInt(0));
		card.setNameToFind(cursor.getString(1));
		card.setCategory(cursor.getString(2));
		card.setUrl(cursor.getString(3));
		card.setActiveInDB(Boolean.valueOf(cursor.getString(4)));
		return card;
	}
}
