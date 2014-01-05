package org.uptime.engine.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uptime.engine.Constants;

public class Round {

	private Integer mRoundNumber;

	private Turn mCurrentTurn;
	
	private List<Turn> mTurnList;

	private boolean mRoundActive = true;

	// private Map<Integer, Turn> mPlayerTurnMap;

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
		mTurnList = new ArrayList<Turn>();
		createNewTurn();
	}

	public void createNewTurn() {
		if (this.isRoundActive()) {
			Integer turnNumber = 0;
			if (mCurrentTurn != null) {
				turnNumber = mCurrentTurn.getTurnNumber();
			}
			mCurrentTurn = new Turn(turnNumber + 1);
			mTurnList.add(mCurrentTurn);
		} else {
			// Display message
		}
	}


	// public Map<Integer, List<Integer>> getPlayerScoreMap() {
	// return mPlayerScoreMap;
	// }

	public Integer getRoundNumber() {
		return mRoundNumber;
	}

	public List<Turn> getTurnList() {
		return mTurnList;
	}

	public Turn getCurrentTurn() {
		return mCurrentTurn;
	}

	public Integer getTeamRoundScore(Team currentTeam) {
		Integer score = Constants.ZERO_VALUE;
		List<Turn> listTurns = this.getTurnList();
		for (Turn turn : listTurns) {
			Map<Team, List<Card>> turnScoreMap = turn.getTeamTurnScore();
			List<Card> listCards = turnScoreMap.get(currentTeam);
			if (listCards != null) {
				score += listCards.size();
			}
		}
		
		return score;
	}

	// public Map<Integer, Turn> getPlayerTurnMap() {
	// return mPlayerTurnMap;
	// }
	//
	// public void addTurnToPlayerMap(Integer playerId, Turn turn) {
	// this.mPlayerTurnMap.put(playerId, turn);
	// }

	// public boolean atLeastOneScore() {
	// return mAtLeastOneScore;
	// }
	//
	// public void setAtLeastOneScore(boolean atLeastOneScore) {
	// this.mAtLeastOneScore = atLeastOneScore;
	// }

}
