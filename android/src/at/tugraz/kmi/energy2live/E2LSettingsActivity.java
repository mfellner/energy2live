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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.kmi.energy2live.widget.ActionBar;
import at.tugraz.kmi.energy2live.widget.ActionBar.IntentAction;

public class E2LSettingsActivity extends Activity {
	public static final String PREF_SERVER_ADDRESS = "PREF_SERVER_ADDRESS";
	private SharedPreferences mSharedPreferences;
	private EditText txtServer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		ActionBar actionBar = (ActionBar) findViewById(R.id.settings_actionbar);
		actionBar.setHomeAction(new IntentAction(this, Utils.createIntent(this, E2LMainActivity.class),
				R.drawable.ic_action_home));

		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		txtServer = (EditText) findViewById(R.id.txt_settings_server);
		init();
	}

	private void init() {
		String server = mSharedPreferences.getString(PREF_SERVER_ADDRESS, "<not set>");
		txtServer.setText(server);
		txtServer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId != EditorInfo.IME_ACTION_DONE)
					return false;
				String server = txtServer.getText().toString();
				if (server.matches("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}")) {
					Editor editor = mSharedPreferences.edit();
					editor.putString(PREF_SERVER_ADDRESS, server);
					editor.commit();
				} else {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_settings_bad_server),
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
	}
}
