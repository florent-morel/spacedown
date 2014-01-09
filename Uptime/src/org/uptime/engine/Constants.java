package org.uptime.engine;

public interface Constants {

	static final int ROUND_FIRST = 1;

	static final int ROUND_SECOND = 2;

	static final int ROUND_THIRD = 3;

	static final Integer ZERO_VALUE = 0;

	static final Integer PENALTY_VALUE = -1000;

	static final Integer DEFAULT_SCORE_TO_REACH = 10000;

	static final Integer DEFAULT_WARM_UP_ROUNDS = 3;

	static final String TEAM_STATS = "TEAM";

	static final String PLAYER_NAME = "PLAYER_NAME";

	static final String RESULT_TURN = "RESULT_TURN";

	static final String NEW_LINE = "\n";

	static final String SPACE = " ";

	static final String GTH = ">";

	static final int RESULT_OK = -1;

	static final int RESULT_ROUND_END = 1;

	static final int RESULT_GAME_OVER = 2;

	static final int PENALTY_APPLIED = 1;

	static final int TOTAL_REACHED = 2;

	static final int GAME_NEW = 3;

	static final int GAME_CONTINUE = 4;

	static final int WHITE_FIST = 7;

	static final int NEW_GAME = 8;

	static final int NEW_GAME_QUICK = 9;

	static final int NEW_GAME_DEBUG = 10;

	static final int GAME_RENAME_PLAYERS = 11;

	static final int ENTER_TURN_SCORE = 12;

	static final int GAME_TURN_HISTORY = 13;

	static final int MENU_GAME_OPTIONS = 100;

	static final int MENU_ADD_PLAYER = 101;

	static final int MENU_SCORE_CHART = 102;

	static final int MENU_START_NEW = 103;

	static final int MENU_SIMULATE_ROUNDS = 1001;

	static final int ACTIVITY_LAUNCH = 0;

	static final int ACTIVITY_SUCCESS = 20;

	static final String RIGHT = "right";

	static final String LEFT = "left";

	static enum RunMode {STANDARD, DEBUG, HARDCODE};

	static final int NUMBER_OF_CARDS = 40;

	static final String DASH = "--";

	static final int TURN_STATS_DONE = 132456;
}
