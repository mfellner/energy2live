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

package at.tugraz.kmi.energy2live.remote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import st.energy2live.data.user.User;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import at.tugraz.kmi.energy2live.E2LSettingsActivity;
import at.tugraz.kmi.energy2live.R;
import at.tugraz.kmi.energy2live.model.E2LActivity;
import at.tugraz.kmi.energy2live.model.E2LUser;

import com.thoughtworks.xstream.XStream;

public class E2LNetworkConnection {
	private static final String API_PROTOCOL = "http://";
	private static final String API_ENDPOINT = ":8080/RDFWebTool/api";
	private static final String API_LOGIN = "/login";
	private static final String API_REGISTER = "/register";
	private static final String API_TRACKLOG = "/tracklog";

	private final Context mContext;
	private final XStream mXstream;
	private final SharedPreferences mSharedPreferences;
	private final List<Callback> mCallbacks;

	public static enum ACTION {
		LOGIN, REGISTER, ACTIVITY
	};

	public interface Callback {
		public void onNetworkConnectionResult(ACTION a, boolean b);
	}

	public E2LNetworkConnection(Context context) {
		mContext = context;
		mXstream = new XStream();
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		mCallbacks = new ArrayList<Callback>();
	}

	public void addCallback(Callback callback) {
		if (!mCallbacks.contains(callback)) {
			mCallbacks.add(callback);
		}
	}

	public void removeCallback(Callback callback) {
		while (mCallbacks.contains(callback)) {
			mCallbacks.remove(callback);
		}
	}

	public void loginUser(E2LUser user) {
		String server = mSharedPreferences.getString(E2LSettingsActivity.PREF_SERVER_ADDRESS, null);
		if (server == null) {
			Toast.makeText(mContext, mContext.getResources().getString(R.string.msg_remote_no_server),
					Toast.LENGTH_SHORT).show();
			return;
		}

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("nickname", user.getName()));
		nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));
		Log.d("E2L", "Login " + user.getName() + " " + user.getPassword());
		Log.d("E2L", API_PROTOCOL + server + API_ENDPOINT + API_LOGIN);

		postData(ACTION.LOGIN, API_PROTOCOL + server + API_ENDPOINT + API_LOGIN, nameValuePairs);
	}

	public void registerUser(E2LUser user) {
		String server = mSharedPreferences.getString(E2LSettingsActivity.PREF_SERVER_ADDRESS, null);
		if (server == null) {
			Toast.makeText(mContext, mContext.getResources().getString(R.string.msg_remote_no_server),
					Toast.LENGTH_SHORT).show();
			return;
		}

		E2LUserAdapter adapter = new E2LUserAdapter(user);
		mXstream.alias(User.class.getName(), E2LUserAdapter.class);
		String xml = mXstream.toXML(adapter);
		// Log.d("E2L", "XML:\n" + xml);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("user", xml));

		postData(ACTION.REGISTER, API_PROTOCOL + server + API_ENDPOINT + API_REGISTER, nameValuePairs);
	}

	public void sendActivityToServer(E2LActivity activity) {
		String server = mSharedPreferences.getString(E2LSettingsActivity.PREF_SERVER_ADDRESS, null);
		if (server == null) {
			Toast.makeText(mContext, mContext.getResources().getString(R.string.msg_remote_no_server),
					Toast.LENGTH_SHORT).show();
			return;
		}

		E2LTrackLogAdapter adapter = new E2LTrackLogAdapter(activity);
		String xml = mXstream.toXML(adapter.getTrackLog());
		// Log.d("E2L", "XML:\n" + xml);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("tracklog", xml));

		postData(ACTION.ACTIVITY, API_PROTOCOL + server + API_ENDPOINT + API_TRACKLOG, nameValuePairs);
	}

	private void postData(final ACTION a, final String url, final List<NameValuePair> nameValuePairs) {
		final ProgressDialog progressDialog = ProgressDialog.show(mContext, "",
				mContext.getString(R.string.msg_remote_post_data), true);
		Thread thread = new Thread() {
			@Override
			public void run() {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				HttpResponse response = null;

				try {
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					response = httpclient.execute(httppost);
				} catch (ClientProtocolException e) {
					Log.e("E2L", "Cannot post data\n", e);
				} catch (IOException e) {
					Log.e("E2L", "Cannot post data\n", e);
				} finally {
					progressDialog.dismiss();
					postDataDone(a, response);
				}
			}
		};
		thread.start();
	}

	private void postDataDone(ACTION a, HttpResponse response) {
		if (response == null)
			return;

		Log.d("E2L HTTP", "status code " + response.getStatusLine().getStatusCode());
		Log.d("E2L HTTP", response.getStatusLine().getReasonPhrase());
		for (Header h : response.getAllHeaders()) {
			Log.d("E2L HTTP", h.getName() + ": " + h.getValue());
		}

		boolean result = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

		for (int i = 0; i < mCallbacks.size(); i++) {
			mCallbacks.get(i).onNetworkConnectionResult(a, result);
		}
	}
}
