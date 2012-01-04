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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import at.tugraz.kmi.energy2live.widget.ActionBar;
import at.tugraz.kmi.energy2live.widget.ActionBar.IntentAction;

public class E2LMainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = (ActionBar) findViewById(R.id.main_actionbar);
		actionBar.addAction(new IntentAction(this, Utils.createIntent(this, E2LSettingsActivity.class),
				R.drawable.ic_action_settings));
	}

	/**
	 * Creates an Intent for this activity.
	 * 
	 * @return Intent for this activity.
	 */
	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, E2LMainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}
}
