package com.datformers.servlet.fbresources;

import org.json.JSONException;
import org.json.JSONObject;

import com.restfb.types.Location;
import com.restfb.types.Post;

public class FbPost {
/*
 * locName varchar(100),
city varchar(100),
country varchar(100)
latitude float,
longitude float,
state varchar(50),
 */
	public String locationName;
	public String city;
	public String country;
	public String state;
	public double latitude;
	public double longitude;
	public String postId;
	
	public FbPost() {
		
	}
	public FbPost(Post post) {
		postId = post.getId();
		locationName = post.getPlace().getName();
		Location loc = post.getPlace().getLocation();
		city  = loc.getCity();
		state = loc.getState();
		country = loc.getCountry();
		latitude = loc.getLatitude();
		longitude = loc.getLongitude();
	}
	
	@Override
	public String toString() {
		return "POST: LocationName: "+locationName+" City: "+city+" State: "+state+" Country: "+country+" Lat: "+latitude+" Long: "+longitude;
	}
	public JSONObject toJSON() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("locationName", locationName);
		object.put("city",city);
		object.put("country",country);
		object.put("state",state);
		object.put("latitude",latitude);
		object.put("longitude",longitude);
		return object;
	}
}
