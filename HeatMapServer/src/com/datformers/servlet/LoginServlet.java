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
			if(request.getParameter("login")!=null){
				
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				//TODO: insert password matching functionality
				if(username.equals(password)) {
					
					out.println("<html><head><title>Login Success!!</title></head>");
		
				}
				else{
					out.println("<html><head><title>Login Failed!!</title></head>");
				}
				
			}
			if(request.getParameter("register")!=null){
				response.sendRedirect("register.html");
				
			}
			
			if(request.getParameter("register")!=null){
				String first = request.getParameter("firstname");
				String last= request.getParameter("lastname");
				String pwd = request.getParameter("password");
				String email = request.getParameter("email");
				String isFbLogin = "N";
				String query1 = "Insert into APPUSER(USER_ID,EMAIL,PASSWORD,FIRST_NAME,LAST_NAME,IS_FACEBOOK_LOGIN)"
						+ " values (usr_id.NEXTVAL,'"+ email+"','"+pwd+"','"+first+"','"+ last +"','"+isFbLogin+"')";
				System.out.println(query1);
				addAppUser add = new addAppUser(query1);
				
				
			}
			
			
		} catch (Exception e) {
			
		}
	}
}
