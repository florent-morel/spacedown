package org.spacedown.engine.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Turn {

	private Integer mTurnNumber;

	private Set<Card> mListCards;

	private List<Card> mListFoundCards;

	private List<Card> mListSkippedCards;

	public Turn(Integer mTurnNumber) {
		super();
		this.mTurnNumber = mTurnNumber;
		mListCards = new HashSet<Card>();
		mListFoundCards = new ArrayList<Card>();
		mListSkippedCards = new ArrayList<Card>();
	}

	public List<Card> getTurnListSkippedCards() {
		return mListSkippedCards;
	}

	public Set<Card> getTurnListCards() {
		return mListCards;
	}

	public List<Card> getTurnListFoundCards() {
		return mListFoundCards;
	}

	public Integer getTurnNumber() {
		return mTurnNumber;
	}

	public void addCardToTurn(Card card) {
		mListCards.add(card);
	}

	public void addFoundCardToTurn(Card card) {
		mListFoundCards.add(card);
	}

	public void addSkippedCardToTurn(Card card) {
		mListSkippedCards.add(card);
	}

	public void removeCardFromTurn(Card card) {
		mListCards.remove(card);
	}

}
