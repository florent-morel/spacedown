package org.uptime.activity;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Game;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
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
		
		if (Constants.ROUND_FIRST == mGame.getCurrentRound().getRoundNumber() || 
				(mGame.getCardsInPlay().size() == 1)) {
			mButtonCardSkip.setVisibility(View.GONE);
		}

		mButtonEndTurn = (Button) findViewById(R.id.buttonEndTurn);
		mButtonEndTurn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == mButtonCardFound.getId()) {
			final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.ping);
			mp1.start();
			mGame.cardFound();

			if (mGame.getCardsInPlay().isEmpty()) {
				// This was the last card in play, round will end, display stats
				// for turn
				Intent intent = new Intent(this, StatisticsEndTurnActivity.class);
				startActivityForResult(intent, Constants.ACTIVITY_TURN_STATS_END_ROUND);
			}
			refreshActivity();

		} else if (v.getId() == mButtonCardSkip.getId()) {
			final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.button_27);
			mp1.start();
			mGame.cardSkip();
		} else if (v.getId() == mButtonEndTurn.getId()) {
			// Display stats for this turn
			Intent intent = new Intent(this, StatisticsEndTurnActivity.class);
			startActivityForResult(intent, Constants.ACTIVITY_TURN_STATS);
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.ACTIVITY_TURN_STATS) {
			// Stats for this turn is done, end turn
			mGame.endTurn();
			refreshActivity();
		}
		if (requestCode == Constants.ACTIVITY_TURN_STATS_END_ROUND) {
			// This round is done
			mGame.endRound();
			refreshActivity();
		}
	}

	/**
	 * Refresh the activity.
	 * 
	 * @param descending
	 */
	private void refreshActivity() {
		if (!mGame.isGameOver() && mGame.isRoundActive()) {
			this.setTexts();
		} else {
			Intent intent = new Intent(this, ScoreActivity.class);
			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
			finish();
		}
	}

}
