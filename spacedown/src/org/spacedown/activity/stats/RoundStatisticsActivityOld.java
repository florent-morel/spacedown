package org.spacedown.activity.stats;

import org.spacedown.GameManager;
import org.spacedown.R;
import org.spacedown.adapter.RoundStatsCardAdapter;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Game;
import org.spacedown.engine.game.Team;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class RoundStatisticsActivityOld extends Activity {

	private static GameManager mGameManager;

	private Game game;

	private Team team;

	private ViewPager statsPager;
	private RoundStatsCardAdapter roundStatsAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats_round);
		mGameManager = GameManager.getSingletonObject();
		game = mGameManager.getGame();

		team = game.getTeam(this.getIntent().getIntExtra(Constants.STATS_TEAM, 0));

		roundStatsAdapter = new RoundStatsCardAdapter(this);
		statsPager = (ViewPager) findViewById(R.id.stats_round_pager);
		statsPager.setAdapter(roundStatsAdapter);
		statsPager.setCurrentItem(Constants.VALUE_ZERO);

		// If you are asking how one could implements a carousel (infinite
		// loop), then that would involve manipulating the PagerAdapter to
		// return a x+1 for getCount() and getItem(int position) to perform a
		// modulus operation on the ‘position’ parameter to determine the
		// correct Fragment index in the member variable ‘List fragments’ e.g:
		//
		// int i = position % fragments.size();

	}

	public Team getTeam() {
		return team;
	}
}
