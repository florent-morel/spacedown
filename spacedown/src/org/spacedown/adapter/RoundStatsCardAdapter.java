package org.spacedown.adapter;

import java.util.List;

import org.spacedown.GameManager;
import org.spacedown.R;
import org.spacedown.activity.stats.RoundStatisticsActivityOld;
import org.spacedown.engine.game.Card;
import org.spacedown.engine.game.Round;
import org.spacedown.engine.game.Team;
import org.spacedown.engine.game.Turn;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

@Deprecated
public class RoundStatsCardAdapter extends PagerAdapter {

	private Context mContext;

	private static GameManager mGameManager;

	private Resources mResources;

	private ListView mStatsCardsList;

	private TextView mStatsCardsRound;

	private Round mRound;

	private View mView;

	private RoundStatisticsActivityOld mActivity;

	// private final Context context;

	public RoundStatsCardAdapter(RoundStatisticsActivityOld activity) {
		super();
		mActivity = activity;
		mContext = activity.getBaseContext();
		this.mResources = mContext.getResources();
		mGameManager = GameManager.getSingletonObject();
	}

	/**
	 * Create the page for the given position. The adapter is responsible for
	 * adding the view to the container given here, although it only must ensure
	 * this is done by the time it returns from {@link #finishUpdate()}.
	 * 
	 * @param container
	 *            The containing View in which the page will be shown.
	 * @param position
	 *            The page position to be instantiated.
	 * @return Returns an Object representing the new page. This does not need
	 *         to be a View, but can be some other container of the page.
	 */
	@Override
	public Object instantiateItem(ViewGroup group, int position) {
		LayoutInflater vi = (LayoutInflater) (mContext).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = vi.inflate(R.layout.activity_stats_round, null);
		mRound = mGameManager.getGame().getSavedRoundList().get(position);

		group.addView(mView, 0);

		// switch to page 2 (index 1) if we're out of bounds
		// if (viewPager.getCurrentItem() < 0 || viewPager.getCurrentItem() >=
		// viewPagerAdapter.getCount()) {
		// viewPager.setCurrentItem(1, false);
		// }

		fillStatsItems();
		return mView;
	}

	private void fillStatsItems() {
		mStatsCardsList = (ListView) mView.findViewById(R.id.statsCardList1);
		mStatsCardsRound = (TextView) mView.findViewById(R.id.statsRound1);
		TurnCardAdapter cardAdapter = buildCardAdapter(mActivity.getTeam(), mRound);

		if (cardAdapter.isEmpty()) {
			mStatsCardsRound.setText(mResources.getString(R.string.stats_card_current_turn));
			mActivity.setTitle(String.format(mResources.getString(R.string.stats_card_current_turn)));
		} else {
			mStatsCardsList.setAdapter(cardAdapter);
			mActivity.registerForContextMenu(mStatsCardsList);
			// mStatsCardsRound.setText(String.format(mResources.getString(R.string.stats_round),
			// mRound.getRoundNumber(),
			// cardAdapter.getCount()));
			mActivity.setTitle(String.format(mResources.getString(R.string.stats_round), mRound.getRoundNumber(),
					cardAdapter.getCount()));
		}
	}

	private TurnCardAdapter buildCardAdapter(Team team, Round round) {

		TurnCardAdapter turnCardAdapter = new TurnCardAdapter(mActivity, R.layout.layout_stats_card_row, false);

		List<Turn> turnList = round.getSavedTurnMap().get(team);
		if (turnList != null) {
			for (Turn turn : turnList) {
				List<Card> listCards = turn.getTurnListFoundCards();
				if (listCards != null && !listCards.isEmpty()) {
					for (Card card : listCards) {
						turnCardAdapter.addItem(card);
					}
				}
			}
		}
		return turnCardAdapter;
	}

	/**
	 * Remove a page for the given position. The adapter is responsible for
	 * removing the view from its container, although it only must ensure this
	 * is done by the time it returns from {@link #finishUpdate()}.
	 * 
	 * @param container
	 *            The containing View from which the page will be removed.
	 * @param position
	 *            The page position to be removed.
	 * @param object
	 *            The same object that was returned by
	 *            {@link #instantiateItem(View, int)}.
	 */
	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((ScrollView) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((ScrollView) object);
	}

	// @Override
	// public CharSequence getPageTitle (int position) {
	// TODO this doesn't work, check why
	// return String.format(mResources.getString(R.string.statistics),
	// mGameManager.getGame().getPlayerById(position).getName());
	// }

	/**
	 * Called when the a change in the shown pages has been completed. At this
	 * point you must ensure that all of the pages have actually been added or
	 * removed from the container as appropriate.
	 * 
	 * @param container
	 *            The containing View which is displaying this adapter's page
	 *            views.
	 */
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
	}

	@Override
	public Object instantiateItem(View pager, int position) {
		TextView v = null;
		// v.setText( titles[ position ] );
		// ((ViewPager)pager).addView( v, 0 );
		return v;
	}

	@Override
	public void finishUpdate(View view) {
	}

	@Override
	public void restoreState(Parcelable p, ClassLoader c) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View view) {
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
