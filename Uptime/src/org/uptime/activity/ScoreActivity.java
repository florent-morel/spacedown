package org.uptime.activity;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.activity.create.CreateGameActivity;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Game;
import org.uptime.engine.game.Team;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Game mGame;

	private TextView mScoreTeam;

	private Button mButtonNextRound;
	private Button mButtonNewGame;

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

		Map<Team, Integer> teamScoreTotal = mGame.getTotalScoreMap();

		Set<Entry<Team, Integer>> entrySet = teamScoreTotal.entrySet();
		StringBuilder builder = new StringBuilder();
		if (entrySet != null && !entrySet.isEmpty()) {
			for (Entry<Team, Integer> entry : entrySet) {
				builder.append(entry.getKey().getName());
				builder.append(": ");
				builder.append(entry.getValue());
				builder.append("\n");
			}
			// } else {
			// builder.append(entry.getKey().getName());
			// builder.append(": ");
			// builder.append("0");
			// builder.append("\n");
		}

		mScoreTeam.setText(builder.toString());
	}

	private void initButtons() {
		mButtonNextRound = (Button) findViewById(R.id.buttonNextRound);
		mButtonNextRound.setOnClickListener(this);
		mButtonNewGame = (Button) findViewById(R.id.buttonNewGame);
		mButtonNewGame.setOnClickListener(this);

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
