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

public class Utils {
	private Utils() {
	};

	/**
	 * Creates an Intent for an activity.
	 * 
	 * @param context Context of the activity.
	 * @param cl Class of the activity.
	 * @return Intent for the activity.
	 */
	public static <T extends Activity> Intent createIntent(Context context, Class<T> cl) {
		Intent i = new Intent(context, cl);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}
}
