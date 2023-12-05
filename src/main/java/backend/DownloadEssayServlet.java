package backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

@WebServlet("/download")
public class DownloadEssayServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Connection conn = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
	    ResultSet rs1 = null;
	    ResultSet rs2 = null;
	    ResultSet rs3 = null;
	    InputStream is = null;
	    OutputStream os = null;
	    PrintWriter pw = response.getWriter();

	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        conn = DriverManager.getConnection("jdbc:mysql://localhost/myUSCPeerReviews?", "root", GlobalVariables.db_pass);
	        
	        String user_id = "";
	        javax.servlet.http.Cookie[] cookies = request.getCookies();
	        if (cookies != null) {
	            for (javax.servlet.http.Cookie cookie : cookies) {
	                if (cookie.getName().equals("user_id")) {
	                    user_id = cookie.getValue();
	                }
	            }
	        }

	        // First query to get the feedback_id from the essays table
	        String sql = "SELECT feedback_id FROM essays WHERE user_id = ?";
	        ps1 = conn.prepareStatement(sql);
	        ps1.setString(1, user_id);
	        rs1 = ps1.executeQuery();

	        if (rs1.next()) {
	            String feedback_id = rs1.getString("feedback_id");

	            // Second query to get the pdf_id from the feedbacks table
	            sql = "SELECT pdf_id FROM feedbacks WHERE feedback_id = ?";
	            ps2 = conn.prepareStatement(sql);
	            ps2.setString(1, feedback_id);
	            rs2 = ps2.executeQuery();

	            if (rs2.next()) {
	                String pdf_id = rs2.getString("pdf_id");

	                // Third query to get the PDF file from the pdf_storage table
	                sql = "SELECT pdf_file FROM pdf_storage WHERE pdf_id = ?";
	                ps3 = conn.prepareStatement(sql);
	                ps3.setString(1, pdf_id);
	                rs3 = ps3.executeQuery();

	                if (rs3.next()) {
	                	java.sql.Blob blob = rs3.getBlob("pdf_file");
						response.setContentType("application/pdf");					
						is = blob.getBinaryStream();
	                    os = response.getOutputStream();
	                    byte[] buffer = new byte[4096];
	                    int bytesRead;
	                    while ((bytesRead = is.read(buffer)) != -1) {
	                    	os.write(buffer, 0, bytesRead);
	                    }
	                    os.flush();
	                } else {
	                    pw.write("No PDF file found for the given feedback.");
	                    pw.flush();
	                }
	            } else {
	                pw.write("No feedback found for the given user.");
	                pw.flush();
	            }
	        } else {
	            pw.write("No essay found for the given user ID.");
	            pw.flush();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rs1 != null) {
	                rs1.close();
	            }
	            if (rs2 != null) {
	                rs2.close();
	            }
	            if (rs3 != null) {
	                rs3.close();
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
	            if (conn != null) {
	                conn.close();
	            }
	            if (is != null) {
	                is.close();
	            }
	            if (os != null) {
	                os.close();
	            }
	        } catch (SQLException sq) {
	            System.out.println("SQLException: " + sq.getMessage());
	        }
	    }
	}
}
