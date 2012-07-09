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

import java.net.*;
import java.util.ArrayList;

import java.io.*;

public class Client {

	//Variable Declaration
	private ObjectInputStream Sinput;
	private ObjectOutputStream Soutput;
	private Socket socket;
	private ArrayList<String[]> tree;
	private int id;

	//Constructor
	Client(int Id) {
		id = Id;
	}

	//Constructor
	Client(Socket sock, int Id) throws IOException {

		id = Id;
		socket = sock;
		Sinput = new ObjectInputStream(socket.getInputStream());
		Soutput = new ObjectOutputStream(socket.getOutputStream());

	}

	//Constructor
	Client(String ip, int port, int Id) {

		id = Id;
		connect(ip, port);
	}
	
	//Initialize the variable tree
	public void setTree (ArrayList<String[]> t)
	{
		this.tree = t;
	}
	
	//Sets up a new connection
	public void RenewConnection(String ip, int port) throws IOException {

		socket = new Socket(ip, port);
		Sinput = new ObjectInputStream(socket.getInputStream());
		Soutput = new ObjectOutputStream(socket.getOutputStream());

	}

	//Connects on a Server
	public boolean connect(String ip, int port) {

		try {
			socket = new Socket(ip, port);
			Sinput = new ObjectInputStream(socket.getInputStream());
			Soutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	//Terminate connection with server
	public void terminateConnection() throws IOException {
		Soutput.writeObject("Terminate");
		Soutput.flush();

		if (socket != null) {
			socket.close();
			socket = null;
		}
	}

	public void register() throws IOException {
		Soutput.writeObject("Register");
		Soutput.writeObject(id + "");
		Soutput.flush();
	}

	public ArrayList<String[]> peerTree() {
		return this.tree;
	}
	
	//Request an action from server
	public boolean Request(String request) throws IOException,
			ClassNotFoundException {
		String[] req = request.split(" ");
		if (req[0].equals("GET")) {
			return receiveFile("a.txt");
		}
		if (req[0].equals("SEND")) {
			return sendFile("a.txt");
		}
		if (req[0].equals("GetTree")) {
			tree = getTree();
			//printTree (tree);
			if (tree != null)
				return true;
			return false;
			// printTree (tree);
		}
		if (req[0].equals("sendTree")) {
			return sendTree(tree);
		}
		if (req[0].equals("Terminate")) {
			this.terminateConnection();
			return true;
		}
		if (req[0].equals("Register")) {
			this.register();
			return true;
		}
		if (req[0].equals("Search")) {
			this.Search(req[1], this.tree);
			return true;
		}

		return false;

	}

	/* Send tree to server */
	public boolean sendTree(ArrayList<String[]> tree)
			throws UnknownHostException, IOException {

		if (this.socket != null && !this.socket.isClosed()
				&& this.socket.isConnected()) {

			Soutput.writeObject("SendTree");
			Soutput.flush();

			for (int i = 0; i < tree.size(); ++i) {
				Soutput.writeObject(tree.get(i)[0] + " " + tree.get(i)[1] + " "
						+ tree.get(i)[2] + " " + tree.get(i)[3] + " "
						+ tree.get(i)[4]);
				Soutput.flush();
			}
			Soutput.writeObject("NULL");
			Soutput.flush();
			return true;
		}
		return false;
	}

	/* Send tree to server */
	public boolean sendTree(ArrayList<String[]> tree, String ip, int port)
			throws UnknownHostException, IOException {

		Socket sock = new Socket(ip, port);

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
		Soutp.writeObject("Terminate");
		Soutp.flush();
		sock.close();
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
	public ArrayList<String[]> getTree() throws UnknownHostException,
			IOException, ClassNotFoundException {
		if (this.socket != null && !this.socket.isClosed()
				&& this.socket.isConnected()) {

			Soutput.writeObject("GetTree");
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
		}
		return null;
	}

	/* get tree from Server* */
	public ArrayList<String[]> getTree(String ip, int port)
			throws UnknownHostException, IOException, ClassNotFoundException {
		Socket sock = new Socket(ip, port);

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
		Soutp.writeObject("Terminate");
		Soutp.flush();
		sock.close();
		return tree;
	}

	/* Send file to server */
	public boolean sendFile(String file) throws UnknownHostException,
			IOException {
		if (this.socket != null && !this.socket.isClosed()
				&& this.socket.isConnected()) {
			Socket sock = new Socket(socket.getInetAddress().getHostAddress(),
					socket.getPort());

			if (sock == null || sock.isClosed() || !sock.isConnected())
				return false;

			ObjectOutputStream Soutp = new ObjectOutputStream(
					sock.getOutputStream());
			Soutp.flush();
			ObjectInputStream Sinp = new ObjectInputStream(
					sock.getInputStream());

			Soutp.writeObject("Send");
			Soutp.flush();

			BufferedInputStream bis;
			BufferedOutputStream bos;
			byte[] byteArray;
			int in;
			try {
				bis = new BufferedInputStream(new FileInputStream(file));
				bos = new BufferedOutputStream(sock.getOutputStream());
				byteArray = new byte[8192];
				while ((in = bis.read(byteArray)) != -1) {
					bos.write(byteArray, 0, in);
				}
				bis.close();
				bos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Soutp.writeObject("Terminate");
			Soutp.flush();
			sock.close();
			return true;
		}
		return false;
	}

	/* Send file to server */
	public boolean sendFile(String file, String ip, int port)
			throws UnknownHostException, IOException {

		Socket sock = new Socket(ip, port);

		if (sock == null || sock.isClosed() || !sock.isConnected())
			return false;

		ObjectOutputStream Soutp = new ObjectOutputStream(
				sock.getOutputStream());
		Soutp.flush();
		ObjectInputStream Sinp = new ObjectInputStream(sock.getInputStream());

		Soutp.writeObject("Send");
		Soutp.flush();

		BufferedInputStream bis;
		BufferedOutputStream bos;
		byte[] byteArray;
		int in;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			bos = new BufferedOutputStream(sock.getOutputStream());
			byteArray = new byte[8192];
			while ((in = bis.read(byteArray)) != -1) {
				bos.write(byteArray, 0, in);
			}
			bis.close();
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Soutp.writeObject("Terminate");
		Soutp.flush();
		sock.close();
		return true;
	}

	/* request file from server */
	public boolean receiveFile(String file) throws UnknownHostException,
			IOException {

		if (this.socket != null && !this.socket.isClosed()
				&& this.socket.isConnected()) {
			Socket sock = new Socket(socket.getInetAddress().getHostAddress(),
					socket.getPort());

			if (sock == null || sock.isClosed() || !sock.isConnected())
				return false;

			ObjectOutputStream Soutp = new ObjectOutputStream(
					sock.getOutputStream());
			Soutp.flush();
			ObjectInputStream Sinp = new ObjectInputStream(
					sock.getInputStream());
			Soutp.writeObject("Get");
			Soutp.flush();

			BufferedInputStream bis;
			BufferedOutputStream bos;
			byte[] data;
			int in;

			try {
				byte[] receivedData = new byte[8192];
				bis = new BufferedInputStream(sock.getInputStream());
				bos = new BufferedOutputStream(new FileOutputStream(file));
				while ((in = bis.read(receivedData)) != -1) {
					bos.write(receivedData, 0, in);
				}
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Soutp.writeObject("Terminate");
			Soutp.flush();
			sock.close();
			return true;
		}
		return false;
	}

	/* request file from server */
	public boolean receiveFile(String file, String ip, int port)
			throws UnknownHostException, IOException {

		Socket sock = new Socket(ip, port);

		if (sock == null || sock.isClosed() || !sock.isConnected())
			return false;

		ObjectOutputStream Soutp = new ObjectOutputStream(
				sock.getOutputStream());
		Soutp.flush();
		ObjectInputStream Sinp = new ObjectInputStream(sock.getInputStream());
		Soutp.writeObject("Get");
		Soutp.flush();

		BufferedInputStream bis;
		BufferedOutputStream bos;
		byte[] data;
		int in;

		try {
			byte[] receivedData = new byte[8192];
			bis = new BufferedInputStream(sock.getInputStream());
			bos = new BufferedOutputStream(new FileOutputStream(file));
			while ((in = bis.read(receivedData)) != -1) {
				bos.write(receivedData, 0, in);
			}
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Soutp.writeObject("Terminate");
		Soutp.flush();
		sock.close();
		return true;
	}

	public void Search(String word, ArrayList<String[]> tree)
			throws NumberFormatException, IOException {
		
				try {
					Soutput.writeObject("Search");
					Soutput.writeObject(word + " " + tree.get(0)[0]);
					Soutput.flush();
				} catch (IOException e) {
				}
	}

	//thewreite oti o QP einai o 0
	public void AnswerToQP(String ans) throws NumberFormatException,
			IOException {
		this.connect(tree.get(0)[3], Integer.parseInt(tree.get(0)[4]));
		try {
			Soutput.writeObject("Answer");
			Soutput.writeObject(ans);
			Soutput.flush();
		} catch (IOException e) {
		}

	}

}