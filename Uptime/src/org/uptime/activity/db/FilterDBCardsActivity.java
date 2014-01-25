package org.uptime.activity.db;

import java.util.List;

import org.uptime.R;
import org.uptime.UpTimeApp;
import org.uptime.database.CardsDataSource;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Card;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class FilterDBCardsActivity extends Activity implements OnClickListener {
	
	private UpTimeApp app;

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

		this.initTexts();
		this.initButtons();

	}

	private void initButtons() {
		buttonProcessRequest = (Button) findViewById(R.id.buttonConfirmFilterCard);
		buttonProcessRequest.setOnClickListener(this);
		activeCard = (CheckBox) findViewById(R.id.checkBoxCardActive);
		activeCard.setOnClickListener(this);
	}

	private void initTexts() {
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
