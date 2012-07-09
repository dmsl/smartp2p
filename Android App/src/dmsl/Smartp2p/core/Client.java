
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


package dmsl.Smartp2p.core;

import java.net.*;
import java.util.ArrayList;

import java.io.*;

import android.os.Environment;

public class Client {

	//Variable decalration
	private ObjectInputStream Sinput;
	private ObjectOutputStream Soutput;
	private Socket socket;
	private ArrayList<String[]> tree;
	private int id;
	private String pass;

	//Constructor
	public Client(int Id, String pas) {
		id = Id;
		pass = pas;
	}

	//Constructor
	public Client(Socket sock, int Id, String pas)  {

		id = Id;
		pass = pas;
		socket = sock;
		try {
			Sinput = new ObjectInputStream(socket.getInputStream());
			Soutput = new ObjectOutputStream(socket.getOutputStream());
			Soutput.flush();
		} catch (StreamCorruptedException exception) {
			return;
		} catch (IOException exception) {
			return;
		}


	}
	
	//Constructor
	public Client(String ip, int port, int Id, String pas) {

		id = Id;
		pass = pas;
		connect(ip, port);
	}
	
	//Initialize the variable tree
	public void setTree(ArrayList<String[]> t) {
		this.tree = t;
	}
	
	//Sets up a new connection
	public boolean RenewConnection(String ip, int port) {

		try {
			SocketAddress sockaddr = new InetSocketAddress(ip, port);
			socket = new Socket();
			socket.connect(sockaddr);
			if (socket.isConnected())
			{
				Sinput = new ObjectInputStream(socket.getInputStream());
				Soutput = new ObjectOutputStream(socket.getOutputStream());
				Soutput.flush();
				return true;
			}
		} catch (UnknownHostException exception) {
			return false;
		} catch (IOException exception) {
			return false;
		}
		return false;
	}

	//Connects on a Server
	@SuppressWarnings("finally")
	public boolean connect(String ip, int port) {

		socket = null;
		try {
			SocketAddress sockaddr = new InetSocketAddress(ip, port);
			socket = new Socket();
			socket.connect(sockaddr);
			if (socket.isConnected())
			{
				Sinput = new ObjectInputStream(socket.getInputStream());
				Soutput = new ObjectOutputStream(socket.getOutputStream());
				Soutput.flush();
				return true;
			}

		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return false;
		
	}

	
	static public String register(String Name, String pass, String ip, int port)  {
		Socket sock;
		ObjectInputStream Sinp = null;
		ObjectOutputStream Soutp = null;
		try {
			SocketAddress sockaddr = new InetSocketAddress(ip, port);
			sock = new Socket();
			sock.connect(sockaddr);
			if (sock.isConnected())
			{
				Sinp = new ObjectInputStream(sock.getInputStream());
				Soutp = new ObjectOutputStream(sock.getOutputStream());
				Soutp.flush();
				
			}				
			
			Soutp.writeObject("Register");
			Soutp.writeObject(Name);
			Soutp.writeObject(pass);
			Soutp.flush();
			
			String reply = (String) Sinp.readObject();
			if (reply.equals("NOT OK")){
				return "Not Complited1";
			}	
			if (reply.equals("User Name Already Exists")){
				return reply;
			}
			return reply;
		} catch (UnknownHostException exception) {
			return "No Connection With Server";
		} catch (IOException exception) {
			return "No Connection With Server";
		} catch (ClassNotFoundException exception) {
			return "Not Complited4";
		}

	}

	//Return variable tree
	public ArrayList<String[]> peerTree() {
		return this.tree;
	}

	//Change status on server
	public boolean goOnline(String port) {
		try {
			Soutput.writeObject("Connect");
			Soutput.writeObject(Integer.toString(id));
			Soutput.writeObject(pass);
			Soutput.writeObject(port);
			Soutput.flush();

			String reply = (String) Sinput.readObject();
			if (reply.equals("OK")){
				this.pass=pass;
				return true;
			}
			
		} catch (IOException exception) {
			return false;
		} catch (ClassNotFoundException exception) {
			return false;
		}

		return false;
	}

	//Terminate connection with server
	public boolean terminateConnection(int id) {
		try {
			Soutput.writeObject("Terminate_Conn");
			Soutput.writeObject(Integer.toString(id));
			Soutput.flush();

			String reply = (String) Sinput.readObject();
			if (reply.equals("OK"))
				return true;
		} catch (IOException exception) {
			return false;
		} catch (ClassNotFoundException exception) {
			return false;
		}

		return false;
	}
	
	//Receive file from server
	public String receiveFile(String file) throws ClassNotFoundException, StreamCorruptedException, IOException {
		
		
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (StreamCorruptedException e) {
			return "1";
		} catch (IOException e) {
			return "1";
		}
		
		byte[] buffer;
		try {
			buffer = (byte[])ois.readObject();
		} catch (OptionalDataException e) {
			return "2";
		} catch (IOException e) {
			return "2";
		}
		
		FileWriter fstream;
		try {
			fstream = new FileWriter(file);
			/*BufferedWriter out = new BufferedWriter(fstream);
			out.write(buffer.toString());*/
		} catch (IOException e2) {
		}
		
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File (file));
		} catch (FileNotFoundException e) {
			return "3";
		}
		try {
			fos.write(buffer);
			fos.close();
		} catch (IOException e) {
			return "4";
		}
		
		return "0";

	}
	
	//Get graph from Server
	public String getGraph(int id, String Alg, String word) throws IOException, ClassNotFoundException
	{
		Soutput.writeObject("GetGraph");
		Soutput.writeObject(Integer.toString(id));
		Soutput.writeObject(word);
		Soutput.writeObject(Alg);
		
		Soutput.flush();
		
		return this.receiveFile(Environment.getExternalStorageDirectory().toString()+"/SmartP2P/pic" + id +".png");
		//System.out.print("ok");
		
		
		
	}

	//Request an action from server
	public boolean Request(String request) throws IOException, ClassNotFoundException{
		String[] req = request.split(" ");
		
		if (req[0].equals("CONNECT")) {
			return goOnline(req[1]);
		}
		if (req[0].equals("TERMINATE_CONN")) {
			return terminateConnection(id);
		}
		if (req[0].equals("GetTree")) {
			tree = getTree(Integer.parseInt(req[1]), req[2], req[3], req[4], req[5], req[6], req[6]);

			if (tree != null)
				return true;
			return false;

		}
		if (req[0].equals("sendTree")) {
			return sendTree(tree, req[1]);
		}
		if (req[0].equals("GetGraph")) {
			getGraph(Integer.parseInt(req[1]), req[2], req[3]);
		}

		if (req[0].equals("Search")) {
			try {
				this.Search(req[1], this.tree);
			} catch (NumberFormatException exception) {
				return false;
			}
			return true;
		}
		if (req[0].equals("Send_Profile")) {
			return this.sendProfile("profile.txt", socket.getInetAddress()
					.getHostAddress(), socket.getPort());

		}
		if (req[0].equals("Send_Info")) {
			return this.sendInfo(socket.getInetAddress().getHostAddress(),
					socket.getPort(), req[1], req[2]);

		}

		return false;

	}

	//Seng GPS coordinates on server
	public boolean sendInfo(String ip, int port, String Lon, String Lat) {

		Socket sock;
		try {


			Soutput.writeObject("SendInfo");
			Soutput.writeObject(id + "");
			Soutput.flush();

			
			Soutput.writeObject(Lon + " " + Lat);
			Soutput.flush();

			String reply = (String) Sinput.readObject();
			if (reply.equals("OK"))
				return true;
		} catch (UnknownHostException exception) {
			return false;
		} catch (IOException exception) {
			return false;
		} catch (ClassNotFoundException exception) {
			return false;
		}


		return false;

	}

	//Send Profile on Server
	public boolean sendProfile(String file, String ip, int port){

		ArrayList<String[]> aList;
		Socket sock;
		try {
			sock = new Socket(ip, port);

			ObjectOutputStream Soutp = new ObjectOutputStream(
					sock.getOutputStream());
			Soutp.flush();
			ObjectInputStream Sinp = new ObjectInputStream(sock.getInputStream());

			Soutp.writeObject("SendProfile");
			Soutp.writeObject(id + "");
			Soutp.flush();

			aList = IO.getFileContent(file, "\t");
			
			for (int i = 0; i < aList.size(); i++) {
				Soutp.writeObject(aList.get(i)[0]); // quantity
				Soutp.writeObject(aList.get(i)[1]); // description
				Soutp.flush();
			}
			Soutp.writeObject("NULL");
			Soutp.writeObject("NULL");
			Soutp.flush();

			String reply = (String) Sinp.readObject();
			if (reply.equals("OK"))
				return true;
		} catch (UnknownHostException exception) {
			return false;
		} catch (IOException exception) {
			return false;
		} catch (ClassNotFoundException exception) {
			return false;
		}

		return false;

	}

	/* Send tree to server */
	public boolean sendTree(ArrayList<String[]> tree, String word){

		if (this.socket != null && !this.socket.isClosed()
				&& this.socket.isConnected()) {

			try {
				Soutput.writeObject("Search");
				Soutput.writeObject(word);
				Soutput.flush();

				for (int i = 0; i < tree.size(); ++i) {
					Soutput.writeObject(tree.get(i)[0] + " " + tree.get(i)[1] + " "
							+ tree.get(i)[2] + " " + tree.get(i)[3]);
					Soutput.flush();
				}
				Soutput.writeObject("NULL");
				Soutput.flush();
				return true;
			} catch (IOException exception) {
				// TODO Auto-generated catch-block stub.
				exception.printStackTrace();
			}

		}
		return false;
	}

	/* Send tree to server */
	public boolean sendTree(ArrayList<String[]> tree, String ip, int port){

		Socket sock;
		try {
			sock = new Socket(ip, port);
			if (sock == null || sock.isClosed() || !sock.isConnected())
				return false;

			ObjectOutputStream Soutp = new ObjectOutputStream(
					sock.getOutputStream());
			Soutp.flush();
			ObjectInputStream Sinp = new ObjectInputStream(sock.getInputStream());

			Soutp.writeObject("SendTree");
			Soutp.flush();

			for (int i = 0; i < tree.size(); ++i) {
				Soutp.writeObject(tree.get(i)[0] + " " + tree.get(i)[1] + " "
						+ tree.get(i)[2] + " " + tree.get(i)[3] + " "
						+ tree.get(i)[4]);
				Soutp.flush();
			}
			Soutp.writeObject("NULL");
			Soutp.flush();

			sock.close();
		} catch (UnknownHostException exception) {
			return false;
		} catch (IOException exception) {
			return false;
		}

		return true;
	}

	public void printTree(ArrayList<String[]> tree) {
		for (int i = 0; i < tree.size(); ++i) {
			System.out.println(tree.get(i)[0] + " " + tree.get(i)[1] + " "
					+ tree.get(i)[2] + " " + tree.get(i)[3] + " "
					+ tree.get(i)[4]);
		}

	}

	/* get tree from Server* */
	public ArrayList<String[]> getTree(int id, String pass, String decisionMaking, String time, String recall, String Alg, String word) {
		if (this.socket != null && !this.socket.isClosed()
				&& this.socket.isConnected()) {

			try {
				Soutput.writeObject("GetTree");
				Soutput.flush();

				Soutput.writeObject(id+"");
				Soutput.writeObject(pass);
				Soutput.writeObject(decisionMaking);
				Soutput.writeObject(time);
				Soutput.writeObject(recall);
				Soutput.writeObject(Alg);
				if (Alg.equals("RW") || Alg.equals("BFS"))
					Soutput.writeObject(word);
				Soutput.flush();
				
				String str;
				ArrayList<String[]> tree = new ArrayList<String[]>();
				while (true) {
					str = (String) Sinput.readObject();
					if (str.equals("NULL"))
						break;
					tree.add(str.split(" "));
				}
				return tree;
			} catch (IOException exception) {
				return null;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/* get tree from Server* */
	public ArrayList<String[]> getTree(String ip, int port){
		Socket sock;
		try {
			sock = new Socket(ip, port);
			if (sock == null || sock.isClosed() || !sock.isConnected())
				return null;

			ObjectOutputStream Soutp = new ObjectOutputStream(
					sock.getOutputStream());
			Soutp.flush();
			ObjectInputStream Sinp = new ObjectInputStream(sock.getInputStream());

			Soutp.writeObject("GetTree");
			Soutp.flush();

			String str;
			ArrayList<String[]> tree = new ArrayList<String[]>();
			while (true) {
				str = (String) Sinp.readObject();
				if (str.equals("NULL"))
					break;
				tree.add(str.split(" "));
			}

			sock.close();
		} catch (UnknownHostException exception) {
			return null;
		} catch (IOException exception) {
			return null;
		} catch (ClassNotFoundException exception) {
			return null;
		}
		return tree;
	}

	
	public void Search(String word, ArrayList<String[]> tree){

		try {
			Soutput.writeObject("Search");
			Soutput.writeObject(word + " " + tree.get(0)[0]);
			Soutput.flush();
		} catch (IOException e) {
		}

	}
	
	public void answer (int res)
	{
		try {
			Soutput.writeObject("Answer");
			Soutput.writeObject(id + " " + res);
			Soutput.flush();
		} catch (IOException e) {
		}
	}
	
	public void answer (ArrayList<String> res)
	{
		try {
			Soutput.writeObject("Answer");
			Soutput.flush();
			
			for (int i=0; i<res.size(); ++i)
			{
				Soutput.writeObject(res.get(i));
				Soutput.flush();
			}
			Soutput.writeObject("NULL");
			Soutput.flush();
		} catch (IOException e) {
		}
	}


}