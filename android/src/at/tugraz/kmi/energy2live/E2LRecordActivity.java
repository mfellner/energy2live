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

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;
import at.tugraz.kmi.energy2live.location.E2LLocationService;
import at.tugraz.kmi.energy2live.widget.ActionBar;
import at.tugraz.kmi.energy2live.widget.ActionBar.IntentAction;

public class E2LRecordActivity extends Activity implements E2LLocationService.Callback {
	private EditText txtName;
	private ToggleButton btnRecordToggle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		ActionBar actionBar = (ActionBar) findViewById(R.id.record_actionbar);
		actionBar.setHomeAction(new IntentAction(this, Utils.createIntent(this, E2LMainActivity.class),
				R.drawable.ic_action_home));

		txtName = (EditText) findViewById(R.id.txt_record_name);
		btnRecordToggle = (ToggleButton) findViewById(R.id.btn_record_start_stop);

		E2LLocationService.addCallback(this);

		if (E2LLocationService.isRunning()) {
			btnRecordToggle.setChecked(true);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		E2LLocationService.removeCallback(this);
	}

	// declared in xml
	public void btnRecordStartStopClicked(View v) {
		if (E2LLocationService.isRunning()) {
			Resources res = getResources();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(res.getString(R.string.msg_stop_recording)).setCancelable(false)
					.setPositiveButton(res.getString(R.string.yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							btnRecordToggle.setChecked(false);
							stopService(new Intent(E2LRecordActivity.this, E2LLocationService.class));
						}
					}).setNegativeButton(res.getString(R.string.no), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							btnRecordToggle.setChecked(true);
							dialog.cancel();
						}
					});
			builder.create().show();
		} else {
			btnRecordToggle.setChecked(true);
			startService(new Intent(this, E2LLocationService.class));
		}
	}

	@Override
	public void onNewLocationFound(Location location) {

	}

	@Override
	public void onLocationServiceStop(boolean serviceStoppedItself, ArrayList<Location> locations) {
		if (serviceStoppedItself) {
			btnRecordToggle.setChecked(false);
		} else {
			Intent intent = new Intent(this, E2LManageActivity.class);
			intent.putExtra(E2LManageActivity.EXTRA_ACTIVITY_NAME, txtName.getText().toString());
			intent.putParcelableArrayListExtra(E2LManageActivity.EXTRA_LOCATIONS, locations);
			startActivity(intent);
			finish();
		}
	}
}
