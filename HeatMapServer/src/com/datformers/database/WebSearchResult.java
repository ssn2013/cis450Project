package com.datformers.database;
import java.io.Serializable;

import org.odata4j.core.OEntity;

public class WebSearchResult implements Serializable{
 
	String id;
	String title;
	String description;
	String displayURL;
	String url;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDisplayURL() {
		return displayURL;
	}
	public void setDisplayURL(String displayURL) {
		this.displayURL = displayURL;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
