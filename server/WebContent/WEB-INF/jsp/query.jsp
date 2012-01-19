<h1>SPARQL Query</h1>

<script type="text/javascript">

	var prefix = 
	    'PREFIX dc:<http://purl.org/dc/elements/1.1/> \n' + 
	    'PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> \n' + 
	    'PREFIX geo:<http://www.w3.org/2003/01/geo/wgs84_pos#> \n' + 
	    'PREFIX foaf:<http://xmlns.com/foaf/0.1/> \n' + 
	    'PREFIX vehicle:<http://tracker.com/vehicle/0.1#> \n' + 
	    'PREFIX track:<http://tracker.com/tracker/0.1#> \n' + 
	    'PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> \n' + 
	    'PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n' + 
	    'PREFIX user:<http://tracker.com/user/0.1#> \n\n';   

	function example1() {
		var value = 
			"SELECT ?id ?name ?homepage ?user WHERE { \n" +
			"?user rdf:type foaf:Person . \n" +
			"?user foaf:name ?name . \n" +
			"?user foaf:homepage ?homepage . \n" +
			"?user user:privacy \"0\" . \n" +
			"?user user:id ?id \n" +
			"} ";
	 
		document.getElementById('queryBox').innerHTML = prefix + value;
	}
	  
	function example2() {
		var value = 
			"SELECT ?geolat ?geolong ?trackdate WHERE { \n" + 
			"<http://tracker.com/track/18> track:Trackpoints ?trackpoints . \n" + 
			"<http://tracker.com/user/thomasmuster> user:privacy \"0\" . \n" + 
			"?trackpoints ?type ?trackpointsobject . \n" + 
			"?trackpointsobject rdf:type geo:Point . \n" + 
			"?trackpointsobject geo:lat ?geolat . \n" + 
			"?trackpointsobject geo:long ?geolong . \n" + 
			"?trackpointsobject dc:date ?trackdate } ";
	 
		document.getElementById('queryBox').innerHTML = prefix + value;
	}
		    
		    
	function example3() {
		var value = 
		    'SELECT ?trackid ?vehicleid ?vehiclename ?vehiclefuel WHERE { \n' +
		    '<http://tracker.com/user/maxmuster> track:hasTrack ?trackid . \n' +
		    '<http://tracker.com/user/maxmuster> user:privacy "0" . \n' +
		    '?trackid vehicle:hasVehicle ?vehicleid . \n' +
		    '?vehicleid vehicle:name ?vehiclename . \n' +
		    '?vehicleid vehicle:fuelConsumtion ?vehiclefuel . } \n';	 
		 
		document.getElementById('queryBox').innerHTML = prefix + value;
	}
</script>


<input class="button" type="button" name="example1" value="Example 1" onclick="javascript:example1();" />
<input class="button" type="button" name="example2" value="Example 2" onclick="javascript:example2();" />
<input class="button" type="button" name="example3" value="Example 3" onclick="javascript:example3();" />


<form method="POST" action="<%=request.getContextPath()%>/tool/query">
<table style="width: 100%">
<tr><td><textarea id="queryBox" name="query" style="width: 100%; height: 300px;">${query}</textarea></td></tr>
<tr><td align="right"><input class="button" type="submit" name="submit" value="Query"/></td></tr>
</table>
</form>

<hr />

<table id="query">${result}</table>
${error}
