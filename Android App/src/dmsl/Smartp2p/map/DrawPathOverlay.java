
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


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class DrawPathOverlay extends Overlay {
	static int widthOfPath;
	static int color = Color.RED;
	private GeoPoint gp1;
	private GeoPoint gp2;
	private int mcolor = 0;

	public DrawPathOverlay(GeoPoint gp1, GeoPoint gp2, int mcolor) {
		this.gp1 = gp1;
		this.gp2 = gp2;

		if (mcolor == 0)
			this.mcolor = color;
		else
			this.mcolor = mcolor;
	}
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		Projection projection = mapView.getProjection();
		if (shadow == false) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			Point point = new Point();
			projection.toPixels(gp1, point);
			paint.setColor(mcolor);
			Point point2 = new Point();
			projection.toPixels(gp2, point2);
			paint.setStrokeWidth(widthOfPath);
			 paint.setStrokeWidth(3);
			canvas.drawLine((float) point.x, (float) point.y, (float) point2.x,
					(float) point2.y, paint);
		}
		return super.draw(canvas, mapView, shadow, when);
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO Auto-generated method stub

		super.draw(canvas, mapView, shadow);
	}

}
