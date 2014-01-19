package org.uptime.engine.game;

import java.util.ArrayList;
import java.util.List;

public class Turn {

	private Integer mTurnNumber;
	
	private List<Card> mListFoundCards;
	
	private List<Card> mListSkippedCards;

	public Turn(Integer mTurnNumber) {
		super();
		this.mTurnNumber = mTurnNumber;
		mListFoundCards = new ArrayList<Card>();
		mListSkippedCards = new ArrayList<Card>();
	}
	
	public List<Card> getTurnListSkippedCards() {
		return mListSkippedCards;
	}
	
	public List<Card> getTurnListFoundCards() {
		return mListFoundCards;
	}

	public Integer getTurnNumber() {
		return mTurnNumber;
	}
	
	public void addFoundCardToTurn(Card card) {
		mListFoundCards.add(card);
	}
	
	public void addSkippedCardToTurn(Card card) {
		mListSkippedCards.add(card);
	}
	
}
