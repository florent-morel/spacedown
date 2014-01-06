package org.uptime.engine.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public Game(Integer numberOfTeams) {
		super();
		initTeams(numberOfTeams);
		initCards();
		initRounds();
	}

	private void initCards() {
		mCardList = new ArrayList<Card>();
		Card card = new Card(1, "Michael Jordan", "Sport");
		mCardList.add(card);
		 card = new Card(2, "Albert Einstein", "Science");
		 mCardList.add(card);
		 card = new Card(3, "Homer Simpson", "Fiction");
		 mCardList.add(card);
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
		// mCurrentRound = new Round(Constants.ROUND_FIRST);
		// mRoundList.add(mCurrentRound);
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

	public Card getNextCardToPlay(boolean skipCard, Integer index, Card currentCard) {
		Card cardToPlay = null;

		if (mCardsInPlay.isEmpty()) {
			endRound();
		} else if (Constants.ROUND_FIRST == mCurrentRound.getRoundNumber()
				|| Constants.ROUND_SECOND == mCurrentRound.getRoundNumber()
				|| Constants.ROUND_THIRD == mCurrentRound.getRoundNumber()) {
			if (skipCard || currentCard == null || currentCard.isFound()) {
				cardToPlay = this.computeNextCard(currentCard, mCardList.indexOf(currentCard) + 1);
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
			// TODO: pick random card for rounds 2 and 3.
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

	public void setNextTeamToPlay() {
		// for (int i = 0; i < mTeamList.size(); i++) {
		// // If end of list reached, start over
		// if (i == (mTeamList.size() - 1)) {
		// mCurrentTeam = mTeamList.get(0);
		// break;
		// } else if (mTeamList.get(i) == mCurrentTeam) {
		// mCurrentTeam = mTeamList.get(i + 1);
		// break;
		// }
		// }

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
		getNextCardToPlay(false, 0, mCurrentCard);
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
		this.getNextCardToPlay(false, 0, this.getCurrentCard());
	}

	public void cardSkip() {
		this.getNextCardToPlay(true, 0, this.getCurrentCard());
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
		Integer score = Constants.ZERO_VALUE;
		Map<Team, Integer> totalScoreMap = this.getTotalScoreMap();
		if (totalScoreMap.get(currentTeam) != null) {
			score = totalScoreMap.get(currentTeam);
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
