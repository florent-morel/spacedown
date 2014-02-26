package org.spacedown.engine.cards.build;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.spacedown.engine.game.Card;

import au.com.bytecode.opencsv.CSVReader;

public class ImportCards {

	/**
	 * @param args
	 */
	public static List<Card> buildCardsFromCSV(FileReader fileReader) {

		CSVReader reader;
		List<String[]> allElements;
		List<Card> listCards= new ArrayList<Card>();
		try {

			reader = new CSVReader(fileReader);
			allElements = reader.readAll();

			if (allElements != null && !allElements.isEmpty()) {
				int i = 0;
				for (String[] strings : allElements) {
					if (strings != null && strings.length == 1) {
						Card newCard = new Card(i, strings[0], "");
						listCards.add(newCard);
						i++;
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listCards;
	}

}
