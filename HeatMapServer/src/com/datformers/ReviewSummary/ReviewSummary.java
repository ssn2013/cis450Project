package com.datformers.ReviewSummary;
import java.util.ArrayList;
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

public class ReviewSummary {

	public String combinedReviews = "";
	public ArrayList<DBObject> res = new ArrayList<DBObject>();
	public String businessID = "";
	public String summary = "";
	public int numSentence = 0;

	public ReviewSummary(String businessID, int numS) {
		this.businessID = businessID;
		this.numSentence=numS;
	}

	public String getSummary() {
		try {

			MongoDBWrapper mdb = new MongoDBWrapper("158.130.106.114", 27017,
					"Reviews");
			mdb.createConnection();
			

			BasicDBObject Query = new BasicDBObject();
			Query.put("business_id", this.businessID);

			BasicDBObject fields = new BasicDBObject();
			fields.put("text", 1);

			res = mdb.executeQuery(Query, fields);
			for (int i = 0; i < res.size(); i++) {
				DBObject current = res.get(i);
				combinedReviews += current.get("text");
			}
			HttpResponse<JsonNode> response = Unirest
					.post("https://textanalysis-text-summarization.p.mashape.com/text-summarizer-text")
					.header("X-Mashape-Key",
							"4153kk3FIomshq0GZ7oIM9SriGcGp1MFdjejsnh6bOl5o3bowA")
					.header("Content-Type", "application/x-www-form-urlencoded")
					.header("Accept", "application/json").field("sentnum", this.numSentence)
					.field("text", combinedReviews).asJson();
			JsonNode summary = response.getBody();

			JSONArray summaryArray = summary.getObject().getJSONArray(
					"sentences");
			for (int i = 0; i < summaryArray.length(); i++) {
				this.summary += summaryArray.get(i)+". ";
			}
			mdb.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return summary;

	}

}
