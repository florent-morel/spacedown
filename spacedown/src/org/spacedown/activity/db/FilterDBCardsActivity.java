package org.spacedown.activity.db;

import java.util.List;

import org.spacedown.R;
import org.spacedown.SpacedownApp;
import org.spacedown.database.CardsDataSource;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Card;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class FilterDBCardsActivity extends Activity implements OnClickListener {

	private SpacedownApp app;

	private Resources mResources;

	private Button buttonProcessRequest;

	private TextView numberOfActiveCards;

	private TextView numberOfInactiveCards;

	private CheckBox activeCard;

	private CardsDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = ((SpacedownApp) getApplication());

		mResources = getResources();
		setContentView(R.layout.activity_filter_db_card);
		setTitle(R.string.create_card_title);

		datasource = new CardsDataSource(app.getApplicationContext());

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

}
