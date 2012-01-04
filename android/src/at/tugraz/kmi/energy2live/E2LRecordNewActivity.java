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
import at.tugraz.kmi.energy2live.widget.ActionBar;
import at.tugraz.kmi.energy2live.widget.ActionBar.IntentAction;

public class E2LRecordNewActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_track);

		ActionBar actionBar = (ActionBar) findViewById(R.id.start_track_actionbar);
		actionBar.setHomeAction(new IntentAction(this, Utils.createIntent(this, E2LMainActivity.class),
				R.drawable.ic_action_home));
	}

}
