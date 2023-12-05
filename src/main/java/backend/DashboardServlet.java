package backend;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter pw = null;
		try {

	        String user_id = "";
	        String username = "";
	        javax.servlet.http.Cookie[] cookies = request.getCookies();
	        if (cookies != null) {
	        	for (javax.servlet.http.Cookie cookie: cookies) {
	        		if (cookie.getName().equals("user_id")) {
	        			user_id = cookie.getValue();
	                }
	        		if (cookie.getName().equals("username")) {
	                    username = cookie.getValue();
	                }
	        	}
	        }
	        System.out.println("user_id: " + user_id);
	        System.out.println("username: " + username);
	        
            
            response.setContentType("application/json");
			pw = response.getWriter();
			
			JDBCConnector jdbc = new JDBCConnector();
			String json = jdbc.getEssays(user_id);
			
			pw.write(json);
			pw.flush();
		}  catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
		
	}
}
