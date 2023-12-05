package backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/MatchUserServlet")
public class MatchUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter pw = null;
		JDBCConnector jdbc = new JDBCConnector();
		try {
			response.setContentType("text/plain");
			pw = response.getWriter();
			jdbc.matchUsers();
			pw.println("finished");
			pw.flush();
		}
		finally {
			if (pw != null) {
				pw.close();
			}
		}
	}
	
	// method for updating the user who just uploaded an essay's reviewer_id 
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {	
		String curr_user_id = request.getParameter("user"); // getting the current user who just uploaded an essay
		
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs1 = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/myUSCPeerReviews?", "root", GlobalVariables.db_pass);
			
			ps = conn.prepareStatement("SELECT * FROM recent_users;");
			rs1 = ps.executeQuery();
			
			String user_id = ""; // the user id of the most recent user who uploaded an essay
			
			
			if (rs1.next()) { // if there is a user w/ an essay waiting in database to be matched, then match with the curr_user
				user_id = rs1.getString("user_id");
				
				// updating the curr_user_id's reviewer_id in the essays SQL table
				ps2 = conn.prepareStatement("UPDATE essays SET reviewer_id = ? WHERE user_id = ?");
				ps2.setString(1, user_id);
				ps2.setString(2, curr_user_id);
				
				// updating the user_id's reviewer_id in the essays SQL table
				ps3 = conn.prepareStatement("UPDATE essays SET reviewer_id = ? WHERE user_id = ?");
				ps3.setString(1, curr_user_id);
				ps3.setString(2, user_id);
				ps3.execute();
			}
			else { // if there is no user w/ an essay waiting in database to be matched, then put the curr_user in the database
				PreparedStatement ps4 = conn.prepareStatement("INSERT INTO recent_users(user_id) VALUES (?)");
				ps4.setString(1, curr_user_id);
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (ps1 != null) {
					ps1.close();
				}
				if (ps2 != null) {
					ps2.close();
				}
				if (ps3 != null) {
					ps3.close();
				}
				if (rs1 != null) {
					rs1.close();
				}
				if (conn != null) {
					conn.close();
				}
			}
			catch (SQLException sq) {
				sq.printStackTrace();
			}
		}
	}
	
	// method for removing the user w/ an essay waiting to be matched in the SQL recent_user table because they've been matched
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		Connection conn = null;
		PreparedStatement ps1 = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/myUSCPeerReviews?", "root", GlobalVariables.db_pass);
			// removing the user in database because they have been matched w/ the curr_user_id
			ps1 = conn.prepareStatement("DELETE FROM recent_users");
			ps1.execute();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (ps1 != null) {
					ps1.close();
				}
			}
			catch (SQLException sq) {
				sq.printStackTrace();
			}
		}
	}
}
