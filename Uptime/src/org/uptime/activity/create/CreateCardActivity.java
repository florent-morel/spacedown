package org.uptime.activity.create;

import org.uptime.R;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Card;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import database.CardsDataSource;

public class CreateCardActivity extends Activity implements OnClickListener {

	private Resources mResources;

	private Button buttonCreateCard;

	private TextView cardCategory;

	private EditText cardNameToFind;

	private CheckBox activeCard;

	private CardsDataSource datasource;

	private Card cardToUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResources = getResources();
		setContentView(R.layout.activity_create_card);
		setTitle(R.string.create_card_title);

		this.initTexts();
		this.initButtons();

		datasource = new CardsDataSource(this);
		datasource.open();

		// TODO: Prefill with existing game values if any
		int intentCardId = this.getIntent().getIntExtra(Constants.CARD_ID, -1);
		if (intentCardId > -1) {
			cardToUpdate = datasource.getCard(intentCardId);
			if (cardToUpdate != null) {
				cardNameToFind.setText(cardToUpdate.getNameToFind());
				cardCategory.setText(cardToUpdate.getCategory());
				activeCard.setChecked(cardToUpdate.isActiveInDB());
			}
		}
	}

	private void initButtons() {
		buttonCreateCard = (Button) findViewById(R.id.buttonConfirmCreateCard);
		buttonCreateCard.setOnClickListener(this);
		activeCard = (CheckBox) findViewById(R.id.checkBoxCardActive);
		activeCard.setOnClickListener(this);
	}

	private void initTexts() {
		cardCategory = (TextView) findViewById(R.id.textCreateCardCategoryCustom);
		cardNameToFind = (EditText) findViewById(R.id.editCreateCardName);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == buttonCreateCard.getId()) {

			String nameToFind = cardNameToFind.getText().toString();
			String category = cardCategory.getText().toString();

			if (nameToFind != null && !nameToFind.isEmpty()) {
				// Create the card and store in DB
				Card cardToPutInDb = null;
				Card newCard = null;
				String message = null;
				boolean isActive = activeCard.isChecked();
				if (cardToUpdate == null) {
					this.createCard();
					cardToPutInDb = new Card();
					cardToPutInDb.setNameToFind(nameToFind);
					cardToPutInDb.setCategory(category);
					cardToPutInDb.setActiveInDB(isActive);
					newCard = datasource.createCard(cardToPutInDb);
					message = String.format(mResources.getString(R.string.dialog_create_card_ok), newCard.getId());
				} else {
					cardToUpdate.setNameToFind(nameToFind);
					cardToUpdate.setCategory(category);
					cardToUpdate.setActiveInDB(isActive);
					newCard = datasource.updateCard(cardToUpdate);
					message = String.format(mResources.getString(R.string.dialog_update_card_ok), newCard.getId());
				}

				Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
				toast.show();
				finish();
			} else {
				Toast toast = Toast.makeText(this, String.format(mResources.getString(R.string.dialog_create_card_ko)),
						Toast.LENGTH_LONG);
				toast.show();
			}

		}
	}

	private void createCard() {
		// TODO Auto-generated method stub

	}

}
