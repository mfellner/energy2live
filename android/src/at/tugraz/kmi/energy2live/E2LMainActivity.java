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

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import at.tugraz.kmi.energy2live.widget.ActionBar;
import at.tugraz.kmi.energy2live.widget.ActionBar.IntentAction;

public class E2LMainActivity extends Activity {
	private ListView lastestActivitiesList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = (ActionBar) findViewById(R.id.main_actionbar);
		actionBar.addAction(new IntentAction(this, Utils.createIntent(this, E2LSettingsActivity.class),
				R.drawable.ic_action_settings));

		lastestActivitiesList = (ListView) findViewById(R.id.latest_activities_list);
		lastestActivitiesList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_activities_item, new String[] {
				"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight" }));
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

	public void recordNewActivityClicked(View v) {
		startActivity(Utils.createIntent(this, E2LRecordNewActivity.class));
	}

	public void addNewActivityClicked(View v) {
		startActivity(Utils.createIntent(this, E2LAddNewActivity.class));
	}
}
