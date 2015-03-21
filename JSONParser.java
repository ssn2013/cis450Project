import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.*;

public class JSONParser {
	public static void main(String[] args){
		business();
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
			    	boolean o = obj.getBoolean("open");
			    	String open = "";
			    	if(o)
			    		open = "true";
			    	else
			    		open = "false";
			    	String state = obj.getString("state");
			    	int stars = obj.getInt("stars");
			    	int latitude = obj.getInt("latitude");
			    	int longitude = obj.getInt("longitude");
			    	JSONObject h = obj.getJSONObject("hours");
			    	String hours = "";
			    	JSONObject j = null;
			    	for(int i=0;i<days.size();i++){
						if(i == days.size() - 1){
							try{
								j = h.getJSONObject(days.get(i));
								hours += j.getString("open") + ",";
								hours += j.getString("close");
							}catch(Exception e){
								hours += "0,";
								hours += "0";
							}
						}
						else{
							try{
								j = h.getJSONObject(days.get(i));
								hours += j.getString("open") + ",";
								hours += j.getString("close") + ",";
							}catch(Exception e){
								hours += "0,";
								hours += "0,";
							}
						}
			    	}
			    	System.out.println(hours);
			    	
			    }
		}catch(Exception e){
			e.printStackTrace();
		}
		   
		

	}

}
