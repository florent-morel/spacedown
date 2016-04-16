package org.spacedown.activity;

import org.spacedown.GameManager;
import org.spacedown.R;
import org.spacedown.activity.stats.StatisticsEndTurnActivity;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Game;
import org.spacedown.engine.game.Round;
import org.spacedown.engine.game.Team;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CardActivity extends Activity implements OnClickListener {

	private static final String TAG = CardActivity.class.getSimpleName();

	private static GameManager mGameManager;

	private Resources mResources;

	private Game mGame;

	private TextView mCurrentRound;
	private TextView mCurrentTeam;
	private TextView mTimer;
	private TextView mTeamTurnScore;
	private TextView mTeamRoundScore;
	private TextView mTeamTotalScore;
	private TextView mRemainingCards;

	private TextView mNameToFind;

	private Button mButtonCardFound;
	private Button mButtonCardSkip;
	

    private ProgressBar bar;

	boolean allowCardFoundButton = true;

	private CountDownTimer timer;

	private static MediaPlayer foundMediaPlayer;
	private static MediaPlayer skipMediaPlayer;
	private static MediaPlayer tictacMediaPlayer;
	private static MediaPlayer endTurnMediaPlayer;

	boolean allowTictac = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card);
		mResources = getResources();

		mGameManager = GameManager.getSingletonObject();
		mGame = mGameManager.getGame();

		foundMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.ping);
		skipMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.button_skip);
		tictacMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.tictac);
		endTurnMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.beep);

		this.initTexts();
		this.setTexts();
		this.initButtons();
		this.initTimer(-1);
	}

	@Override
	protected void onPause() {
		super.onPause();
		timer.cancel();
		tictacMediaPlayer.stop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		timer.cancel();
		initTimer(mGame.getRemainingTimer());
		tictacMediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.tictac);
	}

	/**
	 * Initialize the turn timer.
	 * 
	 * 
	 * @param timerValue
	 */
	private void initTimer(long timerValue) {
		if (timerValue < 0) {
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			Integer timerInt = Integer.valueOf(prefs.getString(mResources.getString(R.string.prefs_timer_val_key),
					Constants.TIMER_DEFAULT));
			timerValue = timerInt * Constants.ONE_SECOND;
			bar.setMax(timerInt);
			bar.setIndeterminate(false);
		}
		timer = new CountDownTimer(timerValue, Constants.ONE_SECOND) {
			@Override
			public void onTick(long millisUntilFinished) {
				mGame.setRemainingTimer(millisUntilFinished);
				long roundedNumber = (millisUntilFinished + 500) / Constants.ONE_SECOND - 1;
				mTimer.setText(String.format(mResources.getString(R.string.card_timer), roundedNumber));
				bar.setProgress((int) Math.round(roundedNumber));
				if (allowTictac && millisUntilFinished <= Constants.TIMER_TICTAC) {
					tictacMediaPlayer.start();
//					bar.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.OVERLAY);
					allowTictac = false;
				}

			}

			@Override
			public void onFinish() {
				// Timer is finished, end turn
				mTimer.setText(String.format(mResources.getString(R.string.card_timer), 0));
				allowTictac = true;
				endTurnMediaPlayer.start();
				// Display stats for this turn
				Intent intent = new Intent(CardActivity.this, StatisticsEndTurnActivity.class);
				startActivityForResult(intent, Constants.ACTIVITY_TURN_STATS);
			};
		}.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_card_actions, menu);

		if (Constants.ROUND_FIRST != mGame.getCurrentRound().getRoundNumber()) {
			menu.removeItem(R.id.action_change_card);
		}

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_change_card:
			replaceCard();
			return true;
		case R.id.action_cancel_found:
			cancelFoundCard();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Replace current card by another one from the list of available cards.
	 */
	private void replaceCard() {
		boolean cardReplaced = mGame.replaceCard();

		if (cardReplaced) {
			Toast toast = Toast.makeText(this, String.format(mResources.getString(R.string.dialog_card_replaced)),
					Toast.LENGTH_SHORT);
			toast.show();
			// If card has been replaced, refresh the activity
			refreshActivity(false);
		}
	}

	private void cancelFoundCard() {
		Constants.CancelCardMode cardRemovedMode = mGame.cancelFoundCard();

		if (Constants.CancelCardMode.NO_FOUND_CARD.equals(cardRemovedMode)) {
			Toast toast = Toast.makeText(this,
					String.format(mResources.getString(R.string.dialog_cancel_card_not_found)), Toast.LENGTH_SHORT);
			toast.show();
		} else {
			if (Constants.CancelCardMode.CURRENT_TURN.equals(cardRemovedMode)) {
				Toast toast = Toast.makeText(this,
						String.format(mResources.getString(R.string.dialog_cancel_card_current_turn)),
						Toast.LENGTH_SHORT);
				toast.show();
			} else if (Constants.CancelCardMode.PREVIOUS_TURN.equals(cardRemovedMode)) {
				Toast toast = Toast.makeText(this,
						String.format(mResources.getString(R.string.dialog_cancel_card_previous_turn)),
						Toast.LENGTH_LONG);
				toast.show();
			} else if (Constants.CancelCardMode.PREVIOUS_ROUND.equals(cardRemovedMode)) {
				Toast toast = Toast.makeText(this,
						String.format(mResources.getString(R.string.dialog_cancel_card_previous_round)),
						Toast.LENGTH_LONG);
				toast.show();
			}

			// If card has been cancelled, refresh the activity
			refreshActivity(false);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	private void initTexts() {
		mCurrentRound = (TextView) findViewById(R.id.textCardCurrentRound);
		mCurrentTeam = (TextView) findViewById(R.id.textCardTeam);
		mTeamTurnScore = (TextView) findViewById(R.id.textCardTurnScore);
		mTeamRoundScore = (TextView) findViewById(R.id.textCardRoundScore);
		mTeamTotalScore = (TextView) findViewById(R.id.textCardTotalScore);
		mRemainingCards = (TextView) findViewById(R.id.textRemainingCards);
		mNameToFind = (TextView) findViewById(R.id.textCardNameToFind);
		mNameToFind.setTypeface(null, Typeface.BOLD);
		mTimer = (TextView) findViewById(R.id.textCardTimer);
		bar = (ProgressBar) findViewById(R.id.progressBarTimer);
	}

	private void setTexts() {
		Round currentRound = mGame.getCurrentRound();
		mCurrentRound.setText(String.format(mResources.getString(R.string.card_current_round),
				currentRound.getRoundNumber()));
		Team currentTeam = mGame.getCurrentTeam();
		mCurrentTeam.setText(String.format(mResources.getString(R.string.card_team), currentTeam.getName()));
		Integer turnScore = currentRound.getTeamTurnScore(currentRound.getCurrentTurn());
		mTeamTurnScore.setText(String.format(mResources.getString(R.string.card_team_turn_score), turnScore));
		Integer roundScore = currentRound.getTeamRoundScore(currentTeam);
		mTeamRoundScore.setText(String.format(mResources.getString(R.string.card_team_round_score), roundScore));
		Integer totalScore = mGame.getTotalScore(currentTeam);
		mTeamTotalScore.setText(String.format(mResources.getString(R.string.card_team_total_score), totalScore));

		int remainingCards = mGame.getNumberCardsInPlay();
		StringBuilder builderRemaining = new StringBuilder();
		if (remainingCards == Constants.VALUE_ONE) {
			builderRemaining.append(String.format(mResources.getString(R.string.card_remaining_last)));
		} else {
			builderRemaining.append(String.format(mResources.getString(R.string.card_remaining), remainingCards));
		}
		mRemainingCards.setText(builderRemaining.toString());
		mNameToFind.setText(mGame.getCurrentCard().getNameToFind());
	}

	private void initButtons() {
		mButtonCardFound = (Button) findViewById(R.id.buttonFoundCard);
		mButtonCardFound.setOnClickListener(this);

		mButtonCardSkip = (Button) findViewById(R.id.buttonSkipCard);
		mButtonCardSkip.setOnClickListener(this);

		if (Constants.ROUND_FIRST != mGame.getCurrentRound().getRoundNumber()
				&& mGame.getNumberCardsInPlay() == Constants.VALUE_ONE) {
			mButtonCardSkip.setVisibility(View.GONE);
		} else {
			mButtonCardSkip.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == mButtonCardFound.getId()) {
			if (allowCardFoundButton && !mGame.getCurrentCard().isFound()) {
				// Protect from multi clicks
				// mButtonCardFound.setEnabled(false);
				allowCardFoundButton = false;
				foundMediaPlayer.start();
				mGame.findCard(mGame.getCurrentCard(), mGame.getCurrentRound().getCurrentTurn(), true);

				if (mGame.getNumberCardsInPlay() == Constants.VALUE_ZERO) {
					timer.cancel();
					tictacMediaPlayer.stop();
					// does not work with this
					// This was the last card in play, round will end, display
					// stats for turn
					Intent intent = new Intent(this, StatisticsEndTurnActivity.class);
					startActivityForResult(intent, Constants.ACTIVITY_TURN_STATS);
				}

				// Process is done, wait a bit before enabling next clicks
				// try {
				// Thread.sleep(1000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }

				refreshActivity(false);
				allowCardFoundButton = true;
				// mButtonCardFound.setEnabled(true);
			}

		} else if (v.getId() == mButtonCardSkip.getId()) {
			skipMediaPlayer.start();
			if (Constants.ROUND_FIRST == mGame.getCurrentRound().getRoundNumber()) {
				// During round 1, skip button is used to replace card
				replaceCard();
			} else {
				mGame.skipCard();
				refreshActivity(false);
			}
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.ACTIVITY_TURN_STATS) {
			// Stats for this turn is done, end turn
			mGame.setRemainingTimer(-1);
			timer.cancel();
			if (mGame.getNumberCardsInPlay() == Constants.VALUE_ZERO) {
				mGame.endRound();
			} else {
				mGame.endTurn();
			}

			if (!mGame.isGameOver() && mGame.isRoundActive()) {
				Intent intent = new Intent(this, NextPlayerActivity.class);
				startActivityForResult(intent, Constants.ACTIVITY_NEXT_PLAYER);
			} else {
				refreshActivity(true);
			}

		} else if (requestCode == Constants.ACTIVITY_NEXT_PLAYER) {
			refreshActivity(true);
		}
	}

	/**
	 * Refresh the activity.
	 * 
	 * @param initTimer
	 */
	private void refreshActivity(boolean initTimer) {
		if (!mGame.isGameOver() && mGame.isRoundActive()) {
			this.setTexts();
			this.initButtons();
			if (initTimer) {
				allowTictac = true;
				this.initTimer(-1);
			}
		} else {
			finish();
		}
	}

}
