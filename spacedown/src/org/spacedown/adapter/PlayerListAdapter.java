package org.spacedown.adapter;

import org.spacedown.R;
import org.spacedown.SpacedownApp;
import org.spacedown.engine.game.Player;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for track list in
 * {@link me.guillaumin.android.osmtracker.activity.TrackManager Track Manager}.
 * For each row's contents, see <tt>tracklist_item.xml</tt>.
 * 
 * @author Florent Morel
 * 
 */
public class PlayerListAdapter extends CursorAdapter {
	protected SpacedownApp app;

	public PlayerListAdapter(SpacedownApp app, Cursor c) {
		super(app, c);
		this.app = app;

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Player player = Player.build(cursor, context.getContentResolver());
		bind(player, context, view);
		bindImageStatus(player, view);
	}

	private void bindImageStatus(Player player, View v) {
		ImageView vStatus = (ImageView) v.findViewById(R.id.player_item_statusicon);
		if (player.isActive()) {
			// Green clock icon for Active
			vStatus.setImageResource(android.R.drawable.presence_online);
			vStatus.setVisibility(View.VISIBLE);
		} else {
			// Yellow clock icon for Inactive
			vStatus.setImageResource(android.R.drawable.presence_away);
			vStatus.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup vg) {
		View view = LayoutInflater.from(vg.getContext()).inflate(R.layout.player_item, vg, false);
		return view;
	}

	/**
	 * Do the binding between data and item view.
	 * 
	 * @param cursor
	 *            Cursor to pull data
	 * @param v
	 *            RelativeView representing one item
	 * @param context
	 *            Context, to get resources
	 * @return The relative view with data bound.
	 */
	protected View bind(Player player, Context context, View v) {
		TextView vId = (TextView) v.findViewById(R.id.player_item_id);
		TextView vName = (TextView) v.findViewById(R.id.player_item_name);

		// Bind id
		String strEquipId = Long.toString(player.getId());
		vId.setText(strEquipId);

		vName.setText(player.getName());

		return v;
	}

}
