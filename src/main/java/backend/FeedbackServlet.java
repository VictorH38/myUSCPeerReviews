package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/FeedbackServlet")
@MultipartConfig
public class FeedbackServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter pw = null;
		BufferedReader br = null;
		Connection conn = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		ResultSet key = null;
		ResultSet feedback = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/myUSCPeerReviews?", "root", GlobalVariables.db_pass);
					
			String matched_id = request.getParameter("matched_id");		
			Part filePart = request.getPart("feedbackfile");		
			InputStream fileContent = filePart.getInputStream();
			
			ps1 = conn.prepareStatement("INSERT INTO pdf_storage(pdf_file) VALUES(?);",java.sql.Statement.RETURN_GENERATED_KEYS);
			ps1.setBlob(1, fileContent);
			String pdfkey = "beforepdf";
			String feedback_id = "beforeessay";
			if (ps1.executeUpdate() > 0) {
				key = ps1.getGeneratedKeys();
				if (key.next()) {
					pdfkey = String.valueOf(key.getLong(1));
					System.out.println("feedback inserted into pdfs");
				}
				ps2 = conn.prepareStatement("INSERT INTO feedbacks(pdf_id) VALUES(?);",java.sql.Statement.RETURN_GENERATED_KEYS);
				ps2.setString(1, pdfkey);
				if (ps2.executeUpdate() > 0) {
					feedback = ps2.getGeneratedKeys();
					if (feedback.next()) {
						feedback_id = String.valueOf(feedback.getLong(1));
						System.out.println("feedback inserted into feedbacks");
						// update using matched_id
						ps3 = conn.prepareStatement("UPDATE essays SET feedback_id = ? WHERE essay_id = ?;");
						ps3.setString(1, feedback_id);
						ps3.setString(2, matched_id);
						if (ps3.executeUpdate() > 0) {
							System.out.println("feedback id in essay");
						}
					}
				}
			}
			pw = response.getWriter();
			pw.write(feedback_id);
			pw.flush();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (key != null) {
					key.close();
				}
				if (feedback != null) {
					feedback.close();
				}
				if (br != null) {
					br.close();
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
				if (pw != null) {
					pw.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter pw = null;
		BufferedReader br = null;
		Connection conn = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		InputStream fileData = null;
		InputStream is = null;
		OutputStream os = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/myUSCPeerReviews?", "root", GlobalVariables.db_pass);
			
			String essay_id = request.getParameter("essay_id");
			
			ps1 = conn.prepareStatement("SELECT * FROM essays WHERE essay_id = ?;");
			ps1.setString(1, essay_id);
			rs1 = ps1.executeQuery();
			String feedback_id = "";
			String pdf_id = "";
			if (rs1.next()) {
				feedback_id = rs1.getString("feedback_id");
				ps2 = conn.prepareStatement("SELECT * FROM feedbacks WHERE feedback_id = ?;");
				ps2.setString(1, feedback_id);
				rs2 = ps2.executeQuery();
				if (rs2.next()) {
					pdf_id = rs2.getString("pdf_id");
					ps3 = conn.prepareStatement("SELECT * FROM pdf_storage WHERE pdf_id = ?;");
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
					}
					else {
						pw = response.getWriter();
						pw.write("failed in pdf storage");
						pw.flush();
					}
				} else {
					pw = response.getWriter();
					pw.write("emptypdf");
					pw.flush();
				}
			} else {
				pw = response.getWriter();
				pw.write("nonexistant");
				pw.flush();
			}
			
			
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
				if (rs1 != null) {
					rs1.close();
				}
				if (rs2 != null) {
					rs2.close();
				}
				if (rs3 != null) {
					rs3.close();
				}
				if (fileData != null) {
					fileData.close();
				}
				if (br != null) {
					br.close();
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
				if (pw != null) {
					pw.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
	}
}
