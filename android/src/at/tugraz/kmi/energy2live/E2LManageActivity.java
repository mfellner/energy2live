package at.tugraz.kmi.energy2live;

import java.sql.SQLException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.kmi.energy2live.database.E2LDatabaseHelper;
import at.tugraz.kmi.energy2live.model.implementation.E2LActivityImplementation;
import at.tugraz.kmi.energy2live.widget.ActionBar;
import at.tugraz.kmi.energy2live.widget.ActionBar.Action;
import at.tugraz.kmi.energy2live.widget.ActionBar.IntentAction;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;

public class E2LManageActivity extends OrmLiteBaseActivity<E2LDatabaseHelper> {
	public static final String EXTRA_ACTIVITY_ID = "EXTRA_ACTIVITY_ID";

	private E2LActivityImplementation mActivity;
	private TextView lblName;
	private TextView lblTime;

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

		Bundle extras = getIntent().getExtras();
		if (extras.get(EXTRA_ACTIVITY_ID) != null) {
			try {
				loadFromActivityObject((Integer) extras.get(EXTRA_ACTIVITY_ID));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
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
			lblName.setText(mActivity.getName());
			lblTime.setText(Utils.SDF_READABLE.format(mActivity.getTime()));
		} else {
			toastMessage("Error: id does not exist");
		}
	}

	private void toastMessage(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
}
