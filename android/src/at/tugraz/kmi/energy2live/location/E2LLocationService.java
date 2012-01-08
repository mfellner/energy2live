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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import at.tugraz.kmi.energy2live.E2LRecordActivity;
import at.tugraz.kmi.energy2live.R;

public class E2LLocationService extends Service {
	private static final int NOTIFICATION_ID = 1;
	private static final long TIMEOUT = 43200000;

	private static boolean RUNNING;
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private NotificationManager mNotificationManager;

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO do something
			waitFor(TIMEOUT);
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			stopSelf(msg.arg1);
		}

		private void waitFor(long millis) {
			long endTime = System.currentTimeMillis() + millis;
			while (System.currentTimeMillis() < endTime) {
				synchronized (this) {
					try {
						wait(endTime - System.currentTimeMillis());
					} catch (Exception e) {
					}
				}
			}
		}
	}

	@Override
	public void onCreate() {
		RUNNING = true;
		HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) getSystemService(ns);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);

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

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO provide binding?
		return null;
	}

	@Override
	public void onDestroy() {
		RUNNING = false;
		mNotificationManager.cancel(NOTIFICATION_ID);
	}

	public static boolean isRunning() {
		return RUNNING;
	}
}
