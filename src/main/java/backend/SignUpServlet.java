package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@WebServlet("/SignUpServlet")
public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	PrintWriter pw = null;
		JDBCConnector jdbc = new JDBCConnector();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			StringBuilder jsonBody = new StringBuilder();
	        String line;
	        while ((line = br.readLine()) != null) {
	            jsonBody.append(line);
	        }

	        JSONParser parser = new JSONParser();
            Object obj = parser.parse(jsonBody.toString());
            JSONObject requestData = (JSONObject) obj;
            String username = (String) requestData.get("username");
            String password = (String) requestData.get("password");
            String first = (String) requestData.get("first");
            String last = (String) requestData.get("last");
			response.setContentType("application/json");
			pw = response.getWriter();
			
			String tempuserID = "";
			javax.servlet.http.Cookie[] cookies = request.getCookies();
	        if (cookies != null) {
	        	for (javax.servlet.http.Cookie cookie: cookies) {
	        		if (cookie.getName().equals("user_id")) {
	                    tempuserID = cookie.getValue();
	                }
	        	}
	        }
	        
	        JSONObject o = null;
	        o = jdbc.addUser(username, password, first, last, tempuserID);
	        
			Cookie userID = new Cookie("user_id", (String) o.get("user_id"));
			userID.setMaxAge(30 * 24 * 60 * 60);
			response.addCookie(userID);
			Cookie uname = new Cookie("username", (String) o.get("username"));
			uname.setMaxAge(30 * 24 * 60 * 60);
			response.addCookie(uname);
			
			
			String query = o.toString();
			pw.println(query);
			pw.flush();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		finally {
			if (pw != null) {
				pw.close();
			}
			if (br != null) {
				br.close();
			}
		}
	}
}
