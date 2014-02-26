package org.uptime.activity;

import org.uptime.R;
import org.uptime.UpTimeApp;
import org.uptime.activity.create.AddPlayer;
import org.uptime.adapter.PlayerListAdapter;
import org.uptime.database.UpTimeContentProvider;
import org.uptime.database.UpTimeContentProvider.Schema;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;

public class ListPlayer extends ListActivity implements OnClickListener {

	/**
	 * Reference to Application object
	 */
	private UpTimeApp app;

	private Resources mResources;

	private Button buttonNewEquip;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// reference to application object
		app = ((UpTimeApp) getApplication());

		mResources = getResources();

		setContentView(R.layout.player_list);

		getListView().setEmptyView(findViewById(R.id.player_list_empty));
		registerForContextMenu(getListView());

		buttonNewEquip = (Button) findViewById(R.id.createNewPlayer);
		buttonNewEquip.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		Cursor cursor = getContentResolver().query(UpTimeContentProvider.CONTENT_URI_PLAYERS, null, null, null, null);
		startManagingCursor(cursor);
		setListAdapter(new PlayerListAdapter(app, cursor));
		// undo change from onPause
		getListView().setEmptyView(findViewById(R.id.player_list_empty));

		super.onResume();
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == buttonNewEquip.getId()) {

			Intent i = new Intent(this, AddPlayer.class);
			// using Bundle to pass equipment id into new activity
			Bundle b = new Bundle();
			b.putLong(Schema.COL_PLAYER_ID, 0);
			i.putExtras(b);
			startActivity(i);
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.player_list_contextmenu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		Intent i;

		switch (item.getItemId()) {
		case R.id.player_list_contextmenu_edit:

			i = new Intent(this, AddPlayer.class);
			// using Bundle to pass track id into new activity
			Bundle b = new Bundle();
			b.putLong(Schema.COL_PLAYER_ID, info.id);
			i.putExtras(b);
			startActivity(i);

			return true;
		case R.id.player_list_contextmenu_delete:

			// Confirm and delete selected track
			new AlertDialog.Builder(this).setTitle(R.string.player_list_delete_dialog_title)
					.setMessage(String.format(mResources.getString(R.string.confirm)))
					.setCancelable(true).setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							deleteEquipment(info.id);
							dialog.dismiss();
						}
					}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).create().show();

			break;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * User has clicked a player.
	 * 
	 * @param lv
	 *            listview; this
	 * @param iv
	 *            item clicked
	 * @param position
	 *            position within list
	 * @param id
	 *            track ID
	 */
	@Override
	protected void onListItemClick(ListView lv, View iv, final int position, final long id) {
		Intent i = new Intent(this, AddPlayer.class);
		// using Bundle to pass equipment id into new activity
		Bundle b = new Bundle();
		b.putLong(Schema.COL_PLAYER_ID, id);
		i.putExtras(b);
		startActivity(i);
	}

	/**
	 * Deletes the equipment with the specified id from DB
	 * 
	 * @param The
	 *            ID of the equipment to be deleted
	 */
	private void deleteEquipment(long id) {
		getContentResolver().delete(ContentUris.withAppendedId(UpTimeContentProvider.CONTENT_URI_PLAYERS, id), null,
				null);
		((CursorAdapter) ListPlayer.this.getListAdapter()).getCursor().requery();
	}

}
