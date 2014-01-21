package org.uptime.activity.create;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.activity.stats.ScoreActivity;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class CreateGameActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Game mGame;

	private Resources mResources;

	private Context context;

	// private EditText mNumberOfTeams;

	private Spinner spinnerNumberOfTeams;

	private Spinner spinnerNumberOfCards;

	private Button mButtonNewGame;

	private Button mButtonContinueGame;

	// private Button mButtonAddTeam;

	private CheckBox mDebugMode;

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

		// mNumberOfTeams = (EditText) findViewById(R.id.editCreateTeam);

		this.initSpinners();
		this.initButtons();

		// Prefill with existing game values if any
		if (mGame != null && !mGame.getTeamList().isEmpty()) {
			// mNumberOfTeams.setText(String.valueOf(mGame.getTeamList().size()));

			// spinnerNumberOfTeams.set
		}

	}

	private void initSpinners() {
		spinnerNumberOfTeams = (Spinner) findViewById(R.id.spinnerNumberOfTeams);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapterNumberOfTeams = ArrayAdapter.createFromResource(this,
				R.array.array_number_of_teams, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapterNumberOfTeams.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinnerNumberOfTeams.setAdapter(adapterNumberOfTeams);

		spinnerNumberOfCards = (Spinner) findViewById(R.id.spinnerNumberOfCards);
		ArrayAdapter<CharSequence> adapterNumberOfCards = ArrayAdapter.createFromResource(this,
				R.array.array_number_of_cards, android.R.layout.simple_spinner_item);
		adapterNumberOfTeams.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerNumberOfCards.setAdapter(adapterNumberOfCards);
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
		mDebugMode = (CheckBox) findViewById(R.id.checkBoxDebugMode);
		mDebugMode.setOnClickListener(this);

		// mButtonAddTeam = (Button) findViewById(R.id.buttonAddTeam);
		// mButtonAddTeam.setOnClickListener(this);

		if (mGame == null || mGame.isGameOver() || mGame.getCardListForGame() == null || mGame.getCardListForGame().isEmpty()) {
			mButtonContinueGame.setVisibility(View.GONE);
			mButtonNewGame.setVisibility(View.VISIBLE);
		} else {
			mButtonContinueGame.setVisibility(View.VISIBLE);
			mButtonNewGame.setVisibility(View.VISIBLE);
		}
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
				// confirmation = displayConfirmation();
			}

			if (confirmation) {
				Constants.RunMode runMode = Constants.RunMode.DB;
				if (mDebugMode.isChecked()) {
					runMode = Constants.RunMode.DEBUG;
				}
				startNewGame(runMode);
			}
		} else if (v.getId() == mButtonContinueGame.getId()) {
			// Go directly to Score activity
			Intent intent = new Intent(this, ScoreActivity.class);
			startActivityForResult(intent, Constants.GAME_NEW);
		}
	}

	private void startNewGame(Constants.RunMode runMode) {
		String numberOfTeams = spinnerNumberOfTeams.getSelectedItem().toString();
		String numberOfCards = spinnerNumberOfCards.getSelectedItem().toString();

		mGameManager.startNewGame(runMode, Integer.valueOf(numberOfTeams), Integer.valueOf(numberOfCards), mResources,
				context);
		mGame = mGameManager.getGame();
		Intent intent = new Intent(this, ScoreActivity.class);
		startActivityForResult(intent, Constants.GAME_NEW);
	}

	/**
	 * TODO Not working, dialog is still showing up when going back to the
	 * activity.
	 * 
	 * @return
	 */
	// private boolean displayConfirmation() {
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// builder.setMessage(R.string.dialog_overwrite_current_game_confirm);
	// builder.setCancelable(false)
	// .setPositiveButton(String.format(mResources.getString(R.string.dialog_confirm_yes)),
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int id) {
	// // Start new game
	// confirmNewGame = true;
	// }
	// })
	// .setNegativeButton(String.format(mResources.getString(R.string.dialog_confirm_no)),
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int id) {
	// // Do not start a new game
	// confirmNewGame = false;
	// }
	// });
	// AlertDialog alert = builder.create();
	// alert.setTitle(R.string.dialog_overwrite_current_game);
	// alert.show();
	// return confirmNewGame;
	// }

}
