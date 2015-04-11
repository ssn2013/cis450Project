package com.datformers.database;

//import com.mongodb.BasicDBObject;
//import com.mongodb.BulkWriteOperation;
//import com.mongodb.BulkWriteResult;
//import com.mongodb.Cursor;
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.DBCursor;
//import com.mongodb.DBObject;
//import com.mongodb.Mongo;
//import com.mongodb.MongoClient;
//import com.mongodb.ParallelScanOptions;
//import com.mongodb.ServerAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import static java.util.concurrent.TimeUnit.SECONDS;



public class MongoDBWrapper {
	String host;
	int port;
	DB database;
	Mongo mongoClient;
	DBCollection reviews;
	
	
	public MongoDBWrapper(String hostname,int port,String name) {
		host=hostname;
		this.port=port;
		createConnection();
		reviews=getCollection(name);
		
	}
	public void createConnection() {
		try {
			mongoClient = new Mongo( host , port );
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		database = mongoClient.getDB( "mydb" );
		boolean auth =database.authenticate("dpk","password".toCharArray());
		
		if(database==null) {
			System.out.println("Error in opening the database!!\n");
			System.exit(0);
		}
	}
	public DBCollection getCollection(String name) {
		if(database==null) {
			System.out.println("Database not Open!!\n");
		}
		return database.getCollection(name);
	}
	public void closeConnection() {
		mongoClient.close();
	}
	public void addReview(ReviewCollection r) {
		int votes[]=r.getVotes();
		BasicDBObject doc = new BasicDBObject("name", "Review")
//		BasicDBObject doc = new BasicDBObject()
		.append("review_id", r.getReviewId())
		.append("type", r.getType())
		.append("text", r.getText())
		.append("date", r.getDate())
		.append("user_id", r.getUserId())
		.append("business_id", r.getBusinessId())
		.append("stars", r.getStars())
		.append("votes", new BasicDBObject("funny",votes[0] ).append("useful", votes[1]).append("cool", votes[2]));
		reviews.insert(doc);
	}
	public ReviewCollection getReview(String reviewID) {
		ReviewCollection r=new ReviewCollection();
		BasicDBObject query = new BasicDBObject("review_id", reviewID);
		DBCursor cursor = reviews.find(query);
		DBObject s = cursor.next();
		r.setReviewId((String)s.get("review_id"));
		r.setBusinessId((String)s.get("business_id"));
		r.setDate((String)s.get("date"));
		r.setStars((String)s.get("stars"));
		r.setUserId((String)s.get("user_id"));
		r.setText((String)s.get("text"));
		r.setType((String)s.get("type"));
		
		return r;	
	}
	public  ArrayList<DBObject> executeQuery(BasicDBObject Query,BasicDBObject fields) {
		 DBCursor cursor = reviews.find(Query, fields);
	
	
	ArrayList<DBObject> ret_value=new ArrayList<DBObject>();
	 while(cursor.hasNext()) {

	       ret_value.add(cursor.next());

	    }
	 return ret_value;
	}
	public void deleteReview(String reviewID) {
		BasicDBObject document = new BasicDBObject("review_id",reviewID);
		
		reviews.remove(document);
		
	}
	
	
}
