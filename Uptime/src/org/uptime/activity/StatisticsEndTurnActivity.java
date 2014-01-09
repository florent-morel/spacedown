package org.uptime.activity;

import java.util.List;
import java.util.Map;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.adapter.TurnCardAdapter;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Card;
import org.uptime.engine.game.Game;
import org.uptime.engine.game.Round;
import org.uptime.engine.game.Team;
import org.uptime.engine.game.Turn;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ListView;

public class StatisticsEndTurnActivity extends Activity {

	private static GameManager mGameManager;

	private Game game;

	private Resources mResources;

	private ListView mStatsCardsEndTurnList;

	private TurnCardAdapter mTurnCardAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats_end_turn);
		mResources = getResources();
		mGameManager = GameManager.getSingletonObject();
		game = mGameManager.getGame();

		refreshList();

		// mStatsCardsList.setOnItemClickListener(new OnItemClickListener() {
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// if (mGameManager.getGame().isGameOver()) {
		// // Prevent from creating next turn
		// // StringBuilder message = new StringBuilder();
		// // buildGameOverMessage(null, null, message);
		// // // Create the dialog
		// // createAlert(message.toString());
		// } else {
		// // playNextTurn();
		// }
		// }
		// });

	}

	/**
	 * Refresh the score board player list.
	 * 
	 * @param descending
	 */
	private void refreshList() {
		Team team = game.getCurrentTeam();

		setTitle(String.format(mResources.getString(R.string.stats_title), team.getName()));

		mStatsCardsEndTurnList = (ListView) findViewById(R.id.statsCardEndTurnList);
		mTurnCardAdapter = new TurnCardAdapter(this, R.layout.layout_stats_card_row);

		if (team != null) {
			Round round = game.getCurrentRound();
			Turn turn = round.getCurrentTurn();
			Map<Team, List<Card>> teamTurnScore = turn.getTeamTurnScore();
			if (teamTurnScore != null && !teamTurnScore.isEmpty()) {
				List<Card> listCards = teamTurnScore.get(team);
				if (listCards != null && !listCards.isEmpty()) {
					for (Card card : listCards) {
						mTurnCardAdapter.addItem(card);
					}
				}
			}
		}

		mStatsCardsEndTurnList.setAdapter(mTurnCardAdapter);

		registerForContextMenu(mStatsCardsEndTurnList);
	}

	@Override
	protected void onPause() {
		super.onPause();
		setResult(Constants.TURN_STATS_DONE);
	}

}
