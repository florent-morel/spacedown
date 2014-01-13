package org.uptime.engine.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.uptime.engine.Constants;

public class Round {

	private Integer mRoundNumber;

	private Turn mCurrentTurn;

	private List<Turn> mSavedTurnList;

	private boolean mRoundActive = true;

	public boolean isRoundActive() {
		return mRoundActive;
	}

	public void setRoundActive(boolean roundActive) {
		this.mRoundActive = roundActive;
	}

	public Round(Integer mRoundNumber) {
		super();
		this.mRoundNumber = mRoundNumber;
		initTurns();
	}

	private void initTurns() {
		mSavedTurnList = new ArrayList<Turn>();
		createNewTurn();
	}

	public void createNewTurn() {
		if (this.isRoundActive()) {
			Integer turnNumber = 0;
			if (mCurrentTurn != null) {
				turnNumber = mCurrentTurn.getTurnNumber();
			}
			mCurrentTurn = new Turn(turnNumber + 1);
		} else {
			// Display message
		}
	}

	public Integer getRoundNumber() {
		return mRoundNumber;
	}

	public List<Turn> getSavedTurnList() {
		return mSavedTurnList;
	}

	public Turn getCurrentTurn() {
		return mCurrentTurn;
	}

	public void saveCurrentTurn() {
		mSavedTurnList.add(mCurrentTurn);
	}

	public Integer getTeamRoundScore(Team currentTeam) {
		Integer score = Constants.ZERO_VALUE;
		List<Turn> listTurns = this.getSavedTurnList();
		for (Turn turn : listTurns) {
			Map<Team, List<Card>> turnScoreMap = turn.getTeamTurnScore();
			List<Card> listCards = turnScoreMap.get(currentTeam);
			if (listCards != null) {
				score += listCards.size();
			}
		}

		// Include current turn of current round
		if (getCurrentTurn() != null) {
			score += getCurrentTurn().getTeamTurnScore(currentTeam);
		}

		return score;
	}

	public void setCurrentTurn(Turn lastTurn) {
		mCurrentTurn = lastTurn;
	}

}
