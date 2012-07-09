
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
import java.util.ArrayList;

import dmsl.Smartp2p.core.Client;
import dmsl.Smartp2p.core.IO;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ResultList extends ListActivity{

	//Variable declaration
	int id;
	String pass;
	String ip;
	int port;
	String Algorithm;	
	String time, recall, decisionMaking, word;
	Client c;		
	Context context;
	int duration;	
	Button printButton;	
	ArrayList <String []> tree;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        
		//Get variables from previews activity
        Bundle extra = getIntent().getExtras();
		if (extra != null) {
			ip = extra.getString("ip");
			port = Integer.parseInt(extra.getString("port"));
			id = Integer.parseInt(extra.getString("id"));
			pass = extra.getString("pass");
			
			time = extra.getString("time");
			recall = extra.getString("recall");
			decisionMaking = extra.getString("dec");
			word = extra.getString("word");
			Algorithm = extra.getString("Alg");
			
			context = getApplicationContext();
			duration = Toast.LENGTH_LONG;
		}
        
		printBut();
		
		connect();
		
		requestForTree ();
		
    }
    
	//Connect on the SmartP2P Server
    void connect()
    {
    	c = new Client(id, pass);
		if (!c.connect(ip, port)) {
			Toast toast = Toast.makeText(context,
					"No Connection Available On Server", duration);
			toast.show();
			finish();
		}
    }
    
	
	//Request for the QRT from the SmartP2P Server
    void requestForTree ()
    {
    	tree = c.getTree(id, pass, decisionMaking, time, recall, Algorithm, word);
    	for (int i=1; i<tree.size(); ++i)
		{
			if (tree.get(i)[1].equals(id+""))
			{				
				Client c = new Client (tree.get(i)[2], Integer.parseInt(tree.get(i)[3]), id, "");
				c.sendTree(tree, word);
				/*Toast toast = Toast.makeText(context,
						tree.get(i)[0] + "  " + tree.get(i)[2] + "  " + tree.get(i)[3], duration);
				toast.show();*/
			}
		}
    	String s;
    	ArrayList<String[]> map = new ArrayList<String[]> ();
    	
    	for (int j = 0; j < tree.size(); j++)
    	{
    		s = tree.get(j)[0]/*parentId*/+ " " + tree.get(j)[1]/*childId*/ + " " + tree.get(j)[4]/*lon*/ + " " + tree.get(j)[5]/*lat*/;    		
    		map.add(s.split(" "));
    	}
    	
    	try {
			IO.writeNewFile(map, "/mnt/sdcard/SmartP2P/map.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
	//fills the rows of the ListAdapter from the Message.msg
    void print ()
    {
    	String m = null;
    	m = tree.get(0)[0] + " " + tree.get(0)[1] + " " + tree.get(0)[2] + " " + tree.get(0)[3] + "\n";
    	for (int i=1; i<tree.size(); ++i)
    		m= m + tree.get(i)[0] + " " + tree.get(i)[1] + " " + tree.get(i)[2] + " " + tree.get(i)[3] + "\n";
    	this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Message.msg.split("\n")));
    }
    
	//preview the results on the screen by touching the result button
    void printBut()
    {
    	printButton = (Button) findViewById(R.id.button1);
		printButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextView resTxtView = (TextView) findViewById(R.id.textView1);
				resTxtView.setText(Message.Res + "  Results: ");
				print ();

			}
		});
    }
    
    
    //creates an option menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.result_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.map:
			Intent map = new Intent(ResultList.this, Maps.class);
			startActivity(map);
			return true;
	        
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

    
    
}
