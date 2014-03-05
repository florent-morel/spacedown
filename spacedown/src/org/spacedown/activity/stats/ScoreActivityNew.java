package org.spacedown.activity.stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.spacedown.GameManager;
import org.spacedown.R;
import org.spacedown.activity.CardActivity;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Game;
import org.spacedown.engine.game.Round;
import org.spacedown.engine.game.Team;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class ScoreActivityNew extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Resources mResources;

	private Game mGame;

	private TableLayout tableScore;

	private Button mButtonNextRound;

	private Map<Integer, TableRow> mapTeamRow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResources = getResources();

		mGameManager = GameManager.getSingletonObject();
		mGame = mGameManager.getGame();
		setContentView(R.layout.activity_score_new);

		// this.refreshActivity();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.refreshActivity();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mButtonNextRound.getId()) {
			launchNextRound();
		} else {
			Set<Entry<Integer, TableRow>> teamRowSet = mapTeamRow.entrySet();

			if (teamRowSet != null && !teamRowSet.isEmpty()) {

				for (Entry<Integer, TableRow> entry : teamRowSet) {
					if (v.getId() == entry.getValue().getId()) {
						Intent intent = new Intent(this,
								RoundStatisticsActivity.class);
						intent.putExtra(Constants.STATS_TEAM, entry.getKey());
						startActivityForResult(intent,
								Constants.ACTIVITY_LAUNCH);
					}
				}
			}
		}

	}

	private void refreshActivity() {
		this.initTableScore();
		// this.initTexts();
		this.initButtons();
	}

	private void initTableScore() {

		mapTeamRow = new HashMap<Integer, TableRow>();

		tableScore = (TableLayout) findViewById(R.id.tabScore);
		tableScore.removeAllViews();

		tableScore.setStretchAllColumns(true);
		tableScore.setShrinkAllColumns(true);

		TableRow rowTitle = new TableRow(this);
		rowTitle.setGravity(Gravity.CENTER_HORIZONTAL);

		TableRow rowLabels = new TableRow(this);

		// title column/row
		TextView title = new TextView(this);
		title.setText(mResources.getString(R.string.score_round_tab_title));

		title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		title.setGravity(Gravity.CENTER);
		title.setTypeface(Typeface.SERIF, Typeface.BOLD);

		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.span = 6;

		rowTitle.addView(title, params);

		// labels row
		TextView teamLabel = new TextView(this);
		teamLabel.setText(mResources.getString(R.string.score_round_tab_team));
		teamLabel.setTypeface(Typeface.DEFAULT_BOLD);
		teamLabel.setGravity(Gravity.CENTER_HORIZONTAL);

		TextView round1Label = new TextView(this);
		round1Label.setText(mResources
				.getString(R.string.score_round_tab_round1));
		round1Label.setTypeface(Typeface.DEFAULT_BOLD);
		round1Label.setGravity(Gravity.CENTER_HORIZONTAL);

		TextView round2Label = new TextView(this);
		round2Label.setText(mResources
				.getString(R.string.score_round_tab_round2));
		round2Label.setTypeface(Typeface.DEFAULT_BOLD);
		round2Label.setGravity(Gravity.CENTER_HORIZONTAL);

		TextView round3Label = new TextView(this);
		round3Label.setText(mResources
				.getString(R.string.score_round_tab_round3));
		round3Label.setTypeface(Typeface.DEFAULT_BOLD);
		round3Label.setGravity(Gravity.CENTER_HORIZONTAL);

		TextView totalLabel = new TextView(this);
		totalLabel
				.setText(mResources.getString(R.string.score_round_tab_total));
		totalLabel.setTypeface(Typeface.DEFAULT_BOLD);
		totalLabel.setGravity(Gravity.CENTER_HORIZONTAL);

		// rowDayLabels.addView(empty);
		rowLabels.addView(teamLabel);
		rowLabels.addView(round1Label);
		rowLabels.addView(round2Label);
		rowLabels.addView(round3Label);
		rowLabels.addView(totalLabel);

		tableScore.addView(rowTitle);
		tableScore.addView(rowLabels);

		// Team row
		List<Team> teamList = mGame.getTeamList();
		for (Team team : teamList) {

			TextView teamName = new TextView(this);
			teamName.setText(team.getName());
			teamName.setTypeface(Typeface.SERIF, Typeface.BOLD);

			TextView scoreRound1 = new TextView(this);
			scoreRound1.setText(Constants.DASH);
			scoreRound1.setGravity(Gravity.CENTER_HORIZONTAL);

			TextView scoreRound2 = new TextView(this);
			scoreRound2.setText(Constants.DASH);
			scoreRound2.setGravity(Gravity.CENTER_HORIZONTAL);

			TextView scoreRound3 = new TextView(this);
			scoreRound3.setText(Constants.DASH);
			scoreRound3.setGravity(Gravity.CENTER_HORIZONTAL);

			List<Round> roundList = mGame.getSavedRoundList();

			if (!roundList.isEmpty()) {
				// build first round
				scoreRound1.setText(roundList.get(0).getTeamRoundScore(team)
						.toString());
				if (roundList.size() > 1) {
					// build second round
					scoreRound2.setText(roundList.get(1)
							.getTeamRoundScore(team).toString());
				}
				if (roundList.size() > 2) {
					// build third round
					scoreRound3.setText(roundList.get(2)
							.getTeamRoundScore(team).toString());
				}
			}

			// build total score
			TextView scoreTotal = new TextView(this);
			Integer totalScore = mGame.getTotalScore(team);
			if (totalScore != null && totalScore.intValue() > -1) {
				scoreTotal.setText(totalScore.toString());
			} else {
				scoreTotal.setText(Constants.DASH);

			}

			scoreTotal.setGravity(Gravity.CENTER_HORIZONTAL);

			TableRow rowTeam = new TableRow(this);
			rowTeam.addView(teamName);
			rowTeam.addView(scoreRound1);
			rowTeam.addView(scoreRound2);
			rowTeam.addView(scoreRound3);
			rowTeam.addView(scoreTotal);

			rowTeam.setClickable(Boolean.TRUE);
			rowTeam.setOnClickListener(this);

			tableScore.addView(rowTeam, new TableRow.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			mapTeamRow.put(team.getId(), rowTeam);
		}
	}

	private void initButtons() {
		mButtonNextRound = (Button) findViewById(R.id.buttonNextRound);
		mButtonNextRound.setOnClickListener(this);

		Round currentRound = mGame.getCurrentRound();

		if (currentRound != null && currentRound.isRoundActive()) {
			mButtonNextRound.setText(String.format(mResources
					.getString(R.string.score_continue_round)));
		}

		if (mGame.isGameOver()) {
			mButtonNextRound.setVisibility(View.GONE);
		} else {
			mButtonNextRound.setVisibility(View.VISIBLE);
		}
	}

	private void launchNextRound() {
		Round currentRound = mGame.getCurrentRound();

		if (currentRound == null || !currentRound.isRoundActive()) {
			// Start a next round only if needed (to come back to current round
			// if back button was pressed)
			mGame.startNextRound();
		}
		Intent intent = new Intent(this, CardActivity.class);
		startActivityForResult(intent,
				Constants.ACTIVITY_CARDACTIVITY_NEXT_ROUND);
	}
}
