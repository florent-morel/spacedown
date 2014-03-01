package org.spacedown.database;

import java.util.ArrayList;
import java.util.List;

import org.spacedown.database.SpacedownContentProvider.Schema;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Card;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class CardsDataSource {

	private static final String TAG = CardsDataSource.class.getSimpleName();

	/**
	 * ContentResolver to interact with content provider
	 */
	private ContentResolver contentResolver;

	public CardsDataSource(Context c) {
		contentResolver = c.getContentResolver();
	}

	public List<Card> addCards(List<Card> cardList) {
		List<Card> addedCards = new ArrayList<Card>();
		if (cardList != null && !cardList.isEmpty()) {
			for (Card card : cardList) {
				addedCards.add(this.getCard(this.createCard(card)));
			}
		}
		return addedCards;
	}

	public long createCard(Card card) {
		// Create entry in CARDS table
		ContentValues values = new ContentValues();
		values.put(Schema.COL_NAME, card.getNameToFind());
		values.put(Schema.COL_CATEGORY, card.getCategory());
		values.put(Schema.COL_URL, card.getUrl());
		int active = Schema.VAL_INACTIVE;
		if (card.isActiveInDB()) {
			active = Schema.VAL_ACTIVE;
		}
		values.put(Schema.COL_ACTIVE, active);
		values.put(Schema.COL_DESCRIPTION, card.getDescription());

		Uri cardUri = contentResolver.insert(SpacedownContentProvider.CONTENT_URI_CARDS, values);
		long cardId = ContentUris.parseId(cardUri);
		return cardId;
	}

	public long updateCard(Card card) {
		long id = -1;
		if (card != null) {
			id = card.getId();
			ContentValues values = new ContentValues();
			values.put(Schema.COL_NAME, card.getNameToFind());
			values.put(Schema.COL_CATEGORY, card.getCategory());
			values.put(Schema.COL_URL, card.getUrl());
			int active = Schema.VAL_INACTIVE;
			if (card.isActiveInDB()) {
				active = Schema.VAL_ACTIVE;
			}
			values.put(Schema.COL_ACTIVE, active);
			values.put(Schema.COL_DESCRIPTION, card.getDescription());

			Uri cardUri = ContentUris
					.withAppendedId(SpacedownContentProvider.CONTENT_URI_CARDS, card.getId());
			contentResolver.update(cardUri, values, null, null);
		}
		return id;
	
	}

	public Card getCard(long cardId) {
		Card card = null;
		Uri cardUri = ContentUris.withAppendedId(SpacedownContentProvider.CONTENT_URI_CARDS, cardId);
		Cursor cursor = contentResolver.query(cardUri, null, null, null, null);
		if (cursor.moveToFirst()) {
			card = Card.build(cursor, contentResolver);
		}
		return card;
	}

	public void deleteCard(Card card) {
		this.deleteCard(card.getId());
	}

	public void deleteCard(long cardId) {
		Log.v(TAG, "Deleting Card with id '" + cardId);
		contentResolver.delete(ContentUris.withAppendedId(SpacedownContentProvider.CONTENT_URI_CARDS, cardId),
				null, null);
	}

	public List<Card> getAllCards(Boolean isActive) {
		List<Card> cards = new ArrayList<Card>();

		int active = Schema.VAL_INACTIVE;
		if (isActive) {
			active = Schema.VAL_ACTIVE;
		}
		
		Cursor cursor = contentResolver.query(SpacedownContentProvider.CONTENT_URI_CARDS, null,
				Schema.COL_ACTIVE + " = " + active, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Card card = Card.build(cursor, contentResolver);
			cards.add(card);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return cards;
	}

	public List<Card> getAllCustomCards() {
		List<Card> cards = new ArrayList<Card>();

		Cursor cursor = contentResolver.query(SpacedownContentProvider.CONTENT_URI_CARDS, null,
				Schema.COL_CATEGORY + " = '" + Constants.CATEGORY_CUSTOM + "'", null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Card card = Card.build(cursor, contentResolver);
			cards.add(card);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return cards;
	}

}
