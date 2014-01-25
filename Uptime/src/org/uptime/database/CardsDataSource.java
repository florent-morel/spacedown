package org.uptime.database;

import java.util.ArrayList;
import java.util.List;

import org.uptime.engine.Constants;
import org.uptime.engine.game.Card;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CardsDataSource {

	// Database fields
	private SQLiteDatabase db;
	private DBHelper dbHelper;
	private String[] allColumns = { DBHelper.COLUMN_ID, DBHelper.COLUMN_NAME, DBHelper.COLUMN_CATEGORY,
			DBHelper.COLUMN_URL, DBHelper.COLUMN_ACTIVE };

	public CardsDataSource(SQLiteDatabase database, DBHelper databaseHelper) {
		dbHelper = databaseHelper;
		db = database;
	}

	public void close() {
		dbHelper.close();
	}

	public List<Card> addCards(List<Card> cardList) {
		List<Card> addedCards = new ArrayList<Card>();
		if (cardList != null && !cardList.isEmpty()) {
			for (Card card : cardList) {
				addedCards.add(this.createCard(card));
			}
		}
		return addedCards;
	}

	public Card createCard(Card card) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.COLUMN_NAME, card.getNameToFind());
		values.put(DBHelper.COLUMN_CATEGORY, card.getCategory());
		values.put(DBHelper.COLUMN_URL, card.getUrl());
		values.put(DBHelper.COLUMN_ACTIVE, Boolean.valueOf(card.isActiveInDB()).toString());

		long insertId = db.insert(DBHelper.TABLE_CARDS, null, values);
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
		db.update(DBHelper.TABLE_CARDS, values, DBHelper.COLUMN_ID + "=" + id, null);
		Card newCard = getCard(id);
		return newCard;
	}

	public Card getCard(long insertId) {
		Cursor cursor = db.query(DBHelper.TABLE_CARDS, allColumns, DBHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Card card = cursorToCard(cursor);
		cursor.close();
		return card;
	}

	public void deleteCard(Card card) {
		long id = card.getId();
		System.out.println("Card deleted with id: " + id);
		db.delete(DBHelper.TABLE_CARDS, DBHelper.COLUMN_ID + " = " + id, null);
	}

	public List<Card> getAllCards(Boolean isActive) {
		List<Card> cards = new ArrayList<Card>();

		Cursor cursor = db.query(DBHelper.TABLE_CARDS, allColumns,
				DBHelper.COLUMN_ACTIVE + " = '" + isActive.toString() + "'", null, null, null, null);

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

	public List<Card> getAllCustomCards() {
		List<Card> cards = new ArrayList<Card>();

		Cursor cursor = db.query(DBHelper.TABLE_CARDS, allColumns,
				DBHelper.COLUMN_CATEGORY + " = '" + Constants.CATEGORY_CUSTOM + "'", null, null, null, null);

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

	/**
	 * Warning! Will drop current card table.
	 */
	public void dropCardTable() {
		dbHelper.dropTable(db, DBHelper.TABLE_CARDS);
	}
}
