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
			connection = DriverManager.getConnection("jdbc:oracle:thin:@//158.130.111.68:1521/mydb.localhost", "system","Verna2813");
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
		if (connection != null) {
			Statement stmt = null;
		    String createBusiness = "Create table Business("
		    		+ "bid varchar(100),"
		    		+ "name varchar(100),"
		    		+ "full_address varchar(1000),"
		    		+ "city varchar(25),"
		    		+ "open varchar(10),"
		    		+ "state varchar(100),"
		    		+ "stars integer,"
		    		+ "latitude float,"
		    		+ "longitude float,"
		    		+ "PRIMARY KEY(bid))";
		    
		    String createYelpUsers = "create table YelpUsers("
		    		+ "user_id varchar(100),"
		    		+ "name varchar(100),"
		    		+ "avg_stars integer,"
		    		+ "fans integer,"
		    		+ "PRIMARY KEY(user_id))";
		    
		    String createCheckin = "create table CheckIn("
		    		+ "bid varchar(100) PRIMARY KEY,"
		    		+ "check_in_info integer,"
		    		+ "FOREIGN KEY (bid) REFERENCES Business(bid) ON DELETE CASCADE)";
		    
		    String createBusinessCategory = "Create table Categories("
		    		+ "bid varchar(100),"
		    		+ "category varchar(100),"
		    		+ "primary key(bid, category),"
		    		+ "FOREIGN KEY (bid) REFERENCES Business(bid)  ON DELETE CASCADE)";
		    
		    String createUsers = "Create table AppUsers("
		    		+ "user_id varchar(100),"
		    		+ "password varchar(100),"
		    		+ "email varchar(100),"
		    		+ "is_facebook_login char(1),"
		    		+ "PRIMARY KEY(user_id))";
		    
		    String createElite = "create table Elite("
		    		+ "user_id varchar(100),"
		    		+ "year integer,"
		    		+ "primary key(user_id, year),"
		    		+ "foreign key (user_id) references YelpUsers(user_id)  ON DELETE CASCADE)";
		    
		    String createNeighbourhood = "create table Neighborhood("
		    		+ "bid varchar(100),"
		    		+ "area varchar(100),"
		    		+ "primary key(bid, area), "
		    		+ "foreign key (bid) references Business(bid)  ON DELETE CASCADE)";
		    
		    String createHours = "create table BusinessHours ("
		    		+ "bid varchar(100),"
		    		+ "day varchar(20),"
		    		+ "open_time varchar(10),"
		    		+ "close_time varchar(10),"
		    		+ "primary key(bid, day),"
		    		+ "foreign key (bid) references Business(bid)  ON DELETE CASCADE)";	
		    
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
		        
		        business(stmt);
				users(stmt);
				checkin(stmt);
				businessCategory(stmt);
				Elite(stmt);
				Neighbourhood(stmt);
				hours(stmt);
				
		    }catch(Exception e ){
		    	e.printStackTrace();
		    }
		}   
		
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
						String insert = "INSERT into Elite values(";
						insert = insert + "\'" + user_id +"\'"+"," + eliteArray.get(i) + ")";
						//System.out.println(insert);
						stmt.executeQuery(insert);
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
			    		String insert = "INSERT into Neighborhood values(";
						insert = insert +"\'" + id +"\'"+"," + "\'" + neighbourhood.getString(i).replaceAll("'", "''")+"\'" + ")";
						//System.out.println(insert);
						stmt.executeQuery(insert);
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
								String insert = "INSERT into BusinessHours values(";
								insert = insert + "\'" + id +"\'," + "\'"+days.get(i) + "\'," + "\'" + j.getString("open")+ "\'," +"\'"+j.getString("close")+"\'"+ ")";
								//System.out.println(insert);
								stmt.executeQuery(insert);
							}catch(Exception e){
								//sql query to insert into hours table (id, days.get(i),0,0)
								String insert = "INSERT into BusinessHours values(";
								insert = insert + "\'" + id +"\'," + "\'" + days.get(i) + "\',\'0\',\'0\'" + ")";
								//System.out.println(insert);
								stmt.executeQuery(insert);
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
		    			category = categoryArray.getString(i).replaceAll("'", "''");
		    			// WRITE SQL QUERY TO ADD (businessID,category) into table
		    			String insert = "INSERT into Categories values(";
						insert = insert + "\"" + businessID +"\'," + "\'" + category  + "\'"+ ")";
						//System.out.println(insert);
						stmt.executeQuery(insert);
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
				String insert = "INSERT into CheckIn values(";
				insert = insert + "\'" + businessId +"\'," + checkinCount + ")";
				//System.out.println(insert);
				stmt.executeQuery(insert);
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
				String name = obj.getString("name").replaceAll("'", "'");
				int avgStars = obj.getInt("average_stars");
		    	int fans = obj.getInt("fans");
		    	String insert = "INSERT into YelpUsers values(";
				insert = insert + "\'" + user_id +"\'," + "\'" + name + "\'," + avgStars + "," +fans + ")";
				//System.out.println(insert);
				stmt.executeQuery(insert);
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
			    	String name = obj.getString("name").replaceAll("'", "''");
			    	String fullAddress = obj.getString("full_address").replaceAll("'", "''");
			    	fullAddress.replaceAll(",", ";");
			    	String city = obj.getString("city").replaceAll("'", "''");
			    	boolean openBoolean = obj.getBoolean("open");
			    	String open = "";
			    	if(openBoolean)
			    		open = "true";
			    	else
			    		open = "false";
			    	String state = obj.getString("state").replaceAll("'", "''");
			    	int stars = obj.getInt("stars");
			    	Double latitude = (Double)obj.get("latitude");
			    	Double longitude = (Double)obj.get("longitude");
			    	String insert = "INSERT into Business values(";
					insert = insert + "\'" + id +"\'," + "\'" + name + "\'," + "\'" + fullAddress + "\'," +"\'"+city + "\'," + "\'" + open + "\'," + "\'" + state + "\'," +stars + "," + latitude + "," + longitude + ")";
					//System.out.println(insert);
					stmt.executeQuery(insert);
			    	
			 }   			
		}catch(Exception e){
			e.printStackTrace();
		}
		   
	}

}
