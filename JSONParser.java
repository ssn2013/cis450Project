import java.io.BufferedReader;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.*;

public class JSONParser {
	public static void main(String[] args){
		try { 
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("Oracle JDBC Driver not present or found");
			e.printStackTrace();
			return;
		}
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:oracle:thin:@//cis550hw1.cfoish237b6z.us-west-2.rds.amazonaws.com:1521/IMDB", "cis550students","cis550hw1");
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
		if (connection != null) {
			Statement stmt = null;
		    String createBusiness = "";
		    String createYelpUsers = "";
		    String createCheckin = "";
		    String createBusinessCategory = "";
		    String createUsers = "";
		    String createElite = "";
		    String createNeighbourhood = "";
		    String createHours = "";	
		    try {
		        stmt = connection.createStatement();
		        stmt.executeQuery(createBusiness);
		        stmt.executeQuery(createYelpUsers);
		        stmt.executeQuery(createUsers);
		        stmt.executeQuery(createCheckin);
		        stmt.executeQuery(createBusinessCategory);
		        stmt.executeQuery(createElite);
		        stmt.executeQuery(createNeighbourhood);
		        stmt.executeQuery(createHours);
		    }catch(Exception e ){
		    	e.printStackTrace();
		    }
		}   
		//business();
		//users();
		//checkin();
		//businessCategory();
		//Elite();
		//Neighbourhood();
		//hours();
	}
	
	public static void Elite(Statement stmt){
		try{
			BufferedReader br = new BufferedReader(new FileReader("/Users/Adi/Documents/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_user.json"));
			String line;
			while ((line = br.readLine()) != null) {
				JSONObject obj = new JSONObject(line);
				String user_id = obj.getString("user_id");
				JSONArray eliteArray = obj.getJSONArray("elite");
				for(int i=0;i<eliteArray.length();i++){
						//query to add (user_id,eliteArray.get(i)) to elite table;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void Neighbourhood(Statement stmt){
		try {
			 BufferedReader br = new BufferedReader(new FileReader("/Users/Adi/Documents/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json"));
			 String line;
			while ((line = br.readLine()) != null) {
			    	JSONObject obj = new JSONObject(line);
			    	String id = obj.getString("business_id");
			    	JSONArray neighbourhood = obj.getJSONArray("neighborhoods");
			    	for(int i=0;i<neighbourhood.length();i++){
			    			// query to add (business_id, neighbourhood.get(i)) to neighbourhood table
			    	}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void hours(Statement stmt){
		try {
			 BufferedReader br = new BufferedReader(new FileReader("/Users/Adi/Documents/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json"));
			 String line;
			 ArrayList<String> days = new ArrayList<String>();
			 days.add("Monday");days.add("Tuesday");days.add("Wednesday");days.add("Thursday");days.add("Friday");days.add("Saturday");days.add("Sunday");
			 while ((line = br.readLine()) != null) {
			    	JSONObject obj = new JSONObject(line);
			    	String id = obj.getString("business_id");
			    	JSONObject hoursObject = obj.getJSONObject("hours");
			    	JSONObject j = null;
			    	for(int i=0;i<days.size();i++){
							try{
								j = hoursObject.getJSONObject(days.get(i));
								//sql query to insert into hours table (id, days.get(i),j.getString("open"),j.getString("close"))
							}catch(Exception e){
								//sql query to insert into hours table (id, days.get(i),0,0)
							}
						
			    	}
			 }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void businessCategory(Statement stmt){
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
	
	public static void checkin(Statement stmt){
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
	
	public static void users(Statement stmt){
		try{
			BufferedReader br = new BufferedReader(new FileReader("/Users/Adi/Documents/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_user.json"));
			String line;
			while ((line = br.readLine()) != null) {
				JSONObject obj = new JSONObject(line);
				String user_id = obj.getString("user_id");
				String name = obj.getString("name");
				int avgStars = obj.getInt("average_stars");
		    	int fans = obj.getInt("fans");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void business(Statement stmt) {
		try {
			 BufferedReader br = new BufferedReader(new FileReader("/Users/Adi/Documents/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json"));
			 String line;
			 ArrayList<String> days = new ArrayList<String>();
			 days.add("Monday");days.add("Tuesday");days.add("Wednesday");days.add("Thursday");days.add("Friday");days.add("Saturday");days.add("Sunday");
			 while ((line = br.readLine()) != null) {
			    	JSONObject obj = new JSONObject(line);
			    	String id = obj.getString("business_id");
			    	String name = obj.getString("name");
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
			    	Double latitude = (Double)obj.get("latitude");
			    	Double longitude = (Double)obj.get("longitude");	
			 }   			
		}catch(Exception e){
			e.printStackTrace();
		}
		   
	}

}
