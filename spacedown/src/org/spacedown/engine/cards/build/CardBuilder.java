package org.spacedown.engine.cards.build;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.spacedown.R;
import org.spacedown.database.CardsDataSource;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Card;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class CardBuilder {
	
	private static final String TAG = CardBuilder.class.getSimpleName();

	private Resources mResources;

	private Context context;

	private CardsDataSource datasource;

	public CardBuilder(Resources mResources, Context context, CardsDataSource data) {
		super();
		datasource = data;
		this.mResources = mResources;
		this.context = context;
	}

	public List<Card> buildAvailableCards(Constants.RunMode runMode, Integer numberOfCards) {
		List<Card> cardList = new ArrayList<Card>();

		if (Constants.RunMode.DB.equals(runMode)) {
			cardList = importCardsFromDB();
		}

		if (Constants.RunMode.IMPORT_CSV.equals(runMode)) {
			cardList = importCardsFromCSV();
		}

		// Last chance: take debug cards
		if (cardList == null || Constants.RunMode.DEBUG.equals(runMode)) {
			if (numberOfCards == null) {
				numberOfCards = 100;
			}
			cardList = buildDebugCards(numberOfCards.intValue());
		}

		return cardList;

	}

	public List<Card> buildCardListForGame(List<Card> cardList, Integer numberOfCards) {
		List<Card> cardsForGame = new ArrayList<Card>();
		if (cardList != null && !cardList.isEmpty()) {
			// If not enough cards available, take all available cards
			if (numberOfCards > cardList.size()) {
				numberOfCards = cardList.size();
			}
			cardsForGame = getRandomCards(cardList, numberOfCards.intValue());
		}
		return cardsForGame;

	}

	private List<Card> importCardsFromDB() {
		List<Card> allCards = datasource.getAllCards(true);
		return allCards;
	}

	private List<Card> importCardsFromCSV() {
		List<Card> cardsFromCSV = null;
		try {
			FileReader fileReader = new FileReader(context.getFilesDir().getPath() + "/testaa.csv");
			cardsFromCSV = ImportCards.buildCardsFromCSV(fileReader);
		} catch (FileNotFoundException e) {
			// File is not found to import cards, fall-back to hard-coded list
			Toast toast = Toast.makeText(context,
					String.format(mResources.getString(R.string.dialog_import_file_not_found)), Toast.LENGTH_LONG);
			toast.show();
		}
		return cardsFromCSV;
	}

	/**
	 * Get random cards
	 * 
	 * @param initialList
	 * @param numberOfCards
	 * @return
	 */
	private List<Card> getRandomCards(List<Card> initialList, int numberOfCards) {
		List<Card> randomList = new ArrayList<Card>();
		
		// Get current time in millis
		 Calendar calendar =  GregorianCalendar.getInstance();
		 long currentTimeInMillis = calendar.getTimeInMillis();
		 Integer allowedTimeInt = fetchAllowedTimeSinceLastPlayed();

		// Get numberOfCards cards out of the initial list
		if (initialList != null && !initialList.isEmpty()) {
			int size = initialList.size();

			// Let's shuffle the initial list
			Collections.shuffle(initialList);
			
			Random randomGenerator = new Random();
			int idx = 0;
			List<Integer> randomIndexList = new ArrayList<Integer>();
			while (idx < numberOfCards) {
				int randomInt = randomGenerator.nextInt(size);
				if (!randomIndexList.contains(randomInt)) {
					randomIndexList.add(randomInt);
					idx++;
					Card newCard = initialList.get(randomInt);
					if (checkCardCanBePlayed(newCard, currentTimeInMillis, allowedTimeInt)) {
						randomList.add(newCard);
					}
				}
			}

		}

		return randomList;

	}

	private Integer fetchAllowedTimeSinceLastPlayed() {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);	
		Integer allowedTimeInt = Integer.valueOf(prefs.getString(mResources.getString(R.string.prefs_allowed_time_val_key),
		Constants.ALLOWED_CARD_LAST_PLAYED_DEFAULT));
		return allowedTimeInt;
	}

	/**
	 * Check if card is allowed to be played
	 * 
	 * 
	 * @param newCard
	 * @param currentTimeInMillis 
	 * @param allowedTimeInt 
	 * @return
	 */
	private boolean checkCardCanBePlayed(Card newCard, long currentTimeInMillis, Integer allowedTimeInt) {
		boolean cardAllowed = false;
		String format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		// Get time allowed since last played
		// Compute time since last played
		long lastPlayed = newCard.getLastPlayed();

		// Log.v(TAG, "currentTimeInMillis: " + currentTimeInMillis);
		// Log.v(TAG, "lastPlayed: " + lastPlayed);

		// If time since last played is ok, allow card
		long timeSinceLastPlayed = currentTimeInMillis - lastPlayed;
		// Log.v(TAG, "timeSinceLastPlayed: " + timeSinceLastPlayed);
		// Log.v(TAG, "allowedTimeInt * 1000: " + allowedTimeInt * 1000);

		if (timeSinceLastPlayed > allowedTimeInt * 1000) {
			cardAllowed = true;
			newCard.setLastPlayed(currentTimeInMillis);
		} else {
			Date date = new Date(lastPlayed);
			Log.v(TAG,
					"Card " + newCard.getNameToFind() + " not allowed because already played on: " + sdf.format(date));
		}
		return cardAllowed;
	}

	/**
	 * Get random cards
	 * 
	 * @param availableCardList
	 * @param numberOfCards
	 * @return
	 */
	public Card getRandomCard(List<Card> availableCardList, List<Card> cardsInPlayList) {
		Card randomCard = null;

		// Get numberOfCards cards out of the initial list
		if (availableCardList != null && !availableCardList.isEmpty()
				&& !cardsInPlayList.containsAll(availableCardList)) {

			// Get current time in millis
			Calendar calendar = GregorianCalendar.getInstance();
			long currentTimeInMillis = calendar.getTimeInMillis();
			Integer allowedTimeInt = fetchAllowedTimeSinceLastPlayed();
			 
			int size = availableCardList.size();
			Random randomGenerator = new Random();
			while (randomCard == null) {
				int randomInt = randomGenerator.nextInt(size);
				Card newCard = availableCardList.get(randomInt);
				if (!cardsInPlayList.contains(newCard) && checkCardCanBePlayed(newCard, currentTimeInMillis, allowedTimeInt)) {
					randomCard = newCard;
				}
			}
		}

		return randomCard;

	}

	private List<Card> buildDebugCards(int i) {
		List<Card> cardList = new ArrayList<Card>();
		Card card = new Card(1, "Michael Jordan", "Sport");
		cardList.add(card);
		card = new Card(2, "Albert Einstein", "Science");
		cardList.add(card);
		if (i > 2) {
			card = new Card(3, "Homer Simpson", "Fiction");
			cardList.add(card);
			card = new Card(4, "Forrest Gump", "Movie");
			cardList.add(card);
			for (int k = 0; k < i - 2; k++) {
				Card newCard = new Card(k + 4, "Michael Jordan " + k, "");
				cardList.add(newCard);
			}
		}
		return cardList;
	}

}
