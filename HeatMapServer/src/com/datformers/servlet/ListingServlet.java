package com.datformers.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.datformers.ReviewSummary.ReviewSummary;
import com.datformers.database.MongoDBWrapper;
import com.datformers.database.OracleDBWrapper;
import com.datformers.utils.DatabaseUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ListingServlet extends HttpServlet {
	private OracleDBWrapper dbWrapper = new OracleDBWrapper(
			DatabaseUtil.getURL(DatabaseUtil.IP), DatabaseUtil.UERNAME,
			DatabaseUtil.PASSWORD);

	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String city = "";
		String category = "";
		String bid = "";
		String review = "";
		String businessName = "";
		String address = "";
		String stars = "";
		String open = "";
		int startIndex = 1;
		int endIndex = 0;
		DBObject res;
		city = request.getParameter("city");
		category = request.getParameter("category");
		startIndex = Integer.parseInt(request.getParameter("index"));
		endIndex = startIndex + 5;

		String queryString = "select * "
				+ "from business inner join categories on categories.bid = business.bid "
				+ "where business.city='" + city
				+ "' and categories.category='" + category + "' and ROWNUM >= "
				+ startIndex + "and ROWNUM < " + endIndex + " order by stars desc";
		ResultSet set = dbWrapper.executeQuery(queryString);
		JSONArray ja = new JSONArray();

		MongoDBWrapper mdb = new MongoDBWrapper(DatabaseUtil.IP, 27017,
				"Reviews");
		mdb.createConnection();

		try {
			while (set.next()) {
				bid = set.getString("bid");
				businessName = set.getString("name");
				stars = set.getString("stars");
				address = set.getString("full_address");
				open = set.getString("open");
				BasicDBObject Query = new BasicDBObject();
				Query.put("business_id", bid);
				BasicDBObject fields = new BasicDBObject();
				fields.put("text", 1);
				res = mdb.executeQueryFirst(Query, fields);
				review = String.valueOf(res.get("text"));
					
				JSONObject jo = new JSONObject();
				jo.put("bid", bid);
				jo.put("name", businessName);
				jo.put("address", address);
				jo.put("stars", stars);
				jo.put("open", open);
				jo.put("review", review);
				ja.add(jo);
			}
			JSONObject mainObj = new JSONObject();
			mainObj.put("items", ja);
			response.setContentType("application/json");
			response.getWriter().println(mainObj.toString());
			mdb.closeConnection();

		} catch (SQLException | IOException e) {
			e.printStackTrace();
			System.out.println("Heat map Servlet: " + e.getMessage());
		}

	}

}
