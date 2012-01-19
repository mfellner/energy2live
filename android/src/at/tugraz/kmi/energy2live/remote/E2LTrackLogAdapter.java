package at.tugraz.kmi.energy2live.remote;

import java.util.Date;
import java.util.List;

import st.energy2live.data.track.TrackLog;
import st.energy2live.data.vehicle.Vehicle;
import st.energy2live.data.vehicle.VehicleA;
import at.tugraz.kmi.energy2live.model.E2LActivity;
import at.tugraz.kmi.energy2live.model.implementation.E2LActivityLocationImplementation;

public class E2LTrackLogAdapter {
	private final TrackLog mTrackLog;

	public E2LTrackLogAdapter(E2LActivity activity) {
		Vehicle vehicle = new VehicleA("A3", "Audi", 7.5);
		mTrackLog = new TrackLog(vehicle);
		List<E2LActivityLocationImplementation> locations = activity.getLocations();
		for (int i = 0; i < locations.size(); i++) {
			E2LActivityLocationImplementation loc = locations.get(i);
			mTrackLog.addTrackPoint(new Date(loc.getTime()), loc.getLatitude(), loc.getLongitude());
		}
	}

	public TrackLog getTrackLog() {
		return mTrackLog;
	}
}
