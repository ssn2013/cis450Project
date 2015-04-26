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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.util.ajax.JSON;
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
	private FbRepository repostory = new FbRepository();
	
	@Override
	public void destroy() {
		repostory.destroyConnection();
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
			fetchDataFromFacebook(requestObject);
			
			response.setContentType("application/json");
			response.getWriter().println(new JSONObject().toString());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Facebook login servlet error: "+e.getMessage());
		}
	} 
	
	private void fetchDataFromFacebook(JSONObject fbResponse) {
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
		    
		    //TODO: Enter all information into database
		    repostory.enterInformation(me);
		    
		} catch(Exception e) {
			System.out.println("Exception: "+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			if(request.getPathInfo()!=null && request.getPathInfo().contains("facebookfriends")) {
				User userDetails = repostory.getInformation("", false);
				response.setContentType("text/html");
				response.getWriter().println(userDetails.toString());
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
