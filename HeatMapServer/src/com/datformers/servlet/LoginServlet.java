package com.datformers.servlet;

import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("Servlet called");
		PrintWriter outWriter = null;
		response.setContentType("text/html");
		System.out.println("Content type set");
		try {
			outWriter = response.getWriter();
			if (outWriter != null)
				System.out.println("Got writer");
			outWriter
					.println("<html><body><p>Hi and welcome to our very first servlet</p></body></html>");
			outWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (request.getParameter("login") != null) {

				String username = request.getParameter("username");
				String password = request.getParameter("password");
				String isValidPwd = "";
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				System.out.println("user:pwd" + username + password);
				if (username.isEmpty() || password.isEmpty()) {
					out.println("<html><head><body><p>A mandatory field is empty!<p></body></head>");
					System.out.println("EMPTY");
					return;
				}
				AddAppUser tmp = null;
				String query = "Select password from APPUSER where email LIKE '"
						+ username + "'";
				System.out.println(query);
				tmp = new AddAppUser(query);
				ResultSet res = tmp.addUser();
				
				if (res.next()) {
					isValidPwd = res.getString("password");
				}
				if (password.equals(isValidPwd)) {
					//out.println("<html><head><body><h3>Login Success!!</h3></body></head>");
					response.sendRedirect("main.html");

				} else {
					System.out.println("Login Failed!!");
					out.println("<html><head><body><h3>Invalid credentials!!</h3></body></head>");
					out.flush();	
					tmp.closeDb();
					return;
				}
				tmp.closeDb();
			

			}
			if (request.getParameter("register") != null) {
				response.sendRedirect("register.html");

			}

			if (request.getParameter("signup") != null) {
				// boolean ifUserAlreadyRegisterd = true;

				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				String first = request.getParameter("firstname");
				String last = request.getParameter("lastname");
				String pwd = request.getParameter("password");
				String email = request.getParameter("email");
				String isFbLogin = "N";

				if (first.isEmpty() || last.isEmpty() || pwd.isEmpty()
						|| email.isEmpty()) {

					out = response.getWriter();
					out.println("<html><head><body><p>A mandatory field is empty!<p></body></head>");
					// System.out.println("<html><head><body><p>A mandatory field is empty!<p></body></head>");

				} else {
					String query = "SELECT count(*) as cnt FROM APPUSER WHERE email LIKE '"
							+ email + "'";
					// System.out.println("Signup query" + query);
					AddAppUser add = new AddAppUser(query);
					ResultSet res = add.addUser();
					int count = 0;
					if (res.next()) {
						count = res.getInt("cnt");
					}
					
					add.closeDb();
					System.out.println("Signup query" + query);
					if (count > 0) {
						// System.out.println("Already Registered");
						out.println("<html><head><body><p>This email id is already registered!!</p></body></head>");
					} else {
						// System.out.println("Registering!");
						String query1 = "Insert into APPUSER(USER_ID,EMAIL,PASSWORD,FIRST_NAME,LAST_NAME,IS_FACEBOOK_LOGIN)"
								+ " values (usr_id.NEXTVAL,'"
								+ email
								+ "','"
								+ pwd
								+ "','"
								+ first
								+ "','"
								+ last
								+ "','"
								+ isFbLogin + "')";
						// System.out.println(query1);
						AddAppUser tmp = new AddAppUser(query1);
						tmp.addUser();
						tmp.closeDb();
						response.sendRedirect("main.html");
						//out.println("<html><head><body><h3>Registration Successful!</h3></body></head>");
					}
				}

			}

		} catch (Exception e) {

		}
	}
}
