package com.datformers.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.datformers.servlet.fbresources.FbPost;
import com.datformers.servlet.fbresources.FbRepository;
import com.datformers.servlet.fbresources.FbUser;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Location;
import com.restfb.types.Post;
import com.restfb.types.User;

public class FacebookLoginServlet extends HttpServlet{
	private FbRepository repository = new FbRepository();
	
	@Override
	public void destroy() {
		repository.destroyConnection();
		super.destroy();
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("Called facebook login servlet");
		try {
			response.setHeader("REQUIRES_AUTH", ""+1);
			
			//Get request body
			BufferedReader br = request.getReader();
			String str;
			StringBuffer buffer = new StringBuffer();
			while((str = br.readLine())!=null)
				buffer.append(str);
			JSONObject requestObject = new JSONObject(buffer.toString());
			System.out.println("FETCHED JSON: "+requestObject.toString());
			
			//fetch data from Facebook
			String userId = fetchDataFromFacebook(requestObject);
			
			response.addCookie(new Cookie("fbUserId", userId));
			response.setContentType("application/json");
			response.getWriter().println(new JSONObject().toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Facebook login servlet error: "+e.getMessage());
		}
	} 
	
	private String fetchDataFromFacebook(JSONObject fbResponse) {
		try {
			
			System.out.println("Trying to fetch from FB");
			String accessToken = fbResponse.getJSONObject("authResponse").getString("accessToken");
			System.out.println("Access Token: "+accessToken);
			
			FacebookClient fbClient = new DefaultFacebookClient(accessToken);
			User user = fbClient.fetchObject("me", User.class);
			FbUser me = new FbUser(user);
			
			//my feeds
			Connection<Post> myFeed = fbClient.fetchConnection("me/feed", Post.class, Parameter.with("with", "location"));
			List<FbPost> fbPosts = new ArrayList<FbPost>();
			for(Post post: myFeed.getData()) {
				fbPosts.add(new FbPost(post));
			}
			me.setPosts(fbPosts);
			
			Connection<User> myFriends = fbClient.fetchConnection("me/friends", User.class);
		    for(User friend: myFriends.getData()) {
		    	me.addFriend(friend.getId());
		    }
		    System.out.println("Fetched Details about person: "+me.toString());
		    
		    //Enter all information into database
		    repository.enterInformation(me);
		    return me.fbUserId;
		} catch(Exception e) {
			System.out.println("Exception: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			if(request.getPathInfo()!=null && request.getPathInfo().contains("facebookfriends")) {
				String responseObjKeys[]  = {"status","data"};
				String city = request.getParameter("city");
				
				Cookie[] cookies = request.getCookies();
				String fbUserId = null;
				for(Cookie cookie: cookies) {
					if(cookie.getName().equals("fbUserId")) {
						System.out.println("GOT COOKIE WITH DETAILS OF FB USER");
						fbUserId = cookie.getValue().trim();
					}
				}
				response.setContentType("application/json");
				JSONObject responseObject = new JSONObject();
				if(fbUserId == null) {
					responseObject.put(responseObjKeys[0], "FAILED");
					responseObject.put(responseObjKeys[1], new JSONObject());
					response.getWriter().println(responseObject.toString());
					return;
				}
				responseObject.put(responseObjKeys[0], "OK");
				JSONObject dataObj = new JSONObject();
				
				//TODO: populate data of checkins of Facebook friends
				if(city.equalsIgnoreCase("Philadelphia"))
					sendDummyData(dataObj, fbUserId, city);
				else 
					sendDummyData2(dataObj);
				
				responseObject.put(responseObjKeys[1], dataObj);
				response.getWriter().println(responseObject.toString());
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch (JSONException je) {
			je.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void sendDummyData2(JSONObject dataObj) throws JSONException {
		FbUser user1 = new FbUser();
		user1.firsName="abc";
		user1.lastName="def";
		user1.fbUserId="1234";
		FbUser user2 = new FbUser();
		user2.firsName="xyz";
		user2.lastName="dfe";
		user2.fbUserId="12345";
		FbPost p1 = new FbPost();
		p1.locationName = "Location1";
		p1.state = "California";
		p1.city = "Las Vegas";
		p1.latitude = 36.171278;
		p1.longitude = -115.143124;
		p1.country = "USA";
		FbPost p2 = new FbPost();
		p2.locationName = "Location2";
		p2.state = "California";
		p2.city = "Las Vegas";
		p2.latitude = 36.168247;
		p2.longitude = -115.145313;
		p2.country = "USA";
		FbPost p3 = new FbPost();		
		p3.locationName = "Location3";
		p3.state = "California";
		p3.city = "Las Vegas";
		p3.latitude = 36.170187;
		p3.longitude = -115.135056;
		p3.country = "USA";
		FbPost p4 = new FbPost();		
		p4.locationName = "Location3";
		p4.state = "California";
		p4.city = "Las Vegas";
		p4.latitude = 36.165666;
		p4.longitude =  -115.136826;
		p4.country = "USA";
		List<FbPost> userSets = new ArrayList<FbPost>();
		userSets.add(p1);
		userSets.add(p2);
		user1.setPosts(new ArrayList<>(userSets));
		userSets.clear();
		userSets.add(p3);
		userSets.add(p4);
		user2.setPosts(userSets);
		
		//Format data
		JSONObject obj1 = new JSONObject();
		obj1.put("name", user1.firsName+" "+user1.lastName);
		JSONArray places1 = new JSONArray();
		for(FbPost post: user1.posts) {
			places1.put(post.toJSON());
		}
		obj1.put("tags", places1);
		dataObj.put(user1.fbUserId, obj1);
		
		JSONObject obj2 = new JSONObject();
		obj2.put("name", user2.firsName+" "+user2.lastName);
		JSONArray places2 = new JSONArray();
		for(FbPost post: user2.posts) {
			places2.put(post.toJSON());
		}
		obj2.put("tags", places2);
		dataObj.put(user2.fbUserId, obj2);
	}

	private void sendDummyData(JSONObject dataObj, String fbUserID, String city) throws JSONException {
		ArrayList<FbUser> friendsDetails = repository.getInformation(fbUserID, city);
		
		/*FbUser user1 = new FbUser();
		user1.firsName="abc";
		user1.lastName="def";
		user1.fbUserId="1234";
		FbUser user2 = new FbUser();
		user2.firsName="xyz";
		user2.lastName="dfe";
		user2.fbUserId="12345";
		FbPost p1 = new FbPost();
		p1.locationName = "Location1";
		p1.state = "Pennsylvania";
		p1.city = "Philadelphia";
		p1.latitude = 39.953208;
		p1.longitude = -75.166816;
		p1.country = "USA";
		FbPost p2 = new FbPost();
		p2.locationName = "Location2";
		p2.state = "Pennsylvania";
		p2.city = "Philadelphia";
		p2.latitude = 39.953619;
		p2.longitude = -75.162905;
		p2.country = "USA";
		FbPost p3 = new FbPost();		
		p3.locationName = "Location3";
		p3.state = "Pennsylvania";
		p3.city = "Philadelphia";
		p3.latitude = 39.948725;
		p3.longitude = -75.167357;
		p3.country = "USA";
		List<FbPost> userSets = new ArrayList<FbPost>();
		userSets.add(p1);
		userSets.add(p2);
		user1.setPosts(new ArrayList<>(userSets));
		userSets.clear();
		userSets.add(p3);
		user2.setPosts(userSets);*/
		
		
		for(int i=0;i<friendsDetails.size();i++){
			JSONObject obj1 = new JSONObject();
			obj1.put("name", friendsDetails.get(i).firsName+" "+friendsDetails.get(i).lastName);
			JSONArray places1 = new JSONArray();
			for(FbPost post: friendsDetails.get(i).posts) {
				places1.put(post.toJSON());
			}
			obj1.put("tags", places1);
			dataObj.put(friendsDetails.get(i).fbUserId, obj1);
		}
		//Format data
		/*JSONObject obj1 = new JSONObject();
		obj1.put("name", user1.firsName+" "+user1.lastName);
		JSONArray places1 = new JSONArray();
		for(FbPost post: user1.posts) {
			places1.put(post.toJSON());
		}
		obj1.put("tags", places1);
		dataObj.put(user1.fbUserId, obj1);
		
		JSONObject obj2 = new JSONObject();
		obj2.put("name", user2.firsName+" "+user2.lastName);
		JSONArray places2 = new JSONArray();
		for(FbPost post: user2.posts) {
			places2.put(post.toJSON());
		}
		obj2.put("tags", places2);
		dataObj.put(user2.fbUserId, obj2);*/
	

	}

}
