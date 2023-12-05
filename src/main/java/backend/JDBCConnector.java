package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JDBCConnector{
	public JDBCConnector() {
		
	}
	
	@SuppressWarnings("unchecked")
	protected JSONObject addUser(String u, String p, String f, String l, String user_id) {
		Connection conn = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs1 = null;
		ResultSet key = null;
		JSONObject o = new JSONObject();
		o.put("status", "fail");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/myUSCPeerReviews?" , "root" , GlobalVariables.db_pass);
			
			ps1 = conn.prepareStatement("SELECT * FROM users WHERE user_id = ?;");
			ps1.setString(1, user_id);
			rs1 = ps1.executeQuery();
			String tempid = "";
			String isguest = "";
			boolean temp = true;
			if (rs1.next()) { // user_id found
				isguest = rs1.getString("is_guest");
				tempid = rs1.getString("user_id");
				if (isguest.equals("1") && u.equals("guest")) { // returning guest, return above info
					temp = false;
					o.remove("status");
					o.put("status", "success");
					o.put("username", u);
					o.put("user_id", tempid);
				} else if (isguest.equals("1") && !u.equals("guest")){ // registering guest, update db and return info; 
					temp = false;
					ps2 = conn.prepareStatement("UPDATE users SET username = ?, pass = ?, fname = ?, lname = ?, is_guest = ? "
							+ "WHERE user_id = ?;",java.sql.Statement.RETURN_GENERATED_KEYS);
					ps2.setString(1, u);
					ps2.setString(2, p);
					ps2.setString(3, f);
					ps2.setString(4, l);
					ps2.setString(5, "0");
					ps2.setString(6, user_id);
					if (ps2.executeUpdate() > 0) {
						System.out.println("guest is now registered");
						o.remove("status");
						o.put("status", "success");
						o.put("username", u);
						o.put("user_id", tempid);
					}
				} else { // repeat user_id cookie, add new user
					temp = true;
				}
			} if (temp) { // user_id not found, add new guest/user
				ps3 = conn.prepareStatement("INSERT INTO users(username,pass,fname,lname,is_guest) VALUES(?,?,?,?,?);",java.sql.Statement.RETURN_GENERATED_KEYS);
				ps3.setString(1, u);
				ps3.setString(2, p);
				ps3.setString(3, f);
				ps3.setString(4, l);
				if (u.equals("guest")) {
					ps3.setString(5, "1");
					System.out.println("adding new guest");
				} else {
					ps3.setString(5, "0");
					System.out.println("adding user guest");
				}
				if (ps3.executeUpdate() > 0) {
					key = ps3.getGeneratedKeys();
					if (key.next()) {
						System.out.println("new user added");
						o.remove("status");
						o.put("status", "success");
						o.put("username", u);
						o.put("user_id", String.valueOf(key.getLong(1)));
					}
				}
			}
		} catch (ClassNotFoundException cfne) {
			System.out.println(cfne.getMessage());
			cfne.printStackTrace();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
				try {
					if (rs1 != null) {
						rs1.close();
					}
					if (key != null) {
						key.close();
					}
					if (conn != null) {
						conn.close();
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
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return o;
	}
	
	@SuppressWarnings("unchecked")
	protected JSONObject loginUser(String u, String p) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONObject o = new JSONObject();
		o.put("status", "fail");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/myUSCPeerReviews?" , "root" , GlobalVariables.db_pass);
			
			ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND pass = ?;");
			ps.setString(1, u);
			ps.setString(2, p);
			rs = ps.executeQuery();
			if (rs.next()) {
				o.remove("status");
				o.put("status", "success");
				o.put("username", u);
				o.put("user_id", rs.getString("user_id"));
			}
		} catch (ClassNotFoundException cfne) {
			System.out.println(cfne.getMessage());
			cfne.printStackTrace();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
				try {
					if (conn != null) {
						conn.close();
					}
					if (ps != null) {
						ps.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return o;
	}
	
	@SuppressWarnings("unchecked")
	protected void matchUsers() {
		Connection conn = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs = null;
		JSONObject o = new JSONObject();
		o.put("status", "fail");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/myUSCPeerReviews?" , "root" , GlobalVariables.db_pass);
			
			ps1 = conn.prepareStatement("SELECT * FROM queue ORDER BY queue_id;");
			rs = ps1.executeQuery();
			if (rs.next()) {
				String queue_id1 = rs.getString("queue_id");
				String user_id1 = rs.getString("user_id");
				String essay_id1 = rs.getString("essay_id");
				System.out.println("user_id1: "+user_id1);
				
				if (rs.next()) {
					String queue_id2 = rs.getString("queue_id");
					String user_id2 = rs.getString("user_id");
					String essay_id2 = rs.getString("essay_id");
					System.out.println("user_id2 outside while: "+user_id2);
					while (user_id1.equals(user_id2)) {
						System.out.println("user_id2 inside while: "+user_id2);
						if (rs.next()) {
							queue_id2 = rs.getString("queue_id");
							user_id2 = rs.getString("user_id");
							essay_id2 = rs.getString("essay_id");
						} else {
							break;
						}
					}
					if (!user_id1.equals(user_id2)) {
						System.out.println("user_id1 matching: "+user_id1);
						System.out.println("user_id2 matching: "+user_id2);
						ps2 = conn.prepareStatement("UPDATE essays SET matched_id = ? WHERE essay_id = ?;");
						ps2.setString(1,essay_id1);
						ps2.setString(2,essay_id2);
						if (ps2.executeUpdate() > 0) {
							ps2.setString(1,essay_id2);
							ps2.setString(2,essay_id1);
							
							if (ps2.executeUpdate() > 0) {
								ps3 = conn.prepareStatement("DELETE FROM queue WHERE queue_id = ?;");
								ps3.setString(1, queue_id1);
								if (ps3.executeUpdate() > 0) {
									ps3.setString(1, queue_id2);
									if (ps3.executeUpdate() > 0) {
										System.out.println("end of matching");
										o.remove("status");
										o.put("status", "ran matching");
									}
								}
							}
						}
					}
				}
			}
		} catch (ClassNotFoundException cfne) {
			System.out.println(cfne.getMessage());
			cfne.printStackTrace();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
				if (ps1 != null) {
					ps1.close();
				}
				if (ps2 != null) {
					ps2.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected String getEssays(String user_id) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONArray json = new JSONArray();
		try { 
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/myUSCPeerReviews?" , "root" , GlobalVariables.db_pass);
			ps = conn.prepareStatement("SELECT * FROM essays WHERE user_id = ?;");
			ps.setString(1,user_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("essay_id", rs.getString("essay_id"));
				jo.put("essay_name", rs.getString("essay_name"));
				jo.put("user_id", rs.getString("user_id"));
				jo.put("matched_id", rs.getString("matched_id"));
				jo.put("pdf_id", rs.getString("pdf_id"));
				jo.put("feedback_id", rs.getString("feedback_id"));
				jo.put("tags", rs.getString("tags"));
				jo.put("status", checkStatus(rs.getString("matched_id")));
				json.add(jo);
				
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
		JSONObject temp = new JSONObject();
		temp.put("essays", json);
		return temp.toJSONString();
	}
	
	protected String checkStatus(String matched_id) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String status = "pending";
		try { 
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/myUSCPeerReviews?" , "root" , GlobalVariables.db_pass);
			ps = conn.prepareStatement("SELECT * FROM essays WHERE essay_id = ?;"); // both need to be null before download
			ps.setString(1,matched_id);
			rs = ps.executeQuery();
			System.out.println("checking feedback status for: "+matched_id);
			while (rs.next()) {
				if (rs.getString("feedback_id") != null) {
					System.out.println("feedback_id for matched_id: "+rs.getString("feedback_id"));
					status = "done";
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sq) {
				System.out.println("sqle: " + sq.getMessage());
			}
		}
		return status;
	}
}
