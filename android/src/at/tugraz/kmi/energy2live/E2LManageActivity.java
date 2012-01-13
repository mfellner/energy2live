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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.kmi.energy2live.database.E2LDatabaseHelper;
import at.tugraz.kmi.energy2live.location.E2LMapOverlay;
import at.tugraz.kmi.energy2live.model.implementation.E2LActivityImplementation;
import at.tugraz.kmi.energy2live.widget.ActionBar;
import at.tugraz.kmi.energy2live.widget.ActionBar.Action;
import at.tugraz.kmi.energy2live.widget.ActionBar.IntentAction;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class E2LManageActivity extends MapActivity {
	public static final String EXTRA_ACTIVITY_ID = "EXTRA_ACTIVITY_ID";
	public static final String EXTRA_ACTIVITY_NAME = "EXTRA_ACTIVITY_NAME";
	public static final String EXTRA_LOCATIONS = "EXTRA_LOCATIONS";

	private E2LDatabaseHelper mDatabaseHelper;
	private E2LActivityImplementation mActivity;
	private TextView lblName;
	private TextView lblTime;
	private MapView mMapView;
	private List<Overlay> mMapOverlays;

	private class DeleteAction implements Action {

		@Override
		public int getDrawable() {
			return R.drawable.ic_action_delete;
		}

		@Override
		public void performAction(View view) {
			Resources res = E2LManageActivity.this.getResources();
			AlertDialog.Builder builder = new AlertDialog.Builder(E2LManageActivity.this);
			builder.setMessage(res.getString(R.string.msg_delete_activity)).setCancelable(false)
					.setPositiveButton(res.getString(R.string.yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							deleteActivity();
						}
					}).setNegativeButton(res.getString(R.string.no), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder.create().show();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage);

		ActionBar actionBar = (ActionBar) findViewById(R.id.manage_actionbar);
		actionBar.setHomeAction(new IntentAction(this, Utils.createIntent(this, E2LMainActivity.class),
				R.drawable.ic_action_home));
		actionBar.addAction(new DeleteAction());

		lblName = (TextView) findViewById(R.id.lbl_manage_activity_name);
		lblTime = (TextView) findViewById(R.id.lbl_manage_activity_time);

		mMapView = (MapView) findViewById(R.id.manage_mapview);
		mMapView.setBuiltInZoomControls(true);
		mMapOverlays = mMapView.getOverlays();

		Integer integer = (Integer) getIntent().getExtras().get(EXTRA_ACTIVITY_ID);
		if (integer != null) {
			try {
				loadFromActivityObject(integer);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		String name = getIntent().getExtras().getString(EXTRA_ACTIVITY_NAME);
		if (name != null) {
			mActivity = new E2LActivityImplementation();
			mActivity.setName(name);
			try {
				Dao<E2LActivityImplementation, Integer> dao = getHelper().getActivityDao();
				dao.create(mActivity);
				loadFromActivityObject(mActivity.getId());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		ArrayList<Parcelable> list = getIntent().getParcelableArrayListExtra(EXTRA_LOCATIONS);
		if (list != null) {
			populateMap(list);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private void deleteActivity() {
		try {
			Dao<E2LActivityImplementation, Integer> dao = getHelper().getActivityDao();
			dao.delete(mActivity);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		toastMessage("Deleted activity!");
		mActivity = null;
		finish();
	}

	private void loadFromActivityObject(int id) throws SQLException {
		Dao<E2LActivityImplementation, Integer> dao = getHelper().getActivityDao();
		if (dao.idExists(id)) {
			mActivity = dao.queryForId(id);
			getHelper().getActivityDao().refresh(mActivity);
			String name = mActivity.getName();
			Log.d("Energy2Live", "Name is " + name);
			if (name != null)
				lblName.setText(name);
			Date time = mActivity.getTime();
			if (time != null)
				lblTime.setText(Utils.SDF_READABLE.format(time));
		} else {
			toastMessage("Error: id does not exist");
		}
	}

	private void populateMap(ArrayList<Parcelable> locations) {
		Drawable drawable = this.getResources().getDrawable(R.drawable.ic_androidmarker);
		E2LMapOverlay itemizedOverlay = new E2LMapOverlay(drawable, this);
		mMapOverlays.clear();

		for (int i = 0; i < locations.size(); i++) {
			Location location = (Location) locations.get(i);
			int latitude = (int) Math.round(location.getLatitude() * 1E6f);
			int longitude = (int) Math.round(location.getLongitude() * 1E6f);
			GeoPoint point = new GeoPoint(latitude, longitude);
			OverlayItem overlayitem = new OverlayItem(point, "Yes,", "this is dog!");
			itemizedOverlay.addOverlay(overlayitem);
			mMapOverlays.add(itemizedOverlay);
		}

		mMapView.postInvalidate();
	}

	private E2LDatabaseHelper getHelper() {
		if (mDatabaseHelper == null) {
			mDatabaseHelper = OpenHelperManager.getHelper(this, E2LDatabaseHelper.class);
		}
		return mDatabaseHelper;
	}

	private void toastMessage(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
}
