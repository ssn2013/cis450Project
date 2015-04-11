package com.datformers.servlet;



import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.datformers.database.OracleDBWrapper;

public class addAppUser {
	String query = "";
	
	addAppUser(String str){
		query = str;
	}
	public void addUser(){
		OracleDBWrapper wrapper = new OracleDBWrapper
				("jdbc:oracle:thin:@//158.130.106.114:1521/mydb.localhost","SYSTEM","Verna2813");
		String query1 = "Insert into APPUSER(USER_ID,EMAIL,PASSWORD,FIRST_NAME,LAST_NAME,IS_FACEBOOK_LOGIN)"
				+ " values (usr_id.NEXTVAL,'aryaa@seas.upenn.edu','test','ARyaa','Gautam','Y')";
		
		ResultSet rs = wrapper.executeQuery(query);
		
		
	}
	
}
