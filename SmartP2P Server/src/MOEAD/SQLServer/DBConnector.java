/*
 * This is framework for searching objects (e.g. images, etc.) captured 
 * by the users in a mobile social community. Our framework is founded on an 
 * in-situ data storage model, where captured objects remain local on their owner’s 
 * smartphones and searches take place over a lookup structure we compute dynamically. 
 * Initially, a query user invokes a search to find an object of interest. 
 * Our structure concurrently optimizes several conflicting objectives using a 
 * MultiObjective Optimization approach and calculates a set of high quality nondominated 
 * Query Routing Trees (QRTs) in a single run. The optimal set is then forwarded to the query 
 * user to select a QRT to be searched based on instant requirements and preferences. 
 * To demonstrate the SmartP2P we utilize our cloud of smartphones (SmartLab) composed 
 * of 40 Android devices. The conference attendees will be able to appreciate how social 
 * content can be efficiently shared without revealing their personal content to a centralized 
 * authority. 
 * 
 *Copyright (C) 2011 - 2012 Christos Aplitsiotis
 *This program is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *at your option) any later version.
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *GNU General Public License for more details.
 *?ou should have received a copy of the GNU General Public License
 *along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

 
package MOEAD.SQLServer;

import java.sql.*;

public class DBConnector{

	private Connection con;
	private ResultSet rs;
	private Statement stmt;
	private PreparedStatement pstmt;
	private String connectionUrl;
	
	public DBConnector(String ip, int port, String DBName) {

		try {
			System.out.println("Connecting...");
			connectionUrl = "jdbc:sqlserver://"+ ip +":"+ port +";databaseName=" + DBName +";integratedSecurity=true;";
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(connectionUrl);
			System.out.println("OK");	
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test(){
		System.out.println("Testing...");
		try {

			String SQL = "SELECT TOP 5 ID, FILE_NO, F1, F2 FROM GEOLIFE_DATA";
			stmt = con.createStatement();
			rs = stmt.executeQuery(SQL);

			while (rs.next()) {
				System.out.println(rs.getInt("ID") + ", "
						+ rs.getInt("FILE_NO") + ", " + rs.getString("F1")
						+ ", " + rs.getString("F2"));
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}
	
	public boolean initExperiment(String time_filter){
		//time_filter = "12:48:4[0-1]";
		boolean result = true;
		
		try {
			//CREATE EXPERIMENT
			System.out.print("Creating Experiment [ " + time_filter + " ]...");
			pstmt = con.prepareStatement("{call dbo.CREATE_EXPERIMENT(?)}");
			// @TIME_FILTER
			pstmt.setString(1, time_filter);
			pstmt.execute();
			pstmt.close();
			System.out.println("OK");

			//System.out.print("Creating JAVA Mapping...");
			pstmt = con.prepareStatement("{call dbo.CREATE_JAVA_MAPPING_TABLE}");
			pstmt.execute();
			pstmt.close();
			//System.out.println("OK");

		} catch (SQLException e) {
			result = false;
			e.printStackTrace();
		}

		return result;		
	}
	
	public int getCountUsers(){
		int result = -1;
		try {
			//System.out.print("COUNT USERS=");
			String SQL = "select dbo.GET_ACTIVE_USERS() as COUNT_USERS";
			pstmt = con.prepareStatement(SQL);
			rs = pstmt.executeQuery();

			rs.next();
			result = rs.getInt("COUNT_USERS");
			//System.out.println(result);
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	//prp na pernei parametro to id tou query peer
	public double[][] getDistanceOfUsers(int id){
		double[][] result = new double[this.getCountUsers()][this.getCountUsers()];

		try {
			//System.out.println("\n\nUSERS - DISTANCE.");
			/*String SQL = "SELECT USER_A, USER_B, DISTANCE FROM RESULTS_WITH_MAPPING ORDER BY USER_A, USER_B";
			stmt = con.createStatement();
			rs = stmt.executeQuery(SQL);*/
			String SQL = "select * from dbo.Distances_between_A_Users (" + id + ")";
			pstmt = con.prepareStatement(SQL);
			rs = pstmt.executeQuery();

			//rs.next();

			//System.out.println("USER_A" + "\t" + "USER_B" + "\t" + "DISTANCE");
			while (rs.next()) {
				result[rs.getInt("USER_A")][rs.getInt("USER_B")] = rs.getDouble("DISTANCE"); 
				System.out.println(rs.getInt("USER_A") + "\t" + rs.getInt("USER_B") + "\t" + rs.getDouble("DISTANCE"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	//prp na pernei parametro to id tou query peer
	public int[][] getNumberOfPapers(String filter, int id){
		int[][] result = new int[this.getCountUsers()][3];
		try {
			System.out.println("\n\nUSERS - PAPERS.");
			String SQL = "select * from dbo.RESULTS_WITH_COUNT (" + id + ", '" + filter + "' )";
			pstmt = con.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			/*String SQL = "{call dbo.RESULTS_WITH_COUNT(?)}";
			pstmt = con.prepareStatement(SQL);
			// @PAPER_FILTER			
			pstmt.setString(1, filter);
			rs = pstmt.executeQuery();*/
			
			//JAVA_ID, COUNT_PAPERS, COUNT_WITH_FILTER
			System.out.println("JAVA_ID" + "\t" + "TOTAL"+ "\t" + "FILTER");
			while (rs.next()) {
				result[rs.getInt("UsersId")][0] = rs.getInt("COUNT_PAPERS");
				result[rs.getInt("UsersId")][1] = rs.getInt("COUNT_WITH_FILTER");
                                result[rs.getInt("UsersId")][2] = rs.getInt("TOTAL_TITLE_BYTES");
				
				System.out.println(rs.getInt("UsersId") + "\t"
						+ rs.getInt("COUNT_PAPERS")+ "\t"
						+ rs.getInt("COUNT_WITH_FILTER"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	

}
