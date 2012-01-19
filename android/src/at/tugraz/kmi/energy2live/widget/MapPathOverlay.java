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

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MapPathOverlay extends Overlay {
	private final List<GeoPoint> mGeoPoints;
	private final Point mPoint1;
	private final Point mPoint2;
	private final Path mPath;
	private final Paint mPaint;

	public MapPathOverlay(List<GeoPoint> geopoints) {
		mGeoPoints = geopoints;
		mPoint1 = new Point();
		mPoint2 = new Point();
		mPath = new Path();
		mPath.incReserve(geopoints.size());

		mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(4);
	}

	@Override
	public void draw(Canvas canvas, MapView mapv, boolean shadow) {
		super.draw(canvas, mapv, shadow);

		Projection projection = mapv.getProjection();
		projection.toPixels(mGeoPoints.get(0), mPoint1);
		mPath.moveTo(mPoint1.x, mPoint1.y);

		for (int i = 1; i < mGeoPoints.size(); i++) {
			projection.toPixels(mGeoPoints.get(i), mPoint2);
			mPath.lineTo(mPoint2.x, mPoint2.y); // TODO improve drawing
			// mPoint1.x = (mPoint1.x + mPoint2.x) / 2;
			// mPoint1.x = (mPoint1.y + mPoint2.y) / 2;
			// mPath.quadTo(mPoint1.x, mPoint1.y, mPoint2.x, mPoint2.y);
			// projection.toPixels(mGeoPoints.get(i), mPoint1);
		}

		canvas.drawPath(mPath, mPaint);
		mPath.rewind();
	}
}
