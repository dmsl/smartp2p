package org.math.plot.render;


import org.math.plot.canvas.*;

public class AWTDrawer3D extends AWTDrawer {

        public AWTDrawer3D(PlotCanvas _canvas) {
                super(_canvas);
                projection = new Projection3D(this);
        }

        public void rotate(int[] t, int[] panelSize) {
                ((Projection3D) projection).rotate(t, panelSize);
        }
        
        public void setAngle(double theta, double phi) {
        	((Projection3D) projection).setView(theta, phi);
        }
}
