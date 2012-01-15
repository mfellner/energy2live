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
