package com.datformers.utils;

public class DatabaseUtil {
	private static String URL;
	public static String IP="158.130.108.8";
	public static String UERNAME = "SYSTEM";
	public static String PASSWORD = "Verna2813";
	public static String getURL(String ip) {
		IP = ip;
		//URL = "jdbc:oracle:thin:@//"+IP+":1521/mydb.localhost";
		URL = "jdbc:oracle:thin:@//"+IP+":1521/mydb.localhost";
		return URL;
	}
}
