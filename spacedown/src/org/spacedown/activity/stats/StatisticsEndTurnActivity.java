package org.spacedown.activity.stats;

import java.util.List;
import java.util.Set;

import org.spacedown.GameManager;
import org.spacedown.R;
import org.spacedown.adapter.TurnCardAdapter;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Card;
import org.spacedown.engine.game.Game;
import org.spacedown.engine.game.Round;
import org.spacedown.engine.game.Team;
import org.spacedown.engine.game.Turn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

		mStatsCardsEndTurnList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Card card = (Card) parent.getItemAtPosition(position);
				
				Turn currentTurn = game.getCurrentRound().getCurrentTurn();
				if (card.isFound()) {
					game.removeFoundCard(card, currentTurn);
				} else {
					// If not found, it might come from the skipped list, remove it
					List<Card> turnListSkippedCards = currentTurn.getTurnListSkippedCards();
					if (turnListSkippedCards != null && !turnListSkippedCards.isEmpty()
							&& turnListSkippedCards.contains(card)) {
						currentTurn.removeCardFromSkipped(card);
					}
					
					game.findCard(card, currentTurn, false);
				}
				
				initButtons();
				refreshList();
			}
		});

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

		if (game.getNumberCardsInPlay() == Constants.VALUE_ZERO) {
			mButtonNextTurn.setText(String.format(mResources.getString(R.string.stats_end_round), game
					.getCurrentRound().getRoundNumber()));
		} else {
			mButtonNextTurn.setText(String.format(mResources.getString(R.string.stats_next_turn), game
					.getNextTeamToPlay().getName()));
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
		mTurnCardAdapter = new TurnCardAdapter(this, R.layout.layout_stats_card_row, true);

		if (team != null) {
			Round round = game.getCurrentRound();
			Turn turn = round.getCurrentTurn();
			Set<Card> listCards = turn.getTurnListCards();
			if (listCards != null && !listCards.isEmpty()) {
				for (Card card : listCards) {
					mTurnCardAdapter.addItem(card);
				}
			}
		}

		mStatsCardsEndTurnList.setAdapter(mTurnCardAdapter);

		registerForContextMenu(mStatsCardsEndTurnList);
	}

	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (game.getNumberCardsInPlay() == Constants.VALUE_ZERO) {
			builder.setMessage(String.format(mResources.getString(R.string.stats_end_round_question), game
					.getCurrentRound().getRoundNumber()));
		} else {
			builder.setMessage(String.format(mResources.getString(R.string.stats_next_turn_question), game
					.getNextTeamToPlay().getName()));
		}
		builder.setCancelable(false)
				.setPositiveButton(String.format(mResources.getString(R.string.dialog_confirm_yes)),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						})
				.setNegativeButton(String.format(mResources.getString(R.string.dialog_confirm_no)),
						new DialogInterface.OnClickListener() {
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
