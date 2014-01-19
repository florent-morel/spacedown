package org.uptime.activity.stats;

import java.util.List;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class StatisticsActivity extends Activity {

	private static GameManager mGameManager;

	private Game game;

	private Resources mResources;

	private ListView mStatsCardsList1;

	private ListView mStatsCardsList2;

	private ListView mStatsCardsList3;

	private TextView mStatsCardsRound1;

	private TextView mStatsCardsRound2;

	private TextView mStatsCardsRound3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);
		mResources = getResources();
		mGameManager = GameManager.getSingletonObject();
		game = mGameManager.getGame();

		mStatsCardsRound1 = (TextView) findViewById(R.id.statsRound1);
		mStatsCardsRound2 = (TextView) findViewById(R.id.statsRound2);
		mStatsCardsRound3 = (TextView) findViewById(R.id.statsRound3);
		mStatsCardsRound2.setVisibility(View.GONE);
		mStatsCardsRound3.setVisibility(View.GONE);
		refreshList();

		if (mStatsCardsList1 != null) {
			mStatsCardsList1.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (mGameManager.getGame().isGameOver()) {
						// Prevent from creating next turn
						// StringBuilder message = new StringBuilder();
						// buildGameOverMessage(null, null, message);
						// // Create the dialog
						// createAlert(message.toString());
					} else {
						// playNextTurn();
					}
				}
			});
		}

	}

	/**
	 * Refresh the score board player list.
	 * 
	 * @param descending
	 */
	private void refreshList() {
		Team team = game.getTeam(this.getIntent().getIntExtra(Constants.STATS_TEAM, 0));

		setTitle(String.format(mResources.getString(R.string.stats_title), team.getName()));

		buildListCards(team);

	}

	private void buildListCards(Team team) {
		if (team != null) {
			List<Round> roundList = game.getSavedRoundList();
			if (!roundList.isEmpty()) {
				// build first round
				mStatsCardsList1 = (ListView) findViewById(R.id.statsCardList1);
				Round round = roundList.get(0);
				TurnCardAdapter cardAdapter = buildCardAdapter(team, round);
				mStatsCardsList1.setAdapter(cardAdapter);
				registerForContextMenu(mStatsCardsList1);
				mStatsCardsRound1.setText(String.format(mResources.getString(R.string.stats_round),
						round.getRoundNumber(), cardAdapter.getCount()));
				if (roundList.size() > 1) {
					// build second round
					mStatsCardsList2 = (ListView) findViewById(R.id.statsCardList2);
					round = roundList.get(1);
					cardAdapter = buildCardAdapter(team, round);
					mStatsCardsList2.setAdapter(cardAdapter);
					registerForContextMenu(mStatsCardsList2);
					mStatsCardsRound2.setText(String.format(mResources.getString(R.string.stats_round),
							round.getRoundNumber(), cardAdapter.getCount()));
					mStatsCardsRound2.setVisibility(View.VISIBLE);
				}
				if (roundList.size() > 2) {
					// build third round
					mStatsCardsList3 = (ListView) findViewById(R.id.statsCardList3);
					round = roundList.get(2);
					cardAdapter = buildCardAdapter(team, round);
					mStatsCardsList3.setAdapter(cardAdapter);
					registerForContextMenu(mStatsCardsList3);
					mStatsCardsRound3.setText(String.format(mResources.getString(R.string.stats_round),
							round.getRoundNumber(), cardAdapter.getCount()));
					mStatsCardsRound3.setVisibility(View.VISIBLE);
				}
			} else {
				mStatsCardsRound1.setText(mResources.getString(R.string.stats_card_current_turn));
			}
		}
	}

	private TurnCardAdapter buildCardAdapter(Team team, Round round) {

		TurnCardAdapter turnCardAdapter = new TurnCardAdapter(this, R.layout.layout_stats_card_row);

		List<Turn> turnList = round.getSavedTurnMap().get(team);
		if (turnList != null) {
			for (Turn turn : turnList) {
				List<Card> listCards = turn.getTurnListFoundCards();
				if (listCards != null && !listCards.isEmpty()) {
					for (Card card : listCards) {
						turnCardAdapter.addItem(card);
					}
				}
			}
		}
		return turnCardAdapter;
	}
}
