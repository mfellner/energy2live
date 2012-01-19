package st.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import st.energy2live.data.track.TrackLog;
import st.energy2live.data.user.User;
import st.energy2live.data.vehicle.Vehicle;
import st.energy2live.data.vehicle.VehicleA;
import st.energy2live.rdf.DataConnection;
import st.energy2live.rdf.RDFController;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

public class KMLTest {

	private static DataConnection dataCon;
	
	public static void main(String[] args) throws Exception {

		RDFController.clearAndLoadIDs();
		dataCon = new DataConnection();
		
		
		File trackDir =  new File("../RDFData/tracks");
//		processFile(new File("../RDFData/tracks/graz_001.kml"));
		
		
		List<User> userList = new ArrayList<User>();
		userList.add(new User("maxmuster", "111", "Max Muster", "max.muster@example.org", "http://www.maxmuster.com", 0));
		userList.add(new User("hansmuster", "111", "Hans Muster", "hans.muster@example.org", "http://www.hansmuster.com", 0));
		userList.add(new User("thomasmuster", "111", "Thomas Muster", "thomas.muster@example.org", "http://www.thomasmuster.com", 0));
		
		for(User user : userList){
			dataCon.insertNewUser(user);
		}
		
		List<VehicleA> vehicleList = new ArrayList<VehicleA>();
		vehicleList.add(new VehicleA("Kleinwagen", "Car Builder AG.", 0.04));
		vehicleList.add(new VehicleA("Mittelklassewagen", "Car Builder AG.", 0.06));
		vehicleList.add(new VehicleA("Mittelklassewagen", "Car Builder AG.", 0.07));
		
		
		for(VehicleA vehicle : vehicleList){
			dataCon.insertNewVehicle(vehicle);
		}
		
		String fileName;
		if(trackDir.exists() && trackDir.isDirectory())
		{
			File[] files = trackDir.listFiles();
			int counter = 0;
			for(File currentFile : files)
			{
				fileName = currentFile.getName();
				if(fileName.contains(".svn") || fileName.contains(".tmp"))
					continue;
				
				System.out.println("Processing File: " + currentFile.getName());
				System.out.println("User: " + userList.get(counter%3).getFullName());
				System.out.println("Vehicle: " + vehicleList.get(counter%3).getId());
				processFile(currentFile, userList.get(counter%3), vehicleList.get(counter%3));
				counter ++;
			}
		}
		
		
		
	}
	
	public static void processFile(File file, User user, Vehicle vehicle) throws Exception{
		
		if(!file.exists())
			return;
	
		Kml kml = Kml.unmarshal(file);;
		
		TrackLog log = new TrackLog(vehicle);
		
		DateTime dateTime = new DateTime();
		
		Document document = (Document) kml.getFeature();
		List<Feature> placemarks = document.getFeature();
		
		Placemark placemark = (Placemark) placemarks.get(1);
		LineString lineString = (LineString) placemark.getGeometry();

		List<Coordinate> coordinates = lineString.getCoordinates();
		for (Coordinate coordinate : coordinates) {
			dateTime = dateTime.plusMinutes(5);
			//System.out.println(coordinate.getLatitude());
			//System.out.println(coordinate.getLongitude());
			log.addTrackPoint(new Date(dateTime.getMillis()),coordinate.getLatitude() , coordinate.getLongitude());
		}
		
		dataCon.insertNewTrackLog(log, user);
		
	}
	

}
