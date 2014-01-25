package org.uptime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Date;

import org.uptime.activity.create.CreateCardActivity;
import org.uptime.activity.create.CreateGameActivity;
import org.uptime.activity.db.FilterDBCardsActivity;
import org.uptime.activity.db.ImportCardsInDBActivity;
import org.uptime.engine.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * LaCrise, a score handling application.
 * 
 * Copyright (C) 2014 Florent Morel.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * LaCrise home activity.
 * 
 * @author florent
 * 
 */
public class UpTimeMainActivity extends Activity implements OnClickListener {

	/**
	 * Reference to Application object
	 */
	private UpTimeApp app;

	private String importDatabaseFileName;

	private Handler mainHandler = new Handler();

	// private GameManager mGameManager;

	private Resources mResources;

	private Button mButtonNewGame;
	private Button mButtonCreateCard;
	private Button mButtonDBListCard;
	private Button mButtonImportCardsInDB;

	// private Button mButtonQuickGame;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// reference to application object
		app = ((UpTimeApp) getApplication());

		mResources = getResources();

		setContentView(R.layout.activity_main);

		mButtonNewGame = (Button) findViewById(R.id.buttonNewGame);
		mButtonNewGame.setOnClickListener(this);

		mButtonCreateCard = (Button) findViewById(R.id.buttonCreateCard);
		mButtonCreateCard.setOnClickListener(this);

		mButtonDBListCard = (Button) findViewById(R.id.buttonDBListCard);
		mButtonDBListCard.setOnClickListener(this);

		mButtonImportCardsInDB = (Button) findViewById(R.id.buttonImportCards);
		mButtonImportCardsInDB.setOnClickListener(this);

		// mButtonQuickGame = (Button) findViewById(R.id.buttonNewGame);
		// mButtonQuickGame.setOnClickListener(this);

		// mGameManager = GameManager.getSingletonObject();
	}

	private void buildMenu(Menu menu, boolean isOnCreate) {
		if (isOnCreate) {
			// menu.add(0, Constants.GAME_NEW, 0, R.string.menu_new);
			// menu.add(0, Constants.NEW_GAME_QUICK, 1,
			// R.string.menu_new_quick);
		}

		// if (!mGameManager.getGame().getPlayerList().isEmpty()) {
		// // A game is ongoing, add menu to continue
		// if (menu.findItem(Constants.GAME_CONTINUE) == null) {
		// menu.add(0, Constants.GAME_CONTINUE, 2, R.string.menu_continue);
		// }
		// } else {
		// if (menu.findItem(Constants.GAME_CONTINUE) != null) {
		// menu.removeItem(Constants.GAME_CONTINUE);
		// }
		// }
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		super.onMenuOpened(featureId, menu);
		buildMenu(menu, false);
		return true;
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// super.onCreateOptionsMenu(menu);
	// buildMenu(menu, true);
	// return true;
	// }

	/**
	 * onCreateOptionsMenu handler
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Process main activity menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle item selection
		switch (item.getItemId()) {

		// case R.id.compassMenuItem:
		//
		// startActivity(new Intent(this, CompassActivity.class));
		//
		// return true;

		case R.id.aboutMenuItem:

			this.showAboutDialog();

			return true;

		case R.id.settingsMenuItem:

			// startActivity(new Intent(this, SettingsActivity.class));

			return true;

		case R.id.quickHelp:

			// showQuickHelp();

			return true;

		case R.id.backupMenuItem:

			backupDatabase();
			return true;

		case R.id.restoreMenuItem:

			restoreDatabase();
			return true;

			// case R.id.scheduledRecordingMenuItem:
			//
			// this.startStopScheduledTrackRecording();
			// return true;

		default:

			return super.onOptionsItemSelected(item);

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// StringBuilder message = new StringBuilder();
		// Turn playedTurn = null;
		// Player player = mGameManager.getCurrentPlayer();
		// if (player != null) {
		// playedTurn = player.getCurrentTurn();
		//
		switch (resultCode) {
		case RESULT_OK:
			break;
		case Constants.RESULT_ROUND_END:
			// displayScores();
			break;
		case Constants.RESULT_GAME_OVER:
			// buildGameOverMessage(player, playedTurn, message);
			break;

		default:
			// TODO Something went wrong
			break;
		}
		//
		// // In any case, build the zero, score and next player messages
		// this.buildZeroMessage(player, message);
		// this.buildScoreMessage(player, message);
		//
		// if (!mGameManager.getGame().isGameOver()) {
		// StringBuilder nextMessage = new StringBuilder();
		// this.buildNextPlayerMessage(nextMessage);
		//
		// Toast toast = Toast.makeText(this, nextMessage.toString(),
		// Toast.LENGTH_LONG);
		// toast.show();
		// }
		//
		// // Create the dialog
		// createAlert(message.toString());
		// }
		//
		// refreshList();
	}

	private void launchNewGame() {
		Intent intent = new Intent(this, CreateGameActivity.class);
		startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
	}

	// private void displayScores() {
	// Intent intent = new Intent(this, ScoreActivity.class);
	// startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
	// }

	@Override
	public void onClick(View v) {

		if (v.getId() == mButtonNewGame.getId()) {
			launchNewGame();
		} else if (v.getId() == mButtonCreateCard.getId()) {
			Intent intent = new Intent(this, CreateCardActivity.class);
			startActivityForResult(intent, Constants.CARD_CREATE);
		} else if (v.getId() == mButtonDBListCard.getId()) {
			Intent intent = new Intent(this, FilterDBCardsActivity.class);
			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
		} else if (v.getId() == mButtonImportCardsInDB.getId()) {
			Intent intent = new Intent(this, ImportCardsInDBActivity.class);
			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
		}

	}

	/**
	 * Copy application database to sd card
	 */
	private void backupDatabase() {

		try {

			File data = Environment.getDataDirectory();

			if (app.getExternalStorageWriteable()) {

				String currentDBPath = app.getDataDir() + Constants.SLASH + Constants.DATABASE_FILE;

				String dateStr = (String) DateFormat.format("yyyyMMdd_kkmmss", new Date());

				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(app.getAppDir() + Constants.SLASH + Constants.PATH_BACKUP + Constants.SLASH
						+ dateStr + Constants.UNDERSCORE + Constants.DATABASE_FILE);

				if (currentDB.exists()) {

					FileInputStream fis = new FileInputStream(currentDB);
					FileOutputStream fos = new FileOutputStream(backupDB);

					FileChannel src = fis.getChannel();
					FileChannel dst = fos.getChannel();
					dst.transferFrom(src, 0, src.size());

					src.close();
					dst.close();
					fis.close();
					fos.close();

					Toast.makeText(UpTimeMainActivity.this,
							getString(R.string.backup_completed) + Constants.SPACE + backupDB.getPath(),
							Toast.LENGTH_LONG).show();

				} else {
					Toast.makeText(
							UpTimeMainActivity.this,
							String.format(mResources.getString(R.string.backup_error),
									mResources.getString(R.string.backup_error_source_not_found)), Toast.LENGTH_LONG)
							.show();
				}

			}
		}

		catch (Exception e) {

			Log.e(Constants.TAG, e.getMessage());

			Toast.makeText(UpTimeMainActivity.this,
					getString(R.string.backup_error) + Constants.SPACE + e.getMessage(), Toast.LENGTH_LONG).show();

		}

	}

	/**
	 * Restoring database from previously saved copy
	 */
	private void restoreDatabase() {

		// show "select a file" dialog
		File importFolder = new File(app.getAppDir() + Constants.SLASH + Constants.PATH_BACKUP + Constants.SLASH);
		final String importFiles[] = importFolder.list();

		if (importFiles == null || importFiles.length == 0) {
			Toast.makeText(UpTimeMainActivity.this, R.string.source_folder_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		importDatabaseFileName = importFiles[0];

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_file);
		builder.setSingleChoiceItems(importFiles, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

				importDatabaseFileName = importFiles[whichButton];
				mainHandler.post(restoreDatabaseRunnable);

				dialog.dismiss();

			}
		});

		AlertDialog alert = builder.create();
		alert.show();

	}

	/**
	 * Runnable performing restoration of the application database from external
	 * source
	 */
	private Runnable restoreDatabaseRunnable = new Runnable() {
		@Override
		public void run() {

			String backupPath = app.getAppDir() + Constants.SLASH + Constants.PATH_BACKUP + Constants.SLASH;
			try {
				// open database in readonly mode
				SQLiteDatabase db = SQLiteDatabase.openDatabase(backupPath + importDatabaseFileName, null,
						SQLiteDatabase.OPEN_READONLY);

				// check version compatibility
				// only same version of the db can be restored
				if (app.getDatabase().getVersion() != db.getVersion()) {
					Toast.makeText(UpTimeMainActivity.this, getString(R.string.restore_db_version_conflict),
							Toast.LENGTH_LONG).show();
					return;
				}

				db.close();

			} catch (SQLiteException e) {

				Toast.makeText(UpTimeMainActivity.this,
						String.format(mResources.getString(R.string.restore_file_error), e.getMessage()),
						Toast.LENGTH_LONG).show();

				return;
			}

			// closing current db
			app.getDatabase().close();

			try {

				File data = Environment.getDataDirectory();

				if (app.getExternalStorageWriteable()) {

					String restoreDBPath = backupPath + importDatabaseFileName;

					File restoreDB = new File(restoreDBPath);
					// File currentDB = new File(app.getDataDir(),
					// Constants.DATABASE_FILE);
					File currentDB = new File(data, app.getDataDir() + Constants.SLASH + Constants.DATABASE_FILE);

					FileInputStream fis = new FileInputStream(restoreDB);
					FileOutputStream fos = new FileOutputStream(currentDB);

					FileChannel src = fis.getChannel();
					FileChannel dst = fos.getChannel();

					dst.transferFrom(src, 0, src.size());

					src.close();
					dst.close();
					fis.close();
					fos.close();

					app.setDatabase();

					Toast.makeText(UpTimeMainActivity.this, getString(R.string.restore_completed), Toast.LENGTH_SHORT)
							.show();

				}

			} catch (Exception e) {

				Log.e(Constants.TAG, e.getMessage());

				app.setDatabase();

				Toast.makeText(UpTimeMainActivity.this, getString(R.string.restore_error) + ": " + e.getMessage(),
						Toast.LENGTH_LONG).show();

			}

		}
	};

	/**
	 * About dialog
	 */
	private void showAboutDialog() {

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.about_dialog, null);

		TextView buildDate = (TextView) layout.findViewById(R.id.build_date);
		buildDate.setText(String.format(mResources.getString(R.string.build_date),
				mResources.getString(R.string.app_build_date)));

		TextView versionView = (TextView) layout.findViewById(R.id.version);
		versionView.setText(getString(R.string.main_app_title) + Constants.SPACE + getString(R.string.ver)
				+ UpTimeApp.getVersionName(this));

		TextView messageView = (TextView) layout.findViewById(R.id.message);

		String aboutStr = getString(R.string.about_dialog_message);
		// adding links to "about" text
		final SpannableString s = new SpannableString(aboutStr);
		Linkify.addLinks(s, Linkify.ALL);

		messageView.setText(s + getString(R.string.acknowledgements));

		messageView.setMovementMethod(LinkMovementMethod.getInstance());

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.about);
		// builder.setIcon(R.drawable.icon);
		builder.setView(layout);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});

		builder.setCancelable(true);

		AlertDialog alert = builder.create();
		alert.show();

	}

}