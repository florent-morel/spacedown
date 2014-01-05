package org.uptime.engine.game;

import java.util.ArrayList;
import java.util.List;

import org.uptime.activity.CardActivity;
import org.uptime.activity.ScoreActivity;
import org.uptime.engine.Constants;

import android.content.Intent;

public class Game {

	private List<Card> mCardList;

	private List<Card> mCardsInPlay;

	private List<Team> mTeamList;

	private Card mCurrentCard;

	private Team mCurrentTeam;

	private Round mCurrentRound;

	private List<Round> mRoundList;

	private boolean mIsGameOver = false;

	public Game() {
		super();
		initTeams();
		initCards();
		initRounds();
	}

	private void initCards() {
		mCardList = new ArrayList<Card>();
		Card card = new Card(1, "Michael Jordan", "Sport");
		mCardList.add(card);
//		card = new Card(2, "Albert Einstein", "Science");
//		mCardList.add(card);
//		card = new Card(3, "Homer Simpson", "Fiction");
//		mCardList.add(card);
		// card = new Card(4, "Forrest Gump", "Movie");
		// mCardList.add(card);

		refreshCards();
	}

	private void refreshCards() {

		for (Card card : mCardList) {
			card.setFound(false);
		}

		mCardsInPlay = new ArrayList<Card>();
		mCardsInPlay.addAll(mCardList);
	}

	private void initTeams() {
		mTeamList = new ArrayList<Team>();
		Team team = new Team(1, "A");
		mTeamList.add(team);
		team = new Team(2, "B");
		mTeamList.add(team);
		team = new Team(3, "C");
		mTeamList.add(team);
		team = new Team(4, "D");
		mTeamList.add(team);

	}

	private void initRounds() {
		mRoundList = new ArrayList<Round>();
//		mCurrentRound = new Round(Constants.ROUND_FIRST);
//		mRoundList.add(mCurrentRound);
	}

	public void startNextRound() {
		if (!this.isGameOver()) {
			Integer roundNumber = 0;
			if (mCurrentRound != null) {
				roundNumber = mCurrentRound.getRoundNumber();
			}
			mCurrentRound = new Round(roundNumber + 1);
			mRoundList.add(mCurrentRound);
			this.setNextTeamToPlay();
			this.refreshCards();
			this.getNextCardToPlay(false, 0, null);
		} else {
			// Display message
		}
	}

	public Card getNextCardToPlay(boolean skipCard, Integer index,
			Card currentCard) {
		Card cardToPlay = null;

		if (mCardsInPlay.isEmpty()) {
			setRoundOver();
		} else if (Constants.ROUND_FIRST == mCurrentRound.getRoundNumber()
				|| Constants.ROUND_SECOND == mCurrentRound.getRoundNumber()
				|| Constants.ROUND_THIRD == mCurrentRound.getRoundNumber()) {
			if (skipCard || currentCard == null || currentCard.isFound()) {
				cardToPlay = this.computeNextCard(currentCard,
						mCardList.indexOf(currentCard) + 1);
				//
				// Start with current index
				// int start = mCardList.indexOf(currentCard);
				// if (start == -1) {
				// start = 0;
				// }
				// for (int i = start; i < mCardList.size(); i++) {
				// // If end of list reached, start over
				// if (i == (mCardList.size() - 1)) {
				// index = 0;
				// break;
				// } else if (mCardList.get(i) == currentCard) {
				// index = i + 1;
				// break;
				// }
				// }
				// cardToPlay = mCardList.get(index);
			} else {
				cardToPlay = currentCard;
			}
			// } else if (Constants.ROUND_SECOND ==
			// mCurrentRound.getRoundNumber()
			// || Constants.ROUND_THIRD == mCurrentRound.getRoundNumber()) {
			// // Get random card
		}

		if (cardToPlay != null) {
			// No more card to play, round is over
			if (cardToPlay.isFound()) {
				// If already found, get a new one.
				cardToPlay = getNextCardToPlay(skipCard, index + 1, cardToPlay);
			} else {
				mCurrentCard = cardToPlay;
			}
		}

		return mCurrentCard;
	}

	private Card computeNextCard(Card currentCard, Integer nextId) {
		Card nextCard;
		int nbCards = this.getCardList().size();
		Card cards[] = new Card[nbCards];
		cards = this.getCardList().toArray(cards);
		if (currentCard == null) {
			nextId = Constants.ZERO_VALUE;
		}

		int modulo = nextId % nbCards;
		nextCard = cards[modulo];

		return nextCard;
	}

	public Team setNextTeamToPlay() {
		for (int i = 0; i < mTeamList.size(); i++) {
			// If end of list reached, start over
			if (i == (mTeamList.size() - 1)) {
				mCurrentTeam = mTeamList.get(0);
				break;
			} else if (mTeamList.get(i) == mCurrentTeam) {
				mCurrentTeam = mTeamList.get(i + 1);
				break;
			}
		}

		return mCurrentTeam;
	}

	public void setTurnOver() {
		setNextTeamToPlay();
		getNextCardToPlay(false, 0, mCurrentCard);
	}

	private void setRoundOver() {
		mCurrentCard = null;
		mCurrentRound.setRoundActive(false);
		if (Constants.ROUND_THIRD == mCurrentRound.getRoundNumber()) {
			this.setGameOver(true);
		}
	}

	public void addCardToTeamScore() {
		mCurrentCard.setFound(Boolean.TRUE);
		// Remove the card from the list of cards yet to be found
		mCardsInPlay.remove(mCurrentCard);
		List<Card> listFoundByTeam = mCurrentRound.getTeamScore().get(
				mCurrentTeam);

		if (listFoundByTeam == null || listFoundByTeam.isEmpty()) {
			listFoundByTeam = new ArrayList<Card>();
		}

		listFoundByTeam.add(mCurrentCard);

		mCurrentRound.getTeamScore().put(mCurrentTeam, listFoundByTeam);

	}

	public void setGameOver(boolean mIsGameOver) {
		this.mIsGameOver = mIsGameOver;
	}

	public boolean isGameOver() {
		return mIsGameOver;
	}

	public boolean isRoundActive() {
		return this.getCurrentRound().isRoundActive();
	}

	public List<Round> getRoundList() {
		return mRoundList;
	}

	public List<Card> getCardList() {
		return mCardList;
	}

	public Card getCurrentCard() {
		return mCurrentCard;
	}

	public Round getCurrentRound() {
		return mCurrentRound;
	}

	public Team getCurrentTeam() {
		if (mCurrentTeam == null) {
			setNextTeamToPlay();
		}
		return mCurrentTeam;
	}

	public void cardFound() {
		this.addCardToTeamScore();
		this.getNextCardToPlay(false, 0, this.getCurrentCard());
	}

	public void cardSkip() {
		this.getNextCardToPlay(true, 0, this.getCurrentCard());
	}

}
