package org.spacedown.activity.stats;

import org.spacedown.GameManager;
import org.spacedown.R;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Game;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class RoundStatisticsActivity extends FragmentActivity {

	private Resources mResources;

	/**
	 * The number of pages (wizard steps) to show in this demo.
	 */
	private static final int NUM_PAGES = 3;
	private static GameManager mGameManager;
	private Game game;
	/**
	 * The pager widget, which handles animation and allows swiping horizontally
	 * to access previous and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_slide);
		mResources = getResources();
		mGameManager = GameManager.getSingletonObject();
		game = mGameManager.getGame();

		int team = this.getIntent().getIntExtra(Constants.STATS_TEAM, 0);
		setTitle(String.format(mResources.getString(R.string.stats_title), game.getTeam(team).getName()));

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(team, getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
	}

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			// If the user is currently looking at the first step, allow the
			// system to handle the
			// Back button. This calls finish() on this activity and pops the
			// back stack.
			super.onBackPressed();
		} else {
			// Otherwise, select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}

	/**
	 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects,
	 * in sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		private int teamId;

		public ScreenSlidePagerAdapter(int team, FragmentManager fm) {
			super(fm);
			this.teamId = team;
		}

		@Override
		public Fragment getItem(int position) {
			ScreenSlidePageFragment screenSlidePageFragment = new ScreenSlidePageFragment();
			Bundle bundle = new Bundle();
			bundle.putInt(Constants.STATS_ROUND, position);

			bundle.putInt(Constants.STATS_TEAM, teamId);

			screenSlidePageFragment.setArguments(bundle);
			return screenSlidePageFragment;
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}
}
