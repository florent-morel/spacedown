package org.uptime.engine.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uptime.cards.build.CardBuilder;
import org.uptime.engine.Constants;

public class Game {

	private List<Card> mCardList;

	private List<Card> mCardsInPlay;

	private List<Team> mTeamList;

	private Card mCurrentCard;

	private Team mCurrentTeam;

	private Round mCurrentRound;

	private List<Round> mRoundList;

	private boolean mIsGameOver = false;

	public Game(Integer numberOfTeams, List<Card> listCards) {
		super();
		initTeams(numberOfTeams);
		initCards(listCards);
		initRounds();
	}

	private void initCards(List<Card> listCards) {
		if (listCards == null) {
			mCardList = CardBuilder.buildCards(Constants.RunMode.DEBUG);
		} else {
			mCardList = listCards;
		}
		refreshCards();
	}

	private void refreshCards() {
		for (Card card : mCardList) {
			card.setFound(false);
		}
		mCardsInPlay = new ArrayList<Card>();
		mCardsInPlay.addAll(mCardList);
	}

	private void initTeams(Integer numberOfTeams) {
		mTeamList = new ArrayList<Team>();
		if (numberOfTeams != null) {
			for (int i = 0; i < numberOfTeams; i++) {
				Team team = new Team(i + 1, Character.toString((char) (i + 65)));
				mTeamList.add(team);
			}
		} else {
			Team team = new Team(1, "AA");
			mTeamList.add(team);
			team = new Team(2, "BB");
			mTeamList.add(team);
			team = new Team(3, "CC");
			mTeamList.add(team);
			team = new Team(4, "DD");
			mTeamList.add(team);
		}
	}

	private void initRounds() {
		mRoundList = new ArrayList<Round>();
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
			reshuffleCardsInPlay();
			this.getNextCardToPlay(null, false);
		} else {
			// Display message
		}
	}

	public Card getNextCardToPlay(Card currentCard, boolean skipCard) {
		if (mCardsInPlay.isEmpty()) {
			endRound();
		} else {
			if (skipCard || currentCard == null || currentCard.isFound()) {
				mCurrentCard = this.computeNextCard(mCardsInPlay, currentCard, mCardsInPlay.indexOf(currentCard) + 1);
			} else {
				mCurrentCard = currentCard;
			}
//			if (Constants.ROUND_FIRST == mCurrentRound.getRoundNumber()) {
//				if (currentCard == null || currentCard.isFound()) {
//					mCurrentCard = this.computeNextCard(mCardList, currentCard, mCardList.indexOf(currentCard) + 1);
//				} else {
//					mCurrentCard = currentCard;
//				}
//			} else if (Constants.ROUND_SECOND == mCurrentRound.getRoundNumber()
//					|| Constants.ROUND_THIRD == mCurrentRound.getRoundNumber()) {
//				// Get random card
//				mCurrentCard = computeNextRandomCard();
//			}
		}

		return mCurrentCard;
	}

	/**
	 * Get the next card by incrementing index.
	 * 
	 * @param cardList
	 * @param currentCard
	 * @param nextId
	 * 
	 * @return
	 */
	private Card computeNextCard(List<Card> cardList, Card currentCard, Integer nextId) {
		Card nextCard;
		int nbCards = cardList.size();
		Card cards[] = new Card[nbCards];
		cards = cardList.toArray(cards);
		if (currentCard == null) {
			nextId = Constants.ZERO_VALUE;
		}

		int modulo = nextId % nbCards;
		nextCard = cards[modulo];

		// Don't consider this card if it has already been found.
		if (nextCard.isFound()) {
			nextCard = computeNextCard(cardList, nextCard, nextId + 1);
		}
		return nextCard;
	}

	/**
	 * Get a random next card.
	 * 
	 * @param initialList
	 * @return
	 */
//	private Card computeNextRandomCard() {
//		Card nextCard = null;
//		List<Card> listCardForCurrentTurn = mCurrentRound.getCurrentTurn().getListCardForCurrentTurn();
//		if (mCardsInPlay != null && !mCardsInPlay.isEmpty()) {
//			int size = mCardsInPlay.size();
//
//			Random randomGenerator = new Random();
//
//			for (int i = 0; i < size; i++) {
//				int randomInt = randomGenerator.nextInt(size);
//				// Get a random card from remaining list
//				nextCard = mCardsInPlay.get(randomInt);
//				if (!listCardForCurrentTurn.contains(nextCard)) {
//					listCardForCurrentTurn.add(nextCard);
//				} else {
//					// If already proposed in current turn, get another one.
//
//				}
//			}
//
//		} else {
//			// If no more cards are available go back to first one in current turn
//			nextCard = computeNextCard(listCardForCurrentTurn, mCurrentCard,
//					listCardForCurrentTurn.indexOf(mCurrentCard) + 1);
//		}
//
//		return nextCard;
//
//	}

	public void setNextTeamToPlay() {
		int nbTeams = this.getTeamList().size();
		Team teams[] = new Team[nbTeams];
		teams = this.getTeamList().toArray(teams);
		Integer nextId = Constants.ZERO_VALUE;
		if (mCurrentTeam != null) {
			nextId = mCurrentTeam.getId();
		}

		int modulo = nextId % nbTeams;
		mCurrentTeam = teams[modulo];
	}

	public void endTurn() {
		mCurrentRound.createNewTurn();
		setNextTeamToPlay();
		reshuffleCardsInPlay();
		getNextCardToPlay(mCurrentCard, false);
	}

	private void reshuffleCardsInPlay() {
		// For second and third round, shuffle remaining cards
		if (Constants.ROUND_SECOND == mCurrentRound.getRoundNumber()
				|| Constants.ROUND_THIRD == mCurrentRound.getRoundNumber()) {
		    Collections.shuffle(mCardsInPlay);
		    mCurrentCard = null;
		}
	}

	private void endRound() {
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
		List<Card> listFoundByTeam = mCurrentRound.getCurrentTurn().getTeamTurnScore().get(mCurrentTeam);

		if (listFoundByTeam == null || listFoundByTeam.isEmpty()) {
			listFoundByTeam = new ArrayList<Card>();
		}

		listFoundByTeam.add(mCurrentCard);

		mCurrentRound.getCurrentTurn().getTeamTurnScore().put(mCurrentTeam, listFoundByTeam);
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
		this.getNextCardToPlay(this.getCurrentCard(), false);
	}

	public void cardSkip() {
		this.getNextCardToPlay(this.getCurrentCard(), true);
	}

	public List<Team> getTeamList() {
		return mTeamList;
	}

	public void addTeamToList(Team team) {
		this.mTeamList.add(team);
	}

	public Map<Team, Integer> getTotalScoreMap() {
		Map<Team, Integer> teamScoreTotal = new HashMap<Team, Integer>();
		List<Round> roundList = this.getRoundList();

		if (roundList == null || roundList.isEmpty()) {
			for (Team team : this.getTeamList()) {
				teamScoreTotal.put(team, Constants.ZERO_VALUE);
			}
		} else {
			for (Team team : this.getTeamList()) {
				Integer totalScore = Constants.ZERO_VALUE;
				for (Round round : roundList) {
					Integer teamRoundScore = round.getTeamRoundScore(team);
					if (teamRoundScore != null) {
						totalScore += teamRoundScore;
					}
				}
				teamScoreTotal.put(team, totalScore);
			}
		}
		return teamScoreTotal;
	}

	public Integer getTotalScore(Team currentTeam) {
		Integer score = null;
		Map<Team, Integer> totalScoreMap = this.getTotalScoreMap();
		if (totalScoreMap.get(currentTeam) != null) {
			score = totalScoreMap.get(currentTeam);
		}
		
		if (score == null) {
			score = Constants.ZERO_VALUE;
		}
		
		return score;
	}

	public Team getTeam(Integer teamId) {
		Team teamToReturn = null;
		for (Team team : getTeamList()) {
			if (team.getId().equals(teamId)) {
				teamToReturn = team;
				break;
			}
		}
		return teamToReturn;
	}
}
