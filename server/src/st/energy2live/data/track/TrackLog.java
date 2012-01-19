package st.energy2live.data.track;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import st.energy2live.data.vehicle.Vehicle;

public class TrackLog implements Serializable {

	private static final long serialVersionUID = 1752370042245122464L;
	
	private List<TrackPoint> trackPoints;
    private Vehicle vehicle;
    private String id;
    
    // Distance in KM
    private double trackDistance = 0;
    
    
    public TrackLog(Vehicle vehicle) {
        this.vehicle = vehicle;
        trackPoints = new ArrayList<TrackPoint>();
        this.id = "";
    }

    public void addTrackPoint(Date dateTime, double latitude, double longitude){
        TrackPoint trackPoint = new TrackPoint(dateTime, latitude, longitude);
        this.trackPoints.add(trackPoint);
        calcDistance();
    }
    
    public String getID(){
    	return id;
    }
    
    public void setID(String id){
    	this.id = id;
    }

	public Vehicle getVehicle() {
		return vehicle;
	}
	
	public double getFuelConsumtion() {
		return trackDistance * vehicle.getFuelConsumtion();
	}
	
	public double getTrackDistance() {
		return trackDistance;
	}
	
	public List<TrackPoint> getTrackPoints() {
		return trackPoints;
	}
	
	// This method calculates the whole distance from startpoint to endpoint
	// Use Android Location for this
	// http://developer.android.com/reference/android/location/Location.html
	protected void calcDistance(){
		
		if(trackPoints.size() < 2)
			return;
		
		double d2r = Math.PI / 180.0;
		
		Point point1 = trackPoints.get(trackPoints.size() - 1).getPoint();
		Point point2 = trackPoints.get(trackPoints.size() - 2).getPoint();
		
		double lat1 = point1.getLatitude();
		double long1 = point1.getLongitude();
		double lat2 = point2.getLatitude();
		double long2 = point2.getLongitude();
		
	    double dlong = (long2 - long1) * d2r;
	    double dlat = (lat2 - lat1) * d2r;
	    double a = Math.pow(Math.sin(dlat/2.0), 2) + Math.cos(lat1*d2r) * Math.cos(lat2*d2r) * Math.pow(Math.sin(dlong/2.0), 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double d = 6367 * c;

	    trackDistance += d;
	}
    
}
