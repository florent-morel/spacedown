package org.spacedown;

import org.spacedown.activity.ListPlayer;
import org.spacedown.activity.PreferencesActivity;
import org.spacedown.activity.create.CreateCardActivity;
import org.spacedown.activity.create.CreateGameActivity;
import org.spacedown.activity.db.FilterDBCardsActivity;
import org.spacedown.activity.db.ImportCardsInDBActivity;
import org.spacedown.engine.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * spacedown, a card game application.
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
public class SpacedownMainActivity extends Activity implements OnClickListener {

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

	}

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

		case R.id.aboutMenuItem:
			this.showAboutDialog();
			return true;

		case R.id.quickHelp:
			// showQuickHelp();
			return true;

		case R.id.settingsMenuItem:
			 startActivity(new Intent(this, PreferencesActivity.class));
			return true;
			
		default:

			return super.onOptionsItemSelected(item);

		}

	}

	private void launchNewGame() {
		Intent intent = new Intent(this, CreateGameActivity.class);
		startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
	}

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
				+ SpacedownApp.getVersionName(this));

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