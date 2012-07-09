package org.math.plot.Draw;



import java.awt.Color;

import java.awt.Graphics2D;
import java.awt.Image;


import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.math.plot.canvas.Plot3DCanvas;

import static java.lang.Math.*;

import static org.math.array.DoubleArray.*;

public class Draw3DPlots {
	
	

	public void Draw (String dataFile, String path)
	{
		
		Plot3DPanel plot = new Plot3DPanel("SOUTH");
		

		ArrayList<String[]> alist = IO
				.getFileContent(
						dataFile,
						" ");
		double[][] data = new double[alist.size()][3];
		for (int i = 0; i < alist.size(); i++) {
			for (int j = 0; j < data[i].length; j++) {
				data[i][j] = Double.parseDouble(alist.get(i)[j]);
			}
		}
		plot.addScatterPlot("data", data);
		
		Plot3DCanvas p =  (Plot3DCanvas) plot.plotCanvas;
		
		//System.out.println(p.getDraw());
		
		//Setting Angle
		p.getDraw().setAngle(-0.4746018366025526, 0.16539816339744748);
		
        //ZOOM
		//p.repaint();
		
		//int[] origin = {151, 181};
        //double[] ratio = {0.6863468634686347, 0.5899014778325123};
        //p.getDraw().dilate(origin, ratio);
        
        //p.repaint();
		
		//((AWTDrawer)p.getDraw()).dilate(null, null);
		//p.repaint();
		//p.ActionMode=0;
		//p.mouseReleased(null);

		//p.zoom();
		

		JFrame frame = new JFrame("a plot panel");
		frame.setSize(1100, 900);
		frame.setContentPane(plot);
		frame.setVisible(true);
		



		File file = new File(
				path);

		do {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        plot.plotToolBar.setVisible(false);

	        Image image = plot.createImage(plot.getWidth(), plot.getHeight());
	        plot.paint(image.getGraphics());
	        image = new ImageIcon(image).getImage();

	        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
	        Graphics2D g = bufferedImage.createGraphics();
	        g.drawImage(image,0, 0, Color.WHITE, null);
	        
	        //BufferedImage clipping = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);//src.getType()); 
	        BufferedImage clipping = new BufferedImage(700, 500, BufferedImage.TYPE_INT_RGB);
	        int x = bufferedImage.getWidth()/2 - 700/2;
	        int y = bufferedImage.getHeight()/2 - 500/2;
	        Graphics2D area = clipping.createGraphics();  
	        area.drawImage(image, 0, 0, clipping.getWidth(), clipping.getHeight(), x, y, x + clipping.getWidth(),  
	            y + clipping.getHeight(), null);  
	        //area.dispose(); 

	        // make it reappear
	        //plot.plotToolBar.setVisible(true);

	        try {
	            ImageIO.write((RenderedImage) clipping, "PNG", file);
	        } catch (IllegalArgumentException ex) {
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        g.dispose();
	        area.dispose();
			System.out.println("Size= " + file.length());
		} while (file.length() < 2000);
		
		//p.ActionMode=0;
		//p.mouseReleased(null);
		
		// otherwise toolbar appears

		frame.dispose();
		
	}
		
	
	
	public static void main(String[] args) {

		String s1 = "C:\\Users\\Christos\\Documents\\SmartP2P workspace\\SmartP2P Server\\src\\MOEAD\\Exp1\\BestPF\\outbests.log";
		String s2 = "C:\\Users\\Christos\\Documents\\SmartP2P workspace\\Plot2\\a.png";
		
		Draw3DPlots d = new Draw3DPlots();
		d.Draw(s1, s2);
		//d.rotate();
	}
}