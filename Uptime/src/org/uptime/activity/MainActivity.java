package org.uptime.activity;

import org.uptime.R;
import org.uptime.engine.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@Deprecated
public class MainActivity extends Activity implements OnClickListener {

	private Button mButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mButton = (Button) findViewById(R.id.button1);
		
		mButton.setOnClickListener(this);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, Constants.MENU_GAME_OPTIONS, 0, R.string.menu_game_options);
		menu.add(0, Constants.MENU_START_NEW, 0, R.string.menu_start_new_game);
//		menu.add(0, Constants.MENU_SCORE_CHART, 0, R.string.menu_score_chart);
//		menu.add(0, Constants.MENU_SIMULATE_ROUNDS, 0, "simulate rounds");

		return true;
	}
	
	public void onClick(View v) {

		if (v.getId() == mButton.getId()) {
			launchNewGame();
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
//		case Constants.MENU_GAME_OPTIONS:
//			launchGameOptions();
//			return true;
		case Constants.MENU_START_NEW:
			launchNewGame();
			return true;
//		case Constants.MENU_SCORE_CHART:
//			launchScoreChart();
//			return true;
//		case Constants.MENU_SIMULATE_ROUNDS:
//			simulateRounds();
//			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}


	private void launchNewGame() {
		Intent intent = new Intent(this, CardActivity.class);
		startActivityForResult(intent, Constants.ACTIVITY_LAUNCH);
	}
}
