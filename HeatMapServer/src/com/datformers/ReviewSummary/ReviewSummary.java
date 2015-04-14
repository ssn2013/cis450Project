package com.datformers.ReviewSummary;

import java.util.ArrayList;
import java.util.HashMap;

import com.datformers.database.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.datformers.utils.DatabaseUtil;

public class ReviewSummary {

	public String combinedReviews = "";
	public ArrayList<DBObject> res = new ArrayList<DBObject>();
	public String businessID = "";
	public String summary = "";
	public int numSentence = 0;

	public ReviewSummary(String businessID, int numS) {
		this.businessID = businessID;
		this.numSentence = numS;
	}

	public HashMap<String, ArrayList<String>> getReviews() {
		ArrayList<String> reviews = new ArrayList<String>();
		try {

			MongoDBWrapper mdb = new MongoDBWrapper(DatabaseUtil.IP, 27017,
					"Reviews");
			mdb.createConnection();

			BasicDBObject Query = new BasicDBObject();
			Query.put("business_id", this.businessID);

			BasicDBObject fields = new BasicDBObject();
			fields.put("text", 1);

			res = mdb.executeQuery(Query, fields);
			for (int i = 0; i < res.size(); i++) {
				DBObject current = res.get(i);
				reviews.add("" + current.get("text"));
				combinedReviews += current.get("text");
			}
			mdb.closeConnection();
			HashMap<String, ArrayList<String>> ret = new HashMap<String, ArrayList<String>>();
			ret.put(combinedReviews, reviews);
			
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getSummary(String combinedReviews) {

		HttpResponse<JsonNode> response = null;
		try {
			response = Unirest
					.post("https://textanalysis-text-summarization.p.mashape.com/text-summarizer-text")
					.header("X-Mashape-Key",
							"R0dFC6DHl7mshuf0EkDrDsxBclyNp1L73K5jsnPNZnzOFKJWO3")
					.header("Content-Type", "application/x-www-form-urlencoded")
					.header("Accept", "application/json")
					.field("sentnum", this.numSentence)
					.field("text", combinedReviews).asJson();
		} catch (UnirestException e) {

			e.printStackTrace();
		}
		JsonNode summ = response.getBody();

		JSONArray summaryArray;
		try {
			summaryArray = summ.getObject().getJSONArray("sentences");
			for (int i = 0; i < summaryArray.length(); i++) {
				this.summary += summaryArray.get(i) + ". ";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return summary;

	}
}



