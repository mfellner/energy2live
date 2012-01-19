<h1>Track Detail View</h1>

<script type="text/javascript">
	function initializeMap() {
	
		var myOptions = {zoom: 15, center: new google.maps.LatLng(${startCoords}), mapTypeId: google.maps.MapTypeId.ROADMAP };
		var map = new google.maps.Map(document.getElementById("mapContent"), myOptions);
		
		var markerStart = new google.maps.Marker({ position: new google.maps.LatLng(${startCoords}), map: map });
		var markerEnd = new google.maps.Marker({ position: new google.maps.LatLng(${endCoords}), map: map });
		
		 var pointArray = [${pointArrayInit}];

		
		var trackPath = new google.maps.Polyline({path: pointArray, strokeColor: "#FF0000", strokeOpacity: 1.0, strokeWeight: 2 });
		trackPath.setMap(map);
	}
</script>

<div id="track_detail">
	<h3>${username}</h3>
	<ul>
	<li><b>Track: </b>${trackid}</li>
	<li><b>Vehicle: </b>${vehiclename} (${vehiclemanu})</li>
	<li><b>Distance: </b>${distance}km</li>
	<li><b>Fuel Consumption: </b>${fuelconsumption}l</li>
	</ul>
</div>

${link_back}

<div id="mapContent"></div>

<script type="text/javascript">
	initializeMap();
</script>

