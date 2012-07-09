
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
import java.util.Calendar;

import dmsl.Smartp2p.core.Client;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class RegisterActivity extends Activity {

	//Variable Declaration
	private TextView txIp;
	private TextView txPort;
	private TextView txUname;
	private TextView txPass;
	private Button regButton;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        regBut();
    }    
    
	//Actions to be done by touching the register button
	void regBut() {

		regButton = (Button) findViewById(R.id.button1);
		regButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			
				//Get the input from textboxes
				txIp = (TextView) findViewById(R.id.editText1);
				String ip = txIp.getText().toString();
				txPort = (TextView) findViewById(R.id.editText2);
				String port = txPort.getText().toString();
				txUname = (TextView) findViewById(R.id.editText3);
				String uname = txUname.getText().toString();
				txPass = (TextView) findViewById(R.id.editText4);
				String pass = txPass.getText().toString();
				
				
				try {
					//calls the static function register from Client Class for the
					//registration of the user to the system 
					String reply = Client.register(uname,pass,ip, Integer.parseInt(port));
					if (reply.equals("NOT OK")) //case function return the message "NOT OK"
					{
						Context context = getApplicationContext();
						CharSequence text = reply;
						int duration = Toast.LENGTH_LONG;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}
					if (reply.equals("User Name Already Exists")) //case function return the message "User Name Already Exists"
					{
						Context context = getApplicationContext();
						CharSequence text = reply;
						int duration = Toast.LENGTH_LONG;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}
					if (reply.equals("No Connection With Server"))  //case function return the message "No Connection With Server"
					{
						Context context = getApplicationContext();
						CharSequence text = reply;
						int duration = Toast.LENGTH_LONG;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
					}
					else //case registration succesfull
					{
						Context context = getApplicationContext();
						CharSequence text = "Your User Id: " + reply;
						int duration = Toast.LENGTH_LONG;

						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
						finish();
					}
					
				} catch (NumberFormatException exception) {

				}

			}
		});
	}
    
}