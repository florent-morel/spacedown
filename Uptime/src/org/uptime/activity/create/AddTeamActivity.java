package org.uptime.activity.create;

import org.uptime.GameManager;
import org.uptime.R;
import org.uptime.engine.Constants;
import org.uptime.engine.game.Team;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity adding a new team to current game.
 * 
 * @author florent
 * 
 */
public class AddTeamActivity extends Activity implements OnClickListener {

	private static GameManager mGameManager;

	private Resources mResources;

	private EditText mName;
	private Button mButtonCreateTeam;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResources = getResources();

		mGameManager = GameManager.getSingletonObject();

		setContentView(R.layout.activity_create_team);

		mName = (EditText) findViewById(R.id.editCreateTeamName);

		mButtonCreateTeam = (Button) findViewById(R.id.buttonCreateTeam);
		mButtonCreateTeam.setOnClickListener(this);

	}

	/**
	 * Add team to the game after validation.
	 */
	private void addTeamToGame() {

		String teamName = mName.getText().toString();

		boolean validName = processTeamName(teamName);

		if (validName) {
			Team newTeam = new Team(mGameManager.getGame().getTeamList().size(), teamName);

			mGameManager.getGame().addTeamToList(newTeam);

			Toast toast = Toast.makeText(this,
					String.format(mResources.getString(R.string.dialog_create_team_confirm), teamName),
					Toast.LENGTH_SHORT);
			toast.show();

			this.setResult(Constants.ACTIVITY_SUCCESS);
			finish();
		}

	}

	private boolean processTeamName(String string) {
		boolean isValid = false;
		if (this.validateNewTeamName(string)) {
			// New team name is valid, process
			isValid = true;
		} else {
			// Wrong input, reject
			Toast toast = Toast.makeText(this, mResources.getString(R.string.dialog_create_team_name_invalid),
					Toast.LENGTH_SHORT);
			toast.show();
		}
		return isValid;
	}

	private boolean validateNewTeamName(String name) {
		boolean isValid = false;

		if (name != null && !name.isEmpty() && !this.isExistingName(name)) {
			isValid = true;
		}

		return isValid;
	}

	/**
	 * Check that no other team has the same name.
	 * 
	 * @param teamName
	 * @return
	 */
	private boolean isExistingName(String teamName) {
		boolean exist = false;
		for (Team team : mGameManager.getGame().getTeamList()) {
			if (teamName.equalsIgnoreCase((team.getName()))) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mButtonCreateTeam.getId()) {
			this.addTeamToGame();
		}
	}

}
