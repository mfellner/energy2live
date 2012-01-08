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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Utils {
	private Utils() {
	};

	public static final SimpleDateFormat SDF_READABLE = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

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

	/**
	 * Sets the values of one calendar on another.
	 * 
	 * @param toSet Calendar to set values on.
	 * @param getFrom Calendar to use values from.
	 */
	public static void setCalendar(Calendar toSet, Calendar getFrom) {
		toSet.set(Calendar.YEAR, getFrom.get(Calendar.YEAR));
		toSet.set(Calendar.DAY_OF_YEAR, getFrom.get(Calendar.DAY_OF_YEAR));
		toSet.set(Calendar.HOUR_OF_DAY, getFrom.get(Calendar.HOUR_OF_DAY));
		toSet.set(Calendar.MINUTE, getFrom.get(Calendar.MINUTE));
		toSet.set(Calendar.SECOND, getFrom.get(Calendar.SECOND));
		toSet.set(Calendar.MILLISECOND, getFrom.get(Calendar.MILLISECOND));
		toSet.set(Calendar.ZONE_OFFSET, getFrom.get(Calendar.ZONE_OFFSET));
		toSet.set(Calendar.DST_OFFSET, getFrom.get(Calendar.DST_OFFSET));
		toSet.set(Calendar.ERA, getFrom.get(Calendar.ERA));
	}

	/**
	 * Creates a deep copy (new instance) of a calendar.
	 * 
	 * @param calendar Calendar to copy.
	 * @return New, deep copied instance of the calendar.
	 */
	public static Calendar deepCopyCalendar(Calendar calendar) {
		Calendar newCalendar = Calendar.getInstance();
		Utils.setCalendar(newCalendar, calendar);
		return newCalendar;
	}
}
