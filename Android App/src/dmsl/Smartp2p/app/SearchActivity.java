
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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import dmsl.Smartp2p.core.Client;
import dmsl.Smartp2p.core.IO;
import dmsl.Smartp2p.core.Server;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends Activity {

	//Variable declaration
	String ip;
	int port;
	int id;
	int myPort;
	String pass;
	Client c;
	Context context;
	int duration;
	private Button GoButton;
	private RadioButton RButton1;
	private RadioButton RButton2;
	private RadioButton RButton3;
	private RadioButton RButton4;
	Server myServer = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		
		//Get variables from previews activity
		Bundle extra = getIntent().getExtras();
		if (extra != null) {
			ip = extra.getString("ip");
			port = Integer.parseInt(extra.getString("port"));
			id = Integer.parseInt(extra.getString("id"));
			pass = extra.getString("pass");
			myPort = Integer.parseInt(extra.getString("myPort")); // local server's port

			context = getApplicationContext();
			duration = Toast.LENGTH_LONG;
			if (ConnectToServer()){ //connects on the SmartP2P server
				myServer = new Server (44001, id); //start the local server
				if (!myServer.isBound())
					finish();
				this.myPort = 44001;
				
				goBut();
			}
			else
				finish();

			
		}

	}
	
	//terminate the connection with the SmartP2P server
	void terminate() {

		if (id != 0)
			c.terminateConnection(id);

		this.stopService(this.getIntent());
		this.finish();

	}


	//connects on the SmartP2P server. The user Sends his profile and gps coordinates
	boolean ConnectToServer() {
		c = new Client(id, pass);
		if (!c.connect(ip, port)) { //connect on SmartP2P server
			Toast toast = Toast.makeText(context,
					"No Connection Available On Server", duration);
			toast.show();
			return false;
		}
		
		//change online status (sends profile and gps coordinates on SmartP2P server)
		if (c.goOnline(myPort + "")) { 
			if (!c.sendProfile("/mnt/sdcard/SmartP2P/profile_"+id+".txt",ip,port))
			{
				Toast toast = Toast.makeText(context,
						"Cannot Upload Profile", duration);
				toast.show();
				terminate();
			}
						
			ArrayList <String []> gps = IO.getFileContent("/mnt/sdcard/SmartP2P/gps.txt", "\t");
			
			if (!c.sendInfo(ip, port, gps.get(id-1)[1], gps.get(id-1)[0]))
			{
				Toast toast = Toast.makeText(context,
						"Cannot Send GPS Info", duration);
				toast.show();
				terminate();
			}

		} else {
			Toast toast = Toast.makeText(context,
					"No Connection Available On Database", duration);
			toast.show();
			return false;
		}
		return true;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.terminate: 
		//terminate the connection with the SmartP2P server
			try {
				this.myServer.peerSocket.close();
				this.myServer.stop();
			} catch (IOException e) {

				e.printStackTrace();
			}
			terminate(); //
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		try {
			this.myServer.peerSocket.close();
			this.myServer.stop();
		} catch (IOException e) {
			
		}
		terminate();
	}
	
	//actions to be done when touching the go button
	void goBut() {

		GoButton = (Button) findViewById(R.id.button1);
		GoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				Message.change("NULL");  //Reset the global counters (number of results, the result string)
				TextView txWord = (TextView) findViewById(R.id.editText1);
				String word = txWord.getText().toString();
				RButton1 = (RadioButton) findViewById(R.id.radio0);
				RButton2 = (RadioButton) findViewById(R.id.radio1);
				RButton3 = (RadioButton) findViewById(R.id.radio2);
				RButton4 = (RadioButton) findViewById(R.id.radio3);
				
				
				//Algorithm selection
				if (RButton3.isChecked()) //MOEAD is selected
				{
					//Sends the variables ip, port, id, word, the agorithm selected to the Search1Activity
					Intent startSearch1 = new Intent(SearchActivity.this, Search1Activity.class);
					Bundle b = new Bundle();
					b.putString("ip", ip);
					b.putString("port", port+"");
					b.putString("id", id+"");
					b.putString("pass", pass);
					b.putString("word", word);
					b.putString("Alg", "MOEAD");
					startSearch1.putExtras(b);
					startActivity(startSearch1);
				}
				if (RButton1.isChecked())//RW is selected
				{
					//Sends the variables ip, port, id, word, the agorithm selected to the Search1Activity
					Intent startSearch1 = new Intent(SearchActivity.this, ResultList.class);
					Bundle b = new Bundle();
					b.putString("ip", ip);
					b.putString("port", port+"");
					b.putString("id", id+"");
					b.putString("pass", pass);
					b.putString("word", word);
					b.putString("Alg", "RW");
					startSearch1.putExtras(b);
					startActivity(startSearch1);
				}
				if (RButton2.isChecked())//BFS is selected
				{
					//Sends the variables ip, port, id, word, the agorithm selected to the Search1Activity
					Intent startSearch1 = new Intent(SearchActivity.this, ResultList.class);
					Bundle b = new Bundle();
					b.putString("ip", ip);
					b.putString("port", port+"");
					b.putString("id", id+"");
					b.putString("pass", pass);
					b.putString("word", word);
					b.putString("Alg", "BFS");
					startSearch1.putExtras(b);
					startActivity(startSearch1);
				}
				
				if (RButton4.isChecked())//NSGAII is selected
				{
					//Sends the variables ip, port, id, word, the agorithm selected to the Search1Activity
					Intent startSearch1 = new Intent(SearchActivity.this, Search1Activity.class);
					Bundle b = new Bundle();
					b.putString("ip", ip);
					b.putString("port", port+"");
					b.putString("id", id+"");
					b.putString("pass", pass);
					b.putString("word", word);
					b.putString("Alg", "NSGAII");
					startSearch1.putExtras(b);
					startActivity(startSearch1);
				}
				
			}
		});
	}
	
}
