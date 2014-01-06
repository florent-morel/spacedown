package org.uptime.activity.create;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.activity.ScoreActivity;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CreateGameActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private EditText mNumberOfTeams;
	private Button mButtonNewGame;
//	private Button mButtonAddTeam;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);
		setTitle(R.string.create_game);

		mGameManager = GameManager.getSingletonObject();

		mNumberOfTeams = (EditText) findViewById(R.id.editCreateTeam);

		this.initButtons();

		// Prefill with existing game values if any
		Game currentGame = mGameManager.getGame();

		if (currentGame != null && !currentGame.getTeamList().isEmpty()) {
			mNumberOfTeams.setText(String.valueOf(currentGame.getTeamList().size()));
		}

	}

	private void initButtons() {
		mButtonNewGame = (Button) findViewById(R.id.buttonNewGame);
		mButtonNewGame.setOnClickListener(this);
//		mButtonAddTeam = (Button) findViewById(R.id.buttonAddTeam);
//		mButtonAddTeam.setOnClickListener(this);
	}

	private void startNewGame() {

		// List<Player> playerList = null;
		//
		// if (mGameManager.getGame() != null) {
		// playerList = mGameManager.getGame().getPlayerList();
		// }

		mGameManager.startNewGame(Integer.valueOf(mNumberOfTeams.getText().toString()));

		Intent intent = new Intent(this, ScoreActivity.class);
		startActivityForResult(intent, Constants.GAME_NEW);
	}

	@Override
	public void onClick(View v) {
//		if (v.getId() == mButtonAddTeam.getId()) {
//			Intent intent = new Intent(this, AddTeamActivity.class);
//			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
//		}
//		else 
			if (v.getId() == mButtonNewGame.getId()) {
			startNewGame();
		}
	}

}
