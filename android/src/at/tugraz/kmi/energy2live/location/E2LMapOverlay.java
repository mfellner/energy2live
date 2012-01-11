package at.tugraz.kmi.energy2live.location;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class E2LMapOverlay extends ItemizedOverlay<OverlayItem> {
	private final ArrayList<OverlayItem> mOverlays;
	private final Context mContext;

	public E2LMapOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mOverlays = new ArrayList<OverlayItem>();
		mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

}
