package st.energy2live.rdf;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFFormat;

import st.energy2live.data.track.TrackLog;
import st.energy2live.data.track.TrackPoint;
import st.energy2live.data.user.User;
import st.energy2live.data.vehicle.Vehicle;
import st.energy2live.data.vehicle.VehicleA;

public class DataConnection {
	
	private String[] prefixes = {"dc@http://purl.org/dc/elements/1.1/", 
								"rdfs@http://www.w3.org/2000/01/rdf-schema#",
								"geo@http://www.w3.org/2003/01/geo/wgs84_pos#",
								"foaf@http://xmlns.com/foaf/0.1/",
								"vehicle@http://tracker.com/vehicle/0.1#",
								"track@http://tracker.com/tracker/0.1#",
								"xsd@http://www.w3.org/2001/XMLSchema#",
								"rdf@http://www.w3.org/1999/02/22-rdf-syntax-ns#",
								"user@http://tracker.com/user/0.1#"};
									
	private String resourceBase = "http://tracker.com/";
    private String sesameServer = "http://localhost:8080/openrdf-sesame";
    private String repositoryID = "1";
    private Repository repository;
    private RepositoryConnection connection;
    
    public DataConnection() throws Exception{
    	this.repository = new HTTPRepository(sesameServer, repositoryID);
    	this.repository.initialize();
    	this.connection = this.repository.getConnection();
    }
	
    public String queryPrefix(){
      String prefixes = "";
    	
  	  for (String prefix : this.prefixes) {
  		prefixes += "PREFIX " + prefix.replace("@", ":<") + "> \n";
  	  }
    	
      return prefixes;
    }
    
    private String xmlPrefix(){
    String prefixes = "";
	String[] values;
	  for (String prefix : this.prefixes) {
		values = prefix.split("@");
		prefixes += " xmlns:" + values[0] + "=\"" + values[1] + "\" ";
	  }
  	
    return prefixes;
    }
	
	public int requestNewUserID() throws Exception {

	  String queryString = queryPrefix() + " SELECT ?id WHERE { ?x rdf:type user:currentID .?x user:value ?id }";
	  TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
	  TupleQueryResult result = tupleQuery.evaluate();
	  
	  result.hasNext();
	  BindingSet bindingSet = result.next();
	  Value id = bindingSet.getValue("id");
	  
	  queryString = queryPrefix() + "DELETE DATA { <" + resourceBase + "user/id> user:value \"" + id.stringValue() + "\" };" +
	  						   		"INSERT DATA { <" + resourceBase + "user/id> user:value \"" + (Integer.parseInt(id.stringValue()) + 1) + "\" }";
	  
	  Update update = this.connection.prepareUpdate(QueryLanguage.SPARQL, queryString);
	  update.execute();
	  
	  return Integer.parseInt(id.stringValue());
	}
	
	public int requestNewVehicleID() throws Exception {
	  String queryString = queryPrefix() + " SELECT ?id WHERE { ?x rdf:type vehicle:Vehicle . ?x vehicle:id ?id }";
	  TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
	  TupleQueryResult result = tupleQuery.evaluate();
	  int currentID = 0;
	  while(result.hasNext()){
		  BindingSet set = result.next();
		  String id = set.getValue("id").toString().replace("\"","").trim();
		  if(Integer.parseInt(id) > currentID)
			  currentID = Integer.parseInt(id);
	  }
	  currentID ++;
	  
	  return currentID;
	}
	
	public int requestNewTrackID() throws Exception {
		
	  String queryString = queryPrefix() + "SELECT ?id WHERE { ?x rdf:type track:currentID .?x track:value ?id }";
	  TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
	  TupleQueryResult result = tupleQuery.evaluate();
	  
	  result.hasNext();
	  BindingSet bindingSet = result.next();
	  Value id = bindingSet.getValue("id");
	  
	  queryString = queryPrefix() + "DELETE DATA { <" + resourceBase + "track/id> track:value \"" + id.stringValue() + "\" };" +
	  						   		"INSERT DATA { <" + resourceBase + "track/id> track:value \"" + (Integer.parseInt(id.stringValue()) + 1) + "\" }";
	  
	  Update update = this.connection.prepareUpdate(QueryLanguage.SPARQL, queryString);
	  update.execute();
	    
	  return Integer.parseInt(id.stringValue());
	}
	
	public void insertNewTrackLog(TrackLog log, User user) throws Exception{
		
		String query = "<rdf:RDF " + xmlPrefix() + " >\n";
		if(log == null || user == null)
			return;
		
		int id = requestNewTrackID();
		
		if(id == 0)
			return;
		
		query += "<track:Tracklog rdf:about=\"" + resourceBase + "track/" + id + "\">\n";
		query += "<vehicle:hasVehicle rdf:resource=\"" + resourceBase + "vehicle/" + log.getVehicle().getId() +"\" />\n";
		query += "<track:Trackpoints>\n";
		query += "<rdf:Seq>\n";
		
		for (TrackPoint point : log.getTrackPoints()) {
			query += "<rdf:li>\n";
			query += "<geo:Point geo:lat='" + point.getPoint().getLatitude() +"' geo:long='" + point.getPoint().getLongitude() + "'>\n";
			query += "<dc:date rdf:datatype='xsd:dateTime'>" + point.getDateTime() +"</dc:date>\n";
			query += "</geo:Point>\n";
			query += "</rdf:li>\n";
		}
		
		query += "</rdf:Seq>\n";		
		query += "</track:Trackpoints>\n";
		query += "</track:Tracklog>\n";
		
		query += "<foaf:Person rdf:about=\"" + resourceBase + "user/" + user.getNickName() +"\">\n";
		query += "<track:hasTrack rdf:resource=\"" + resourceBase + "track/" + id +"\" />\n";
		query += "</foaf:Person>\n";
		
		query += "</rdf:RDF>\n";
		
		//System.out.println(query);
		
		this.connection.add(new StringReader(query), resourceBase, RDFFormat.RDFXML);
	}
	
	public boolean insertNewUser(User user) throws Exception {
	
		String query = "<rdf:RDF " + xmlPrefix() + " >";
		if(user == null)
			return false;
		
		int id = 0;
		id = requestNewUserID();
		
		if(id == 0)
			return false;
		
		if(userExists(user.getNickName().toLowerCase()))
			return false;

		query += "<foaf:Person rdf:about=\"" + resourceBase + "user/" + user.getNickName().toLowerCase() + "\">";
		query += "<foaf:name>" + user.getFullName() + "</foaf:name>";
		query += "<foaf:mbox rdf:resource=\"mailto:" + user.getEmail() + "\"/>";
		query += "<foaf:homepage rdf:resource=\"" + user.getHomePage() + "\"/>";;
		query += "<user:privacy>" + user.getPrivacy() + "</user:privacy>";
		query += "<user:password>" + user.getPassword() + "</user:password>";
		query += "<user:id>" + id + "</user:id>";
		query += "</foaf:Person>";
		query += "</rdf:RDF>";
		
		System.out.println(query);
		
		this.connection.add(new StringReader(query), "", RDFFormat.RDFXML);
		
		return true;
	}
	
	public boolean insertNewVehicle(Vehicle vehicle) throws Exception {
		
		String query = "<rdf:RDF " + xmlPrefix() + " >";
		if(vehicle == null)
			return false;
		
		int id = 0;
		id = requestNewVehicleID();
		
		if(id == 0)
			return false;
		
		vehicle.setId(id);
		
		query += "<vehicle:Vehicle rdf:about=\"" + resourceBase + "vehicle/" + id + "\">";
		query += "<vehicle:name>" + vehicle.getName() + "</vehicle:name>";
		query += "<vehicle:fuelConsumtion>" + vehicle.getFuelConsumtion() + "</vehicle:fuelConsumtion>";
		query += "<vehicle:manufacturer>" + vehicle.getManuFacturer() + "</vehicle:manufacturer>";
		query += "<vehicle:id>" + id + "</vehicle:id>";
		query += "</vehicle:Vehicle>";
		query += "</rdf:RDF>";
		
		System.out.println(query);
		
		this.connection.add(new StringReader(query), "", RDFFormat.RDFXML);
		
		return true;
	}
	
	public boolean userExists(String nickname) throws Exception { 
		return userExists(new User(nickname, null, null, null, null, 0));
	}
	
	
	public boolean userExists(User user) throws Exception {
		
	  String queryString = queryPrefix() + " SELECT ?type WHERE { <" + resourceBase + "user/" + user.getNickName() + "> rdf:type  ?type }";
	  TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
	  TupleQueryResult result = tupleQuery.evaluate();
	  
	  if(result.hasNext())
		  return true;

	  return false;
	}
	
	public boolean login(String user, String password) throws Exception {
		
	  String queryString = queryPrefix() + " SELECT ?type WHERE {" + 
	  									   "<" + resourceBase + "user/" + user.toLowerCase() + "> rdf:type ?type." +
	  									   "<" + resourceBase + "user/" + user.toLowerCase() + "> user:password \"" + password + "\" }";
	  
	  TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
	  TupleQueryResult result = tupleQuery.evaluate();
	  
	  if(result.hasNext())
		  return true;

	  return false;
	}
	
	public User getUserByID(String username) throws Exception {
		String queryString = queryPrefix() + 
				"SELECT ?id ?name ?mbox ?homepage WHERE {" +
				"<" + resourceBase + "user/" + username + "> foaf:name ?name . " +
				"<" + resourceBase + "user/" + username + "> foaf:mbox ?mbox . " +
				"<" + resourceBase + "user/" + username + "> foaf:homepage ?homepage ." +
				"<" + resourceBase + "user/" + username + "> user:privacy \"0\" . " +
				"<" + resourceBase + "user/" + username + "> user:id ?id }";
		
		TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = tupleQuery.evaluate();
		
		result.hasNext();
		BindingSet set = result.next();
		
		return new User(username, "", set.getValue("name").toString().replace("\"",""), set.getValue("mbox").toString(), set.getValue("homepage").toString(), 0);
	}
	
	public List<User> getUsers() throws Exception {
		List<User> userList = new ArrayList<User>();
		
		String queryString = queryPrefix() + 
				"SELECT ?id ?name ?mbox ?homepage ?user WHERE {" + 
				"?user rdf:type foaf:Person . " +
				"?user foaf:name ?name . " +
				"?user foaf:mbox ?mbox . " +
				"?user foaf:homepage ?homepage ." +
				"?user user:privacy \"0\" . " +
				"?user user:id ?id }";
			
		TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = tupleQuery.evaluate();
		
		while(result.hasNext()){
			BindingSet set = result.next();
			String tmpString = set.getValue("user").toString();
			String nickname = tmpString.substring((tmpString.lastIndexOf("/") + 1), tmpString.length());
			
			userList.add(new User(nickname, "", set.getValue("name").toString().replace("\"",""), set.getValue("mbox").toString(), set.getValue("homepage").toString(), 0));
		}
		return userList;
	}
	
	
	public List<TrackLog> getUserTracks(String username) throws Exception{
		List<TrackLog> trackList = new ArrayList<TrackLog>();
		
		String queryString = queryPrefix() + 
				"SELECT ?trackid ?vehicleid ?vehiclename ?vehiclefuel ?vehiclemanu WHERE {" + 
				"<http://tracker.com/user/" + username + "> track:hasTrack ?trackid . " + 
				"<http://tracker.com/user/" + username + "> user:privacy \"0\" . " + 
				"?trackid vehicle:hasVehicle ?vehicleid . " +
				"?vehicleid vehicle:name ?vehiclename . " +
				"?vehicleid vehicle:fuelConsumtion ?vehiclefuel . " +
				"?vehicleid vehicle:manufacturer ?vehiclemanu . " +
				"}";
		
		TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = tupleQuery.evaluate();
		
		while(result.hasNext()){
			BindingSet set = result.next();
			
			String fuel = set.getValue("vehiclefuel").toString().replace("\"","");
			VehicleA vehicle = new VehicleA(set.getValue("vehiclename").toString().replace("\"",""), set.getValue("vehiclemanu").toString().replace("\"",""), Double.parseDouble(fuel));
			TrackLog log = new TrackLog(vehicle);
			log.setID(set.getValue("trackid").toString());
			
			trackList.add(log);
		}
		
		return trackList;
	}
	
	public User getUserByTrackID(String trackID) throws Exception{
		
		String queryString = queryPrefix() + 
				"SELECT ?id ?name ?mbox ?homepage ?user WHERE {" + 
				"?user track:hasTrack <" + trackID + "> . " + 
				"?user foaf:name ?name . " +
				"?user foaf:mbox ?mbox . " +
				"?user foaf:homepage ?homepage ." +
				"?user user:privacy \"0\" . " +
				"?user user:id ?id }";

		TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = tupleQuery.evaluate();
		
		result.hasNext();
		BindingSet set = result.next();
		
		String tmpString = set.getValue("user").toString();
		String nickname = tmpString.substring((tmpString.lastIndexOf("/") + 1), tmpString.length());
		
		return new User(nickname, "", set.getValue("name").toString().replace("\"",""), set.getValue("mbox").toString(), set.getValue("homepage").toString(), 0);
	}
	
	public List<TrackLog> getTrackList() throws Exception{
		List<TrackLog> trackList = new ArrayList<TrackLog>();
		
		String queryString = queryPrefix() + 
				"SELECT ?trackid ?vehicleid ?vehiclename ?vehiclefuel ?vehiclemanu WHERE {" + 
				"?trackid rdf:type track:Tracklog . " +
				"?trackid vehicle:hasVehicle ?vehicleid . " +
				"?vehicleid vehicle:name ?vehiclename . " +
				"?vehicleid vehicle:fuelConsumtion ?vehiclefuel . " +
				"?vehicleid vehicle:manufacturer ?vehiclemanu . " +
				"}";
		
		TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = tupleQuery.evaluate();
		
		while(result.hasNext()){
			BindingSet set = result.next();
			
			String fuel = set.getValue("vehiclefuel").toString().replace("\"","");
			VehicleA vehicle = new VehicleA(set.getValue("vehiclename").toString().replace("\"",""), set.getValue("vehiclemanu").toString().replace("\"",""), Double.parseDouble(fuel));
			TrackLog log = new TrackLog(vehicle);
			log.setID(set.getValue("trackid").toString());
			
			trackList.add(log);
		}
		
		return trackList;
	}
	
	public TrackLog getUserTrackByID(String username, String trackID) throws Exception{
		String currentTrack = resourceBase + "track/" + trackID;
		String currentUsername = resourceBase + "user/" + username;
		
		String queryString = queryPrefix() + 
				"SELECT ?vehiclename ?vehiclefuel ?vehiclemanu WHERE {" + 
				"<" + currentUsername + "> user:privacy \"0\" . " + 
				"<" + currentTrack + "> vehicle:hasVehicle ?vehicle . " +
				"?vehicle vehicle:name ?vehiclename . " +
				"?vehicle vehicle:fuelConsumtion ?vehiclefuel . " +
				"?vehicle vehicle:manufacturer ?vehiclemanu }"; 
		
		TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		TupleQueryResult result = tupleQuery.evaluate();
		
		result.hasNext();
		BindingSet set = result.next();
		String fuel = set.getValue("vehiclefuel").toString().replace("\"","");
		VehicleA vehicle = new VehicleA(set.getValue("vehiclename").toString().replace("\"",""), set.getValue("vehiclemanu").toString().replace("\"",""), Double.parseDouble(fuel));
		TrackLog log = new TrackLog(vehicle);
		
		queryString = queryPrefix() + 
				"SELECT ?geolat ?geolong ?trackdate WHERE {" + 
				"<" + currentTrack + "> track:Trackpoints ?trackpoints . " + 
				"<" + currentUsername + "> user:privacy \"0\" . " + 
				"?trackpoints ?type ?trackpointsobject . " + 
				"?trackpointsobject rdf:type geo:Point . " + 
				"?trackpointsobject geo:lat ?geolat . " + 
				"?trackpointsobject geo:long ?geolong . " + 
				"?trackpointsobject dc:date ?trackdate }"; 
		
		tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		result = tupleQuery.evaluate();
		
		while(result.hasNext()){
			set = result.next();
			String latitude = set.getValue("geolat").toString().replace("\"","");
			String longitude = set.getValue("geolong").toString().replace("\"","");
			String datetime = set.getValue("trackdate").toString();
			datetime = datetime.substring(datetime.indexOf("\"") + 1, datetime.lastIndexOf("\""));
			
			SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
			Date date = format.parse(datetime);
			log.addTrackPoint(date, Double.parseDouble(latitude), Double.parseDouble(longitude));
		}
		return log;
	}
	
	
	public List<TrackLog> getAllUserTracks(String username) throws Exception{
		String currentUsername = resourceBase + "user/" + username;
		
		List<TrackLog> logList = getUserTracks(username);
		
		
		for(TrackLog log : logList){
			String queryString = queryPrefix() + 
					"SELECT ?geolat ?geolong ?trackdate WHERE {" + 
					"<" + log.getID() + "> track:Trackpoints ?trackpoints . " + 
					"<" + currentUsername + "> user:privacy \"0\" . " + 
					"?trackpoints ?type ?trackpointsobject . " + 
					"?trackpointsobject rdf:type geo:Point . " + 
					"?trackpointsobject geo:lat ?geolat . " + 
					"?trackpointsobject geo:long ?geolong . " + 
					"?trackpointsobject dc:date ?trackdate }";
			
			TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			TupleQueryResult result = tupleQuery.evaluate();
			
			
			while(result.hasNext()){
				BindingSet set = result.next();
				String latitude = set.getValue("geolat").toString().replace("\"","");
				String longitude = set.getValue("geolong").toString().replace("\"","");
				String datetime = set.getValue("trackdate").toString();
				datetime = datetime.substring(datetime.indexOf("\"") + 1, datetime.lastIndexOf("\""));
				
				SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
				Date date = format.parse(datetime);
				
				log.addTrackPoint(date, Double.parseDouble(latitude), Double.parseDouble(longitude));
			}
		}
		
		return logList;
	}
	
	
	public List<String[]> query(String query) throws Exception {
		
		System.err.println("Query: " + query);
		TupleQuery tupleQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
		TupleQueryResult result = tupleQuery.evaluate();
		
		BindingSet set;
		List<String> names = result.getBindingNames();		
		List<String[]> list = new ArrayList<String[]>();
		list.add((String[]) names.toArray(new String[names.size()]));
		
		String[] buffer;
		Value value;
		while(result.hasNext()){
			set = result.next();	
			
			buffer = new String[names.size()];
			int index = 0;
			for (String binding : names) {
				value = set.getValue(binding);
				
				if(value != null)
					buffer[index++]  = value.toString();
				else
					buffer[index++]  = "";	
			}	
			
			list.add(buffer);
		}
			
		return list;
	}
	

}
