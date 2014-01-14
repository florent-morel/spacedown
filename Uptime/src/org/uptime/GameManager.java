package org.uptime;

import org.uptime.engine.Constants;
import org.uptime.engine.game.Game;

import android.content.Context;
import android.content.res.Resources;

/**
 * Singleton handling the game instance.
 * 
 * Starts a new game, sorts players by rank, computes a turn score...
 * 
 * @author florent
 * 
 */
public class GameManager {

	private static GameManager mGameManager;

	private Game mGame;

	private GameManager() {
	}

	public static GameManager getSingletonObject() {
		if (mGameManager == null) {
			mGameManager = new GameManager();
		}
		return mGameManager;
	}

	public Game getGame() {
		return mGame;
	}

	public void startNewGame(Constants.RunMode runMode, Integer numberOfTeams, Integer numberOfCards,
			Resources resources, Context context) {
		mGame = new Game(runMode, numberOfTeams, numberOfCards, resources, context);
	}

	public Game initNewGame() {
		mGame = new Game();
		return mGame;
	}

}
