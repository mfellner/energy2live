package st.energy2live.controller;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import st.energy2live.data.track.TrackLog;
import st.energy2live.data.track.TrackPoint;
import st.energy2live.data.user.User;
import st.energy2live.rdf.DataConnection;

@Controller
@RequestMapping("/tool")
public class ToolController {
	
	private DataConnection con;
	
	@RequestMapping("/home")
    public String getContentHome(Model model) {
        String message = "";
		model.addAttribute("message", message);
        return "home";
	}
	
	
	@RequestMapping("/users")
    public String getUsers(Model model) throws Exception {
        con = new DataConnection();
        List<User> userList = (List<User>)con.getUsers();
        
        String table = "<table><thead><tr><td>Name</td><td>Mail</td><td>Website</td><td></td></tr></thead>";
		
        for(User user : userList){
			table += "<tr>";
			table += "<td>" + user.getFullName() + "</td>";
			table += "<td><a href=\"" + user.getEmail() + "\" target=\"blank_\">" + user.getEmail().substring(user.getEmail().indexOf(":") + 1) + "</a></td>";
			table += "<td><a href=\"" + user.getHomePage() + "\" target=\"blank_\">" + user.getHomePage() + "</a></td>";
			table += "<td><a href=\"tracks/" + user.getNickName() + "\">show Tracks</a></td>";
			table += "</tr>";
		}
		table += "</table>";
		
		model.addAttribute("table", table);
        return "user";
	}
	
	
	@RequestMapping("/tracks/{username}")
    public String getUserTracks(Model model, @PathVariable String username) throws Exception {
		con = new DataConnection();
		
		User user = con.getUserByID(username);
		
        List<TrackLog> trackList = (List<TrackLog>)con.getUserTracks(username);
        String table = "<table><thead><tr><td>Track ID</td><td>Vehicle Name</td><td>Consumption</td><td></td></tr></thead>";
        
		for(TrackLog log : trackList){
			table += "<tr>";
			table += "<td>Track " + log.getID().substring(log.getID().lastIndexOf("/") + 1) + "</td>";
			table += "<td>" + log.getVehicle().getName() + "</td>";
			table += "<td class=\"td_center\">" + log.getVehicle().getFuelConsumtion() + "</td>";
			table += "<td><a href=\"../track/" + username + "/" + log.getID().substring(log.getID().lastIndexOf("/") + 1) + "\">detail view</a></td>";
			table += "</tr>";
		}
		
		
		table += "<tfoot>";
		table += "<td>Overview of all tracks:</td><td><a href=\"../track/" + username + "/all" + "\">detail view</a></td>";
		table += "</tfoot>";
		table += "</table>";
		
		model.addAttribute("username", user.getFullName());
		model.addAttribute("table", table);
        return "tracks";
	}
	
	
	@RequestMapping("/tracklist")
    public String getTrackList(Model model) throws Exception {
		con = new DataConnection();
		
        List<TrackLog> trackList = (List<TrackLog>)con.getTrackList();
        String table = "<table><thead><tr><td>Track ID</td><td>Vehicle Name</td><td>Consumption</td><td>User</td><td></td></tr></thead>";
        
		for(TrackLog log : trackList){
			
			User user = con.getUserByTrackID(log.getID());
			
			table += "<tr>";
			table += "<td>Track " + log.getID().substring(log.getID().lastIndexOf("/") + 1) + "</td>";
			table += "<td>" + log.getVehicle().getName() + "</td>";
			table += "<td class=\"td_center\">" + log.getVehicle().getFuelConsumtion() + "</td>";
			table += "<td>" + user.getFullName() + "</td>";
			table += "<td><a href=\"track/" + user.getNickName() + "/" + log.getID().substring(log.getID().lastIndexOf("/") + 1) + "\">detail view</a></td>";
			table += "</tr>";
		}
		
		
		table += "<tfoot>";
		table += "<td>Overview of all tracks:</td><td><a href=\"tracks\">detail view</a></td>";
		table += "</tfoot>";
		table += "</table>";
		
		model.addAttribute("table", table);
        return "trackList";
	}
	
	
	@RequestMapping("/track/{username}/{trackid}")
    public String getUserTrackDetail(Model model, @PathVariable("username") String username, @PathVariable("trackid") String trackID) throws Exception {
		String link_back = "<a href=\"../../tracks/" + username + "\">back</a>";
		con = new DataConnection();
		TrackLog log = con.getUserTrackByID(username, trackID);
		
		User user = con.getUserByID(username);
        
		String vehicleName = log.getVehicle().getName();
		String vehicleManu = log.getVehicle().getManuFacturer();
		
        Locale.setDefault(Locale.ENGLISH);
        DecimalFormat decFormat = new DecimalFormat(",##0.00");
        String distance = decFormat.format(log.getTrackDistance());
        String fuelConsumption = decFormat.format(log.getFuelConsumtion());
        
        String startCoords = "";
        String endCoords = "";
        String pointArrayInit = "";
        
        int counter = 0;
		for(TrackPoint point : log.getTrackPoints()){
			String currentCoords = point.getPoint().getLatitude() + "," + point.getPoint().getLongitude();
			if(counter == 0)
				startCoords = currentCoords;
			else
				pointArrayInit += ",";
			if(counter == log.getTrackPoints().size()-1)
				endCoords = currentCoords;
			
			pointArrayInit += "new google.maps.LatLng(" + currentCoords + ")";
			counter++;
		}
        
        model.addAttribute("link_back", link_back);
		model.addAttribute("startCoords", startCoords);
		model.addAttribute("endCoords", endCoords);
		model.addAttribute("pointArrayInit", pointArrayInit);
		
		model.addAttribute("username", user.getFullName());
		model.addAttribute("trackid", trackID);
		model.addAttribute("vehiclename", vehicleName);
		model.addAttribute("vehiclemanu", vehicleManu);
		model.addAttribute("distance", distance);
		model.addAttribute("fuelconsumption", fuelConsumption);
        return "trackDetail";
        
	}
	
	
	@RequestMapping("/track/{username}/all")
    public String getAllUserTracksDetail(Model model, @PathVariable("username") String username) throws Exception {
		String link_back = "<a href=\"../../tracks/" + username + "\">back</a>";
		con = new DataConnection();
		User user = con.getUserByID(username);
		Locale.setDefault(Locale.ENGLISH);
		
		List<TrackLog> trackList = (List<TrackLog>)con.getAllUserTracks(username);
		
		double distanceAverage = 0.0;
		double fuelConsumptionAverage = 0.0;
		DecimalFormat decFormat = new DecimalFormat(",##0.00");
		int counter = 0;
		
		String tracksContent = "";
		
		String centerCoords = "";
		String startCoords = "";
        String endCoords = "";
        
        
		for(TrackLog log : trackList){
			
			distanceAverage += log.getTrackDistance();
			fuelConsumptionAverage += log.getFuelConsumtion();
			
			String pointArrayInit = "";
			int innerCounter = 0;
			for(TrackPoint point : log.getTrackPoints()){
				String currentCoords = point.getPoint().getLatitude() + "," + point.getPoint().getLongitude();
				if(counter == 0)
					centerCoords = currentCoords;
				if(innerCounter == 0){
					//tracksContent += "var markerStart" + counter + " = new google.maps.Marker({ position: new google.maps.LatLng(" + currentCoords + "), map: map });";
				}else{
					pointArrayInit += ",";
				}
				if(innerCounter == log.getTrackPoints().size()-1){
					//tracksContent += "var markerEnd" + counter + " = new google.maps.Marker({ position: new google.maps.LatLng(" + currentCoords + "), map: map });";
				}
				pointArrayInit += "new google.maps.LatLng(" + currentCoords + ")";
				innerCounter++;
			}
			
			tracksContent += "var pointArray" + counter + " = [" + pointArrayInit + "];";
			tracksContent += "var trackPath" + counter + " = new google.maps.Polyline({path: pointArray" + counter + ", strokeColor: \"#FF0000\", strokeOpacity: 1.0, strokeWeight: 2 }); trackPath" + counter + ".setMap(map);";
			counter ++;
		}
		
		String distance = "";
    	String fuelConsumption = "";
		
		if(trackList.size() > 0){
			distance = decFormat.format(distanceAverage/trackList.size());
        	fuelConsumption = decFormat.format(fuelConsumptionAverage/trackList.size());
		}
		
		model.addAttribute("link_back", link_back);
		model.addAttribute("username", user.getFullName());
		
		model.addAttribute("distance", distance);
		model.addAttribute("fuelconsumption", fuelConsumption);
		
		model.addAttribute("centerCoords", centerCoords);
		model.addAttribute("tracksContent", tracksContent);
		
		return "allUserTracksDetail";
	}
	
	
	@RequestMapping("/tracks")
    public String getAllTracks(Model model) throws Exception {
		con = new DataConnection();
		List<User> userList = (List<User>)con.getUsers();
		Locale.setDefault(Locale.ENGLISH);
		
		Random rand = new Random();
		
		double distanceAverage = 0.0;
		double fuelConsumptionAverage = 0.0;
		DecimalFormat decFormat = new DecimalFormat(",##0.00");
		
		String centerCoords = "";
		String startCoords = "";
        String endCoords = "";
        
        String tracksContent = "";
        
		int counterUser = 0;
		int counterTracks = 0;
		
		int randomUser = rand.nextInt(userList.size());
		
		String userContent = "<ul>";
		
		for(User user : userList){
			
			String colorString = "";
			for(int i=0; i<3; i++){
				String hex = Integer.toHexString(rand.nextInt(256));
				if(hex.length()< 2)
					colorString += "0" + hex;
				else
					colorString += hex;
			}
			
			userContent += "<li>" + user.getFullName() + "</li><li><div class=\"colorCube\" style=\"background: #" + colorString + "\"></div></li>";
			
			List<TrackLog> trackList = (List<TrackLog>)con.getAllUserTracks(user.getNickName());
			
			for(TrackLog log : trackList){
				distanceAverage += log.getTrackDistance();
				fuelConsumptionAverage += log.getFuelConsumtion();
				
				String pointArrayInit = "";
				int innerCounter = 0;
				for(TrackPoint point : log.getTrackPoints()){
					String currentCoords = point.getPoint().getLatitude() + "," + point.getPoint().getLongitude();
					if(randomUser == counterUser)
						centerCoords = currentCoords;
					if(innerCounter == 0){
						//tracksContent += "var markerStart" + counterTracks + " = new google.maps.Marker({ position: new google.maps.LatLng(" + currentCoords + "), map: map });";
					}else{
						pointArrayInit += ",";
					}
					if(innerCounter == log.getTrackPoints().size()-1){
						//tracksContent += "var markerEnd" + counterTracks + " = new google.maps.Marker({ position: new google.maps.LatLng(" + currentCoords + "), map: map });";
					}
					pointArrayInit += "new google.maps.LatLng(" + currentCoords + ")";
					innerCounter++;
				}
				
				tracksContent += "var pointArray" + counterTracks + " = [" + pointArrayInit + "];";
				tracksContent += "var trackPath" + counterTracks + " = new google.maps.Polyline({path: pointArray" + counterTracks + ", strokeColor: \"#" + colorString + "\", strokeOpacity: 1.0, strokeWeight: 2 }); trackPath" + counterTracks + ".setMap(map);";
				
				counterTracks ++;
			}
			counterUser ++;
		}
		
		userContent += "</ul>";
		
		String distance = "";
    	String fuelConsumption = "";
		
		if(counterTracks > 0){
			distance = decFormat.format(distanceAverage/counterTracks);
        	fuelConsumption = decFormat.format(fuelConsumptionAverage/counterTracks);
		}
		
		model.addAttribute("distance", distance);
		model.addAttribute("fuelconsumption", fuelConsumption);
		
		model.addAttribute("centerCoords", centerCoords);
		model.addAttribute("tracksContent", tracksContent);
		
		model.addAttribute("userContent", userContent);
		
		return "allTracks";
	}
	
	//@RequestMapping(method = RequestMethod.POST, value = "/query")
	@RequestMapping("/query")
    public String getResultByQuery(@RequestParam String query, Model model) throws Exception {
		
		con = new DataConnection();
		
		boolean newQuery = false;
		if(query.equals(""))
		{
			query = con.queryPrefix() + "\n";
			newQuery = true;
		}
		
		String result = "";
		
		if(!newQuery){
			
			List<String[]> list;
			try {
				list = con.query(query);
			}
			catch (Exception e) {
				model.addAttribute("error", "<p class=\"errormsg\">There is an error in the syntax!</p>");
				model.addAttribute("query", query);
				return "query";
			}

			
			String[] heads = list.get(0);
			list.remove(0);
			
			result += "<tr>";
			for (String head : heads) {
				result += "<th>" + head + "</th>";
			}
			result += "</tr>";
			
			
			for (String[] line : list) {
				result += "<tr>";			
				for (String item : line) {
					
					if(item.contains("http://"))
						item = "<a href=\"" + item + "\">" + item + "</a>";
					
					result += "<td>" + item + "</td>";
				}
				result += "</tr>";
			}
			
		}
		
		model.addAttribute("query", query);
		model.addAttribute("result", result);
		

		return "query";
	}
	
	
}
