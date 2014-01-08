package org.uptime.engine.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uptime.engine.Constants;

public class Turn {

	private Integer mTurnNumber;
	
	private Map<Team, List<Card>> mTeamRunScore;
	
	private List<Card> listCardForCurrentTurn;


	public Turn(Integer mTurnNumber) {
		super();
		this.mTurnNumber = mTurnNumber;
		List<Card> listCardForCurrentTurn = new ArrayList<Card>();
		mTeamRunScore = new HashMap<Team, List<Card>>();
	}
	
	public Map<Team, List<Card>> getTeamTurnScore() {
		return mTeamRunScore;
	}

	public Integer getTurnNumber() {
		return mTurnNumber;
	}

	public Integer getTeamTurnScore(Team currentTeam) {
		Integer score = Constants.ZERO_VALUE;
		List<Card> listCards = getTeamTurnScore().get(currentTeam);
		if (listCards != null) {
			score = listCards.size();
		}

		return score;
	}

	public List<Card> getListCardForCurrentTurn() {
		return listCardForCurrentTurn;
	}

	public void addCardToCurrentTurn(Card card) {
		listCardForCurrentTurn.add(card);
	}
	
	
	
}
