package org.uptime.activity.stats;

import java.util.List;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.activity.CardActivity;
import org.uptime.activity.create.CreateGameActivity;
import org.uptime.adapter.ScoreTeamAdapter;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Game;
import org.uptime.engine.game.Round;
import org.uptime.engine.game.Team;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

public class ScoreActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Resources mResources;

	private Game mGame;

	private TextView mScoreTeam;

	private ListView mScoreTeamList;

	private ScoreTeamAdapter mScoreTeamAdapter;

	private Button mButtonNextRound;
	private Button mButtonNewGame;
	private Button mButtonReplayGame;

	TableLayout scoreTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		mResources = getResources();

		mGameManager = GameManager.getSingletonObject();
		mGame = mGameManager.getGame();

		this.refreshActivity();

		mScoreTeamList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(parent.getContext(), StatisticsActivity.class);
//				Intent intent = new Intent(parent.getContext(), RoundStatisticsActivity.class);
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void initTexts() {
		mScoreTeam = (TextView) findViewById(R.id.textScoreBoard);

		Round currentRound = mGame.getCurrentRound();
		if (currentRound == null) {
			mScoreTeam.setText(String.format(mResources.getString(R.string.score_round_nothing)));
		} else {
			if (currentRound.isRoundActive()) {
				mScoreTeam.setText(String.format(mResources.getString(R.string.score_round_active), currentRound
						.getRoundNumber(), mGame.getCurrentTeam().getName()));
			} else {
				mScoreTeam.setText(String.format(mResources.getString(R.string.score_round_finished), currentRound
						.getRoundNumber(), mGame.getNextTeamToPlay().getName()));
			}
		}

	}

	private void initButtons() {
		mButtonNextRound = (Button) findViewById(R.id.buttonNextRound);
		mButtonNextRound.setOnClickListener(this);
		mButtonNewGame = (Button) findViewById(R.id.buttonNewGame);
		mButtonNewGame.setOnClickListener(this);
		mButtonReplayGame = (Button) findViewById(R.id.buttonReplayGame);
		mButtonReplayGame.setOnClickListener(this);

		Round currentRound = mGame.getCurrentRound();

		if (currentRound != null && currentRound.isRoundActive()) {
			mButtonNextRound.setText(String.format(mResources.getString(R.string.score_continue_round)));
		}

		if (mGame.isGameOver()) {
			mButtonNextRound.setVisibility(View.GONE);
			mButtonNewGame.setVisibility(View.VISIBLE);
			mButtonReplayGame.setVisibility(View.VISIBLE);
		} else {
			mButtonNextRound.setVisibility(View.VISIBLE);
			mButtonNewGame.setVisibility(View.GONE);
			mButtonReplayGame.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mButtonNextRound.getId()) {
			launchNextRound();
		} else if (v.getId() == mButtonNewGame.getId()) {
			Intent intent = new Intent(this, CreateGameActivity.class);
			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
		} else if (v.getId() == mButtonReplayGame.getId()) {
			mGame.replayGame();
			this.refreshActivity();
		}

	}

	private void refreshActivity() {
		this.initListScore();
		this.initTexts();
		this.initButtons();
	}

	private void initListScore() {
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
