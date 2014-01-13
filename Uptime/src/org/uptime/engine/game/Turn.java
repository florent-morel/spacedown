package org.uptime.engine.game;

import java.util.ArrayList;
import java.util.List;

public class Turn {

	private Integer mTurnNumber;
	
	private List<Card> mListCards;

	public Turn(Integer mTurnNumber) {
		super();
		this.mTurnNumber = mTurnNumber;
		mListCards = new ArrayList<Card>();
	}
	
	public List<Card> getTurnListCards() {
		return mListCards;
	}

	public Integer getTurnNumber() {
		return mTurnNumber;
	}
	
	public void addCardToTurn(Card card) {
		mListCards.add(card);
	}
	
}
