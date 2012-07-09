
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


package dmsl.Smartp2p.app;

import java.util.ArrayList;

public class Message {

	//Global Variable Declaration
	public static String msg; //Stores the titles the Query User Receives
	public static int Res; //Stores the numbers of titles the Query User Receives
	public static String serverPort;
	public static ArrayList<String[]> gps = new ArrayList<String []> ();
	
	public static synchronized void change(String ca) {
		if (ca.equals("NULL")) {
			
			//initialize variables
			msg="";
			Res=0;

		} else if (ca.equals("print file")) {

		} else{
			//concutinating the string message
			msg = ca + "\n" + msg;
		}
	}
	
	public static synchronized void Sum() {

		Res++; //increase variable Res
	}
	
	public static synchronized void port (String port)
	{
		serverPort = port;
	}
	
	public static synchronized void GPS (String ca) {
		if (ca.equals("NULL")) {
			
			gps.clear();
		
		} else {
			
			gps.add(ca.split(" "));
			
		}
	}
	
}
