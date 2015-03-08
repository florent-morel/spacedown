package org.spacedown.activity;

import org.spacedown.GameManager;
import org.spacedown.R;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
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

	private Button mButtonNextTurn;

    private ProgressBar mProgress;
	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();

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
			nextTeam.setText(String.format(mResources.getString(R.string.stats_end_round), game
					.getCurrentRound().getRoundNumber()));
		} else {
			nextTeam.setText(String.format(mResources.getString(R.string.stats_next_turn), game.getCurrentTeam()
					.getName()));
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mButtonNextTurn.getId()) {
			countDown();
			// Finish activity
			while (progressBarStatus < 100) {
				// Wait until progress bar is finished
			}
			finish();
		}
	}
	private void countDown() {

        mProgress.setVisibility(View.VISIBLE);
        mProgress.setProgress(0);
        mProgress.setMax(100);
        

		// prepare for a progress bar dialog
//		progressBar = new ProgressDialog(this.getBaseContext());
//		progressBar.setCancelable(true);
//		progressBar.setMessage("File downloading ...");
//		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		progressBar.setProgress(0);
//		progressBar.setMax(100);
//		progressBar.show();

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
//							progressBar.setProgress(progressBarStatus);
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
//					progressBar.dismiss();
				}
			}

			private int doSomeTasks(int seconds) {
//				try {
//					wait(1000);
					seconds+=20;
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				return seconds;
			}
		}).start();

	}

}
