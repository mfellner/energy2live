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
	private final GeoPoint mGeoPoint;
	private final Point mPoint;
	private final Bitmap mBitmap;

	public DrawableMapOverlay(Context context, GeoPoint geoPoint, int drawable) {
		mGeoPoint = geoPoint;
		mPoint = new Point();
		mBitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		super.draw(canvas, mapView, shadow);

		mapView.getProjection().toPixels(mGeoPoint, mPoint);

		int x = mPoint.x - (mBitmap.getWidth() / 2);
		int y = mPoint.y - (mBitmap.getHeight() / 2);
		canvas.drawBitmap(mBitmap, x, y, null);
		return true;
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		return false;
	}
}
