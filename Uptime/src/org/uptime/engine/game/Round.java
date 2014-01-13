package org.uptime.engine.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uptime.engine.Constants;

public class Round {

	private Integer mRoundNumber;

	private Turn mCurrentTurn;

	private Map<Team, List<Turn>> mSavedTurnMap;

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
		mSavedTurnMap = new HashMap<Team, List<Turn>>();
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

	public Map<Team, List<Turn>> getSavedTurnMap() {
		return mSavedTurnMap;
	}

	public Turn getCurrentTurn() {
		return mCurrentTurn;
	}

	public void saveCurrentTurn(Team currentTeam) {
		List<Turn> list = mSavedTurnMap.get(currentTeam);
		if (list == null) {
			list = new ArrayList<Turn>();
			mSavedTurnMap.put(currentTeam, list);
		}
		list.add(getCurrentTurn());
		
		mCurrentTurn = null;
	}

	public Integer getTeamRoundScore(Team currentTeam) {
		Integer score = Constants.ZERO_VALUE;
		List<Turn> listTurns = mSavedTurnMap.get(currentTeam);
		if (listTurns != null) {
			for (Turn turn : listTurns) {
				List<Card> listCards = turn.getTurnListCards();
				if (listCards != null) {
					score += listCards.size();
				}
			}
		}

		// Include current turn of current round
		score += getTeamTurnScore(getCurrentTurn());

		return score;
	}

	public Integer getTeamTurnScore(Turn turn) {
		Integer score = Constants.ZERO_VALUE;

		// Include current turn of current round
		if (turn != null) {
			score += turn.getTurnListCards().size();
		}

		return score;
	}

	public void setCurrentTurn(Turn lastTurn) {
		mCurrentTurn = lastTurn;
	}

}
