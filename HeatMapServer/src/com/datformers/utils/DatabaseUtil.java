package com.datformers.utils;

public class DatabaseUtil {
	private static String URL;
//	public static String IP="datformers.cvm5dbupuzl3.us-east-1.rds.amazonaws.com";
//	public static String MongoIP="127.0.0.1";
	public static String IP="158.130.104.54";
	public static String MongoIP="158.130.104.54";
//	public static String UERNAME = "admin";
//	public static String PASSWORD = "adminmaster";
	public static String UERNAME = "SYSTEM";
	public static String PASSWORD = "Verna2813";
	public static String getURL(String ip) {
		IP = ip;
		//URL = "jdbc:oracle:thin:@//"+IP+":1521/mydb.localhost";
//		URL = "jdbc:oracle:thin:@//"+IP+":1521/YELPDB";
		URL = "jdbc:oracle:thin:@//"+IP+":1521/mydb.localhost";
		return URL;
	}
}
