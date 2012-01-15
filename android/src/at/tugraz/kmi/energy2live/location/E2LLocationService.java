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
	private static final int MAX_ACCELERATION = 30; // m/s
	private static final int MAX_SPEED = 80; // m/s

	private final int mMinTimeDelta = 10000; // ms
	private final int mMinDistDelta = 100; // m

	private static boolean RUNNING;
	private boolean mStoppedMyself;
	private int mStartId;
	private ServiceHandler mServiceHandler;
	private NotificationManager mNotificationManager;
	private LocationManager mLocationManager;
	private ArrayList<Location> mLocations;
	private int mAverageSpeed;
	private static List<Callback> CALLBACKS;

	private final class ServiceHandler extends Handler {
		private final Object lock;
		static final int MSG_STOP = 0;
		static final int MSG_REQUEST_LOCATION_UPDATES = 1;
		static final int MSG_PAUSE_LOCATION_UPDATES = 2;
		static final int MSG_ON_LOCATION_CHANGED = 3;

		public ServiceHandler(Looper looper) {
			super(looper);
			lock = new Object();
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_STOP:
				mLocationManager.removeUpdates(E2LLocationService.this);
				mStoppedMyself = true;
				stopSelf(mStartId);
				sleep();
				break;
			case MSG_REQUEST_LOCATION_UPDATES:
				mLocationManager.removeUpdates(E2LLocationService.this);
				mLocationManager.requestLocationUpdates((String) msg.obj, msg.arg1, msg.arg2, E2LLocationService.this);
				sleep();
				break;
			case MSG_PAUSE_LOCATION_UPDATES:
				sleep();
				break;
			case MSG_ON_LOCATION_CHANGED:
				Location location = (Location) msg.obj;
				if (calculateNewAverages(location)) {
					mLocations.add(location);
					for (int i = 0; i < CALLBACKS.size(); i++) {
						CALLBACKS.get(i).onNewLocationFound(location);
					}
				}
				sleep();
				break;
			}
		}

		private void sleep() {
			synchronized (lock) {
				try {
					wait();
				} catch (Exception e) {
				}
			}
		}

		public void wakeUp() {
			synchronized (lock) {
				try {
					lock.notify();
				} catch (Exception e) {
				}
			}
		}
	}

	public interface Callback {
		public void onNewLocationFound(Location location);

		public void onLocationServiceStop(boolean serviceStoppedItself, ArrayList<Location> locations);
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
		mLocations = new ArrayList<Location>(10);
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
		msg.obj = LocationManager.GPS_PROVIDER;
		msg.arg1 = mMinTimeDelta;
		msg.arg2 = mMinDistDelta;
		mServiceHandler.sendMessage(msg);

		// if we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		RUNNING = false;
		mServiceHandler.wakeUp();
		mServiceHandler.sendEmptyMessage(-1);
		mNotificationManager.cancel(NOTIFICATION_ID);
		for (int i = 0; i < CALLBACKS.size(); i++) {
			CALLBACKS.get(i).onLocationServiceStop(mStoppedMyself, mLocations);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d("Energy2Live", "Location found lat:" + location.getLatitude() + " long:" + location.getLongitude());
		mServiceHandler.wakeUp();
		Message msg = mServiceHandler.obtainMessage();
		msg.what = ServiceHandler.MSG_ON_LOCATION_CHANGED;
		msg.obj = location;
		mServiceHandler.sendMessage(msg);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// mServiceHandler.wakeUp();
		// String otherProvider = oppositeProviderOf(provider);
		// if (mLocationManager.isProviderEnabled(otherProvider)) {
		// Message msg = mServiceHandler.obtainMessage();
		// msg.what = ServiceHandler.MSG_REQUEST_LOCATION_UPDATES;
		// msg.obj = otherProvider;
		// msg.arg1 = mMinTimeDelta;
		// msg.arg2 = mMinDistDelta;
		// mServiceHandler.sendMessage(msg);
		// } else {
		// // TODO make toast
		// Log.d("E2L", provider + " disabled, stop");
		// mServiceHandler.sendEmptyMessage(ServiceHandler.MSG_STOP);
		// }
	}

	@Override
	public void onProviderEnabled(String provider) {
		mServiceHandler.wakeUp();
		if (provider.equals(LocationManager.GPS_PROVIDER)) {
			Message msg = mServiceHandler.obtainMessage();
			msg.what = ServiceHandler.MSG_REQUEST_LOCATION_UPDATES;
			msg.obj = LocationManager.GPS_PROVIDER;
			msg.arg1 = mMinTimeDelta;
			msg.arg2 = mMinDistDelta;
			mServiceHandler.sendMessage(msg);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		mServiceHandler.wakeUp();
		switch (status) {
		case LocationProvider.AVAILABLE:
			Log.d("E2L", provider + " AVAILABLE");
			switchToProvider(LocationManager.GPS_PROVIDER);
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			Log.d("E2L", provider + " TEMPORARILY_UNAVAILABLE");
			if (provider.equals(LocationManager.GPS_PROVIDER)
					&& mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				switchToProvider(LocationManager.NETWORK_PROVIDER);
			} else {
				mServiceHandler.sendEmptyMessage(ServiceHandler.MSG_PAUSE_LOCATION_UPDATES);
			}
			break;
		case LocationProvider.OUT_OF_SERVICE:
			Log.d("E2L", provider + " OUT_OF_SERVICE");
			if (provider.equals(LocationManager.GPS_PROVIDER)
					&& mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				switchToProvider(LocationManager.NETWORK_PROVIDER);
			} else {

			}
			break;
		}
	}

	private void switchToProvider(String provider) {
		Log.d("E2L", "switchToProvider " + provider);
		Message msg = mServiceHandler.obtainMessage();
		msg.what = ServiceHandler.MSG_REQUEST_LOCATION_UPDATES;
		msg.obj = provider;
		msg.arg1 = mMinTimeDelta;
		msg.arg2 = mMinDistDelta;
		mServiceHandler.sendMessage(msg);
	}

	private boolean calculateNewAverages(Location loc0) {
		int n = mLocations.size();
		if (n < 3)
			return true;

		Location loc1 = mLocations.get(n - 1);
		Location loc2 = mLocations.get(n - 2);
		Location loc3 = mLocations.get(n - 3);

		long dt = Math.round((loc0.getTime() - loc1.getTime()) / 1000f);
		if (dt == 0)
			dt = 1;
		int ds = Math.round(loc0.distanceTo(loc1));
		int v = Math.round(ds / dt); // velocity m/s

		if (Math.abs(mAverageSpeed - v) > MAX_ACCELERATION || v > MAX_SPEED)
			return false;

		mAverageSpeed = Math.round((loc0.getTime() + loc1.getTime() + loc2.getTime() + loc3.getTime()) / 4f);

		return true;
	}

	private String oppositeProviderOf(String provider) {
		if (provider.equals(LocationManager.GPS_PROVIDER)) {
			return LocationManager.NETWORK_PROVIDER;
		} else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
			return LocationManager.NETWORK_PROVIDER;
		}
		return null;
	}
}
