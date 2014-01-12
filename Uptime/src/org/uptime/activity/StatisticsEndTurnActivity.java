package org.uptime.activity;

import java.util.List;
import java.util.Map;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.adapter.TurnCardAdapter;
import org.uptime.engine.game.Card;
import org.uptime.engine.game.Game;
import org.uptime.engine.game.Round;
import org.uptime.engine.game.Team;
import org.uptime.engine.game.Turn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class StatisticsEndTurnActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Game game;

	private Resources mResources;

	private ListView mStatsCardsEndTurnList;

	private TurnCardAdapter mTurnCardAdapter;

	private Button mButtonNextTurn;

	private TextView mNoCardFound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats_end_turn);
		mResources = getResources();
		mGameManager = GameManager.getSingletonObject();
		game = mGameManager.getGame();

		refreshList();
		initButtons();
		initTexts();

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

	private void initTexts() {
		mNoCardFound = (TextView) findViewById(R.id.textCardCurrentTurn);	

		if (mTurnCardAdapter != null && !mTurnCardAdapter.isEmpty()) {
			mNoCardFound.setVisibility(View.GONE);
		}	
	}

	private void initButtons() {
		mButtonNextTurn = (Button) findViewById(R.id.buttonNextTurn);
		mButtonNextTurn.setOnClickListener(this);
		
		if (game.getCardsInPlay().isEmpty()) {
			mButtonNextTurn.setText(String.format(mResources.getString(R.string.stats_end_round), game.getCurrentRound().getRoundNumber()));
		} else {
			mButtonNextTurn.setText(String.format(mResources.getString(R.string.stats_next_turn), game.getNextTeamToPlay().getName()));
		}
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
	
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(String.format(mResources.getString(R.string.stats_end_round_question), game.getCurrentRound().getRoundNumber()))
		.setCancelable(false)
		.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
			finish();
		}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
		dialog.cancel();
		}
		});
		AlertDialog alert = builder.create();
		alert.show();
		}
		

	@Override
	public void onClick(View v) {
		if (v.getId() == mButtonNextTurn.getId()) {
			// Finish activity
			finish();
		}
	}

}
