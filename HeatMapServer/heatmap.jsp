<%@ page import="java.io.*,java.util.*,java.sql.*"%>
<%@ page import="javax.servlet.http.*,javax.servlet.*" %>
<%@ page import="com.datformers.database.OracleDBWrapper,com.datformers.utils.DatabaseUtil" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>

<html>
<head>
</head>
 <style>
      html, body, #map-canvas {
        height: 100%;
        margin: 0px;
        padding: 0px
      }
</style>
<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true&libraries=visualization"></script>
<script>
var map, pointarray, heatmap;

var taxiData = [
  new google.maps.LatLng(37.782551, -122.445368),
  new google.maps.LatLng(37.782745, -122.444586),
  new google.maps.LatLng(37.782842, -122.443688),
  new google.maps.LatLng(37.782919, -122.442815),
  new google.maps.LatLng(37.782992, -122.442112),
  new google.maps.LatLng(37.783100, -122.441461),
  new google.maps.LatLng(37.783206, -122.440829),
  new google.maps.LatLng(37.783273, -122.440324),
  new google.maps.LatLng(37.783316, -122.440023),
  new google.maps.LatLng(37.783357, -122.439794),
  new google.maps.LatLng(37.783371, -122.439687),
  new google.maps.LatLng(37.783368, -122.439666),
  new google.maps.LatLng(37.783383, -122.439594),
  new google.maps.LatLng(37.783508, -122.439525),
  new google.maps.LatLng(37.783842, -122.439591),
  new google.maps.LatLng(37.784147, -122.439668),
  new google.maps.LatLng(37.784206, -122.439686),
  new google.maps.LatLng(37.784386, -122.439790),
  new google.maps.LatLng(37.784701, -122.439902),
  new google.maps.LatLng(37.784965, -122.439938),
  new google.maps.LatLng(37.785010, -122.439947),
  new google.maps.LatLng(37.785360, -122.439952),
  new google.maps.LatLng(37.785715, -122.440030),
  new google.maps.LatLng(37.786117, -122.440119),
  new google.maps.LatLng(37.786564, -122.440209),
  new google.maps.LatLng(37.786905, -122.440270),
  new google.maps.LatLng(37.786956, -122.440279),
  new google.maps.LatLng(37.800224, -122.433520),
  new google.maps.LatLng(37.800155, -122.434101),
  new google.maps.LatLng(37.800160, -122.434430),
  new google.maps.LatLng(37.800378, -122.434527),
  new google.maps.LatLng(37.751266, -122.403355)
];
function testAjax()
{
var xmlhttp;
xmlhttp=new XMLHttpRequest();
xmlhttp.onreadystatechange=function()
{
if (xmlhttp.readyState==4 && xmlhttp.status==200)
{
	document.getElementById("ajaxResponse").innerHTML=xmlhttp.responseText;
	alert("Got response from server");
}
}
xmlhttp.open("GET","heatmap",true);
xmlhttp.send();
}

var pointArray
function initialize() {
  var mapOptions = {
    zoom: 13,
    center: new google.maps.LatLng(37.774546, -122.433523),
    mapTypeId: google.maps.MapTypeId.ROADMAP
  };

  map = new google.maps.Map(document.getElementById('map-canvas'),
      mapOptions);

  pointArray = new google.maps.MVCArray(taxiData);

  heatmap = new google.maps.visualization.HeatmapLayer({
    data: pointArray
  });

  heatmap.setMap(map);
}

function reloadMap() {
var xmlhttp;
var cityVal = document.getElementById("citySelect").value;
var categoryVal = document.getElementById("categorySelect").value;
xmlhttp=new XMLHttpRequest();
url="heatmap/points1?city="+cityVal+"&category="+categoryVal;
alert("Calling url: "+url);
xmlhttp.onreadystatechange=function()
{
if (xmlhttp.readyState==4 && xmlhttp.status==200)
{
	//document.getElementById("ajaxResponse").innerHTML=xmlhttp.responseText;
	var responseObj = JSON.parse(xmlhttp.responseText); 
	alert("Got response from server for Change Map for city: "+responseObj.city+" and category: "+responseObj.category);
	var pointsArray = responseObj.points;
	var newMapPointsArray = new Array(pointsArray.length);
	for (i = 0; i < pointsArray.length; i++) { 
    	//alert("Points: "+pointsArray[i].latitude+" "+pointsArray[i].longitude);
    	newMapPointsArray[i] = new google.maps.LatLng(pointsArray[i].latitude, pointsArray[i].longitude);
	}
	pointArray = new google.maps.MVCArray(newMapPointsArray); 
}
}
xmlhttp.open("GET",url,true);
xmlhttp.send();
}
google.maps.event.addDomListener(window, 'load', initialize);
</script>
<body>

<!--This is a comment. Comments are not displayed in the browser-->
<% 
OracleDBWrapper dbWrapper = new OracleDBWrapper(DatabaseUtil.getURL("158.130.106.114"), DatabaseUtil.UERNAME,DatabaseUtil.PASSWORD);	
String queryCity = "select distinct city from business order by city";
String queryCategory = "select distinct category from categories order by category";
java.sql.ResultSet rsCity = dbWrapper.executeQuery(queryCity);
java.sql.ResultSet rsCategory = dbWrapper.executeQuery(queryCategory); 
try{
%>
<div id="ajaxResponse">Content to be changed</div>
<div>
<table><tr><td>City:</td><td><select id="citySelect">
<% while(rsCity.next()){ %>
<option value="<%= rsCity.getString("city") %>"><%= rsCity.getString("city") %></option>
<% } %>
</select></td></tr>
<tr><td>Category:</td><td><select id="categorySelect">
<%
while(rsCategory.next()){
%>
<option value="<%= rsCategory.getString("category") %>"><%= rsCategory.getString("category") %></option>
<%
}
%>
</select></td>
<%
}
catch(Exception e){e.printStackTrace();}
finally{
if(rsCity!=null) rsCity.close();
if(rsCategory!=null) rsCategory.close();
dbWrapper.closeConnection();
}
%>
</tr>
<tr><td colspan="2"><button type="button" onclick="testAjax()">Change Content</button>
</td></tr>
<tr><td colspan="2"><button type="button" onclick="reloadMap()">Reload Map</button>
</td></tr>
</table>
<br/>
<%
out.println("Your IP address is " + request.getRemoteAddr());
%>
</div>
<div id="map-canvas">
</div>
</body>
</html>