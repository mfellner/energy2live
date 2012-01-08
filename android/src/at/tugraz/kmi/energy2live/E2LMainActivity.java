/** Copyright 2011 Maximilian Fellner, Gernot Solic, Florian Sumann
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package at.tugraz.kmi.energy2live;

import java.sql.SQLException;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import at.tugraz.kmi.energy2live.database.E2LDatabaseHelper;
import at.tugraz.kmi.energy2live.model.implementation.E2LActivityImplementation;
import at.tugraz.kmi.energy2live.widget.ActionBar;
import at.tugraz.kmi.energy2live.widget.ActionBar.IntentAction;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class E2LMainActivity extends OrmLiteBaseActivity<E2LDatabaseHelper> implements ListView.OnItemClickListener {
	private static final long MAX_LATEST_ACTIVITIES = 10;
	private ListView lastestActivitiesList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = (ActionBar) findViewById(R.id.main_actionbar);
		actionBar.addAction(new IntentAction(this, Utils.createIntent(this, E2LSettingsActivity.class),
				R.drawable.ic_action_settings));

		lastestActivitiesList = (ListView) findViewById(R.id.latest_activities_list);
		lastestActivitiesList.setOnItemClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			populateViews();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(Utils.createIntent(this, E2LSettingsActivity.class));
			return true;
		case R.id.menu_about:
			// TODO: show about dialog
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		E2LActivityImplementation activity = (E2LActivityImplementation) parent.getItemAtPosition(position);
		if (activity != null && activity.getId() != null) {
			Intent intent = new Intent(this, E2LManageActivity.class);
			intent.putExtra(E2LManageActivity.EXTRA_ACTIVITY_ID, activity.getId());
			startActivity(intent);
		}
	}

	public void recordNewActivityClicked(View v) {
		startActivity(Utils.createIntent(this, E2LRecordActivity.class));
	}

	public void addNewActivityClicked(View v) {
		startActivity(Utils.createIntent(this, E2LAddNewActivity.class));
	}

	private void populateViews() throws SQLException {
		Dao<E2LActivityImplementation, Integer> dao = getHelper().getActivityDao();
		QueryBuilder<E2LActivityImplementation, Integer> builder = dao.queryBuilder();
		builder.limit(MAX_LATEST_ACTIVITIES);
		// builder.orderBy(E2LActivityImplementation.DATE_FIELD_NAME, false).limit(30L);
		List<E2LActivityImplementation> list = dao.query(builder.prepare());
		ArrayAdapter<E2LActivityImplementation> arrayAdapter = new E2LActivityArrayAdapter(this,
				R.layout.list_activities_row, list, getHelper());
		lastestActivitiesList.setAdapter(arrayAdapter);
	}
}
