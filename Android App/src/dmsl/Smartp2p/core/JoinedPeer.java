
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.Socket;
import java.util.ArrayList;

import dmsl.Smartp2p.app.Message;


class JoinedPeer extends Thread {
	
	//Variable declaration
	byte[] data;
	Socket socket;
	ObjectInputStream Sinput;
	ObjectOutputStream Soutput;
	String id;
	ArrayList<String[]> tree;

	//Sends the QRT to the user
	public void sendTree(ArrayList<String[]> tree) throws IOException {
		for (int i = 0; i < tree.size(); ++i) {
			Soutput.writeObject(tree.get(i)[0] + " " + tree.get(i)[1] + " "
					+ tree.get(i)[2] + " " + tree.get(i)[3]/* + " "
					+ tree.get(i)[4]*/);
			Soutput.flush();
		}
		Soutput.writeObject("NULL");
		Soutput.flush();
	}
	
	//Print QRT on Server's console
	public void printTree(ArrayList<String[]> tree) {
		for (int i = 0; i < tree.size(); ++i) {
			System.out.println(tree.get(i)[0] + " " + tree.get(i)[1] + " "
					+ tree.get(i)[2] + " " + tree.get(i)[3] + " "
					+ tree.get(i)[4]);
		}

	}

	//Receive the QRT from another Server
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

		//printTree(tree);
		return tree;

	}

	//Contructor
	JoinedPeer (Socket socket, int id1) throws IOException {

		this.socket = socket;
		Soutput = new ObjectOutputStream(socket.getOutputStream());
		Soutput.flush();
		Sinput = new ObjectInputStream(socket.getInputStream());
		id = String.valueOf(id1);
	}

	//Search localy for results in a query from other user. if there is an answer connects directly with the user and sends
	//the results
	public void Search(String word){

		for (int i=0; i<tree.size(); ++i)
		{
			if (tree.get(i)[1].equals(id))
			{
				Client c = new Client (tree.get(i)[2], Integer.parseInt(tree.get(i)[3]), Integer.parseInt(id), "");
				c.sendTree(tree, word);
			}
		}

		ArrayList <String> result = IO.find("/mnt/sdcard/SmartP2P/profile"+id+".txt", word);

		if (result!=null && result.size()>0)
		{
			Client c2 = new Client (tree.get(0)[2], Integer.parseInt(tree.get(0)[3]), Integer.parseInt(id), "");
			c2.answer(result);
		}

	}
	
	
	//the user receives the results from other users to his query
	public void answer () throws OptionalDataException, ClassNotFoundException, IOException
	{
		while (true)
		{
			String res = (String) Sinput.readObject();
			if (res.equals("NULL"))
				break;
			Message.change(res);
			Message.Sum(/*num[1]*/);
		}
	}
	
	public void run() {

		while (true) {
			try {

				String request = (String) Sinput.readObject();
				System.out.println(request + ": ");

				if (request.equals("Search")) {
					String req = (String) Sinput.readObject();
					
					
					tree = this.receiveTree();
					Search(req);
				}
				
				if (request.equals("Answer"))
				{
					answer ();
				}

			} catch (IOException e) {
				return;
			} catch (ClassNotFoundException o) {

			}
		}

	}

}