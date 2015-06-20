package com.datformers.ReviewSummary;


import java.util.ArrayList;

import com.datformers.database.MongoDBWrapper;
import com.datformers.utils.DatabaseUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Training {

	public static ArrayList<DBObject> res = new ArrayList<DBObject>();
	
	public static void main(String args[]){
		MongoDBWrapper mdb = new MongoDBWrapper(DatabaseUtil.IP, 27017,
				"Reviews");
		mdb.createConnection();

		BasicDBObject Query = new BasicDBObject();
		

		BasicDBObject fields = new BasicDBObject();
		fields.put("review_id", 1);
		fields.put("_id", 0);
		
		res = mdb.executeQuery(Query, fields);
		
		mdb.closeConnection();
	}
}



