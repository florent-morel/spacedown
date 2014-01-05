package org.uptime.engine.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Round {

	private Integer mRoundNumber;

	private Map<Team, List<Card>> mTeamScore;

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
		mTeamScore = new HashMap<Team, List<Card>>();
	}

	// public Map<Integer, List<Integer>> getPlayerScoreMap() {
	// return mPlayerScoreMap;
	// }

	public Integer getRoundNumber() {
		return mRoundNumber;
	}

	public Map<Team, List<Card>> getTeamScore() {
		return mTeamScore;
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
