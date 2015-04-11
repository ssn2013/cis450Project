package com.datformers.database;


public class ReviewCollection {

private String reviewId;
private int[] votes;
private String type;
private String businessId;
private String userId;
private String stars;
private String date;
private String text;
public String getReviewId() {
	return reviewId;
}
public void setReviewId(String reviewId) {
	this.reviewId = reviewId;
}
public int[] getVotes() {
	return votes;
}
public void setVotes(int []votes) {
	this.votes = votes;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getBusinessId() {
	return businessId;
}
public void setBusinessId(String businessId) {
	this.businessId = businessId;
}
public String getUserId() {
	return userId;
}
public void setUserId(String userId) {
	this.userId = userId;
}
public String getStars() {
	return stars;
}
public void setStars(String stars) {
	this.stars = stars;
}
public String getDate() {
	return date;
}
public void setDate(String date) {
	this.date = date;
}
public String getText() {
	return text;
}
public void setText(String text) {
	this.text = text;
}

}
