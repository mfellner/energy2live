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
import java.util.Calendar;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.kmi.energy2live.database.E2LDatabaseHelper;
import at.tugraz.kmi.energy2live.model.implementation.E2LActivityImplementation;
import at.tugraz.kmi.energy2live.widget.ActionBar;
import at.tugraz.kmi.energy2live.widget.ActionBar.IntentAction;
import at.tugraz.kmi.energy2live.widget.E2LDialogDurationSelect;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;

public class E2LAddNewActivity extends OrmLiteBaseActivity<E2LDatabaseHelper> implements OnDismissListener {
	private EditText txtName;
	private Button btnDuration;
	private E2LDialogDurationSelect selectDurationDialog;
	private E2LActivityImplementation mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new);

		ActionBar actionBar = (ActionBar) findViewById(R.id.add_new_actionbar);
		actionBar.setHomeAction(new IntentAction(this, Utils.createIntent(this, E2LMainActivity.class),
				R.drawable.ic_action_home));

		txtName = (EditText) findViewById(R.id.txt_add_new_name);
		btnDuration = (Button) findViewById(R.id.btn_add_new_duration);

		mActivity = new E2LActivityImplementation();
		selectDurationDialog = new E2LDialogDurationSelect(this);
		selectDurationDialog.setOnDismissListener(this);
	}

	@Override
	public void onDismiss(DialogInterface d) {
		if (d.toString().equals(E2LDialogDurationSelect.ID)) {
			int h = selectDurationDialog.getSelectedHours();
			int m = selectDurationDialog.getSelectedMinutes();
			String hText = getResources().getString(R.string.hours);
			String mText = getResources().getString(R.string.minutes);
			String text = h > 0 ? Integer.toString(h) + " " + hText + ", " : "";
			text += Integer.toString(m) + " " + mText;
			btnDuration.setText(text);
			long milliseconds = (h * 3600000) + (m * 60000);
			mActivity.setDuration(milliseconds);
		}
	}

	// declared in xml
	public void addNewActivityClicked(View v) {
		try {
			if (!fillActivityObject())
				return;
			Dao<E2LActivityImplementation, Integer> dao = getHelper().getActivityDao();
			dao.create(mActivity);
			dao.refresh(mActivity);
			// TODO: toast create successful
			startActivity(Utils.createIntent(this, E2LMainActivity.class));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// declared in xml
	public void buttonDurationClicked(View v) {
		selectDurationDialog.show();
	}

	private boolean fillActivityObject() {
		String name = txtName.getText().toString();

		mActivity.setName(name);
		mActivity.setTime(Calendar.getInstance().getTime());

		if (mActivity.hasEmptyFields()) {
			// TODO: make toast
			return false;
		}
		return true;
	}
}
