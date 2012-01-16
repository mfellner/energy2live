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

package at.tugraz.kmi.energy2live.model.implementation;

import java.io.Serializable;

import android.location.Location;

import com.j256.ormlite.field.DatabaseField;

public class E2LActivityLocationImplementation implements Serializable {
	private static final long serialVersionUID = -6567505636989115666L;

	@DatabaseField
	private double mLatitude;

	@DatabaseField
	private double mLongitude;

	@DatabaseField
	private long mTime;

	public E2LActivityLocationImplementation() {

	}

	public E2LActivityLocationImplementation(Location location) {
		setLatitude(location.getLatitude());
		setLongitude(location.getLongitude());
		setTime(location.getTime());
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double latitude) {
		mLatitude = latitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double longitude) {
		mLongitude = longitude;
	}

	public long getTime() {
		return mTime;
	}

	public void setTime(long time) {
		mTime = time;
	}
}
