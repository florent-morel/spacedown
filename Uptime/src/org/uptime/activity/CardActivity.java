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

	private TextView mCurrentRound;
	private TextView mCurrentTeam;
	private TextView mTeamTurnScore;
	private TextView mTeamRoundScore;
	private TextView mTeamTotalScore;
	private TextView mRemainingCards;

	private TextView mNameToFind;

	private Button mButtonCardFound;
	private Button mButtonCardSkip;
	private Button mButtonEndTurn;

	boolean allowCardFoundButton = true;

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

		if (Constants.ROUND_FIRST != mGame.getCurrentRound().getRoundNumber()) {
//		    MenuItem item = menu.findItem(R.id.action_change_card);
//		    item.setEnabled(false);
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
			Toast toast = Toast.makeText(this,
					String.format(mResources.getString(R.string.dialog_card_replaced)), Toast.LENGTH_SHORT);
			toast.show();
			// If card has been replaced, refresh the activity
			refreshActivity();
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
			refreshActivity();
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
	}

	private void setTexts() {
		Round currentRound = mGame.getCurrentRound();
		mCurrentRound.setText(String.format(mResources.getString(R.string.card_current_round), currentRound.getRoundNumber()));
		Team currentTeam = mGame.getCurrentTeam();
		mCurrentTeam.setText(String.format(mResources.getString(R.string.card_team), currentTeam.getName()));
		Integer turnScore = currentRound.getTeamTurnScore(currentRound.getCurrentTurn());
		mTeamTurnScore.setText(String.format(mResources.getString(R.string.card_team_turn_score), turnScore));
		Integer roundScore = currentRound.getTeamRoundScore(currentTeam);
		mTeamRoundScore.setText(String.format(mResources.getString(R.string.card_team_round_score), roundScore));
		Integer totalScore = mGame.getTotalScore(currentTeam);
		mTeamTotalScore.setText(String.format(mResources.getString(R.string.card_team_total_score), totalScore));
		
		int remainingCards = mGame.getCardsInPlay().size();
		StringBuilder builderRemaining = new StringBuilder();
		if (remainingCards == 1) {
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

		if (Constants.ROUND_FIRST == mGame.getCurrentRound().getRoundNumber() || (mGame.getCardsInPlay().size() == 1)) {
			mButtonCardSkip.setVisibility(View.GONE);
		}

		mButtonEndTurn = (Button) findViewById(R.id.buttonEndTurn);
		mButtonEndTurn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		if (v.getId() == mButtonCardFound.getId()) {
			if (allowCardFoundButton && !mGame.getCurrentCard().isFound()) {
				// Protect from multi clicks
//				mButtonCardFound.setEnabled(false);
				allowCardFoundButton = false;
				final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.ping);
				mp1.start();
				mGame.findCard();

				if (mGame.getCardsInPlay().isEmpty()) {
					// This was the last card in play, round will end, display
					// stats
					// for turn
					Intent intent = new Intent(this, StatisticsEndTurnActivity.class);
					startActivityForResult(intent, Constants.ACTIVITY_TURN_STATS_END_ROUND);
				}
				
				// Process is done, wait a bit before enabling next clicks
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				
				refreshActivity();
				allowCardFoundButton = true;
//				mButtonCardFound.setEnabled(true);
			}

		} else if (v.getId() == mButtonCardSkip.getId()) {
			final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.button_skip);
			mp1.start();
			mGame.skipCard();
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
