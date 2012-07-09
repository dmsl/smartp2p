
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

import java.io.File;
import java.io.IOException;

import dmsl.Smartp2p.core.Client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Search1Activity extends Activity implements SeekBar.OnSeekBarChangeListener {
	
	//Variable declaration
	ImageView im;
    SeekBar mSeekBar;
    TextView mProgressText;
    Double time, recall;
    int decisionMaking = 0;
    private Button GoButton;
    String word;
	String ip;
	int port;
	int id;
	String pass; 
	String Algorithm;
	Client c;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search1)
		;
		//Get variables from previews activity
		Bundle extra = getIntent().getExtras();
		if (extra != null) {
			ip = extra.getString("ip");
			port = Integer.parseInt(extra.getString("port"));
			id = Integer.parseInt(extra.getString("id"));
			pass = extra.getString("pass");
			word = extra.getString("word");
			Algorithm = extra.getString("Alg");
		}
		
		c = new Client(id, pass); //Creates new Client
		if (!c.connect(ip, port)) { //Connects with SmartP2P server
			finish();
		}
		try {
			c.getGraph(id, Algorithm, word); //asks for the graph that represets the Pareto Front
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		im = (ImageView) findViewById(R.id.imageView1);
		
		
		File imgFile = new  File(Environment.getExternalStorageDirectory().toString()+"/SmartP2P/pic" + id +".png");
		if(imgFile.exists()){

		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

		    im.setImageBitmap(myBitmap);	    

		}
        mSeekBar = (SeekBar)findViewById(R.id.seekBar1); //seek bar declaration which represents the persentage of time and recall

        mSeekBar.setOnSeekBarChangeListener(this);
        mProgressText = (TextView)findViewById(R.id.progress);
        this.mProgressText.setText(null);
        time=0.5;
        recall=0.5;
        
        
        CheckBox repeatChkBx = ( CheckBox ) findViewById( R.id.checkBox1); 
        repeatChkBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    decisionMaking=1;
                }
                else
                	decisionMaking=0;

            }
        });
        
        GoBut();
		
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
    	recall=Double.parseDouble(progress+"")/100;
    	time=Double.parseDouble((100-progress)+"")/100;
        mProgressText.setText(null);
		
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	//actions to be done when touching the go button
	void GoBut() {

		GoButton = (Button) findViewById(R.id.button1);
		GoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Message.change("NULL"); //Reset the global counters (number of results, the result string)
				
				//Sends the variables ip, port, pass, id, word, the agorithm selected, the value of time, recall, decision making to the ResultList activity
				Intent getTree = new Intent(Search1Activity.this, ResultList.class);
				Bundle b = new Bundle();
				b.putString("ip", ip);
				b.putString("port", port+"");
				b.putString("id", id+"");
				b.putString("pass", pass);
				b.putString("dec", String.valueOf(decisionMaking));
				b.putString("time", String.valueOf(time));
				b.putString("recall", String.valueOf(recall));
				b.putString("word", word);
				b.putString("Alg", Algorithm);
				getTree.putExtras(b);
				startActivity(getTree);

			}
		});
	}

}
