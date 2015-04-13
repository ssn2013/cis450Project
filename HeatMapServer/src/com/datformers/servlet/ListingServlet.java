package com.datformers.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.datformers.ReviewSummary.ReviewSummary;
import com.datformers.database.OracleDBWrapper;
import com.datformers.utils.DatabaseUtil;

public class ListingServlet extends HttpServlet{
	private OracleDBWrapper dbWrapper = new OracleDBWrapper(DatabaseUtil.getURL("158.130.106.114"), DatabaseUtil.UERNAME,DatabaseUtil.PASSWORD);	
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response){
		String city="";
		String category="";
		String bid = "";
		String summary = "";
		String businessName="";
		String stars = "";
		city = request.getParameter("city");
		category= request.getParameter("category");
		
		String queryString ="select * "
				+"from business inner join categories on categories.bid = business.bid " 
				+"where business.city='"+city+"' and categories.category='"+category+"' and ROWNUM < 20";
		ResultSet set = dbWrapper.executeQuery(queryString);
		JSONArray ja = new JSONArray();
		
		try {
			while(set.next()) {
				bid=set.getString("bid");
				ReviewSummary sum = new ReviewSummary(bid, 3);
				summary = sum.getSummary();
				
				businessName = set.getString("name");
				stars = set.getString("stars");
				
				JSONObject jo = new JSONObject();
				jo.put("bid", bid);
				jo.put("name", businessName);
				jo.put("summary", summary);
				jo.put("stars", stars);
				ja.add(jo);
			}
			
			JSONObject mainObj = new JSONObject();
			mainObj.put("items", ja);
			response.setContentType("application/json");
			response.getWriter().println(mainObj.toString());
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Heat map Servlet: "+e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
