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
