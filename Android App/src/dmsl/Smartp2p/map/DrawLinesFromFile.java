
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


package dmsl.Smartp2p.map;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class DrawLinesFromFile {

	public static void drawLinesInAllTree (ArrayList<String[]> geoFile ,
			ArrayList<String[]> treeFile, MapView mapView1)
	{
		int parent=0;
		int child=0;
		for (int i = 0; i < geoFile.size(); ++i) {
			
			parent = Integer.parseInt(geoFile.get(i)[0]);
			
			GeoPoint point1 = new GeoPoint(
					(int) ( Double.parseDouble(geoFile.get(i)[3]) * 1E6),
					(int) ( Double.parseDouble(geoFile.get(i)[2]) * 1E6));
			
			for (int j = 1; j < geoFile.size(); ++j) {
				
				child = Integer.parseInt(geoFile.get(j)[1]);
				
				if (parent == child){
					
					GeoPoint point2 = new GeoPoint(
							(int) ( Double.parseDouble(geoFile.get(j)[3]) * 1E6),
							(int) ( Double.parseDouble(geoFile.get(j)[2]) * 1E6));
					
					System.out.println("Parent: " + parent + "  " + "Child" + child );
					mapView1.getOverlays().add(new DrawPathOverlay(point1, point2, 0));
				}
			}
		}
	}
	

	public static ArrayList<String[]> getFileContent1(String file) {

		String strLine;

		ArrayList<String[]> str = new ArrayList<String[]>();

		try {

			FileInputStream fstream = new FileInputStream(file);

			DataInputStream in = new DataInputStream(fstream);

			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			while ((strLine = br.readLine()) != null) {

				str.add(strLine.split("	"));

			}

			in.close();

		} catch (Exception e) {

			System.err.println("Error: " + e.getMessage());

		}

		return str;

	}
	
	public static ArrayList<String[]> getFileContent(String file) {

		String strLine;

		ArrayList<String[]> str = new ArrayList<String[]>();

		try {

			FileInputStream fstream = new FileInputStream(file);

			DataInputStream in = new DataInputStream(fstream);

			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			while ((strLine = br.readLine()) != null) {

				str.add(strLine.split(" "));

			}

			in.close();

		} catch (Exception e) {

			System.err.println("Error: " + e.getMessage());

		}

		return str;

	}
	
}
