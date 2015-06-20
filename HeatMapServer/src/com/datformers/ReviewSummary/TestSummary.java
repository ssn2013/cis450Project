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

public class TestSummary {

	public static String combinedReviews = "";
	public static ArrayList<DBObject> res = new ArrayList<DBObject>();
	public static String businessID = "AgtFQVxo-PGQVsOdY6Nvdg";
	public static String summary = "";
	public static int numSentence = 3;


	public static void main(String args[]) {
		try {
			//System.out.println(System.currentTimeMillis());
			MongoDBWrapper mdb = new MongoDBWrapper("158.130.161.205", 27017,
					"Reviews");
			mdb.createConnection();
			

			BasicDBObject Query = new BasicDBObject();
			Query.put("business_id", businessID);

			BasicDBObject fields = new BasicDBObject();
			fields.put("text", 1);

			res = mdb.executeQuery(Query, fields);
			for (int i = 0; i < res.size(); i++) {
				DBObject current = res.get(i);
				combinedReviews += current.get("text");
			}
			System.out.println("COMBINED REVIEWS");
			System.out.println(combinedReviews);
			HttpResponse<JsonNode> response = Unirest
					.post("https://textanalysis-text-summarization.p.mashape.com/text-summarizer-text")
					.header("X-Mashape-Key",
							"R0dFC6DHl7mshuf0EkDrDsxBclyNp1L73K5jsnPNZnzOFKJWO3")
					.header("Content-Type", "application/x-www-form-urlencoded")
					.header("Accept", "application/json").field("sentnum", numSentence)
					.field("text", combinedReviews).asJson();
			JsonNode summ = response.getBody();

			JSONArray summaryArray = summ.getObject().getJSONArray(
					"sentences");
			for (int i = 0; i < summaryArray.length(); i++) {
				summary += summaryArray.get(i)+". ";
			}
			mdb.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("SUMMARY");
		System.out.println(summary);
		//System.out.println(System.currentTimeMillis());
		System.exit(0);
	}

}
