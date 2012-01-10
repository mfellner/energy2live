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

package at.tugraz.kmi.energy2live.location;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import at.tugraz.kmi.energy2live.E2LRecordActivity;
import at.tugraz.kmi.energy2live.R;

public class E2LLocationService extends Service implements LocationListener {
	private static final int NOTIFICATION_ID = 1;
	private static final int MIN_TIME_DELTA = 60000; // ms
	private static final int MIN_DIST_DELTA = 100; // m

	private static boolean RUNNING;
	private boolean mStoppedMyself;
	private int mStartId;
	private Location mLastLocation;
	private ServiceHandler mServiceHandler;
	private NotificationManager mNotificationManager;
	private LocationManager mLocationManager;
	private static List<Callback> CALLBACKS;

	private final class ServiceHandler extends Handler {
		static final int MSG_STOP = 0;
		static final int MSG_REQUEST_LOCATION_UPDATES = 1;
		static final int MSG_PAUSE_LOCATION_UPDATES = 2;

		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_STOP:
				mLocationManager.removeUpdates(E2LLocationService.this);
				mStoppedMyself = true;
				stopSelf(mStartId);
				break;
			case MSG_REQUEST_LOCATION_UPDATES:
				mLocationManager.removeUpdates(E2LLocationService.this);
				mLocationManager.requestLocationUpdates((String) msg.obj, msg.arg1, msg.arg2, E2LLocationService.this);
				break;
			case MSG_PAUSE_LOCATION_UPDATES:
				break;
			}
		}
	}

	public interface Callback {
		public void onNewLocationFound(Location location);

		public void onLocationServiceStop(boolean serviceStoppedItself);
	}

	public static void addCallback(Callback callback) {
		if (CALLBACKS == null) {
			CALLBACKS = new ArrayList<Callback>(1);
		}
		CALLBACKS.add(callback);
	}

	public static void removeCallback(Callback callback) {
		CALLBACKS.remove(callback);
	}

	public static boolean isRunning() {
		return RUNNING;
	}

	@Override
	public void onCreate() {
		RUNNING = true;
		HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		mServiceHandler = new ServiceHandler(thread.getLooper());
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mStartId = startId;

		// put up notification message
		String activityName = "dummy activity"; // TODO get from intent
		int icon = android.R.drawable.stat_notify_error; // TODO change icon
		CharSequence tickerText = "Recording activity \"" + activityName + "\"";
		long when = System.currentTimeMillis();
		Context context = getApplicationContext();
		CharSequence contentTitle = getResources().getString(R.string.app_name);
		CharSequence contentText = tickerText;

		Intent notificationIntent = new Intent(this, E2LRecordActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		Notification notification = new Notification(icon, tickerText, when);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		mNotificationManager.notify(NOTIFICATION_ID, notification);

		// send initial message to handler
		Message msg = mServiceHandler.obtainMessage();
		msg.what = ServiceHandler.MSG_REQUEST_LOCATION_UPDATES;
		msg.obj = LocationManager.NETWORK_PROVIDER;
		msg.arg1 = MIN_TIME_DELTA;
		msg.arg2 = MIN_DIST_DELTA;
		mServiceHandler.sendMessage(msg);

		// if we get killed, after returning from here, restart
		return START_STICKY;
	}

	private String oppositeProviderOf(String provider) {
		if (provider.equals(LocationManager.GPS_PROVIDER)) {
			return LocationManager.NETWORK_PROVIDER;
		} else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
			return LocationManager.NETWORK_PROVIDER;
		}
		return null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		RUNNING = false;
		mNotificationManager.cancel(NOTIFICATION_ID);
		for (int i = 0; i < CALLBACKS.size(); i++) {
			CALLBACKS.get(i).onLocationServiceStop(mStoppedMyself);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d("Energy2Live", "Location found lat:" + location.getLatitude() + " long:" + location.getLongitude());
		mLastLocation = location;
		for (int i = 0; i < CALLBACKS.size(); i++) {
			CALLBACKS.get(i).onNewLocationFound(location);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		String otherProvider = oppositeProviderOf(provider);
		if (mLocationManager.isProviderEnabled(otherProvider)) {
			Message msg = mServiceHandler.obtainMessage();
			msg.what = ServiceHandler.MSG_REQUEST_LOCATION_UPDATES;
			msg.obj = otherProvider;
			mServiceHandler.sendMessage(msg);
		} else {
			// TODO make toast
			mServiceHandler.sendEmptyMessage(ServiceHandler.MSG_STOP);
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		if (provider.equals(LocationManager.GPS_PROVIDER)) {
			Message msg = mServiceHandler.obtainMessage();
			msg.what = ServiceHandler.MSG_REQUEST_LOCATION_UPDATES;
			msg.obj = LocationManager.GPS_PROVIDER;
			mServiceHandler.sendMessage(msg);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		switch (status) {
		case LocationProvider.AVAILABLE:
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			break;
		case LocationProvider.OUT_OF_SERVICE:
			break;
		}
	}
	// private void waitFor(long millis) {
	// long endTime = System.currentTimeMillis() + millis;
	// while (System.currentTimeMillis() < endTime) {
	// synchronized (this) {
	// try {
	// wait(endTime - System.currentTimeMillis());
	// } catch (Exception e) {
	// }
	// }
	// }
	// }
}
