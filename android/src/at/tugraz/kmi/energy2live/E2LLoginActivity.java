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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import at.tugraz.kmi.energy2live.model.E2LUser;
import at.tugraz.kmi.energy2live.model.implementation.E2LUserImplementation;
import at.tugraz.kmi.energy2live.remote.E2LNetworkConnection;
import at.tugraz.kmi.energy2live.remote.E2LNetworkConnection.ACTION;

public class E2LLoginActivity extends Activity implements E2LNetworkConnection.Callback {
	private EditText txtEmail;
	private EditText txtPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		txtEmail = (EditText) findViewById(R.id.txt_login_email);
		txtPassword = (EditText) findViewById(R.id.txt_login_password);
	}

	// declared in xml
	public void doLogin(View v) {
		String email = txtEmail.getText().toString(); // TODO email is prompted as "username"
		String password = txtPassword.getText().toString();

		if (email.length() == 0 && password.length() == 0) { // TODO remove later
			Log.w("E2L", "Start withouth login");
			startActivity(Utils.createIntent(this, E2LMainActivity.class));
			finish();
			return;
		}

		E2LUser user = new E2LUserImplementation(email, password);
		E2LNetworkConnection network = new E2LNetworkConnection(this);
		network.addCallback(this);
		network.loginUser(user);
	}

	// declared in xml
	public void doRegister(View v) {
		// TODO: perform registration
	}

	@Override
	public void onNetworkConnectionResult(ACTION a, boolean b) {
		switch (a) {
		case LOGIN:
			if (b) {
				startActivity(Utils.createIntent(this, E2LMainActivity.class));
				finish();
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(E2LLoginActivity.this, getResources().getString(R.string.msg_login_failed),
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			break;
		}
	}
}
