package org.spacedown.activity.stats;

import java.util.List;

import org.spacedown.GameManager;
import org.spacedown.R;
import org.spacedown.activity.CardActivity;
import org.spacedown.activity.create.CreateGameActivity;
import org.spacedown.adapter.ScoreTeamAdapter;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Game;
import org.spacedown.engine.game.Round;
import org.spacedown.engine.game.Team;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ScoreActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Resources mResources;

	private Game mGame;

	private TextView mScoreTeam;
	private TextView mRemainingCards;

	private ListView mScoreTeamList;

	private GridView gridView;

	private ScoreTeamAdapter mScoreTeamAdapter;

	private Button mButtonNextRound;
	// private Button mButtonNewGame;
	// private Button mButtonReplayGame;

	TableLayout scoreTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		mResources = getResources();

		mGameManager = GameManager.getSingletonObject();
		mGame = mGameManager.getGame();

		if (mGame.getCardListForGame() == null || mGame.getCardListForGame().isEmpty()) {
			// No cards available for the game, display message and exit
			Toast toast = Toast.makeText(this, String.format(mResources.getString(R.string.create_game_ko)),
					Toast.LENGTH_LONG);
			toast.show();
			finish();
		}

		this.refreshActivity();

		mScoreTeamList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Intent intent = new Intent(parent.getContext(),
				// StatisticsActivity.class);
				Intent intent = new Intent(parent.getContext(), RoundStatisticsActivity.class);
				intent.putExtra(Constants.STATS_TEAM, mGame.getTeamList().get(position).getId());
				startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.refreshActivity();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_score_actions, menu);

		// if (!mGame.isGameOver()) {
		// menu.removeItem(R.id.action_new_game);
		// menu.removeItem(R.id.action_replay_game);
		// }
		//
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_new_game:
			Intent intent = new Intent(this, CreateGameActivity.class);
			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
			return true;
		case R.id.action_replay_game:
			mGame.replayGame();
			this.refreshActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initTexts() {
		mScoreTeam = (TextView) findViewById(R.id.textScoreBoard);
		mRemainingCards = (TextView) findViewById(R.id.textScoreRemainingCards);

		Round currentRound = mGame.getCurrentRound();
		if (currentRound == null) {
			mScoreTeam.setText(String.format(mResources.getString(R.string.score_round_nothing)));
		} else {
			if (currentRound.isRoundActive()) {
				mScoreTeam.setText(String.format(mResources.getString(R.string.score_round_active),
						currentRound.getRoundNumber(), mGame.getCurrentTeam().getName()));
			} else {
				mScoreTeam.setText(String.format(mResources.getString(R.string.score_round_finished),
						currentRound.getRoundNumber(), mGame.getNextTeamToPlay().getName()));
			}
		}

		int remainingCards = mGame.getNumberCardsInPlay();
		StringBuilder builderRemaining = new StringBuilder();
		if (remainingCards == Constants.VALUE_ZERO) {
			// No more card to play => we'll start next round
			builderRemaining.append(String.format(mResources.getString(R.string.card_remaining), mGame
					.getCardListForGame().size()));
		} else if (remainingCards == 1) {
			builderRemaining.append(String.format(mResources.getString(R.string.card_remaining_last)));
		} else {
			builderRemaining.append(String.format(mResources.getString(R.string.card_remaining), remainingCards));
		}
		mRemainingCards.setText(builderRemaining.toString());

	}

	private void initButtons() {
		mButtonNextRound = (Button) findViewById(R.id.buttonNextRound);
		mButtonNextRound.setOnClickListener(this);

		Round currentRound = mGame.getCurrentRound();

		if (currentRound != null && currentRound.isRoundActive()) {
			mButtonNextRound.setText(String.format(mResources.getString(R.string.score_continue_round)));
		}

		if (mGame.isGameOver()) {
			mButtonNextRound.setVisibility(View.GONE);
		} else {
			mButtonNextRound.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mButtonNextRound.getId()) {
			launchNextRound();
		}

	}

	private void refreshActivity() {
		this.initListScore();
		this.initTexts();
		this.initButtons();
	}

	private void initListScore() {
//		gridView = (GridView) findViewById(R.id.gridView1);
		mScoreTeamList = (ListView) findViewById(R.id.scoreTeamScoreList);
		mScoreTeamAdapter = new ScoreTeamAdapter(this, R.layout.layout_team_score_row);
		List<Team> teamList = mGame.getTeamList();
		for (Team team : teamList) {
			mScoreTeamAdapter.addItem(team);
		}

		mScoreTeamList.setAdapter(mScoreTeamAdapter);
	}

	private void launchNextRound() {
		Round currentRound = mGame.getCurrentRound();

		if (currentRound == null || !currentRound.isRoundActive()) {
			// Start a next round only if needed (to come back to current round
			// if back button was pressed)
			mGame.startNextRound();
		}
		Intent intent = new Intent(this, CardActivity.class);
		startActivityForResult(intent, Constants.ACTIVITY_CARDACTIVITY_NEXT_ROUND);
	}

}
