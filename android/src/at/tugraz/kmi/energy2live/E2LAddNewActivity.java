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

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import at.tugraz.kmi.energy2live.database.E2LDatabaseHelper;
import at.tugraz.kmi.energy2live.model.implementation.E2LActivityImplementation;
import at.tugraz.kmi.energy2live.widget.ActionBar;
import at.tugraz.kmi.energy2live.widget.ActionBar.IntentAction;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;

public class E2LAddNewActivity extends OrmLiteBaseActivity<E2LDatabaseHelper> {
	private EditText txtName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new);

		ActionBar actionBar = (ActionBar) findViewById(R.id.add_new_actionbar);
		actionBar.setHomeAction(new IntentAction(this, Utils.createIntent(this, E2LMainActivity.class),
				R.drawable.ic_action_home));

		txtName = (EditText) findViewById(R.id.txt_add_new_name);
	}

	// declared in xml
	public void addNewActivityClicked(View v) {
		try {
			E2LActivityImplementation activity = createActivityObject();
			if (activity == null)
				return;
			Dao<E2LActivityImplementation, Integer> dao = getHelper().getActivityDao();
			dao.create(activity);
			// TODO: toast create successful
			startActivity(Utils.createIntent(this, E2LMainActivity.class));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private E2LActivityImplementation createActivityObject() {
		String name = txtName.getText().toString();
		if (name == null || name.length() == 0) {
			// TODO: make toast
			return null;
		}

		E2LActivityImplementation activity = new E2LActivityImplementation();
		activity.setName(name);
		return activity;
	}
}
