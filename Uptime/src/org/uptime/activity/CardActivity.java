package org.uptime.activity;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CardActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Game mGame;

	private TextView mCurrentTeam;
	private TextView mNameToFind;

	private Button mButtonCardFound;
	private Button mButtonCardSkip;
	private Button mButtonEndTurn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card);

		mGameManager = GameManager.getSingletonObject();
		mGame = mGameManager.getGame();

		this.initTexts();
		
		this.initButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void initTexts() {
		mCurrentTeam = (TextView) findViewById(R.id.textCardTeam);
		mCurrentTeam.setText(mGame.getCurrentTeam().getName());
		
		mNameToFind = (TextView) findViewById(R.id.textCardNameToFind);
		mNameToFind.setText(mGame.getCurrentCard().getNameToFind());
	}

	private void initButtons() {
		mButtonCardFound = (Button) findViewById(R.id.buttonFoundCard);
		mButtonCardFound.setOnClickListener(this);

		mButtonCardSkip = (Button) findViewById(R.id.buttonSkipCard);
		mButtonCardSkip.setOnClickListener(this);
		if(Constants.ROUND_FIRST == mGame.getCurrentRound().getRoundNumber()) {
			mButtonCardSkip.setVisibility(View.GONE);
		}

		mButtonEndTurn = (Button) findViewById(R.id.buttonEndTurn);
		mButtonEndTurn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == mButtonCardFound.getId()) {
			mGame.cardFound();
		} 
		else if (v.getId() == mButtonCardSkip.getId()) {
			mGame.cardSkip();
		}
		else if (v.getId() == mButtonEndTurn.getId()) {
			mGame.setTurnOver();
		}
		
		if (!mGame.isGameOver() && mGame.isRoundActive()) {
			refreshActivity();
		}
		else {
			Intent intent = new Intent(this, ScoreActivity.class);
			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
		}
	}

	/**
	 * Refresh the activity.
	 * 
	 * @param descending
	 */
	private void refreshActivity() {
		mNameToFind.setText(mGame.getCurrentCard().getNameToFind());
		
		mCurrentTeam.setText(mGame.getCurrentTeam().getName());
	}

}
