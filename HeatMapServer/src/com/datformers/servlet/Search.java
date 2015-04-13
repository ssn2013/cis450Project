package com.datformers.servlet;

import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.datformers.database.BingSearch;
import com.datformers.database.WebSearchResult;

public class Search extends HttpServlet{

	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response){
		try{
		JSONObject main = new JSONObject();
		JSONArray arr = new JSONArray();
		String query = request.getParameter("query");
		BingSearch bs = new BingSearch();
		ArrayList<WebSearchResult> res = bs.search(query);
		for(int i=0;i<res.size();i++){
			WebSearchResult cur = res.get(i);
			JSONObject obj = new JSONObject();
			obj.put("title", cur.getTitle());
			obj.put("description",cur.getDescription());
			obj.put("displayURL", cur.getDisplayURL());
			obj.put("url", cur.getUrl());
			arr.put(obj);
		}
		main.put("result", arr);
		response.setContentType("application/json");
		response.getWriter().println(main.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
