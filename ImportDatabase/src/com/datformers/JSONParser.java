package com.datformers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.*;

public class JSONParser {
	public static void main(String[] args){
		//business();
		//users();
		//checkin();
		businessCategory();
	}
	
	public static void businessCategory(){
		try{
			BufferedReader br = new BufferedReader(new FileReader("/Users/Adi/Documents/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json"));
			String line;
			while ((line = br.readLine()) != null) {
		    	JSONObject obj = new JSONObject(line);
		    	String businessID = obj.getString("business_id");
		    	JSONArray categoryArray = obj.getJSONArray("categories");
		    	String category = "";
		    	for(int i=0;i<categoryArray.length();i++){
		    			category = categoryArray.getString(i);
		    			// WRITE SQL QUERY TO ADD (businessID,category) into table
		    	}
		    	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void checkin(){
		try{
			BufferedReader br = new BufferedReader(new FileReader("/Users/Adi/Documents/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_checkin.json"));
			String line;
			while ((line = br.readLine()) != null) {
				JSONObject obj = new JSONObject(line);
				String businessId = obj.getString("business_id");
				JSONObject checkin = obj.getJSONObject("checkin_info");
				int checkinCount = 0;
				for(int i = 0; i<checkin.names().length(); i++){
				    checkinCount += checkin.getInt(checkin.names().getString(i));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void users(){
		try{
			BufferedReader br = new BufferedReader(new FileReader("/Users/Adi/Documents/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_user.json"));
			String line;
			while ((line = br.readLine()) != null) {
				JSONObject obj = new JSONObject(line);
				String user_id = obj.getString("user_id");
				String name = obj.getString("name");
				int avgStars = obj.getInt("average_stars");
				JSONArray eliteArray = obj.getJSONArray("elite");
		    	String elite = "";
		    	for(int i=0;i<eliteArray.length();i++){
		    		if(i == eliteArray.length()-1)
		    			elite+=eliteArray.get(i);
		    		else
		    			elite+=eliteArray.get(i) + ",";
		    	}
		    	int fans = obj.getInt("fans");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void business() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("/Users/Adi/Documents/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json"));
			 String line;
			 ArrayList<String> days = new ArrayList<String>();
			 days.add("Monday");days.add("Tuesday");days.add("Wednesday");days.add("Thursday");days.add("Friday");days.add("Saturday");days.add("Sunday");
			    while ((line = br.readLine()) != null) {
			    	JSONObject obj = new JSONObject(line);
			    	String id = obj.getString("business_id");
			    	String name = obj.getString("name");
			    	JSONArray neighbourhood = obj.getJSONArray("neighborhoods");
			    	String n = "";
			    	for(int i=0;i<neighbourhood.length();i++){
			    		if(i == neighbourhood.length()-1)
			    			n+=neighbourhood.get(i);
			    		else
			    			n+=neighbourhood.get(i) + ",";
			    	}
			    	String fullAddress = obj.getString("full_address");
			    	String city = obj.getString("city");
			    	boolean openBoolean = obj.getBoolean("open");
			    	String open = "";
			    	if(openBoolean)
			    		open = "true";
			    	else
			    		open = "false";
			    	String state = obj.getString("state");
			    	int stars = obj.getInt("stars");
			    	int latitude = obj.getInt("latitude");
			    	int longitude = obj.getInt("longitude");
			    	JSONObject hoursObject = obj.getJSONObject("hours");
			    	String hours = "";
			    	JSONObject j = null;
			    	for(int i=0;i<days.size();i++){
						if(i == days.size() - 1){
							try{
								j = hoursObject.getJSONObject(days.get(i));
								hours += j.getString("open") + ",";
								hours += j.getString("close");
							}catch(Exception e){
								hours += "0,";
								hours += "0";
							}
						}
						else{
							try{
								j = hoursObject.getJSONObject(days.get(i));
								hours += j.getString("open") + ",";
								hours += j.getString("close") + ",";
							}catch(Exception e){
								hours += "0,";
								hours += "0,";
							}
						}
			    	}
			    	
			    }
		}catch(Exception e){
			e.printStackTrace();
		}
		   
		

	}

}
