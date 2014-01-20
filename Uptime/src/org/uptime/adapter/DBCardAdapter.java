package org.uptime.adapter;

import java.util.ArrayList;
import java.util.List;

import org.uptime.R;
import org.uptime.engine.game.Card;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DBCardAdapter extends ArrayAdapter<Card> {

	private List<Card> mItems = new ArrayList<Card>();

	public LayoutInflater mInflater;

	public DBCardAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);

		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(Card card) {
		mItems.add(card);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Card getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, final View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ((Activity) getContext())
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.layout_stats_card_row, null);
		}

		Card card = mItems.get(position);
		buildFirstLine(v, card);
		buildSecondLine(v, card);

		return v;

	}

	private void buildFirstLine(View v, Card card) {
		TextView cardId = (TextView) v.findViewById(R.id.cardId);
		cardId.setText(card.getId().toString());
		TextView nameToFind = (TextView) v.findViewById(R.id.nameToFind);
		StringBuilder firstLineBuilder = new StringBuilder();
		firstLineBuilder.append(card.getNameToFind());

		nameToFind.setText(firstLineBuilder);
		if (!card.isActiveInDB()) {
			nameToFind.setPaintFlags(nameToFind.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			nameToFind.setPaintFlags(nameToFind.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
		}
	}

	private void buildSecondLine(View v, Card card) {
		TextView category = (TextView) v.findViewById(R.id.category);
		StringBuilder secondLineBuilder = new StringBuilder();
		secondLineBuilder.append(card.getCategory());

		category.setText(secondLineBuilder);
	}

}
