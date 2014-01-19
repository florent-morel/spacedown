package org.uptime.activity.db;

import java.util.List;

import org.uptime.R;
import org.uptime.activity.create.CreateCardActivity;
import org.uptime.adapter.DBCardAdapter;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Card;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import database.CardsDataSource;

public class DBDisplayCardsActivity extends Activity implements OnClickListener {

	private Resources mResources;

	private ListView dbCardList;
	private TextView numberOfCards;

	private DBCardAdapter dbCardAdapter;

	private CardsDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_db_cards);
		mResources = getResources();

		datasource = new CardsDataSource(this);
		datasource.open();
		this.refreshActivity();

		dbCardList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Card card = (Card) parent.getItemAtPosition(position);
				Intent intent = new Intent(parent.getContext(), CreateCardActivity.class);
				intent.putExtra(Constants.CARD_ID, card.getId());
				startActivityForResult(intent, Constants.CARD_UPDATE);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		this.refreshActivity();
	}

	private void refreshActivity() {
		initTexts();

		List<Card> listCards = datasource.getAllCards();
		if (listCards != null && !listCards.isEmpty()) {
			numberOfCards.setText(String.format(mResources.getString(R.string.db_number_cards), listCards.size()));
			if (listCards.size() < 25) {
				refreshList(listCards);
			} else {
				// Need to filter for display purpose
			}
		} else {
			numberOfCards.setText(String.format(mResources.getString(R.string.db_no_card)));
		}
		initButtons();}

	private void initTexts() {
		numberOfCards = (TextView) findViewById(R.id.textDbDisplayCard);
	}

	private void initButtons() {
		// mButtonNextTurn = (Button) findViewById(R.id.buttonNextTurn);
		// mButtonNextTurn.setOnClickListener(this);
		//
		// if (game.getCardsInPlay().isEmpty()) {
		// mButtonNextTurn.setText(String.format(mResources.getString(R.string.stats_end_round),
		// game
		// .getCurrentRound().getRoundNumber()));
		// } else {
		// mButtonNextTurn.setText(String.format(mResources.getString(R.string.stats_next_turn),
		// game
		// .getNextTeamToPlay().getName()));
		// }
	}

	/**
	 * Refresh the score board player list.
	 * 
	 * @param listCards
	 * 
	 * @param descending
	 */
	private void refreshList(List<Card> listCards) {

		dbCardList = (ListView) findViewById(R.id.dbCardList);
		dbCardAdapter = new DBCardAdapter(this, R.layout.layout_stats_card_row);

		if (listCards != null && !listCards.isEmpty()) {
			for (Card card : listCards) {
				dbCardAdapter.addItem(card);
			}
		}

		dbCardList.setAdapter(dbCardAdapter);
		registerForContextMenu(dbCardList);
	}

	// public void onBackPressed() {
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// if (game.getCardsInPlay().isEmpty()) {
	// builder.setMessage(String.format(mResources.getString(R.string.stats_end_round_question),
	// game
	// .getCurrentRound().getRoundNumber()));
	// } else {
	// builder.setMessage(String.format(mResources.getString(R.string.stats_next_turn_question),
	// game
	// .getNextTeamToPlay().getName()));
	// }
	// builder.setCancelable(false)
	// .setPositiveButton(String.format(mResources.getString(R.string.dialog_confirm_yes)),
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int id) {
	// finish();
	// }
	// })
	// .setNegativeButton(String.format(mResources.getString(R.string.dialog_confirm_no)),
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int id) {
	// dialog.cancel();
	// }
	// });
	// AlertDialog alert = builder.create();
	// alert.show();
	// }

	@Override
	public void onClick(View v) {
		// if (v.getId() == mButtonNextTurn.getId()) {
		// // Finish activity
		// finish();
		// }
	}

}
