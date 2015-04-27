package com.datformers.servlet.fbresources;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.datformers.database.OracleDBWrapper;
import com.datformers.servlet.AddAppUser;
import com.datformers.utils.DatabaseUtil;
import com.restfb.types.User;

public class FbRepository {
	
	OracleDBWrapper wrapper;
	
	public FbRepository() {
		wrapper = new OracleDBWrapper(DatabaseUtil.getURL(DatabaseUtil.IP), DatabaseUtil.UERNAME, DatabaseUtil.PASSWORD);
	}
	
	public void destroyConnection () {
		wrapper.closeConnection();
	}

	public void enterInformation(FbUser user) {
		//storing the information in APPUSER Table
		String isFbLogin="Y";
		String query = "SELECT count(*) as cnt FROM APPUSER WHERE email LIKE '"
				+ user.email + "'";
		int user_id=-1;
		
		ResultSet res = wrapper.executeQuery(query);
		int count = 0;
		try {
			if (res.next()) {
				count = res.getInt("cnt");
			}
			res.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		if (count > 0) {
			String query1 = "Update APPUSER SET IS_FACEBOOK_LOGIN='Y' WHERE email LIKE '"
					+ user.email
					+ "'";
					wrapper.executeQuery(query1);
					
		}else {
			System.out.println("creating new user");
			String query1 = "Insert into APPUSER(USER_ID,EMAIL,PASSWORD,FIRST_NAME,LAST_NAME,IS_FACEBOOK_LOGIN)"
					+ " values (usr_id.NEXTVAL,'"
					+ user.email
					+ "','"
					+ ""
					+ "','"
					+ user.firsName
					+ "','"
					+ user.lastName
					+ "','"
					+ isFbLogin + "')";
			// System.out.println(query1);
			wrapper.executeQuery(query1);
			
		}
		
//		//storing info in FBUser if not already present
			res=null;
			res = wrapper
					.executeQuery("Select user_id From APPUSER Where email LIKE '"
							+ user.email + "'");
			try {
				if (res.next()) {
					user_id = res.getInt("user_id");
				}
				res.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println(user_id);
			if(user_id!=-1) {
				if(!checkDupEntryFbUsers(user_id,user.fbUserId)) {
			String FBUserTableQuery = "Insert into FBUSERS(fbuser_id,appuser_id)"
					+ " values('"
					+ user.fbUserId
					+ "',"
					+ user_id + ")";
			System.out.println(FBUserTableQuery);
			wrapper.executeQuery(FBUserTableQuery);
				}
			
			System.out.println("query happend");
			}
//		//storing FB Friends info
		List<String> friends=user.friends;
		System.out.println("starting firneds");
		for(String frnd:friends){
			if(!checkIfPresent("fbuser_id",frnd, "FBUSERS")) {
				query="Insert into FBFRIENDS(user_id,friend_id) "
						+ " values ("
						+ user_id+","
						+"'"+frnd+"')";
				wrapper.executeQuery(query);
			}
			
		}
		System.out.println("friend add done");
		// put fb posts
		List<FbPost> posts=user.posts;
		
		for(FbPost post:posts) {
		String query1 = "Insert into FBPOSTS(ID,FBUSER_ID,LOCNAME,CITY,COUNTRY,LATITUDE,LONGITUDE,STATE)"
				+ " values (fbusr_id.NEXTVAL,'"
				+ user.fbUserId
				+ "','"
				+ post.locationName
				+ "','"
				+ post.city
				+ "','"
				+ post.country
				+ "',"
				+ post.latitude
				+ ","
				+ post.longitude
				+ ",'"
				+ post.state
				+ "')";
		
		// System.out.println(query1);
		wrapper.executeQuery(query1);
		}
		System.out.println("posts done check done");
	}

	public User getInformation(String appUserId, boolean FbUserId) {
		return null;
	}
	public boolean checkIfPresent(String field,String userId,String tablename ) {
	    // select the number of rows in the table
		String query="SELECT COUNT(*) FROM "+tablename+" WHERE "+field+"="+userId;
		ResultSet rs =wrapper.executeQuery(query);
	    int rowCount = -1;
		try {
			if (rs.next()) {
			rowCount = rs.getInt(1);
			}
			 rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	    if (rowCount!=-1) return true;
	    return false;
	  }
	
	public boolean checkDupEntryFbUsers(int user_id,String fbUserId) {
		String query = "SELECT count(*) as cnt FROM FBUSERS WHERE appuser_id="+user_id+" and fbuser_id LIKE '"+fbUserId+"'"; 
				
		
		ResultSet res = wrapper.executeQuery(query);
		int count = 0;
		try {
			if (res.next()) {
				count = res.getInt("cnt");
			}
			res.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		if(count>0) return true; 
		else return false;
	}	
	
}
