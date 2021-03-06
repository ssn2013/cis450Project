package com.datformers.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.continuations.SuspendedInvocationException;
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
	public MongoDBWrapper mdb = null;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		if (request.getParameter("city") != null) {
			System.out.println("City reached");
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

			// String queryString = "select * "
			// +
			// "from (select *,rownum as r from business inner join categories on categories.bid = business.bid "
			// + "where business.city='" + city
			// + "' and categories.category='" + category + "') where r >= "
			// + startIndex + " and r < " + endIndex + " order by stars desc";

			// String
			// queryString="With a as ( select * from business inner join categories on categories.bid = business.bid"
			// +
			// " where business.city='"+city+"' and categories.category='"+category+"' and ROWNUM < "+endIndex
			// +" order by stars desc)"
			// +
			// "select * from ( select /*+ FIRST_ROWS(n) */a.*, ROWNUM rnum from a)"
			// +"where rnum  >= "+ startIndex;

			// + "' and categori where ROWNUM <=

			// + "where business.city='" + city
			// + "' and categories.category='" + category + "'

			String queryString = "select * from ( select a.*, rownum rnum from (  select * from business "
					+ "inner join categories on categories.bid = business.bid where business.city='"
					+ city
					+ "' and categories.category='"
					+ category
					+ "' order by stars desc,Name asc)a "
					+ "where rownum <"
					+ endIndex + ") where rnum >= " + startIndex;

			ResultSet set = dbWrapper.executeQuery(queryString);

			JSONArray ja = new JSONArray();

			if (mdb == null) {
				mdb = new MongoDBWrapper(DatabaseUtil.MongoIP, 27017, "Reviews");
				// mdb.createConnection();
			}
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

			} catch (SQLException | IOException e) {
				e.printStackTrace();
				System.out.println("Heat map Servlet: " + e.getMessage());
			}

		}

		else if (request.getParameter("lat") != null) {

			float lat1 = Float.parseFloat(request.getParameter("lat"));
			float lon2 = Float.parseFloat(request.getParameter("lon"));
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
			category = request.getParameter("category");
			// startIndex = Integer.parseInt(request.getParameter("index"));
			// endIndex = startIndex + 5;

			// String queryString = "select * "
			// +
			// "from (select *,rownum as r from business inner join categories on categories.bid = business.bid "
			// + "where business.city='" + city
			// + "' and categories.category='" + category + "') where r >= "
			// + startIndex + " and r < " + endIndex + " order by stars desc";

			// String
			// queryString="With a as ( select * from business inner join categories on categories.bid = business.bid"
			// +
			// " where business.city='"+city+"' and categories.category='"+category+"' and ROWNUM < "+endIndex
			// +" order by stars desc)"
			// +
			// "select * from ( select /*+ FIRST_ROWS(n) */a.*, ROWNUM rnum from a)"
			// +"where rnum  >= "+ startIndex;

			// + "' and categori where ROWNUM <=

			// + "where business.city='" + city
			// + "' and categories.category='" + category + "'

			String queryString = "select * "
					+ "from ( select a.*, rownum rnum "
					+ "from ( select * from business inner join categories on categories.bid = business.bid"
					+ " where  business.latitude<=" + (lat1 + 2)
					+ "and  business.latitude>=" + (lat1 - 2)
					+ " and categories.category='" + category
					+ "' order by stars desc  ) a " + "where rownum <= " + 10
					+ " ) where rnum >= " + 0;

			System.out.println("=====" + queryString);

			ResultSet set = dbWrapper.executeQuery(queryString);
			JSONArray ja = new JSONArray();

			MongoDBWrapper mdb = new MongoDBWrapper(DatabaseUtil.MongoIP, 27017,
					"Reviews");
			mdb.createConnection();
			// System.out.println("++++" +queryString);

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
					// String latitude = set.getString("latitude");
					// String longitude = set.getString("longitude");
					JSONObject jo = new JSONObject();
					jo.put("bid", bid);
					jo.put("name", businessName);
					jo.put("address", address);
					jo.put("stars", stars);
					jo.put("open", open);
					jo.put("review", review);
					// jo.put("lat", latitude);
					// jo.put("lon", longitude);
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

	@Override
	public void destroy() {
		mdb.closeConnection();
	}
}
