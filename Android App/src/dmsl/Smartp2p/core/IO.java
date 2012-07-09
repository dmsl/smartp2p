
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class IO {

	//Reads a file and returns an ArrayList with the content of the file.
	public static ArrayList<String[]> getFileContent(String file, String del) {

		String strLine;

		ArrayList<String[]> str = new ArrayList<String[]>();

		try {

			FileInputStream fstream = new FileInputStream(file);

			DataInputStream in = new DataInputStream(fstream);

			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			while ((strLine = br.readLine()) != null) {

				str.add(strLine.split(del));

			}

			in.close();

		} catch (Exception e) {

			System.err.println("Error: " + e.getMessage());

		}

		return str;

	}

	//display the content of an ArrayList
	public static void displayFileContent(ArrayList<String[]> str) {

		for (int i = 0; i < str.size(); ++i) {

			//for (int j = 0; j < str.get(i).length; ++j) 
			{

				System.out.print(str.get(i)[4] + " ");

			}

			System.out.println();

		}

	}
	
	//Search a spesific word in an ArrayList and returns another ArrayList with the results founded
	public static ArrayList <String> find(String path, String word) {

		FileInputStream fstream;
		ArrayList <String> res =  new ArrayList<String> ();
		int count = 0;
		try {
			fstream = new FileInputStream(path);
			
			if (fstream==null)
				return null;
			
			DataInputStream in = new DataInputStream(fstream);

			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			String [] temp;
			while ((strLine = br.readLine()) != null) 
			{

				temp = strLine.split(" ");
				for (int i=0; i<temp.length; ++i)
				{
					String a;
					a = temp[i].toLowerCase();
					if (a.contains(word))
					{
						count++;
						res.add(strLine);
						break;
					}
				}

			}

			in.close();

		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}


		return res;

	}

	//Writes the content of an ArrayList into a File
	public static void writeNewFile (ArrayList<String[]> str, String a) throws IOException
	{
		FileWriter fstream = new FileWriter(a);

		BufferedWriter out = new BufferedWriter(fstream);

		for (int i = 0; i < str.size(); ++i) {
			
			for (int j =0; j<str.get(i).length; ++j)
			{
				if (j!=0)
					out.write(" ");
				out.write(str.get(i)[j]);
			}
			out.write(" " + i + "\n");

		}
		out.close();
		
	}

	
}
