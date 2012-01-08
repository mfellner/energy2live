package at.tugraz.kmi.energy2live;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.tugraz.kmi.energy2live.database.E2LDatabaseHelper;
import at.tugraz.kmi.energy2live.model.implementation.E2LActivityImplementation;

public class E2LActivityArrayAdapter extends ArrayAdapter<E2LActivityImplementation> {
	private final E2LDatabaseHelper dbHelper;

	public E2LActivityArrayAdapter(Context context, int textViewResourceId, List<E2LActivityImplementation> objects,
			E2LDatabaseHelper databaseHelper) {
		super(context, textViewResourceId, objects);
		dbHelper = databaseHelper;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_activities_row, null);
		}

		E2LActivityImplementation activity = getItem(position);
		try {
			dbHelper.getActivityDao().refresh(activity);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		String name = activity.getName() != null ? activity.getName() : "<null>";
		fillText(convertView, R.id.lst_activities_item_name, name);

		String time = activity.getTime() != null ? Utils.SDF_READABLE.format(activity.getTime()) : "<null>";
		fillText(convertView, R.id.lst_activities_item_time, time);

		return convertView;
	}

	private void fillText(View v, int id, String text) {
		TextView textView = (TextView) v.findViewById(id);
		textView.setText(text == null ? "" : text);
	}
}
