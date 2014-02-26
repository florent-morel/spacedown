package org.spacedown.activity;

import org.spacedown.GameManager;
import org.spacedown.R;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Game;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NextPlayerActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Game game;

	private Resources mResources;

	private TextView nextTeam;

	private Button mButtonNextTurn;

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
			// Finish activity
			finish();
		}
	}

}
