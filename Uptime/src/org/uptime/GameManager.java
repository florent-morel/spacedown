package org.uptime;

import java.util.List;

import org.uptime.engine.Constants;
import org.uptime.engine.game.Card;
import org.uptime.engine.game.Game;

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

	public void startNewGame(Integer numberOfTeams, List<Card> listCards) {
		mGame = new Game(numberOfTeams, listCards);
	}

}
