package org.uptime.activity;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Game;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CardActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Resources mResources;

	private Game mGame;

	private TextView mCurrentRoundValue;
	private TextView mCurrentTeamValue;
	private TextView mTeamTurnScoreValue;
	private TextView mTeamRoundScoreValue;
	private TextView mTeamTotalScoreValue;

	private TextView mNameToFind;

	private Button mButtonCardFound;
	private Button mButtonCardSkip;
	private Button mButtonEndTurn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card);
		mResources = getResources();

		mGameManager = GameManager.getSingletonObject();
		mGame = mGameManager.getGame();

		this.initTexts();
		this.setTexts();

		this.initButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void initTexts() {
		mCurrentRoundValue = (TextView) findViewById(R.id.textCardCurrentRound);

		mCurrentTeamValue = (TextView) findViewById(R.id.textCardTeamValue);

		mTeamTurnScoreValue = (TextView) findViewById(R.id.textCardTurnScoreValue);

		mTeamRoundScoreValue = (TextView) findViewById(R.id.textCardRoundScoreValue);

		mTeamTotalScoreValue = (TextView) findViewById(R.id.textCardTotalScoreValue);

		mNameToFind = (TextView) findViewById(R.id.textCardNameToFind);
	}

	private void setTexts() {
		mCurrentRoundValue.setText(String.format(mResources.getString(R.string.card_current_round), mGame
				.getCurrentRound().getRoundNumber()));
		mCurrentTeamValue.setText(mGame.getCurrentTeam().getName());
		Integer turnScore = mGame.getCurrentRound().getCurrentTurn().getTeamTurnScore(mGame.getCurrentTeam());
		mTeamTurnScoreValue.setText(turnScore.toString());
		Integer roundScore = mGame.getCurrentRound().getTeamRoundScore(mGame.getCurrentTeam());
		mTeamRoundScoreValue.setText(roundScore.toString());
		Integer totalScore = mGame.getTotalScore(mGame.getCurrentTeam());
		mTeamTotalScoreValue.setText(totalScore.toString());
		mNameToFind.setText(mGame.getCurrentCard().getNameToFind());
	}

	private void initButtons() {
		mButtonCardFound = (Button) findViewById(R.id.buttonFoundCard);
		mButtonCardFound.setOnClickListener(this);

		mButtonCardSkip = (Button) findViewById(R.id.buttonSkipCard);
		mButtonCardSkip.setOnClickListener(this);
		if (Constants.ROUND_FIRST == mGame.getCurrentRound().getRoundNumber()) {
			mButtonCardSkip.setVisibility(View.GONE);
		}

		mButtonEndTurn = (Button) findViewById(R.id.buttonEndTurn);
		mButtonEndTurn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == mButtonCardFound.getId()) {
			mGame.cardFound();
		} else if (v.getId() == mButtonCardSkip.getId()) {
			mGame.cardSkip();
		} else if (v.getId() == mButtonEndTurn.getId()) {
			// Display stats for this turn
			// Intent intent = new Intent(this,
			// StatisticsEndTurnActivity.class);
			// startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
			mGame.endTurn();
		}

		if (!mGame.isGameOver() && mGame.isRoundActive()) {
			refreshActivity();
		} else {
			Intent intent = new Intent(this, ScoreActivity.class);
			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
			finish();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO not working, always getting 0 as value for resultCode
		// if (resultCode == Constants.TURN_STATS_DONE) {
		// // Stats for this turn is done, end turn
		// mGame.endTurn();
		//
		// if (!mGame.isGameOver() && mGame.isRoundActive()) {
		// refreshActivity();
		// }
		// }
	}

	/**
	 * Refresh the activity.
	 * 
	 * @param descending
	 */
	private void refreshActivity() {
		this.setTexts();
	}

}
