package org.spacedown.activity.db;

import java.util.List;

import org.spacedown.R;
import org.spacedown.SpacedownApp;
import org.spacedown.activity.create.CreateCardActivity;
import org.spacedown.adapter.DBCardAdapter;
import org.spacedown.database.CardsDataSource;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Card;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class DBDisplayCardsActivity extends Activity implements OnClickListener {

	private SpacedownApp app;

	private Resources mResources;

	private ListView dbCardList;

	private TextView numberOfCards;

	private CardsDataSource datasource;

	private long selectedCardId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = ((SpacedownApp) getApplication());

		setContentView(R.layout.activity_display_db_cards);
		mResources = getResources();

		datasource = new CardsDataSource(app.getApplicationContext());

		this.refreshActivity();

		if (dbCardList != null) {
			dbCardList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Card card = (Card) parent.getItemAtPosition(position);
					Intent intent = new Intent(parent.getContext(), CreateCardActivity.class);
					intent.putExtra(Constants.CARD_ID, card.getId());
					startActivityForResult(intent, Constants.CARD_UPDATE);
				}
			});
		}
	}

	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 * 
	 *      Builds the contextual menu when long press on a player.
	 * 
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_db_display_cards_context, menu);

		// Display player name as header
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		Card card = (Card) dbCardList.getAdapter().getItem(info.position);
		menu.setHeaderTitle(card.getNameToFind());
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Card card = (Card) dbCardList.getAdapter().getItem(info.position);
		selectedCardId = card.getId();
		switch (item.getItemId()) {
		case R.id.action_update_card:
			Intent intent = new Intent(this, CreateCardActivity.class);
			intent.putExtra(Constants.CARD_ID, selectedCardId);
			startActivityForResult(intent, Constants.CARD_UPDATE);
			return true;
		case R.id.action_delete_card:
			this.warnCancel();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void warnCancel() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(String.format(mResources.getString(R.string.db_cancel_card_question)));
		builder.setCancelable(false)
				.setPositiveButton(String.format(mResources.getString(R.string.dialog_confirm_yes)),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								datasource.deleteCard(selectedCardId);
								DBDisplayCardsActivity.this.refreshActivity();
							}
							
						})
				.setNegativeButton(String.format(mResources.getString(R.string.dialog_confirm_no)),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.refreshActivity();
	}

	private void refreshActivity() {
		initTexts();
		refreshListCards();
		initButtons();
	}

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
	private void refreshListCards() {
		boolean displayActiveCards = this.getIntent().getBooleanExtra(Constants.CARD_ACTIVE, Boolean.TRUE);
		List<Card> listCards = datasource.getAllCards(displayActiveCards);
		if (listCards != null && !listCards.isEmpty() && listCards.size() < Constants.MAX_CARDS_LIST) {
			dbCardList = (ListView) findViewById(R.id.dbCardList);
			DBCardAdapter dbCardAdapter = new DBCardAdapter(this, R.layout.layout_stats_card_row);
			for (Card card : listCards) {
				dbCardAdapter.addItem(card);
			}

			dbCardList.setAdapter(dbCardAdapter);
			registerForContextMenu(dbCardList);
			numberOfCards.setText(String.format(mResources.getString(R.string.db_number_cards), listCards.size()));
		} else {
			numberOfCards.setText(String.format(mResources.getString(R.string.db_no_card)));
			if (dbCardList != null) {
				dbCardList.setVisibility(View.GONE);
			}
		}
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
