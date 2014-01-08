package org.uptime.cards.build;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uptime.engine.Constants;
import org.uptime.engine.game.Card;

public class CardBuilder {

	public static List<Card> buildCards(Constants.RunMode runMode) {
		List<Card> cardList = new ArrayList<Card>();

		if (Constants.RunMode.STANDARD.equals(runMode)) {
//			cardList = ImportCards.buildCardsFromCSV();
		} else if (Constants.RunMode.DEBUG.equals(runMode)) {
			cardList = buildDebugCards();
		}

		return cardList;

	}

	public static List<Card> getRandomCards(List<Card> initialList) {
		List<Card> randomList = new ArrayList<Card>();

		// Get 40 cards out of the initial list
		if (initialList != null && !initialList.isEmpty()) {
			int size = initialList.size();

		    Random randomGenerator = new Random();
		    int idx = 0;
		    List<Integer> randomIndexList = new ArrayList<Integer>();
		    while (idx < Constants.NUMBER_OF_CARDS) {
		      int randomInt = randomGenerator.nextInt(size);
		      if (!randomIndexList.contains(randomInt)) {
		    	  randomIndexList.add(randomInt);
		    	  idx++;
		    	  Card newCard = initialList.get(randomInt);
		    	  randomList.add(newCard);
		      }
		    }
			
		}
		
		return randomList;

	}

	private static List<Card> buildDebugCards() {
		List<Card> cardList = new ArrayList<Card>();
		Card card = new Card(1, "Michael Jordan", "Sport");
		cardList.add(card);
		card = new Card(2, "Albert Einstein", "Science");
		cardList.add(card);
		card = new Card(3, "Homer Simpson", "Fiction");
		cardList.add(card);
		card = new Card(4, "Forrest Gump", "Movie");
		cardList.add(card);
		return cardList;
	}

}
