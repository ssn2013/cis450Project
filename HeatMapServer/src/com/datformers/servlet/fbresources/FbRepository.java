package com.datformers.servlet.fbresources;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
			
			
			if(user_id!=-1) {
				if(!checkIfPresent("fbuser_id",user.fbUserId, "FBUSERS")) {
					String FBUserTableQuery = "Insert into FBUSERS(fbuser_id,appuser_id)"
					+ " values('"
					+ user.fbUserId
					+ "',"
					+ user_id + ")";
			
			wrapper.executeQuery(FBUserTableQuery);
				}
			
			
			}
//		//storing FB Friends info
		List<String> friends=user.friends;
		
		for(String frnd:friends){
			if(!checkDupEntryFbFriends(user.fbUserId,frnd) && checkIfPresent("fbuser_id",frnd, "FBUSERS")) {
				query="Insert into FBFRIENDS(user_id,friend_id) "
						+ " values ('"
						+ user.fbUserId+"',"
						+"'"+frnd+"')";
				
				wrapper.executeQuery(query);
			}
			
		}
		
		// put fb posts
		List<FbPost> posts=user.posts;
		
		for(FbPost post:posts) {
			if(postPresent(user.fbUserId,post)) continue;
		String query1 = "Insert into FBPOSTS(ID,FBUserID,LOCNAME,CITY,COUNTRY,LATITUDE,LONGITUDE,STATE)"
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
		
	}

	public ArrayList<FbUser> getInformation(String fbID, String city_name) {
		ArrayList<FbUser> friendsDetails = new ArrayList<FbUser>();
		String fbUserID = fbID;
		String city = city_name;
		ArrayList<String> friendList = new ArrayList<String>();
		
		// GET THIS GUYS FRIENDS
		String getFriendsQuery = "select FRIEND_ID from fbfriends where USER_ID='"+fbUserID+"'";
		ResultSet rs =wrapper.executeQuery(getFriendsQuery);
		try {
			while(rs.next()){
				friendList.add(rs.getString("FRIEND_ID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		for(int i=0;i<friendList.size();i++){
			List<FbPost> userPostSet = new ArrayList<FbPost>();
			String appID = "";
			String getAppUserQuery = "select * from fbusers where FBUSER_ID='"+friendList.get(i)+"'";
			rs = wrapper.executeQuery(getAppUserQuery);
			try {
				if(rs.next()){
					appID = rs.getString("APPUSER_ID");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			String userDetailsQuery = "select * from appuser where USER_ID='"+appID+"'";
			rs = wrapper.executeQuery(userDetailsQuery);
			FbUser friend = new FbUser();
			try {
				if(rs.next()){
					friend.firsName = rs.getString("FIRST_NAME");
					friend.lastName = rs.getString("LAST_NAME");
					friend.email = rs.getString("EMAIL");
					friend.fbUserId = friendList.get(i);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			String postsQuery = "select * from fbposts where FBUSERID='"+friendList.get(i)+"' and CITY='"+city+"'";
			rs=wrapper.executeQuery(postsQuery);
			
			try {
				while(rs.next()){
					FbPost post = new FbPost();
					post.locationName = rs.getString("LOCNAME");
					post.state = rs.getString("STATE");
					post.city = rs.getString("CITY");
					post.latitude = Double.parseDouble(rs.getString("LATITUDE"));
					post.longitude = Double.parseDouble(rs.getString("LONGITUDE"));
					post.country = rs.getString("COUNTRY");
					userPostSet.add(post);
				}
				friend.setPosts(new ArrayList<>(userPostSet));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			friendsDetails.add(friend);
		}
		
		return friendsDetails;
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
	  
	    if (rowCount>0) return true;
	    return false;
	  }
	
	public boolean checkDupEntryFbFriends(String user_id,String fbUserId) {
		String query = "SELECT count(*) as cnt FROM FBFRIENDS WHERE user_id="+user_id+" and friend_id LIKE '"+fbUserId+"'"; 
				
		
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
	public boolean postPresent(String user,FbPost Post) {
		String query = "SELECT count(*) as cnt FROM FBPOSTS WHERE FBUSERID="+user
				       +" and LOCNAME LIKE '"+Post.locationName+"' and CITY LIKE '"+Post.city+"'"
				       +" and COUNTRY LIKE '"+Post.country+"' and LATITUDE="+Post.latitude
				       +" and LONGITUDE="+Post.longitude+" and STATE LIKE '"+Post.state+"'";
				
		
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
