package org.spacedown.activity;

import org.spacedown.GameManager;
import org.spacedown.R;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Game;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NextPlayerActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Game game;

	private Resources mResources;

	private TextView nextTeam;
	private TextView countDownNextPlayer;

	private Button mButtonNextTurn;

	private ProgressBar mProgress;
	private int progressBarStatus = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.next_player);
		mResources = getResources();

		mGameManager = GameManager.getSingletonObject();
		game = mGameManager.getGame();

		this.initTexts();
		this.initButtons();
	}

	private void initButtons() {
		mButtonNextTurn = (Button) findViewById(R.id.buttonNextTeam);
		mButtonNextTurn.setOnClickListener(this);

		mProgress = (ProgressBar) findViewById(R.id.progressBar);
		mProgress.setVisibility(View.GONE);
	}

	private void initTexts() {
		nextTeam = (TextView) findViewById(R.id.nextPlayerNameText);
		nextTeam.setTypeface(null, Typeface.BOLD);

		if (game.getNumberCardsInPlay() == Constants.VALUE_ZERO) {
			nextTeam.setText(String.format(mResources.getString(R.string.stats_end_round), game.getCurrentRound()
					.getRoundNumber()));
		} else {
			nextTeam.setText(String.format(mResources.getString(R.string.stats_next_turn), game.getCurrentTeam()
					.getName()));
		}

		countDownNextPlayer = (TextView) findViewById(R.id.countDownNextPlayer);
		countDownNextPlayer.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mButtonNextTurn.getId()) {
			countDownBeforeNextPlayer();
		}
	}

	private void countDownBeforeNextPlayer() {
		mProgress.setVisibility(View.VISIBLE);
		countDownNextPlayer.setVisibility(View.VISIBLE);
		mProgress.setProgress(progressBarStatus);

		long timer = Constants.TIMER_BEFORE_NEXT_PLAYER * 1000; // 3 seconds before next player starts

		/** CountDownTimer starts with 2 minutes and every onTick is 1 second */
		CountDownTimer cdt = new CountDownTimer(timer, Constants.ONE_SECOND) {

			public void onTick(long millisUntilFinished) {
				long roundedNumber = (millisUntilFinished + 500) / Constants.ONE_SECOND;
				countDownNextPlayer.setText(String.format(mResources.getString(R.string.card_timer), roundedNumber - 1));
			}

			@Override
			public void onFinish() {
				// Finish activity
				finish();
			}
		}.start();
	}

}
