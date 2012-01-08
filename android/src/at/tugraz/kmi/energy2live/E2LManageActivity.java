package at.tugraz.kmi.energy2live;

import android.os.Bundle;
import at.tugraz.kmi.energy2live.database.E2LDatabaseHelper;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class E2LManageActivity extends OrmLiteBaseActivity<E2LDatabaseHelper> {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_manage);

		// ActionBar actionBar = (ActionBar) findViewById(R.id.manage_actionbar);
		// actionBar.setHomeAction(new IntentAction(this, Utils.createIntent(this,
		// E2LMainActivity.class),
		// R.drawable.ic_action_home));

	}
}
