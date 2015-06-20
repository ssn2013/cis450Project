package com.datformers.servlet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.omg.PortableServer.Servant;

import com.datformers.ReviewSummary.ReviewSummary;
import com.datformers.database.OracleDBWrapper;
import com.datformers.utils.DatabaseUtil;

public class Details extends HttpServlet{
	private OracleDBWrapper dbWrapper = new OracleDBWrapper(
			DatabaseUtil.getURL(DatabaseUtil.IP), DatabaseUtil.UERNAME,
			DatabaseUtil.PASSWORD);
	
	@Override
	public void destroy(){
		dbWrapper.closeConnection();
	}
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response){
		HashMap<Integer, String> days = new HashMap<Integer, String>();
		days.put(1, "Sunday");
		days.put(2, "Monday");
		days.put(3, "Tuesday");
		days.put(4, "Wednesday");
		days.put(5, "Thursday");
		days.put(6, "Friday");
		days.put(7, "Saturday");
		
		String bid = request.getParameter("bid");
		String name="";
		String stars="'";
		String address="";
		String lat="'";
		String lon="";
		ArrayList<String> reviews = new ArrayList<String>();
		HashMap<String, String> idReviewMapping = new HashMap<String, String>();
		HashMap<String, String> idDenseMapping = new HashMap<String, String>();
		
		
		String summary="Adarsh is summary";
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		String lookingFor=days.get(day);
		String queryString = "select * "
				+ "from business inner join businesshours on businesshours.BID = business.bid "
				+ "where business.bid='"+bid+"' and businesshours.DAY='"+lookingFor+"'";
		ResultSet set = dbWrapper.executeQuery(queryString);
		try {
			while(set.next()){
				name = set.getString("name");
				stars = set.getString("stars");
				address=set.getString("full_address");
				lat=set.getString("latitude");
				lon=set.getString("longitude");
				
			}
			JSONArray ja = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("name",name);
			obj.put("stars",stars);
			obj.put("address",address);
			obj.put("lat",lat);
			obj.put("lon",lon);
			ReviewSummary sum = new ReviewSummary(bid, 4);
			HashMap<String, ArrayList<String>> ret = sum.getReviews();
			String combinedReviews = "";
			for ( String key : ret.keySet() ) {
			    combinedReviews=key;
			}
			//summary = sum.getSummary(combinedReviews);
			
			reviews = ret.get(combinedReviews);
			for(int i=0;i<reviews.size();i++){
				ja.add(reviews.get(i));
				String id = reviews.get(i).split("\\$",2)[0];
				//System.out.println(id+"--"+reviews.get(i).split("\\$",2)[1]);
				idReviewMapping.put(id,reviews.get(i).split("\\$",2)[1]);
				idDenseMapping.put(id, "0");
			}
			//FileInputStream fstream = new FileInputStream("");
			ServletContext context = getServletContext();
			FileInputStream fstream = new FileInputStream(context.getRealPath("info_dense.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				String id = strLine.split("\\t")[0];
				//System.out.println(idDenseMapping.get("\"rkCkBuEy9awSZ2MzCrV7XA\""));
				if(idDenseMapping.containsKey(id)){
					idDenseMapping.put(id,strLine.split("\\t")[1]);
					
				}
			}
			
			//Close the input stream
			br.close();
			
			//ja.add(reviews.get(i));
			obj.put("summary", summary);
			obj.put("reviews", ja);
			
			//System.out.println(obj.toString());
			response.setContentType("application/json");
			response.getWriter().println(obj.toString());
			
		} catch (SQLException | JSONException | IOException e) {
			e.printStackTrace();
			dbWrapper.closeConnection();
		}
		
		
		
	}

}
