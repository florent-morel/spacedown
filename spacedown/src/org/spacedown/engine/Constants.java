package org.spacedown.engine;

public interface Constants {

	static final String TAG = "Spacedown";

	static final String APP_NAME = "Spacedown";

	static final String DATABASE_FILE = APP_NAME + ".db";

	// Store as hidden files just in case
	static final String PATH_APP_NAME = ".Spacedown";
	static final String PATH_DATABASE = "database";
	static final String PATH_BACKUP = "backup";
	static final String PATH_LOGS = "logs";
	static final String PATH_DEBUG = "debug";

	static final int ROUND_FIRST = 1;

	static final int ROUND_SECOND = 2;

	static final int ROUND_THIRD = 3;

	static final String STATS_TEAM = "TEAM";

	static final String STATS_ROUND = "ROUND";

	static final String PLAYER_NAME = "PLAYER_NAME";

	static final String RESULT_TURN = "RESULT_TURN";

	static final String SPACE = " ";

	static final String SLASH = "/";

	static final String UNDERSCORE = "_";

	static final int RESULT_OK = -1;

	static final int ACTIVITY_LAUNCH = 0;

	static final int ACTIVITY_SUCCESS = 20;

	static enum RunMode {
		DB, DEBUG, IMPORT_CSV, HARDCODE
	};

	static enum CancelCardMode {
		NO_FOUND_CARD, CURRENT_TURN, PREVIOUS_TURN, PREVIOUS_ROUND
	};

	static final int NUMBER_OF_CARDS = 40;
	
	static final int MAX_CARDS_LIST = 1000;

	static final String DASH = "--";
	
	static final int GAME_NEW = 3;

	static final int ACTIVITY_TURN_STATS = 132456;

	static final int ACTIVITY_NEXT_PLAYER = 145654;

	static final int ACTIVITY_TURN_STATS_END_ROUND = 45564;

	static final int ACTIVITY_CARDACTIVITY_NEXT_ROUND = 543564;

	static final int CARD_CREATE = 32132145;

	static final int CARD_UPDATE = 5641323;

	static final String CARD_ID = "CARD_ID";

	static final String CARD_ACTIVE = "CARD_ACTIVE";

	static final Integer VALUE_ZERO = 0;

	static final Integer VALUE_ONE = 1;

	static final String CATEGORY_CUSTOM = "Custom";

	static final String TIMER_DEFAULT = "6";

	static final long TIMER_TICTAC = 5000;

	static final long TIMER_BEFORE_NEXT_PLAYER = 3;

	static final long ONE_SECOND = 1000;
}
