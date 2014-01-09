package org.uptime.activity.create;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.activity.ScoreActivity;
import org.uptime.cards.build.CardBuilder;
import org.uptime.cards.build.ImportCards;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Card;
import org.uptime.engine.game.Game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class CreateGameActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Game mGame;

	private Resources mResources;

	private Context context;

	private EditText mNumberOfTeams;
	private Button mButtonNewGame;
	private Button mButtonContinueGame;
	// private Button mButtonAddTeam;
	private CheckBox mImportCards;

	private List<Card> mCardList;
	boolean confirmNewGame = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResources = getResources();
		context = getApplicationContext();
		setContentView(R.layout.activity_create_game);
		setTitle(R.string.create_game);

		mGameManager = GameManager.getSingletonObject();
		mGame = mGameManager.getGame();

		mNumberOfTeams = (EditText) findViewById(R.id.editCreateTeam);

		this.initButtons();

		// Prefill with existing game values if any
		if (mGame != null && !mGame.getTeamList().isEmpty()) {
			mNumberOfTeams.setText(String.valueOf(mGame.getTeamList().size()));
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		this.initButtons();
	}

	private void initButtons() {
		mButtonNewGame = (Button) findViewById(R.id.buttonNewGame);
		mButtonNewGame.setOnClickListener(this);
		mButtonContinueGame = (Button) findViewById(R.id.buttonContinueGame);
		mButtonContinueGame.setOnClickListener(this);
		mImportCards = (CheckBox) findViewById(R.id.checkBoxImportCards);
		mImportCards.setOnClickListener(this);

		// mButtonAddTeam = (Button) findViewById(R.id.buttonAddTeam);
		// mButtonAddTeam.setOnClickListener(this);

		if (mGame == null || mGame.isGameOver()) {
			mButtonContinueGame.setVisibility(View.GONE);
			mButtonNewGame.setVisibility(View.VISIBLE);
		} else {
			mButtonContinueGame.setVisibility(View.VISIBLE);
			mButtonNewGame.setVisibility(View.VISIBLE);
		}
	}

	private void startNewGame() {

		// List<Player> playerList = null;
		//
		// if (mGameManager.getGame() != null) {
		// playerList = mGameManager.getGame().getPlayerList();
		// }

		mGameManager.startNewGame(Integer.valueOf(mNumberOfTeams.getText().toString()), mCardList);
		mGame = mGameManager.getGame();
		
		Intent intent = new Intent(this, ScoreActivity.class);
		startActivityForResult(intent, Constants.GAME_NEW);
	}

	@Override
	public void onClick(View v) {
		// if (v.getId() == mButtonAddTeam.getId()) {
		// Intent intent = new Intent(this, AddTeamActivity.class);
		// startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
		// }
		// else
		if (v.getId() == mButtonNewGame.getId()) {
			boolean confirmation = true;
			if (mGame != null && !mGame.isGameOver()) {
				// Ask for confirmation since it will cancel previous game
//				confirmation = displayConfirmation();
			}
			
			if (confirmation) {
				if (mImportCards.isChecked()) {
					importCardsFromCSV();
				}
				startNewGame();
			}
		} else if (v.getId() == mButtonContinueGame.getId()) {
			// Go directly to Score activity
			Intent intent = new Intent(this, ScoreActivity.class);
			startActivityForResult(intent, Constants.GAME_NEW);
		}
	}

	/**
	 * Not working, getting
	 * android.view.WindowManager$BadTokenException: Unable to add window -- token null is not for an application
	 * 
	 * @return
	 */
	private synchronized boolean displayConfirmation() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Ask the user if they want to start a new game
				AlertDialog.Builder alert = new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.dialog_overwrite_current_game)
						.setMessage(R.string.dialog_overwrite_current_game_confirm)
						.setPositiveButton(R.string.dialog_confirm_yes, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// Start new game
								confirmNewGame = true;
								notify();
							}

						}).setNegativeButton(R.string.dialog_confirm_no, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// Do not start a new game
								confirmNewGame = false;
								notify();
							}

						});
				alert.show();
			}
		});

		try {
			wait();
		} catch (InterruptedException e) {
		}
		return confirmNewGame;
	}
	

	private void importCardsFromCSV() {
		try {
			FileReader fileReader = new FileReader(context.getFilesDir().getPath() + "/testaa.csv");
			List<Card> cardsFromCSV = ImportCards.buildCardsFromCSV(fileReader);
			mCardList = CardBuilder.getRandomCards(cardsFromCSV);
		} catch (FileNotFoundException e) {
			Toast toast = Toast.makeText(this,
					String.format(mResources.getString(R.string.dialog_import_file_not_found)), Toast.LENGTH_LONG);
			toast.show();
			mCardList = CardBuilder.buildCards(Constants.RunMode.HARDCODE);
		}
	}

}
