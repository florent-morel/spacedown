package org.uptime.activity.db;

import java.util.List;

import org.uptime.R;
import org.uptime.engine.cards.build.CardBuilder;
import org.uptime.engine.game.Card;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import database.CardsDataSource;

public class ImportCardsInDBActivity extends Activity implements OnClickListener {

	private Resources mResources;

	private Button buttonProcessRequest;

	private TextView numberOfActiveCards;

	private TextView numberOfInactiveCards;

	private CheckBox keepCustomCards;

	private CardsDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResources = getResources();
		setContentView(R.layout.activity_import_card_in_db);
		setTitle(R.string.create_card_title);

		datasource = new CardsDataSource(this);
		datasource.open();
		this.refreshActivity();

	}

	private void refreshActivity() {
		this.initTexts();
		this.initButtons();
	}

	private void initButtons() {
		buttonProcessRequest = (Button) findViewById(R.id.buttonConfirmFilterCard);
		buttonProcessRequest.setOnClickListener(this);
		keepCustomCards = (CheckBox) findViewById(R.id.checkBoxKeepCustomCards);
		keepCustomCards.setOnClickListener(this);
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
			CardBuilder cardBuilder = new CardBuilder(mResources, this);
			List<Card> importHardCodedListToDB = cardBuilder.importHardCodedListToDB(keepCustomCards.isChecked());
			if (importHardCodedListToDB != null && !importHardCodedListToDB.isEmpty()) {
				Toast toast = Toast.makeText(
						this,
						String.format(mResources.getString(R.string.dialog_import_cards_ok),
								importHardCodedListToDB.size()), Toast.LENGTH_LONG);
				toast.show();
			} else {
				Toast toast = Toast.makeText(this,
						String.format(mResources.getString(R.string.dialog_import_cards_ko)), Toast.LENGTH_LONG);
				toast.show();
			}
			this.refreshActivity();
		}
	}

}
