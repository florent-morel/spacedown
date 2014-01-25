package org.uptime.activity.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.List;

import org.uptime.R;
import org.uptime.UpTimeApp;
import org.uptime.database.CardsDataSource;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Card;

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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class FilterDBCardsActivity extends Activity implements OnClickListener {

	private UpTimeApp app;

	private String importDatabaseFileName;

	private Handler mainHandler = new Handler();

	private Resources mResources;

	private Button buttonProcessRequest;

	private TextView numberOfActiveCards;

	private TextView numberOfInactiveCards;

	private CheckBox activeCard;

	private CardsDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = ((UpTimeApp) getApplication());

		mResources = getResources();
		setContentView(R.layout.activity_filter_db_card);
		setTitle(R.string.create_card_title);

		datasource = new CardsDataSource(app.getDatabase(), app.getDbHelper());

		this.refreshActivity();

	}

	/**
	 * onCreateOptionsMenu handler
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_db_display, menu);
		return true;
	}

	/**
	 * Process main activity menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle item selection
		switch (item.getItemId()) {

		case R.id.settingsMenuItem:
			// startActivity(new Intent(this, SettingsActivity.class));

			return true;

		case R.id.backupMenuItem:
			backupDatabase();
			return true;

		case R.id.restoreMenuItem:
			restoreDatabase();
			return true;

		default:
			return super.onOptionsItemSelected(item);

		}

	}

	@Override
	public void onResume() {
		super.onResume();
		this.refreshActivity();
	}

	private void refreshActivity() {
		this.initCardTexts();
		this.initButtons();
	}

	private void initButtons() {
		buttonProcessRequest = (Button) findViewById(R.id.buttonConfirmFilterCard);
		buttonProcessRequest.setOnClickListener(this);
		activeCard = (CheckBox) findViewById(R.id.checkBoxCardActive);
		activeCard.setOnClickListener(this);
	}

	private void initCardTexts() {
		datasource = new CardsDataSource(app.getDatabase(), app.getDbHelper());
		numberOfActiveCards = (TextView) findViewById(R.id.textFilterDbDisplayActiveCards);
		numberOfInactiveCards = (TextView) findViewById(R.id.textFilterDbDisplayInactiveCards);

		List<Card> listActiveCards = datasource.getAllCards(true);
		if (listActiveCards != null && !listActiveCards.isEmpty()) {
			numberOfActiveCards.setText(String.format(mResources.getString(R.string.db_number_cards),
					listActiveCards.size()));
		} else {
			numberOfActiveCards.setText(String.format(mResources.getString(R.string.db_no_active_card)));
		}

		List<Card> listInactiveCards = datasource.getAllCards(false);
		if (listInactiveCards != null && !listInactiveCards.isEmpty()) {
			numberOfInactiveCards.setText(String.format(mResources.getString(R.string.db_number_cards_inactive),
					listInactiveCards.size()));
		} else {
			numberOfInactiveCards.setText(String.format(mResources.getString(R.string.db_no_inactive_card)));
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == buttonProcessRequest.getId()) {
			Intent intent = new Intent(this, DBDisplayCardsActivity.class);
			intent.putExtra(Constants.CARD_ACTIVE, activeCard.isChecked());
			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
		}
	}

	// All this backup / restore section is dupe from main activity and should be exported.
	
	
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

					Toast.makeText(this,
							getString(R.string.backup_completed) + Constants.SPACE + backupDB.getPath(),
							Toast.LENGTH_LONG).show();

				} else {
					Toast.makeText(
							this,
							String.format(mResources.getString(R.string.backup_error),
									mResources.getString(R.string.backup_error_source_not_found)), Toast.LENGTH_LONG)
							.show();
				}

			}
		}

		catch (Exception e) {

			Log.e(Constants.TAG, e.getMessage());

			Toast.makeText(FilterDBCardsActivity.this,
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
			Toast.makeText(FilterDBCardsActivity.this, R.string.source_folder_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		importDatabaseFileName = importFiles[0];

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_file);
		builder.setSingleChoiceItems(importFiles, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

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
					Toast.makeText(FilterDBCardsActivity.this, getString(R.string.restore_db_version_conflict),
							Toast.LENGTH_LONG).show();
					return;
				}

				db.close();

			} catch (SQLiteException e) {

				Toast.makeText(FilterDBCardsActivity.this,
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

					Toast.makeText(FilterDBCardsActivity.this, getString(R.string.restore_completed), Toast.LENGTH_SHORT)
							.show();

				}

			} catch (Exception e) {

				Log.e(Constants.TAG, e.getMessage());

				app.setDatabase();

				Toast.makeText(FilterDBCardsActivity.this, getString(R.string.restore_error) + ": " + e.getMessage(),
						Toast.LENGTH_LONG).show();

			}

		}
	};

}
