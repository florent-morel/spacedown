package org.uptime.adapter;

import java.util.ArrayList;
import java.util.List;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Game;
import org.uptime.engine.game.Round;
import org.uptime.engine.game.Team;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ScoreTeamAdapter extends ArrayAdapter<Team> {

	private List<Team> mItems = new ArrayList<Team>();

	private static GameManager mGameManager;

	private Game mGame;

	public LayoutInflater mInflater;

	public ScoreTeamAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mGameManager = GameManager.getSingletonObject();
		mGame = mGameManager.getGame();
	}

	public void addItem(Team team) {
		mItems.add(team);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Team getItem(int position) {
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
			v = vi.inflate(R.layout.layout_team_score_row, null);
		}

		Team team = mItems.get(position);

		TextView teamName = (TextView) v.findViewById(R.id.cardId);
		teamName.setText(team.getName());

		buildScore(v, team);

		return v;

	}

	private void buildScore(View v, Team team) {
		TextView round1 = (TextView) v.findViewById(R.id.Round1);
		round1.setText(Constants.DASH);
		TextView round2 = (TextView) v.findViewById(R.id.Round2);
		round2.setText(Constants.DASH);
		TextView round3 = (TextView) v.findViewById(R.id.Round3);
		round3.setText(Constants.DASH);
		TextView total = (TextView) v.findViewById(R.id.totalScore);
		total.setText(Constants.DASH);

		List<Round> roundList = mGame.getSavedRoundList();

		if (!roundList.isEmpty()) {
			// build first round
			round1.setText(roundList.get(0).getTeamRoundScore(team).toString());
			if (roundList.size() > 1) {
				// build second round
				round2.setText(roundList.get(1).getTeamRoundScore(team).toString());
			}
			if (roundList.size() > 2) {
				// build third round
				round3.setText(roundList.get(2).getTeamRoundScore(team).toString());
			}
		}
		// build total score
		total.setText(mGame.getTotalScore(team).toString());
	}

}
