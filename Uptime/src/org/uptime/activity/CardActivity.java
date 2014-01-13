package org.uptime.activity;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.activity.stats.StatisticsEndTurnActivity;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Game;
import org.uptime.engine.game.Round;
import org.uptime.engine.game.Team;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
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

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_card_actions, menu);

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		// case R.id.new_game:
		// newGame();
		// return true;
		case R.id.action_cancel_found:
			cancelFoundCard();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void cancelFoundCard() {
		boolean isCardRemoved = mGame.cancelCardFound();

		if (!isCardRemoved) {
			Toast toast = Toast.makeText(this,
					String.format(mResources.getString(R.string.dialog_cancel_card_not_found)), Toast.LENGTH_SHORT);
			toast.show();
		} else {

			refreshActivity();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
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
		Round currentRound = mGame.getCurrentRound();
		mCurrentRoundValue.setText(String.format(mResources.getString(R.string.card_current_round), currentRound.getRoundNumber()));
		Team currentTeam = mGame.getCurrentTeam();
		mCurrentTeamValue.setText(currentTeam.getName());
		Integer turnScore = currentRound.getTeamTurnScore(currentRound.getCurrentTurn());
		mTeamTurnScoreValue.setText(turnScore.toString());
		Integer roundScore = currentRound.getTeamRoundScore(currentTeam);
		mTeamRoundScoreValue.setText(roundScore.toString());
		Integer totalScore = mGame.getTotalScore(currentTeam);
		mTeamTotalScoreValue.setText(totalScore.toString());
		mNameToFind.setText(mGame.getCurrentCard().getNameToFind());
	}

	private void initButtons() {
		mButtonCardFound = (Button) findViewById(R.id.buttonFoundCard);
		mButtonCardFound.setOnClickListener(this);

		mButtonCardSkip = (Button) findViewById(R.id.buttonSkipCard);
		mButtonCardSkip.setOnClickListener(this);

		if (Constants.ROUND_FIRST == mGame.getCurrentRound().getRoundNumber() || (mGame.getCardsInPlay().size() == 1)) {
			mButtonCardSkip.setVisibility(View.GONE);
		}

		mButtonEndTurn = (Button) findViewById(R.id.buttonEndTurn);
		mButtonEndTurn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		boolean allowCardFoundButton = true;
		
		if (v.getId() == mButtonCardFound.getId()) {
			if (allowCardFoundButton && !mGame.getCurrentCard().isFound()) {
				// Protect from multi clicks
				allowCardFoundButton = false;
				final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.ping);
				mp1.start();
				mGame.cardFound();

				if (mGame.getCardsInPlay().isEmpty()) {
					// This was the last card in play, round will end, display
					// stats
					// for turn
					Intent intent = new Intent(this, StatisticsEndTurnActivity.class);
					startActivityForResult(intent, Constants.ACTIVITY_TURN_STATS_END_ROUND);
				}
				refreshActivity();
				
				// Process is done, enable next clicks
				allowCardFoundButton = true;
			}

		} else if (v.getId() == mButtonCardSkip.getId()) {
			final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.button_skip);
			mp1.start();
			mGame.cardSkip();
			refreshActivity();
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
			this.initButtons();
		} else {
//			Intent intent = new Intent(this, ScoreActivity.class);
//			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
			finish();
		}
	}

}
