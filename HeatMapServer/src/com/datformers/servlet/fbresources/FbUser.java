package com.datformers.servlet.fbresources;

import java.util.ArrayList;
import java.util.List;

import com.restfb.types.User;

public class FbUser {

	public String firsName;	
	public String lastName;
	public String email;
	public String birthday;
	public String fbUserId;
	public List<String> friends = new ArrayList<String>();
	public List<FbPost> posts = new ArrayList<FbPost>();
	
	public FbUser() {
		
	}
	public FbUser(User user) {
		firsName = user.getFirstName();
		lastName = user.getLastName();
		email = user.getEmail();
		birthday = user.getBirthday();
		fbUserId = user.getId();
	}
	public void setPosts(List<FbPost> posts) { 
		this.posts = posts;
	}
	public void addFriend(String friendId) {
		friends.add(friendId);
	}
	@Override 
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("USER:- FirstName: "+firsName+" LastName: "+lastName+" Email: "+email+" Birthday: "+birthday+"\nFriends:");
		for(String id: friends) {
			buffer.append(' '+id+' ');
		}
		buffer.append("\nPosts: ");
		for(FbPost post: posts) {
			buffer.append(post.toString()+'\n');
		}
		return buffer.toString();
	}	
}
