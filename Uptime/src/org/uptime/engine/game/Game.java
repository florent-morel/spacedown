package org.uptime.engine.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uptime.database.CardsDataSource;
import org.uptime.engine.Constants;
import org.uptime.engine.cards.build.CardBuilder;

import android.content.Context;
import android.content.res.Resources;

public class Game {

	private CardBuilder cardBuilder;

	private List<Card> mAvailableCardListFromDB;

	private List<Card> mCardListForGame;

	private List<Card> mCardsCurrentlyInPlay;

	private List<Card> mDiscardedCardsInDB;

	private List<Team> mTeamList;

	private Card mCurrentCard;

	private Team mCurrentTeam;

	private Round mCurrentRound;

	private List<Round> mSavedRoundList;

	private boolean mIsGameOver = false;

	private CardsDataSource datasource;

	public Game() {
		super();
	}

	public Game(Constants.RunMode runMode, Integer numberOfTeams, Integer numberOfCards, Resources resources,
			Context context, CardsDataSource data) {
		super();

		datasource = data;
		
		cardBuilder = new CardBuilder(resources, context, datasource);

		mDiscardedCardsInDB = new ArrayList<Card>();

		initTeams(numberOfTeams);
		initCards(runMode, numberOfCards);
		
		initRounds();
	}

	private void initCards(Constants.RunMode runMode, Integer numberOfCards) {
		mAvailableCardListFromDB = cardBuilder.buildAvailableCards(runMode, numberOfCards);

		// Once we have the available cards
		mCardListForGame = cardBuilder.buildCardListForGame(mAvailableCardListFromDB, numberOfCards);

		refreshCards();
	}

	private void refreshCards() {
		for (Card card : mCardListForGame) {
			card.setFound(Boolean.FALSE);
		}
		mCardsCurrentlyInPlay = new ArrayList<Card>();
		mCardsCurrentlyInPlay.addAll(mCardListForGame);
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
		mCurrentRound = null;
		mSavedRoundList = new ArrayList<Round>();
	}

	public void startNextRound() {
		if (!this.isGameOver()) {
			Integer roundNumber = 0;
			if (mCurrentRound != null) {
				roundNumber = mCurrentRound.getRoundNumber();
			}
			mCurrentRound = new Round(roundNumber + 1);
			mSavedRoundList.add(mCurrentRound);
			mCurrentTeam = getNextTeamToPlay();
			this.refreshCards();
			reshuffleCardsInPlay();
			this.getNextCardToPlay(null, false);
		} else {
			// Display message
		}
	}

	public Card getNextCardToPlay(Card currentCard, boolean skipCard) {
		if (!mCardsCurrentlyInPlay.isEmpty()) {
			if (skipCard || currentCard == null || currentCard.isFound()) {
				mCurrentCard = this.computeNextCard(mCardsCurrentlyInPlay, currentCard,
						mCardsCurrentlyInPlay.indexOf(currentCard) + 1);
			} else {
				mCurrentCard = currentCard;
			}
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

		// Pick first card if no current card.
		if (currentCard == null) {
			nextId = Constants.VALUE_ZERO;
		}

		int modulo = nextId % nbCards;
		nextCard = cards[modulo];

		// Don't consider this card if it has already been found.
		if (nextCard.isFound()) {
			nextCard = computeNextCard(cardList, nextCard, nextId + 1);
		}
		return nextCard;
	}

	public Team getNextTeamToPlay() {
		Team nextTeam = null;

		int nbTeams = this.getTeamList().size();
		Team teams[] = new Team[nbTeams];
		teams = this.getTeamList().toArray(teams);
		Integer nextId = Constants.VALUE_ZERO;
		if (mCurrentTeam != null) {
			nextId = mCurrentTeam.getId();
		}

		int modulo = nextId % nbTeams;
		nextTeam = teams[modulo];
		return nextTeam;
	}

	public void endTurn() {
		skippedCardsBackInGame();

		mCurrentRound.saveCurrentTurn(mCurrentTeam);
		mCurrentRound.createNewTurn();
		mCurrentTeam = getNextTeamToPlay();
		reshuffleCardsInPlay();
		getNextCardToPlay(mCurrentCard, false);
	}

	/**
	 * Check if some cards have been skipped and put them back in the game.
	 */
	private void skippedCardsBackInGame() {
		List<Card> turnListSkippedCards = mCurrentRound.getCurrentTurn().getTurnListSkippedCards();
		if (!turnListSkippedCards.isEmpty()) {
			mCardsCurrentlyInPlay.addAll(turnListSkippedCards);
			turnListSkippedCards.clear();
		}
	}

	private void reshuffleCardsInPlay() {

		// For second and third round, shuffle remaining cards
		if (Constants.ROUND_SECOND == mCurrentRound.getRoundNumber()
				|| Constants.ROUND_THIRD == mCurrentRound.getRoundNumber()) {
			Collections.shuffle(mCardsCurrentlyInPlay);
			mCurrentCard = null;
		}
	}

	public void endRound() {
		mCurrentCard = null;
		mCurrentRound.saveCurrentTurn(mCurrentTeam);
		mCurrentRound.setRoundActive(Boolean.FALSE);
		
		if (Constants.ROUND_THIRD == mCurrentRound.getRoundNumber()) {
			// Update database with list of discarded cards
			if (mDiscardedCardsInDB != null && !mDiscardedCardsInDB.isEmpty()) {
				for (Card discardedCard : mDiscardedCardsInDB) {
					discardedCard.setActiveInDB(Boolean.FALSE);
					datasource.updateCard(discardedCard);
				}
				// Flush list of discarded cards
				mDiscardedCardsInDB.clear();
			}
		}
		
		if (Constants.ROUND_THIRD == mCurrentRound.getRoundNumber()) {
			this.setGameOver(Boolean.TRUE);

		}
	}

	private void addCardToTeamScore() {
		mCurrentCard.setFound(Boolean.TRUE);
		// Remove the card from the list of cards yet to be found
		mCardsCurrentlyInPlay.remove(this.getCurrentCard());

		// If no more cards in play, check if some cards have been skipped and
		// put them back in the game.
		if (mCardsCurrentlyInPlay.isEmpty()) {
			skippedCardsBackInGame();
		}

		mCurrentRound.getCurrentTurn().addFoundCardToTurn(mCurrentCard);
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

	public List<Round> getSavedRoundList() {
		return mSavedRoundList;
	}

	public List<Card> getCardListForGame() {
		return mCardListForGame;
	}

	public Card getCurrentCard() {
		return mCurrentCard;
	}

	public Round getCurrentRound() {
		return mCurrentRound;
	}

	public Team getCurrentTeam() {
		if (mCurrentTeam == null) {
			mCurrentTeam = getNextTeamToPlay();
		}
		return mCurrentTeam;
	}

	public void findCard() {
		this.addCardToTeamScore();
		this.getNextCardToPlay(this.getCurrentCard(), false);
	}

	/**
	 * Needs to remove card from team list. Put it back in game. Set current
	 * card to card just found.
	 */
	public Constants.CancelCardMode cancelFoundCard() {
		Constants.CancelCardMode cardRemovedMode = Constants.CancelCardMode.NO_FOUND_CARD;
		List<Card> listFoundByTeam = null;
		Turn currentTurn = mCurrentRound.getCurrentTurn();
		if (currentTurn != null) {
			listFoundByTeam = currentTurn.getTurnListFoundCards();
		}

		// Simple case: card is in current turn and turn is not finished
		cardRemovedMode = removeCardFromCurrentTurn(listFoundByTeam, mCardsCurrentlyInPlay);

		if (Constants.CancelCardMode.NO_FOUND_CARD.equals(cardRemovedMode)) {
			// Card not found in current turn, try in previous one
			cardRemovedMode = removeCardFromPreviousTurn(mCurrentRound, mCardsCurrentlyInPlay);
		}

		if (Constants.CancelCardMode.NO_FOUND_CARD.equals(cardRemovedMode)) {
			// Card not found in current round, try in previous one
			cardRemovedMode = removeCardFromPreviousRound();
		}

		return cardRemovedMode;
	}

	/**
	 * Method to remove the last found card.
	 * 
	 * @return true if the card has been removed and other variables are set
	 *         back to what they were.
	 */
	private Constants.CancelCardMode removeCardFromCurrentTurn(List<Card> listFoundByTeam, List<Card> cardsInPlay) {
		Constants.CancelCardMode cardRemovedMode = Constants.CancelCardMode.NO_FOUND_CARD;

		// From list of found cards for given team
		if (listFoundByTeam != null && !listFoundByTeam.isEmpty()) {
			// Get last found card
			Card lastFoundCard = listFoundByTeam.get(listFoundByTeam.size() - 1);

			// Remove the card from the list
			listFoundByTeam.remove(listFoundByTeam.size() - 1);

			// Set it as not found
			lastFoundCard.setFound(Boolean.FALSE);

			// Put it back at first place to cards in play
			cardsInPlay.add(Constants.VALUE_ZERO, lastFoundCard);

			// Needed in case of previous round
			mCardsCurrentlyInPlay = cardsInPlay;

			// Update list found cards for team: NOT NEEDED
			// teamTurnScore.put(mCurrentTeam, listFoundByTeam);

			// Set current card to last found card
			mCurrentCard = lastFoundCard;

			cardRemovedMode = Constants.CancelCardMode.CURRENT_TURN;
		}
		return cardRemovedMode;
	}

	/**
	 * Method to remove the last found card in case it was the last of a turn.
	 * 
	 * @param cardsInPlay
	 * 
	 * @return true if the card has been removed and other variables are set
	 *         back to what they were.
	 */
	private Constants.CancelCardMode removeCardFromPreviousTurn(Round round, List<Card> cardsInPlay) {
		Constants.CancelCardMode cardRemovedMode = Constants.CancelCardMode.NO_FOUND_CARD;

		// Get last team
		List<Team> teamList = getTeamList();
		int indexOfCurrentTeam = teamList.indexOf(mCurrentTeam);
		Team lastTeam = null;
		if (indexOfCurrentTeam == 0) {
			// If first team, take last one
			lastTeam = teamList.get(teamList.size() - 1);
		} else {
			lastTeam = teamList.get(indexOfCurrentTeam - 1);
		}

		// From list of turns in given round
		List<Turn> turnList = round.getSavedTurnMap().get(lastTeam);
		if (turnList != null && !turnList.isEmpty()) {
			// Take last turn cards
			Turn lastTurn = turnList.get(turnList.size() - 1);
			List<Card> listFoundByTeam = lastTurn.getTurnListFoundCards();

			// Try to remove last card for this case
			cardRemovedMode = removeCardFromCurrentTurn(listFoundByTeam, cardsInPlay);

			if (!Constants.CancelCardMode.NO_FOUND_CARD.equals(cardRemovedMode)) {
				// Set previous team as current team
				mCurrentTeam = lastTeam;

				// Remove current turn from saved turn
				round.removeTurnFromSavedMap(lastTeam, lastTurn);
				// getSavedTurnMap().remove(lastTurn);

				// Set previous turn as current turn in current round
				round.setCurrentTurn(lastTurn);

				cardRemovedMode = Constants.CancelCardMode.PREVIOUS_TURN;
			}
		}
		return cardRemovedMode;
	}

	/**
	 * Method to remove the last found card in case it was the last of a round.
	 * 
	 * @return true if the card has been removed and other variables are set
	 *         back to what they were.
	 */
	private Constants.CancelCardMode removeCardFromPreviousRound() {
		Constants.CancelCardMode cardRemovedMode = Constants.CancelCardMode.NO_FOUND_CARD;

		// No need to check previous round if we are at the first one
		if (mSavedRoundList != null && mSavedRoundList.size() > 1) {
			int indexOfCurrentRound = mSavedRoundList.indexOf(mCurrentRound);
			// Get last round
			Round lastRound = mSavedRoundList.get(indexOfCurrentRound - 1);

			// Try to remove last card for this case
			// Previous round => cards in play are empty at this stage
			cardRemovedMode = removeCardFromPreviousTurn(lastRound, new ArrayList<Card>());

			if (!Constants.CancelCardMode.NO_FOUND_CARD.equals(cardRemovedMode)) {
				// Remove current round from current game
				mSavedRoundList.remove(mCurrentRound);

				// Set back previous round as active
				lastRound.setRoundActive(Boolean.TRUE);

				// Set previous round as current round
				mCurrentRound = lastRound;

				// In case game was over, fix it.
				this.setGameOver(Boolean.FALSE);

				cardRemovedMode = Constants.CancelCardMode.PREVIOUS_ROUND;
			}
		}
		return cardRemovedMode;
	}

	public void skipCard() {
		// Remove current card from list of cards in play and put it in the list
		// of skipped cards
		mCardsCurrentlyInPlay.remove(this.getCurrentCard());
		this.getCurrentRound().getCurrentTurn().addSkippedCardToTurn(this.getCurrentCard());

		// If no more cards in play, check if some cards have been skipped and
		// put them back in the game.
		if (mCardsCurrentlyInPlay.isEmpty()) {
			skippedCardsBackInGame();
		}

		this.getNextCardToPlay(this.getCurrentCard(), true);
	}

	public List<Team> getTeamList() {
		return mTeamList;
	}

	public void addTeamToList(Team team) {
		this.mTeamList.add(team);
	}

	public List<Card> getCardsInPlay() {
		return mCardsCurrentlyInPlay;
	}

	public Map<Team, Integer> getTotalScoreMap() {
		Map<Team, Integer> teamScoreTotal = new HashMap<Team, Integer>();
		List<Round> roundList = this.getSavedRoundList();

		if (roundList == null || roundList.isEmpty()) {
			for (Team team : this.getTeamList()) {
				teamScoreTotal.put(team, Constants.VALUE_ZERO);
			}
		} else {
			for (Team team : this.getTeamList()) {
				Integer totalScore = Constants.VALUE_ZERO;
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
			score = Constants.VALUE_ZERO;
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

	public void replayGame() {
		setGameOver(false);
		refreshCards();
		initRounds();
	}

	public boolean replaceCard() {
		boolean cardReplaced = false;
		// Pick a new one from available deck
		Card newCard = cardBuilder.getRandomCard(mAvailableCardListFromDB, mCardListForGame);
		if (newCard != null) {
			// Remove current card from list of cards in play and cards for game
			mCardListForGame.remove(mCurrentCard);
			mCardsCurrentlyInPlay.remove(mCurrentCard);
			// Add the discarded card to the appropriate list for further
			// process
			mDiscardedCardsInDB.add(mCurrentCard);

			// Add new card to lists of cards
			mCardListForGame.add(newCard);
			mCardsCurrentlyInPlay.add(newCard);

			// Get next card to play
			this.getNextCardToPlay(null, false);

			cardReplaced = true;
		}

		return cardReplaced;
	}

	/**
	 * Get the number of cards currently in play (including skipped cards for
	 * this turn).
	 * 
	 * @return
	 */
	public Integer getNumberCardsInPlay() {
		Integer numberCards = Constants.VALUE_ZERO;

		numberCards += mCardsCurrentlyInPlay.size();

		if (mCurrentRound != null) {
			Turn currentTurn = mCurrentRound.getCurrentTurn();
			if (currentTurn != null) {
				List<Card> turnListSkippedCards = currentTurn.getTurnListSkippedCards();
				if (turnListSkippedCards != null && !turnListSkippedCards.isEmpty()) {
					numberCards += turnListSkippedCards.size();
				}
			}
		}

		return numberCards;
	}
}
