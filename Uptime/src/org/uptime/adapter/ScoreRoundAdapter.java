package org.uptime.adapter;

import java.util.ArrayList;
import java.util.List;

import org.uptime.R;
import org.uptime.engine.game.Round;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ScoreRoundAdapter /*extends ArrayAdapter<Round> */{
//
//	private List<Round> mItems = new ArrayList<Round>();
//
//	private Resources mResources;
//
//	public LayoutInflater mInflater;
//
//	public ScoreRoundAdapter(Context context, int textViewResourceId) {
//		super(context, textViewResourceId);
//		this.mResources = context.getResources();
//
//		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	}
//
//	public void addItem(Round round) {
//		mItems.add(round);
//	}
//
//	@Override
//	public int getCount() {
//		return mItems.size();
//	}
//
//	@Override
//	public Round getItem(int position) {
//		return mItems.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@Override
//	public View getView(final int position, final View convertView, ViewGroup parent) {
//		View v = convertView;
//		if (v == null) {
//			LayoutInflater vi = (LayoutInflater) ((Activity) getContext())
//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			v = vi.inflate(R.layout.layout_stats_round_row, null);
//		}
//
//		Round round = mItems.get(position);
//
//		TextView roundNumber = (TextView) v.findViewById(R.id.turnId);
//		roundNumber.setText(String.format(mResources.getString(R.string.stats_round_number), position + 1));
//
//		buildFirstLine(v, round);
//
//		buildSecondLine(v, round);
//
//		return v;
//
//	}
//
//	private void buildFirstLine(View v, Round round) {
//		TextView firstLine = (TextView) v.findViewById(R.id.firstLine);
//		StringBuilder firstLineBuilder = new StringBuilder();
//		firstLineBuilder.append(round.getNameToFind());
//
//		firstLine.setText(firstLineBuilder);
//	}
//
//	private void buildSecondLine(View v, Round round) {
//		TextView secondLine = (TextView) v.findViewById(R.id.secondLine);
//		StringBuilder secondLineBuilder = new StringBuilder();
//		secondLineBuilder.append(round.getCategory());
//
//		secondLine.setText(secondLineBuilder);
//	}
//
}
