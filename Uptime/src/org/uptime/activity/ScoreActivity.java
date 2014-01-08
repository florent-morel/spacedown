package org.uptime.activity;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.activity.create.CreateGameActivity;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Card;
import org.uptime.engine.game.Game;
import org.uptime.engine.game.Round;
import org.uptime.engine.game.Team;
import org.uptime.engine.game.Turn;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ScoreActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Game mGame;

	private TextView mScoreTeam;

	private Button mButtonNextRound;
	private Button mButtonNewGame;

	private Button mButtonStats;

	TableLayout scoreTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);

		mGameManager = GameManager.getSingletonObject();
		mGame = mGameManager.getGame();

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
		// scoreTable = (TableLayout) findViewById(R.id.tableScore);

		// Map<Team, Integer> teamScoreTotal = mGame.getTotalScoreMap();
		//
		// Set<Entry<Team, Integer>> entrySet = teamScoreTotal.entrySet();
		// StringBuilder builder = new StringBuilder();
		// if (entrySet != null && !entrySet.isEmpty()) {
		// for (Entry<Team, Integer> entry : entrySet) {
		// builder.append(entry.getKey().getName());
		// builder.append(": ");
		// builder.append(entry.getValue());
		// builder.append("\n");
		// }
		// // } else {
		// // builder.append(entry.getKey().getName());
		// // builder.append(": ");
		// // builder.append("0");
		// // builder.append("\n");
		// }

		List<Team> teamList = mGame.getTeamList();
		StringBuilder builder = new StringBuilder();
		for (Team team : teamList) {
			builder.append(team.getName());
			builder.append(": ");
			List<Round> roundList = mGame.getRoundList();
			for (Round round : roundList) {
				Integer teamRoundScore = round.getTeamRoundScore(team);
				builder.append(teamRoundScore);
				builder.append("|");
			}
			builder.append("--Total: ");
			builder.append(mGame.getTotalScore(team));
			builder.append("\n");
		}

		mScoreTeam.setText(builder.toString());

		// List<Team> teamList = mGame.getTeamList();
		// TableRow row;
		// TextView t1 = (TextView) findViewById(R.id.textTableTeamName);
		// for (Team team : teamList) {
		// row = new TableRow(this);
		// t1 = new TextView(this);
		// t1.setText(team.getName());
		// row.addView(t1);
		//
		//
		// TextView tRound1 = (TextView) findViewById(R.id.textTableRound1);
		// Integer teamRoundScore1 = Constants.ZERO_VALUE;
		// if (!mGame.getRoundList().isEmpty() && mGame.getRoundList().get(0) !=
		// null) {
		// teamRoundScore1 =
		// mGame.getRoundList().get(0).getTeamRoundScore(team);
		// }
		// tRound1.setText(teamRoundScore1);
		// row.addView(tRound1);
		//
		// TextView tRound2 = (TextView) findViewById(R.id.textTableRound2);
		// Integer teamRoundScore2 = Constants.ZERO_VALUE;
		// if (!mGame.getRoundList().isEmpty() && mGame.getRoundList().get(1) !=
		// null) {
		// teamRoundScore2 =
		// mGame.getRoundList().get(1).getTeamRoundScore(team);
		// }
		// tRound1.setText(teamRoundScore2);
		// row.addView(tRound2);
		// TextView tRound3 = (TextView) findViewById(R.id.textTableRound3);
		// Integer teamRoundScore3 = Constants.ZERO_VALUE;
		// if (!mGame.getRoundList().isEmpty() && mGame.getRoundList().get(2) !=
		// null) {
		// teamRoundScore3 =
		// mGame.getRoundList().get(2).getTeamRoundScore(team);
		// }
		// tRound1.setText(teamRoundScore3);
		// row.addView(tRound3);
		//
		// TextView tTotal = (TextView) findViewById(R.id.textTableTotal);
		// Integer teamTotalScore = mGame.getTotalScore(team);
		// tRound1.setText(teamTotalScore);
		// row.addView(tTotal);

		// List<Round> roundList = mGame.getRoundList();
		// for (Round round : roundList) {
		// Integer teamRoundScore = round.getTeamRoundScore(team);
		// TextView textTeamScore = new TextView(this);
		// textTeamScore.setText(teamRoundScore);
		// row.addView(textTeamScore);
		// }
		// End of the Rounds: display total

		// TextView textTeamTotalScore = new TextView(this);
		// textTeamTotalScore.setText(10);
		// row.addView(textTeamTotalScore);

		// scoreTable.addView(row, new
		// TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));
		// }

	}

	private void initButtons() {
		mButtonNextRound = (Button) findViewById(R.id.buttonNextRound);
		mButtonNextRound.setOnClickListener(this);
		mButtonNewGame = (Button) findViewById(R.id.buttonNewGame);
		mButtonNewGame.setOnClickListener(this);

		mButtonStats = (Button) findViewById(R.id.button100);
		mButtonStats.setOnClickListener(this);

		if (mGame.isGameOver()) {
			mButtonNextRound.setVisibility(View.GONE);
			mButtonNewGame.setVisibility(View.VISIBLE);
		} else {
			mButtonNextRound.setVisibility(View.VISIBLE);
			mButtonNewGame.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == mButtonNextRound.getId()) {
			launchNextRound();
		} else if (v.getId() == mButtonNewGame.getId()) {
			Intent intent = new Intent(this, CreateGameActivity.class);
			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
		} else if (v.getId() == mButtonStats.getId()) {
			Intent intent = new Intent(this, StatisticsActivity.class);
			intent.putExtra(Constants.TEAM_STATS, mGame.getTeamList().get(0).getId());
			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
		}

	}

	private void refreshActivity() {
		this.initTexts();
		this.initButtons();
	}

	private void launchNextRound() {
		mGame.startNextRound();
		Intent intent = new Intent(this, CardActivity.class);
		startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
	}

}
