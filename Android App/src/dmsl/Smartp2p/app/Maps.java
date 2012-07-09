
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
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.MapView.LayoutParams;

import dmsl.Smartp2p.core.IO;
import dmsl.Smartp2p.map.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

public class Maps extends MapActivity {

	//Variable declaration
	MapView mapView;
	MapController mc;
	GeoPoint p;
	ArrayList<String[]> geoFile;
	ArrayList<String[]> treeFile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		geoFile = IO.getFileContent("/mnt/sdcard/SmartP2P/map.txt", " "); //reads the coordinates from file

		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		mc = mapView.getController();

		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		double lat = Double.parseDouble(geoFile.get(0)[3]);
		double lng = Double.parseDouble(geoFile.get(0)[2]);

		p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6)); //creates a GeoPoint

		mc.animateTo(p);
		mc.setZoom(10);
		DrawLinesFromFile.drawLinesInAllTree(geoFile, treeFile, mapView);
		Drawable defaultIcon = this.getResources().getDrawable(R.drawable.pin);

		MyItemizedOverlay mO = new MyItemizedOverlay(defaultIcon, this);

		OverlayItem item = new OverlayItem(p, "Query Peer: ", geoFile.get(0)[0]);
		item.setMarker(defaultIcon);
		mO.addOverlay(item);
		listOfOverlays.add(mO);

		for (int i = 1; i < geoFile.size(); ++i) {

			if (Integer.parseInt(geoFile.get(i)[1]) != -1) 
			{
				lat = Double.parseDouble(geoFile.get(i)[3]);
				lng = Double.parseDouble(geoFile.get(i)[2]);
				p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

				item = new OverlayItem(p, "Peer: ", geoFile.get(i)[0]);
				mO.addOverlay(item);
				listOfOverlays.add(mO);
			}
		}

		mapView.invalidate();

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public class MapOverlay extends Overlay implements OnTabChangeListener,
			OnTouchListener {
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// ---translate the GeoPoint to screen pixels---
			Point screenPts = new Point();
			mapView.getProjection().toPixels(p, screenPts);

			return true;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			// ---when user lifts his finger---
			if (event.getAction() == 1) {
				GeoPoint p = mapView.getProjection().fromPixels(
						(int) event.getX(), (int) event.getY());

			}
			return false;
		}

		@Override
		public void onTabChanged(String tabId) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		MapController mc = mapView.getController();
		switch (keyCode) {
		case KeyEvent.KEYCODE_3:
			mc.zoomIn();
			break;
		case KeyEvent.KEYCODE_1:
			mc.zoomOut();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

}