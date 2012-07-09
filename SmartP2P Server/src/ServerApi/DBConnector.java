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

package ServerApi;

import java.sql.*;

import com.microsoft.sqlserver.jdbc.*;


public class DBConnector {

	private Connection con;

	private ResultSet rs;

	private Statement stmt; // for typical SQL statements

	private PreparedStatement pstmt; // for stored procedures

	private String connectionUrl;



	public DBConnector(String ip, int port, String DBName) {

		try {

			connectionUrl = "jdbc:sqlserver://"+ ip +":"+ port +";databaseName=" + DBName +";integratedSecurity=true;";
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			con = DriverManager.getConnection(connectionUrl);

		} catch (ClassNotFoundException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		} catch (SQLException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

	}

	public ResultSet getReadOnlyRecordSet(String query) throws SQLException {
		rs = null;

		stmt = con.createStatement();

		rs = stmt.executeQuery(query);
		return rs;
	}

	public ResultSet GetResult() {
		return rs;
	}

	public void setResultSet(Object a) {
		rs = (ResultSet) a;
	}

	public void MakeStatement() throws SQLException {
		stmt = con.createStatement();

	}

	public Statement getStatement() {
		return stmt;
	}

	public void setPreparedStatement(PreparedStatement a) {
		pstmt = a;
		// pstmt = con.c

	}

	public Connection getConnection() {
		return con;
	}

	public PreparedStatement getPreparedStatement() {
		return pstmt;
	}

	public void test() {

		System.out.println("Testing...");

		String name = "Koullis";
		try {

			String SQL = "Select dbo.GET_USER_ID_BY_NAME('" + name + "') AS NUM";

			stmt = con.createStatement();

			rs = stmt.executeQuery(SQL);

			while (rs.next()) {

				System.out.println(rs.getString("NUM") + " ");

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

		System.out.println("OK");

	}

}
