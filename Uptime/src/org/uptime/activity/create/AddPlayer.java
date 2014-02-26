package org.uptime.activity.create;

import org.uptime.R;
import org.uptime.UpTimeApp;
import org.uptime.database.PlayersDataSource;
import org.uptime.database.UpTimeContentProvider;
import org.uptime.database.UpTimeContentProvider.Schema;
import org.uptime.engine.game.Player;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AddPlayer extends Activity implements OnClickListener {

	private static final String TAG = AddPlayer.class.getSimpleName();

	/**
	 * Reference to app object
	 */
	private UpTimeApp app;

	private PlayersDataSource playerDS;

	private Resources mResources;

	private Player player;

	private Button buttonSavePlayer;

	private EditText createPlayerName;

	private CheckBox createEqActive;

	/**
	 * Initialize activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		app = ((UpTimeApp) getApplicationContext());

		mResources = getResources();

		playerDS = new PlayersDataSource(app.getApplicationContext());

		setContentView(R.layout.add_player);

		this.updateActivity();
		
		Bundle b = getIntent().getExtras();
		// Try to see if we are called with an existing player
		long playerId = b.getLong(Schema.COL_PLAYER_ID);
		if (playerId != 0) {
			ContentResolver contentResolver = getContentResolver();
			Uri playerUri = ContentUris.withAppendedId(UpTimeContentProvider.CONTENT_URI_PLAYERS, playerId);
			Cursor cursor = contentResolver.query(playerUri, null, null, null, null);

			if (cursor.moveToFirst()) {
				player = Player.build(cursor, contentResolver);
				if (player != null) {
					this.prefillActivity();
				}
			}
		}
	}

	private void prefillActivity() {
		createPlayerName.setText(player.getName());
		createEqActive.setChecked(player.isActive());
	}

	private void updateActivity() {
		this.setTitle(String.format(mResources.getString(R.string.display_player_menu)));
		createPlayerName = (EditText) findViewById(R.id.addPlayerNameEdit);
		createEqActive = (CheckBox) findViewById(R.id.checkCreatePlayerActive);

		buttonSavePlayer = (Button) findViewById(R.id.buttonSavePlayer);
		buttonSavePlayer.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == buttonSavePlayer.getId()) {
			updatePlayer();
		}

	}

	private void updatePlayer() {
		if (player == null) {
			player = new Player();
			player.setName("");
			player.setActive(Boolean.TRUE);
			player.setId(playerDS.createPlayer(player));
		}

		player.setName(createPlayerName.getText().toString());
		player.setActive(createEqActive.isChecked());

		int affectedRows = playerDS.updatePlayer(player);
		if (affectedRows == 0) {
			Toast.makeText(app, "update player failed", Toast.LENGTH_SHORT).show();
			Log.v(TAG, "updateEquipment updates failed: " + player.toString());
		} else {
			finish();
		}
	}

}
