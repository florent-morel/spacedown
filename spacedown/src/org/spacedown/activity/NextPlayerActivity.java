package org.spacedown.activity;

import org.spacedown.GameManager;
import org.spacedown.R;
import org.spacedown.activity.stats.StatisticsEndTurnActivity;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NextPlayerActivity extends Activity implements OnClickListener {

	private static final String TAG = NextPlayerActivity.class.getSimpleName();

	private static GameManager mGameManager;

	private Game game;

	private Resources mResources;

	private TextView nextTeam;
	private TextView countDownNextPlayer;

	private Button mButtonNextTurn;

	private ProgressBar mProgress;
	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private long remainingTimer = -1;
	private Handler progressBarHandler = new Handler();
	boolean allowFinish = false;

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

		if (game.getNumberCardsInPlay() == Constants.VALUE_ZERO) {
			nextTeam.setText(String.format(mResources.getString(R.string.stats_end_round), game.getCurrentRound()
					.getRoundNumber()));
		} else {
			nextTeam.setText(String.format(mResources.getString(R.string.stats_next_turn), game.getCurrentTeam()
					.getName()));
		}

		countDownNextPlayer = (TextView) findViewById(R.id.countDownNextPlayer);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mButtonNextTurn.getId()) {
			countDown2();
			// Finish activity
//			while (!allowFinish) {
//				// Wait until progress bar is finished
//			}
//			finish();
		}
	}

	private void countDown2() {
		// mProgress = (ProgressBar) findViewById(R.id.progress);
		mProgress.setVisibility(View.VISIBLE);
		mProgress.setProgress(progressBarStatus);

		long timer = Constants.TIMER_BEFORE_NEXT_PLAYER * 1000; // 3 seconds before next player starts

		/** CountDownTimer starts with 2 minutes and every onTick is 1 second */
		CountDownTimer cdt = new CountDownTimer(timer, Constants.ONE_SECOND) {

			public void onTick(long millisUntilFinished) {
				remainingTimer = millisUntilFinished;
				Log.v(TAG, "CountDownTimer, millisUntilFinished=" + millisUntilFinished);
				long roundedNumber = (millisUntilFinished + 500) / Constants.ONE_SECOND;
				Log.v(TAG, "CountDownTimer, display=" + (roundedNumber - 1));
				countDownNextPlayer.setText(String.format(mResources.getString(R.string.card_timer), roundedNumber - 1));
//				dTotal = Constants.TIMER_BEFORE_NEXT_PLAYER - (millisUntilFinished * 1000);
//				progressBarStatus = (int) ((dTotal / Constants.TIMER_BEFORE_NEXT_PLAYER) * 100);
//				mProgress.setProgress(progressBarStatus);
			}

			@Override
			public void onFinish() {
				// Finish activity
				finish();
			}
		}.start();
	}

	private void countDown() {

		mProgress.setVisibility(View.VISIBLE);
		mProgress.setProgress(0);
		mProgress.setMax(100);

		// prepare for a progress bar dialog
		// progressBar = new ProgressDialog(this.getBaseContext());
		// progressBar.setCancelable(true);
		// progressBar.setMessage("File downloading ...");
		// progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// progressBar.setProgress(0);
		// progressBar.setMax(100);
		// progressBar.show();

		// reset progress bar status
		progressBarStatus = 0;

		new Thread(new Runnable() {
			public void run() {
				while (progressBarStatus < 100) {

					// process some tasks
					progressBarStatus = doSomeTasks(progressBarStatus);

					// your computer is too fast, sleep 1 second
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Update the progress bar
					progressBarHandler.post(new Runnable() {
						public void run() {
							// progressBar.setProgress(progressBarStatus);
							mProgress.setProgress(progressBarStatus);
						}
					});
				}

				// ok, file is downloaded,
				if (progressBarStatus >= 100) {

					// sleep 2 seconds, so that you can see the 100%
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// close the progress bar dialog
					// progressBar.dismiss();
				}
			}

			private int doSomeTasks(int seconds) {
				// try {
				// wait(1000);
				seconds += 20;
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				return seconds;
			}
		}).start();

	}

}
