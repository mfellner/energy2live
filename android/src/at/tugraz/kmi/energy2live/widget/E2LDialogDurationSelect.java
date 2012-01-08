package at.tugraz.kmi.energy2live.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import at.tugraz.kmi.energy2live.R;

public class E2LDialogDurationSelect extends Dialog implements View.OnClickListener {
	public static final String ID = "E2LDialogDurationSelect";

	private final String[] mHoursArray;
	private final String[] mMinutesArray;
	private final Spinner spnnrHours;
	private final Spinner spnnrMinutes;
	private int mSelectedHours;
	private int mSelectedMinutes;

	public E2LDialogDurationSelect(Context context) {
		super(context);
		setContentView(R.layout.dialog_duration_select);
		setCancelable(false);

		mHoursArray = new String[24];
		for (int i = 0; i < mHoursArray.length; i++) {
			mHoursArray[i] = Integer.toString(i);
		}

		mMinutesArray = new String[60];
		for (int i = 0; i < mMinutesArray.length; i++) {
			mMinutesArray[i] = Integer.toString(i);
		}

		findViewById(R.id.btn_duration_select).setOnClickListener(this);
		spnnrHours = (Spinner) findViewById(R.id.spnnr_duration_hours);
		spnnrMinutes = (Spinner) findViewById(R.id.spnnr_duration_minutes);
		spnnrHours.setSelection(0);
		spnnrMinutes.setSelection(0);

		spnnrHours.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line,
				mHoursArray));
		spnnrMinutes.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line,
				mMinutesArray));
	}

	@Override
	public String toString() {
		return ID;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_duration_select:
			selectButtonClicked();
			break;
		}
	}

	public int getSelectedHours() {
		return mSelectedHours;
	}

	public int getSelectedMinutes() {
		return mSelectedMinutes;
	}

	private void selectButtonClicked() {
		mSelectedHours = Integer.parseInt(mHoursArray[spnnrHours.getSelectedItemPosition()]);
		mSelectedMinutes = Integer.parseInt(mMinutesArray[spnnrMinutes.getSelectedItemPosition()]);
		if (mSelectedHours > 0 || mSelectedMinutes > 0)
			dismiss();
		// else
		// TODO: make toast
	}
}
