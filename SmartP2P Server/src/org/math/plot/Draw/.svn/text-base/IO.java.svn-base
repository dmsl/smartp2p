package org.math.plot.Draw;





import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class IO {

	
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

	public static void displayFileContent(ArrayList<String[]> str) {

		for (int i = 0; i < str.size(); ++i) {

			//for (int j = 0; j < str.get(i).length; ++j) 
			{

				System.out.print(str.get(i)[4] + " ");

			}

			System.out.println();

		}

	}
	
	public static ArrayList <String> find(String path, String word) {

		FileInputStream fstream;
		ArrayList <String> res =  new ArrayList<String> ();
		int count = 0;
		try {
			fstream = new FileInputStream(path);
			
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return res;

	}


	
}
