package org.uptime;


import org.uptime.activity.create.CreateCardActivity;
import org.uptime.activity.create.CreateGameActivity;
import org.uptime.activity.db.FilterDBCardsActivity;
import org.uptime.engine.Constants;
import org.uptime.engine.cards.build.CardBuilder;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * LaCrise, a score handling application.   
 *    
 * Copyright (C) 2012 Florent Morel.
 *    
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or 
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * LaCrise home activity. 
 * 
 * @author florent
 *
 */
public class UpTime extends Activity implements OnClickListener {

//	private GameManager mGameManager;

	private Resources mResources;
	private Button mButtonNewGame;
	private Button mButtonCreateCard;
	private Button mButtonDBListCard;
	private Button mButtonImportCardsInDB;
//	private Button mButtonQuickGame;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mResources = getResources();

		mButtonNewGame = (Button) findViewById(R.id.buttonNewGame);
		mButtonNewGame.setOnClickListener(this);

		mButtonCreateCard = (Button) findViewById(R.id.buttonCreateCard);
		mButtonCreateCard.setOnClickListener(this);

		mButtonDBListCard = (Button) findViewById(R.id.buttonDBListCard);
		mButtonDBListCard.setOnClickListener(this);

		mButtonImportCardsInDB = (Button) findViewById(R.id.buttonImportCards);
		mButtonImportCardsInDB.setOnClickListener(this);

//		mButtonQuickGame = (Button) findViewById(R.id.buttonNewGame);
//		mButtonQuickGame.setOnClickListener(this);

//		mGameManager = GameManager.getSingletonObject();
	}

	private void buildMenu(Menu menu, boolean isOnCreate) {
		if (isOnCreate) {
//			menu.add(0, Constants.GAME_NEW, 0, R.string.menu_new);
//			menu.add(0, Constants.NEW_GAME_QUICK, 1, R.string.menu_new_quick);
		}

//		if (!mGameManager.getGame().getPlayerList().isEmpty()) {
//			// A game is ongoing, add menu to continue
//			if (menu.findItem(Constants.GAME_CONTINUE) == null) {
//				menu.add(0, Constants.GAME_CONTINUE, 2, R.string.menu_continue);
//			}
//		} else {
//			if (menu.findItem(Constants.GAME_CONTINUE) != null) {
//				menu.removeItem(Constants.GAME_CONTINUE);
//			}
//		}
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		super.onMenuOpened(featureId, menu);
		buildMenu(menu, false);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		buildMenu(menu, true);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//		StringBuilder message = new StringBuilder();
//		Turn playedTurn = null;
//		Player player = mGameManager.getCurrentPlayer();
//		if (player != null) {
//			playedTurn = player.getCurrentTurn();
//
			switch (resultCode) {
			case RESULT_OK:
				break;
			case Constants.RESULT_ROUND_END:
//				displayScores();
				break;
			case Constants.RESULT_GAME_OVER:
//				buildGameOverMessage(player, playedTurn, message);
				break;

			default:
				// TODO Something went wrong
				break;
			}
//
//			// In any case, build the zero, score and next player messages
//			this.buildZeroMessage(player, message);
//			this.buildScoreMessage(player, message);
//
//			if (!mGameManager.getGame().isGameOver()) {
//				StringBuilder nextMessage = new StringBuilder();
//				this.buildNextPlayerMessage(nextMessage);
//
//				Toast toast = Toast.makeText(this, nextMessage.toString(),
//						Toast.LENGTH_LONG);
//				toast.show();
//			}
//
//			// Create the dialog
//			createAlert(message.toString());
//		}
//
//		refreshList();
	}

	private void launchNewGame() {
		Intent intent = new Intent(this, CreateGameActivity.class);
		startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
	}

//	private void displayScores() {
//		Intent intent = new Intent(this, ScoreActivity.class);
//		startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
//	}

	@Override
	public void onClick(View v) {

		if (v.getId() == mButtonNewGame.getId()) {
			launchNewGame();
		}
		else if (v.getId() == mButtonCreateCard.getId()) {
			Intent intent = new Intent(this, CreateCardActivity.class);
			startActivityForResult(intent, Constants.CARD_CREATE);
		}
		else if (v.getId() == mButtonDBListCard.getId()) {
			Intent intent = new Intent(this, FilterDBCardsActivity.class);
			startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
		}
		else if (v.getId() == mButtonImportCardsInDB.getId()) {
			CardBuilder cardBuilder = new CardBuilder(mResources, this);
			cardBuilder.importHardCodedListToDB();
		}
		


		
	}
}