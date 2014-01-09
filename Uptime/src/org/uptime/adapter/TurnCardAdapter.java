package org.uptime.adapter;

import java.util.ArrayList;
import java.util.List;

import org.uptime.R;
import org.uptime.engine.game.Card;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TurnCardAdapter extends ArrayAdapter<Card> {

	private List<Card> mItems = new ArrayList<Card>();

	private Resources mResources;

	public LayoutInflater mInflater;

	public TurnCardAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.mResources = context.getResources();

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

		TextView cardNumber = (TextView) v.findViewById(R.id.teamName);
		cardNumber.setText(String.format(mResources.getString(R.string.stats_card_number), position + 1));

		buildFirstLine(v, card);

		buildSecondLine(v, card);

		return v;

	}

	private void buildFirstLine(View v, Card card) {
		TextView firstLine = (TextView) v.findViewById(R.id.Round);
		StringBuilder firstLineBuilder = new StringBuilder();
		firstLineBuilder.append(card.getNameToFind());

		firstLine.setText(firstLineBuilder);
	}

	private void buildSecondLine(View v, Card card) {
		TextView secondLine = (TextView) v.findViewById(R.id.secondLine);
		StringBuilder secondLineBuilder = new StringBuilder();
		secondLineBuilder.append(card.getCategory());

		secondLine.setText(secondLineBuilder);
	}

}
