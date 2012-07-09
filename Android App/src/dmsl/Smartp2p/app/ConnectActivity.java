
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
import java.net.Socket;
import java.net.UnknownHostException;

import dmsl.Smartp2p.core.Client;
import dmsl.Smartp2p.core.KeepAlivePeer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectActivity extends Activity {

	//Variable Derclaration
	private TextView txIp;
	private TextView txPort;
	private TextView txId;
	private TextView txPass;
	private TextView txMyPort;
	private Button connectButton;
	KeepAlivePeer k;
	Client c;
	String id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect);
		connectBut();
	}

	//actions to be done by touching the connect button
	void connectBut() {

		connectButton = (Button) findViewById(R.id.button1);
		connectButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			
				//reads the input from the textboxes
				txIp = (TextView) findViewById(R.id.editText1);
				String ip = txIp.getText().toString();
				txPort = (TextView) findViewById(R.id.editText2);
				String port = txPort.getText().toString();
				txId = (TextView) findViewById(R.id.editText3);
				id = txId.getText().toString();
				txPass = (TextView) findViewById(R.id.editText4);
				String pass = txPass.getText().toString();				
				
				//Sends the variables ip, port, pass, id, user's port to the SearchActivity 
				Intent startSearch = new Intent(ConnectActivity.this, SearchActivity.class);
				Bundle b = new Bundle();
				b.putString("ip", ip);
				b.putString("port", port);
				b.putString("id", id);
				b.putString("pass", pass);
				b.putString("myPort", "44001"/*myPort*/);
				startSearch.putExtras(b);
				startActivity(startSearch);
			}
		});
	}


}