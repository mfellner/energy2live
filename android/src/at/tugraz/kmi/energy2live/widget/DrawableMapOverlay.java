package at.tugraz.kmi.energy2live.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class DrawableMapOverlay extends Overlay {
	private final GeoPoint geoPoint;
	private final Context context;
	private final int drawable;

	public DrawableMapOverlay(Context context, GeoPoint geoPoint, int drawable) {
		this.context = context;
		this.geoPoint = geoPoint;
		this.drawable = drawable;
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		super.draw(canvas, mapView, shadow);

		Point screenPoint = new Point();
		mapView.getProjection().toPixels(geoPoint, screenPoint);

		Bitmap markerImage = BitmapFactory.decodeResource(context.getResources(), drawable);

		canvas.drawBitmap(markerImage, screenPoint.x - markerImage.getWidth() / 2,
				screenPoint.y - markerImage.getHeight() / 2, null);
		return true;
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		// Handle tapping on the overlay here
		return true;
	}
}
