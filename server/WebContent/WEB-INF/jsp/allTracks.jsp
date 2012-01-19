<h1>Tracks Detail View</h1>

<script type="text/javascript">
	function initializeMap() {
	
		var myOptions = {zoom: 12, center: new google.maps.LatLng(${centerCoords}), mapTypeId: google.maps.MapTypeId.ROADMAP };
		var map = new google.maps.Map(document.getElementById("mapContent"), myOptions);
		${tracksContent}
	}
</script>

<div id="user_color_list">${userContent}</div>

<div id="track_detail">
	<ul>
	<li><b>Average Distance: </b>${distance}km</li>
	<li><b>Average Fuel Consumption: </b>${fuelconsumption}l</li>
	</ul>
</div>

${link_back}

<div id="mapContent"></div>

<script type="text/javascript">
	initializeMap();
</script>