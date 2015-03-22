package com.datformers.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet{
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("Servlet called");
		PrintWriter outWriter = null;
		response.setContentType("text/html");
		System.out.println("Content type set");
		try {
			outWriter = response.getWriter();
			if(outWriter!=null)
				System.out.println("Got writer");
			outWriter.println("<html><body><p>Hi and welcome to our very first servlet</p></body></html>");
			outWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			//TODO: insert password matching functionality
			if(username.equals(password)) {
				response.sendRedirect("main.html");
			}
		} catch (Exception e) {
			
		}
	}
}
