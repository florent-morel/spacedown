package org.spacedown.activity.stats;

import java.util.List;

import org.spacedown.GameManager;
import org.spacedown.R;
import org.spacedown.adapter.TurnCardAdapter;
import org.spacedown.engine.Constants;
import org.spacedown.engine.game.Card;
import org.spacedown.engine.game.Game;
import org.spacedown.engine.game.Round;
import org.spacedown.engine.game.Team;
import org.spacedown.engine.game.Turn;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class ScreenSlidePageFragment extends Fragment {

	private static GameManager mGameManager;

	private Game game;

	private Context context;

	private Resources mResources;

	private Team team;
	
	private int roundNumber;

	private ListView mStatsCardsList;

	private TextView mStatsCardsRound;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
		super.onCreate(savedInstanceState);

		context = inflater.getContext();

		mResources = getResources();
		mGameManager = GameManager.getSingletonObject();
		game = mGameManager.getGame();

		Bundle bundle = this.getArguments();
		
		int teamId = bundle.getInt(Constants.STATS_TEAM, 0);
		this.team = game.getTeam(teamId);

		roundNumber = bundle.getInt(Constants.STATS_ROUND, 0);

		mStatsCardsRound = (TextView) rootView.findViewById(R.id.statsRound);

		// inflater.setTitle(String.format(mResources.getString(R.string.stats_title),
		// team.getName()));
		refreshList(rootView);
		return rootView;
	}

	/**
	 * Refresh the score board player list.
	 * 
	 * @param descending
	 */
	private void refreshList(ViewGroup rootView) {
		buildListCards(rootView);
	}

	private void buildListCards(ViewGroup rootView) {
		if (team != null) {
			List<Round> roundList = game.getSavedRoundList();
			if (!roundList.isEmpty() && roundList.size() > roundNumber) {
				// build first round
				mStatsCardsList = (ListView) rootView.findViewById(R.id.statsCardList);
				Round round = roundList.get(roundNumber);
				TurnCardAdapter cardAdapter = buildCardAdapter(team, round);
				mStatsCardsList.setAdapter(cardAdapter);
				registerForContextMenu(mStatsCardsList);
				mStatsCardsRound.setText(String.format(mResources.getString(R.string.stats_round),
						round.getRoundNumber(), cardAdapter.getCount()));
			} else {
				mStatsCardsRound.setText(mResources.getString(R.string.stats_card_current_turn));
			}
		}
	}

	private TurnCardAdapter buildCardAdapter(Team team, Round round) {

		TurnCardAdapter turnCardAdapter = new TurnCardAdapter(context, R.layout.layout_stats_card_row, false);

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
}