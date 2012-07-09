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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.math.plot.Draw.Draw3DPlots;

import BFS.BFSAlg;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

class JoinedPeer extends Thread {

	//Variable Declaration
	byte[] data;
	Socket socket;
	ObjectInputStream Sinput;
	ObjectOutputStream Soutput;
	DBConnector con;

	//Send the QRT to Client
	public void sendTree(ArrayList<String[]> tree) throws IOException {

		String str = "";
		for (int i = 0; i < tree.size(); ++i) {
			str = "";
			for (int j = 0; j < tree.get(i).length; ++j) {
				if (j != 0)
					str = str + " ";
				str = str + tree.get(i)[j];
			}
			Soutput.writeObject(str);
			Soutput.flush();
		}
		Soutput.writeObject("NULL");
		Soutput.flush();
	}

	//Convert QRT based on Real Ids
	public ArrayList<String[]> convertTree(ArrayList<String[]> tree, String Qid)
			throws SQLException {
		ArrayList<String[]> tree2 = null;
		String str = null;
		int id1 = 0, id2 = 0, port = 0;
		String ip = null;
		String lon = null, lat = null;

		CallableStatement cstmt = null;
		String SQL;

		tree2 = new ArrayList<String[]>();
		for (int i = 0; i < tree.size(); ++i) {
			SQL = "Select dbo.ReturnMappedUser('" + Qid + "','" + i
					+ "') as id";
			con.getReadOnlyRecordSet(SQL);
			while (con.GetResult().next()) {
				id1 = con.GetResult().getInt("id");
			}
			/**/
			SQL = "Select dbo.GET_PORT('" + id1 + "') as id";
			con.getReadOnlyRecordSet(SQL);
			while (con.GetResult().next()) {
				port = con.GetResult().getInt("id");
			}
			SQL = "Select dbo.GET_IP('" + id1 + "') as id";
			con.getReadOnlyRecordSet(SQL);
			while (con.GetResult().next()) {
				ip = con.GetResult().getString("id");
			}

			SQL = "Select dbo.ReturnLastLonUser('" + id1 + "') as id";
			con.getReadOnlyRecordSet(SQL);
			while (con.GetResult().next()) {
				lon = con.GetResult().getString("id");
			}
			SQL = "Select dbo.ReturnLastLatUser('" + id1 + "') as id";
			con.getReadOnlyRecordSet(SQL);
			while (con.GetResult().next()) {
				lat = con.GetResult().getString("id");
			}

			if (tree.get(i)[0].equals("-1"))
				id2 = -1;
			else {
				System.out.println("sdfsdfsdfsdip: " + Qid + "dfsdfs: "
						+ tree.get(i)[0]);
				SQL = "Select dbo.ReturnMappedUser('" + Qid + "','"
						+ tree.get(i)[0] + "') as id2";
				con.getReadOnlyRecordSet(SQL);
				while (con.GetResult().next()) {
					id2 = con.GetResult().getInt("id2");
				}
			}
			if (id2 == 0)
				id2 = -1;
			if (id1 == id2)
				str = new String(id1 + " " + "-1" + " " + ip + " " + port + " "
						+ lon + " " + lat);
			else
				str = new String(id1 + " " + id2 + " " + ip + " " + port + " "
						+ lon + " " + lat);
			tree2.add(str.split(" "));
		}
		return tree2;
	}

	//Print QRT on Server's console
	public void printTree(ArrayList<String[]> tree) {
		for (int i = 0; i < tree.size(); ++i) {
			System.out.println(tree.get(i)[0] + " " + tree.get(i)[1] + " "
					+ tree.get(i)[2] + " " + tree.get(i)[3] + " "
					+ tree.get(i)[4]);
		}

	}

	//Send the QRT at Client
	public void sendTree() throws IOException, ClassNotFoundException,
			SQLException {
		String id = (String) Sinput.readObject();
		System.out.println("id: " + id);
		String pass = (String) Sinput.readObject();
		System.out.println("pass: " + pass);
		String decision = (String) Sinput.readObject();
		System.out.println("dec: " + decision);
		String time = (String) Sinput.readObject();
		System.out.println("time: " + time);
		String recall = (String) Sinput.readObject();
		System.out.println("recall: " + recall);
		String Alg = (String) Sinput.readObject();
		System.out.println("Algorithm: " + Alg);

		if (Alg.equals("MOEAD")) {
			ArrayList<String[]> outputAlgorithm = new ArrayList<String[]>();
			outputAlgorithm = IO
					.getFileContent(
							"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\SmartP2P Server\\src\\MOEAD\\Exp"
									+ id + "\\BestPF\\outBests.log", " ");

			int minPos = 0;

			if (decision.equals("0")) {
				Double minVal = Double.valueOf(outputAlgorithm.get(0)[1]);
				for (int i = 1; i < outputAlgorithm.size(); ++i)
					if (Double.valueOf(outputAlgorithm.get(i)[1]) < minVal) {
						minPos = i;
						minVal = Double.valueOf(outputAlgorithm.get(i)[1]);
					}
			} else {
				Double minVal, x1, x2, y1, y2, temp;

				x1 = Double.valueOf(time);
				System.out.println("Time:  " + time + "  Recall: " + recall);
				y1 = Double.valueOf(time);
				x2 = Double.valueOf(outputAlgorithm.get(0)[2]);
				y2 = Double.valueOf(outputAlgorithm.get(0)[3]);

				minVal = distance(x1, x2, y1, y2);
				System.out.println("first min: " + minVal);
				for (int i = 1; i < outputAlgorithm.size(); ++i) {
					x2 = Double.valueOf(outputAlgorithm.get(i)[2]);
					y2 = Double.valueOf(outputAlgorithm.get(i)[3]);
					temp = distance(x1, x2, y1, y2);
					System.out.println("temp: " + temp);
					if (temp < minVal) {
						minPos = i;
						minVal = temp;
					}
				}
				System.out.println("minpos: " + minPos);
			}

			ArrayList<String[]> tree = new ArrayList<String[]>();
			tree = IO
					.getFileContent(
							"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\SmartP2P Server\\src\\MOEAD\\Exp"
									+ id
									+ "\\PFtrees\\PFtree"
									+ minPos
									+ ".log", " ");

			tree = this.convertTree(tree, id);
			for (int i = 0; i < tree.size(); ++i)
				System.out.println(tree.get(i)[0] + "  " + tree.get(i)[1]
						+ "  " + tree.get(i)[2] + "  " + tree.get(i)[3]);

			sendTree(tree);
		}

		if (Alg.equals("NSGAII")) {
			ArrayList<String[]> outputAlgorithm = new ArrayList<String[]>();
			outputAlgorithm = IO
					.getFileContent(
							"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\SmartP2P Server\\src\\NSGAII\\Exp"
									+ id + "\\BestPF\\outBests.log", " ");

			int minPos = 0;

			if (decision.equals("0")) {
				Double minVal = Double.valueOf(outputAlgorithm.get(0)[1]);
				for (int i = 1; i < outputAlgorithm.size(); ++i)
					if (Double.valueOf(outputAlgorithm.get(i)[1]) < minVal) {
						minPos = i;
						minVal = Double.valueOf(outputAlgorithm.get(i)[1]);
					}
			} else {
				Double minVal, x1, x2, y1, y2, temp;

				x1 = Double.valueOf(time);
				System.out.println("Time:  " + time + "  Recall: " + recall);
				y1 = Double.valueOf(time);
				x2 = Double.valueOf(outputAlgorithm.get(0)[2]);
				y2 = Double.valueOf(outputAlgorithm.get(0)[3]);

				minVal = distance(x1, x2, y1, y2);
				System.out.println("first min: " + minVal);
				for (int i = 1; i < outputAlgorithm.size(); ++i) {
					x2 = Double.valueOf(outputAlgorithm.get(i)[2]);
					y2 = Double.valueOf(outputAlgorithm.get(i)[3]);
					temp = distance(x1, x2, y1, y2);
					System.out.println("temp: " + temp);
					if (temp < minVal) {
						minPos = i;
						minVal = temp;
					}
				}
				System.out.println("minpos: " + minPos);
			}

			ArrayList<String[]> tree = new ArrayList<String[]>();
			tree = IO
					.getFileContent(
							"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\SmartP2P Server\\src\\NSGAII\\Exp"
									+ id
									+ "\\PFtrees\\PFtree"
									+ minPos
									+ ".log", " ");

			tree = this.convertTree(tree, id);
			for (int i = 0; i < tree.size(); ++i)
				System.out.println(tree.get(i)[0] + "  " + tree.get(i)[1]
						+ "  " + tree.get(i)[2] + "  " + tree.get(i)[3]);

			sendTree(tree);
		}

		if (Alg.equals("RW")) {
			String word = (String) Sinput.readObject();
			System.out.println("word: " + word);
			CallableStatement cstmt = null;
			String SQL;

			SQL = "{call dbo.create_Query (?)}";

			try {
				cstmt = con.getConnection().prepareCall(SQL);
				cstmt.setString(1, id);
				cstmt.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			IO.deleteDir(new File("./" + "src/RW/Exp" + id));
			RW.src.RW.RWAlg C = new RW.src.RW.RWAlg(Integer.parseInt(id), word);
			ArrayList<String[]> tree = new ArrayList<String[]>();
			tree = IO
					.getFileContent(
							"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\SmartP2P Server\\src\\RW\\Exp"
									+ id + "\\tree" + ".log", " ");

			tree = this.convertTree(tree, id);
			for (int i = 0; i < tree.size(); ++i)
				System.out.println(tree.get(i)[0] + "  " + tree.get(i)[1]
						+ "  " + tree.get(i)[2] + "  " + tree.get(i)[3]);

			sendTree(tree);

		}

		if (Alg.equals("BFS")) {
			String word = (String) Sinput.readObject();
			System.out.println("word: " + word);
			CallableStatement cstmt = null;
			String SQL;

			SQL = "{call dbo.create_Query (?)}";

			try {
				cstmt = con.getConnection().prepareCall(SQL);
				cstmt.setString(1, id);
				cstmt.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			IO.deleteDir(new File("./" + "src/BFS/Exp" + id));
			BFSAlg C = new BFS.BFSAlg(Integer.parseInt(id), word);
			ArrayList<String[]> tree = new ArrayList<String[]>();
			tree = IO
					.getFileContent(
							"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\SmartP2P Server\\src\\BFS\\Exp"
									+ id + "\\tree" + ".log", " ");

			tree = this.convertTree(tree, id);
			for (int i = 0; i < tree.size(); ++i)
				System.out.println(tree.get(i)[0] + "  " + tree.get(i)[1]
						+ "  " + tree.get(i)[2] + "  " + tree.get(i)[3]);

			sendTree(tree);

		}

	}
	
	//Calculate the distance
	public Double distance(Double x1, Double x2, Double y1, Double y2) {
		Double res = null;

		res = Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));

		return res;
	}

	
	public void register() throws IOException, ClassNotFoundException,
			SQLException {
		String name = (String) Sinput.readObject();
		System.out.println("id: " + name);
		String pass = (String) Sinput.readObject();
		System.out.println("pass: " + pass);

		CallableStatement cstmt = null;
		String SQL;

		SQL = "{call dbo.REGISTER_USER (?,?,?,?)}";

		try {
			cstmt = con.getConnection().prepareCall(SQL);
			cstmt.setString(1, socket.getInetAddress().getHostAddress());
			cstmt.setInt(2, socket.getPort());
			cstmt.setString(3, name);
			cstmt.setString(4, pass);
			cstmt.executeQuery();
		} catch (SQLException e) {
			String uId = null;
			if (e.getSQLState() == null && e.getErrorCode() != 50005) {
				SQL = "Select dbo.GET_USER_ID_BY_NAME('" + name + "') as num";

				con.getReadOnlyRecordSet(SQL);
				while (con.GetResult().next()) {
					uId = con.GetResult().getString("num");
				}
				Soutput.writeObject(uId);
				Soutput.flush();
				return;
			} else if (e.getErrorCode() == 50005) {
				Soutput.writeObject("User Name Already Exists");
				Soutput.flush();
				return;
			}
			Soutput.writeObject("NOT OK");
			Soutput.flush();
			return;
		}
	}

	//Connects to another Server
	public void connect() throws IOException, ClassNotFoundException {
		String id = (String) Sinput.readObject();
		System.out.println("id: " + id);
		String pass = (String) Sinput.readObject();
		System.out.println("pass: " + pass);
		String port = (String) Sinput.readObject();
		System.out.println("port: " + port);
		CallableStatement cstmt = null;
		String SQL;

		SQL = "{call dbo.CONNECT_USER (?,?,?,?)}";

		try {
			cstmt = con.getConnection().prepareCall(SQL);
			cstmt.setString(3, socket.getInetAddress().getHostAddress());
			cstmt.setString(4, port);
			cstmt.setString(1, id);
			cstmt.setString(2, pass);
			cstmt.executeQuery();
		} catch (SQLException e) {
			if (e.getSQLState() == null && e.getErrorCode() != 50004) {
				Soutput.writeObject("OK");
				Soutput.flush();
				return;
			}
			Soutput.writeObject("NOT OK");
			Soutput.flush();
			return;
		}
	}

	//terminate connection
	public void terminateConnection() throws IOException,
			ClassNotFoundException {
		String id = (String) Sinput.readObject();
		System.out.println("id: " + id);

		CallableStatement cstmt = null;
		String SQL;

		SQL = "{call dbo.TERMINATE_CONNECTION_USER (?)}";

		try {
			cstmt = con.getConnection().prepareCall(SQL);
			cstmt.setString(1, id);
			cstmt.executeQuery();
		} catch (SQLException e) {
			if (e.getSQLState() == null) {
				Soutput.writeObject("OK");
				Soutput.flush();
				return;
			}
			Soutput.writeObject("NOT OK");
			Soutput.flush();
			return;
		}
	}

	//Receive QRT from another Server
	public ArrayList<String[]> receiveTree() throws IOException,
			ClassNotFoundException {
		ArrayList<String[]> tree = new ArrayList<String[]>();
		String str;

		while (true) {
			str = (String) Sinput.readObject();
			if (str.equals("NULL"))
				break;
			tree.add(str.split(" "));
		}

		printTree(tree);
		return tree;

	}

	//Receive the Profile from a Client and add it into the database
	public void receiveANDaddProfile() throws IOException,
			ClassNotFoundException {
		CallableStatement cstmt = null;
		String SQL;

		String id = (String) Sinput.readObject();
		System.out.println("id: " + id);
		ArrayList<String[]> aList = new ArrayList<String[]>();
		String str_q;
		String str_d;

		while (true) {
			str_q = (String) Sinput.readObject();
			str_d = (String) Sinput.readObject();

			if (str_q.equals("NULL") || str_d.equals("NULL"))
				break;
			String s = str_q + "\t" + str_d;
			aList.add(s.split("\t"));
		}

		int flag = 1;
		System.out.println("prof started");
		for (int i = 0; i < aList.size(); i++) {

			SQL = "{call dbo.INSERT_PROFILES (?,?,?)}";

			try {
				cstmt = con.getConnection().prepareCall(SQL);
				cstmt.setString(3, aList.get(i)[0]);
				cstmt.setString(2, aList.get(i)[1]);
				cstmt.setString(1, "" + id);
				cstmt.executeQuery();
			} catch (SQLException e) {
				if (e.getSQLState() == null && e.getErrorCode() != 50002)
					flag = flag * 1;
				else
					flag = flag * 0;

			}
		}
		System.out.println("prof ended");
		if (flag == 1) {
			Soutput.writeObject("OK");
			Soutput.flush();
			return;
		}
		Soutput.writeObject("NOT OK");
		Soutput.flush();
		return;

	}

	//Receive the GPS coordinates from a Client and add it into the database
	public void receiveANDaddInfo() throws IOException, ClassNotFoundException {
		CallableStatement cstmt = null;
		String SQL;

		String id = (String) Sinput.readObject();
		System.out.println("id: " + id);

		String str = (String) Sinput.readObject();
		String[] str2 = str.split(" ");

		SQL = "{call dbo.INSERT_INFO (?,?,?)}";

		try {
			cstmt = con.getConnection().prepareCall(SQL);
			cstmt.setString(3, str2[0]);
			cstmt.setString(2, str2[1]);
			cstmt.setString(1, "" + id);
			cstmt.executeQuery();
		} catch (SQLException e) {
			if (e.getSQLState() == null && e.getErrorCode() != 50003) {
				Soutput.writeObject("OK");
				Soutput.flush();
				return;
			}
			Soutput.writeObject("NOT OK");
			Soutput.flush();
			return;
		}

	}

	//Constructor
	JoinedPeer(Socket socket) throws IOException {

		con = new DBConnector("127.0.0.1", 1433, "SMARTP2P");
		this.socket = socket;
		Soutput = new ObjectOutputStream(socket.getOutputStream());
		Soutput.flush();
		Sinput = new ObjectInputStream(socket.getInputStream());

	}

	public void Search(String req, String id) throws NumberFormatException,
			IOException, ClassNotFoundException {

		Client pReq = new Client(Integer.parseInt(id));
		pReq.setTree(this.receiveTree());
		pReq.Request("Search " + req);

	}

	//Create the graph that represents the Pareto Front and send it to the client
	public boolean GetGraph() throws IOException, ClassNotFoundException,
			MatlabConnectionException, MatlabInvocationException,
			InterruptedException {
		CallableStatement cstmt = null;
		String SQL;

		String id = (String) Sinput.readObject();
		System.out.println("id: " + id);
		String word = (String) Sinput.readObject();
		System.out.println("word: " + word);
		String Alg = (String) Sinput.readObject();
		System.out.println("Algorithm: " + Alg);

		SQL = "{call dbo.create_Query (?)}";

		try {
			cstmt = con.getConnection().prepareCall(SQL);
			cstmt.setString(1, id);
			cstmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (Alg.equals("MOEAD")) {
			IO.deleteDir(new File("./" + "src/MOEAD/Exp" + id));
			IO.deleteDir(new File(
					"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\Figures\\figure3Duser"
							+ id + ".png"));
			MOEAD.Algorithm C = new MOEAD.Algorithm(Integer.parseInt(id), word);

			Draw3DPlots draw = new Draw3DPlots();
			draw.Draw(
					"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\SmartP2P Server\\src\\MOEAD\\Exp"
							+ id + "\\BestPF\\outBests.log",
					"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\Figures\\figure3Duser"
							+ id + ".png");
			draw.Draw(
					"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\SmartP2P Server\\src\\MOEAD\\Exp"
							+ id + "\\BestPF\\outBests.log",
					"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\Figures\\figure3Duser"
							+ id + ".png");
			Thread.sleep(1000);

			this.sendFile("C:\\Users\\Christos\\Documents\\SmartP2P workspace\\Figures\\figure3Duser"
					+ id + ".png");
		}

		if (Alg.equals("NSGAII")) {
			IO.deleteDir(new File("./" + "src/NSGAII/Exp" + id));
			IO.deleteDir(new File(
					"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\Figures\\figure3Duser"
							+ id + ".png"));
			NSGAII.Algorithm C = new NSGAII.Algorithm(Integer.parseInt(id),
					word);

			Draw3DPlots draw = new Draw3DPlots();
			draw.Draw(
					"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\SmartP2P Server\\src\\MOEAD\\Exp"
							+ id + "\\BestPF\\outBests.log",
					"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\Figures\\figure3Duser"
							+ id + ".png");
			draw.Draw(
					"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\SmartP2P Server\\src\\MOEAD\\Exp"
							+ id + "\\BestPF\\outBests.log",
					"C:\\Users\\Christos\\Documents\\SmartP2P workspace\\Figures\\figure3Duser"
							+ id + ".png");
			Thread.sleep(1000);

			this.sendFile("C:\\Users\\Christos\\Documents\\SmartP2P workspace\\Figures\\figure3Duser"
					+ id + ".png");
		}

		return true;
	}

	//Send File to a client
	public void sendFile(String file) throws IOException {

		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);
		ObjectOutputStream oos = new ObjectOutputStream(
				socket.getOutputStream());
		oos.writeObject(buffer);

	}

	public void run() {

		while (true) {
			try {

				String request = (String) Sinput.readObject();
				System.out.println(request + ": ");

				if (request.equals("SendProfile")) {
					receiveANDaddProfile();
				}
				if (request.equals("SendInfo")) {
					receiveANDaddInfo();
				}
				if (request.equals("GetTree")) {
					sendTree();
				}
				if (request.equals("SendTree")) {
					printTree(receiveTree());
				}
				if (request.equals("Register")) {
					register();
				}
				if (request.equals("Terminate_Conn")) {
					terminateConnection();
				}
				if (request.equals("Connect")) {
					connect();
				}
				if (request.equals("GetGraph")) {
					GetGraph();
				}
				if (request.equals("Search")) {
					String req = (String) Sinput.readObject();
					String[] SplittedReq = req.split(" ");
					System.out.println("word:" + SplittedReq[0]);
					System.out.println("id:" + SplittedReq[1]);
				}

			} catch (IOException e) {
				return;
			} catch (ClassNotFoundException o) {

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MatlabConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MatlabInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
