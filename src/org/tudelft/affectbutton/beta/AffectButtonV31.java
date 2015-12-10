package org.tudelft.affectbutton.beta;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import org.tudelft.affectbutton.*;

public class AffectButtonV31 extends JButton implements MouseListener, MouseWheelListener, MouseMotionListener {
	/*	Copyright Joost Broekens, 2008-2011
	When using this, please cite the (Broekens & Brinkman, 2009) ACII Paper.
	This is the AffectButton, a Java button to input emotions.
	The button is based on a well known and widely used emotion theory (see e.g., Mehrabian, 1984, but also many others).
	It has been used in robotics and agents (by e.g., Breazael, 2001; Broekens & DeGroot, 2004; and many others) to express emotions
	It assumes there are three fundamental factors that can be used to describe an emotion (or emotional situation):
	Pleasure - describing how positive/negative the emotion is
	Arousal - describing how active/passive the emotions is
	Dominance - describing how much you feel in charge/dominated
	The main button class, AffectButton, is acting just like any Java button (see example code below)
	the button is scalable (although a very small button will not render the face very well of course, 75*75 being approximately the lowest values that make sense from a user input point of view).
	An extra event, AffectButtonActionEvent, has been introduce that extends the ActionEvent to handle the affective feedback.
	
	The interaction is as follows: move inside the button
	This version uses only the mouse coordinates, not the mouse wheel.
	It has been evaluated in the (Broekens & Brinkman, 2009) ACII Paper 
	
	Current version v3.2, fixed dragging bug, and decided on 0.55 border size and 1.1 sensitivity, as based on latest large scale studies. 
	*/
	private static String[] emotionEvents;
	private String actionCommand;
	private String label;
	private Vector listeners;
	private Face face;      
	private double p, a, d;
	private boolean in=false, down=false, osStyle;
	private Graphics buffer;
	private Image im;
	private VizThread viz;
	
	private boolean oldState=true;
	
	public static final int OS=0;
	public static final int CUSTOM=1;
	
	public AffectButtonV31(){
		this.face=new PADFaceMapped();
		init();
	}
	public AffectButtonV31(Face face){
		this.face=face;
		init();
	}
	private void init(){
		osStyle=true;
		viz=new VizThread(this, 50);
		viz.start();
		p=0;a=0;d=0;
		addMouseListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		listeners=new Vector();
		emotionEvents=new String[27];
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
	}
	//button funtcions;
	public void setStyle(int style){
		switch (style){
			case OS: osStyle=true; break;
			case CUSTOM: osStyle=false; break;
			default: osStyle=true; break;
		}
	}
	
	public void addActionListener(ActionListener l){
		listeners.add(l);
	}
	public String getActionCommand(){
		return actionCommand;
	}
	public ActionListener[] getActionListeners(){
		ActionListener[] listenersArray=new ActionListener[listeners.size()];
		listeners.copyInto(listenersArray);
		return listenersArray;
	}
	public String getLabel(){
		return label;
	}
	public void removeActionListener(ActionListener l){
		listeners.remove(l);
	}
	public void setActionCommand(String command){
		actionCommand=command;
	}
	public void setLabel(String label){
		this.label=label;
	}
	public void paint(Graphics gg){
		if (osStyle){
			int faceSize=Math.min(getWidth()-4, getHeight()-4);
			if (face==null)
				face=new PADFaceMapped(Math.max(2, (getWidth()-faceSize)/2), Math.max(2, (getHeight()-faceSize)/2), faceSize);
			if (buffer==null){
				im=this.createImage(getWidth(), getHeight());
		        buffer=im.getGraphics();
			}
			super.paint(buffer);
			
			buffer.setColor(new Color(255,255,255,0));
			buffer.fillRect(0,0,im.getWidth(null),im.getHeight(null));
	        
			face.setDimensions(Math.max(2, (getWidth()-faceSize)/2), Math.max(2, (getHeight()-faceSize)/2), faceSize);
			face.paint(buffer);
			
			//draw buffer to screen
	        gg.drawImage(im, 0, 0, null, null);
			
			
		} else {
			//this is the CUSTOM way of drawing the AffectButton
			int faceSize=Math.min(getWidth()-4, getHeight()-4);
			if (face==null)
				face=new PADFaceMapped(Math.max(2, (getWidth()-faceSize)/2), Math.max(2, (getHeight()-faceSize)/2), faceSize);
			if (buffer==null){
				im=this.createImage(getWidth(), getHeight());
		        buffer=im.getGraphics();
			}
			
			buffer.setColor(this.getBackground());
	        buffer.fillRect(0,0,getWidth(),getHeight());
			
	        buffer.setColor(Color.gray);
			buffer.drawRect(0, 0, getWidth()-1, getHeight()-1);
			
			if (in){
				buffer.setColor(new Color(200,200,200));
				buffer.drawLine(0, 0, getWidth()-1, 0);
				buffer.drawLine(0, 0, 0, getHeight()-1);
				buffer.setColor(Color.darkGray);
				buffer.drawLine(getWidth()-1, getHeight()-1, getWidth()-1, 0);
				buffer.drawLine(getWidth()-1, getHeight()-1, 0, getHeight()-1);
			}
			if (down){
				buffer.setColor(Color.darkGray);
				buffer.drawLine(0, 0, getWidth()-1, 0);
				buffer.drawLine(0, 0, 0, getHeight()-1);
				buffer.setColor(new Color(200,200,200));
				buffer.drawLine(getWidth()-1, getHeight()-1, getWidth()-1, 0);
				buffer.drawLine(getWidth()-1, getHeight()-1, 0, getHeight()-1);
			} 
			face.setDimensions(Math.max(2, (getWidth()-faceSize)/2), Math.max(2, (getHeight()-faceSize)/2), faceSize);
			face.paint(buffer);
			
			 //draw buffer to screen
	        gg.drawImage(im, 0, 0, null, null);
		}
       
	}
	public void paint(){
		paint(this.getGraphics());
	}
	public boolean isActive(){
		return in;
	}
	public void setXY(double x, double y){
		double sensitivity=1.1; //XXX test with users between 1.1 and 1.2: a sensitivity of 1 means the complete space of the button is used. 2 means only the middle part is used, making it more sensitive and more easy to get to extreme arousal (from a CHI perspective).
		x=x*sensitivity;
		y=y*sensitivity;
		
		x=(x>1?1:(x<-1?-1:x));
		y=(y>1?1:(y<-1?-1:y));
		
		//including mehrabian
		//normal
		//p=0.897*x-0.223*y;
		//a=-0.561*x+0.827*y;
		//d=0.879*x+0.300*y;
		//rotated
		//p=0.906*x-0.182*y;
		//a=-0.153*x+0.988*y;
		//d=0.923*x+-0.105*y;
		//correlations
		//p=x;
		//a=y;
		//d=0.714*p-0.256*y;
		
		//excluding mehrabian
		//normal
		//p=0.896*x-0.247*y;
		//a=-0.614*x+0.789*y;
		//d=0.885*x+0.297*y;
		//rotated
		//p=0.909*x-0.194*y;
		//a=-0.180*x+0.983*y;
		//d=0.922*x+-0.144*y;

		
		//new way
		p=x;
		d=y;
		a=Math.sqrt(Math.pow(x, 2)+Math.pow(y,2))/(Math.sqrt(1+0.25))*-Math.sin(Math.atan2(y, x)*4/*+Math.PI/2*/);

		p=(p>1?1:(p<-1?-1:p));
		d=(d>1?1:(d<-1?-1:d));
		a=(a>1?1:(a<-1?-1:a));
	}
	public void old_setXY(double x, double y){
		//x and y between -1 and 1
		double factor=0.55;//old XXX factor used in acii, affine, mpref: 0.66, now it is 0.55 to increase responsiveness of arousal
		double sensitivity=1.1; //XXX test with users between 1.1 and 1.2: a sensitivity of 1 means the complete space of the button is used. 2 means only the middle part is used, making it more sensitive and more easy to get to extreme arousal (from a CHI perspective).
		p=x*sensitivity;
		d=y*sensitivity;
		//now do the mapping to arousal.
		//Arousal is controlled in the outer ring of the button.
		//The inner ring is a square of size 66% of the button in which p and d are controlled (see <factor> below).
		//Arousal is controlled in the outer ring (between 66% and the edge of the button).
		//Arousal is based on the position of the mouse pointer in the outer ring.
		//So at the edge, a = 1, and within the 66% of the inner square, a=-1.
		
		double x1,x2,y1,y2,d1,a_0,rc;
		if (p>factor | d>factor | p<-factor | d<-factor){
			if (p==0){
				if (d>0)
				{	x1=0;
					y1=factor;
					x2=0;
					y2=1;
				} else
				{
					x1=0;
					y1=-factor;
					x2=0;
					y2=-1;
				}
			} else {
				rc=d/p;
				if (p>=d & !(-p>=d)){
					x1=factor;
					x2=1;
					y1=x1*rc;
					y2=x2*rc;
				} else if (p>=d & -p>=d) {
					y1=-factor;
					y2=-1;
					x1=y1/rc;
					x2=y2/rc;
				} else if (!(p>=d) & -p>=d){
					x1=-factor;
					x2=-1;
					y1=x1*rc;
					y2=x2*rc;						
				} else {
					//(!(p>=d) & !(-p>=d))
					y1=factor;
					y2=1;
					x1=y1/rc;
					x2=y2/rc;
				}
			}
			d1=Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2));
			a_0=Math.sqrt(Math.pow(x1-p, 2)+Math.pow(y1-d, 2));
			a=2*(a_0/d1)-1;
		} else
			a=-1;
		//TEST: not in orginal XXX make p and d behave as if only in factor area, hence p and d completely indepdendent from a, as a is controlled outside, need to check corrs in SAM data and mehrabian
		//p=p/factor;
		//d=d/factor;
		
		p=(p>1?1:(p<-1?-1:p));
		d=(d>1?1:(d<-1?-1:d));
		a=(a>1?1:(a<-1?-1:a));
	}
	//mouse handling stuff
	public void mouseMoved(MouseEvent e) {
		if (isEnabled()){
			p=((double)e.getX()-(double)getWidth()/(double)2)/((double)(getWidth()+1)/2.0);
			d=-((double)e.getY()-(double)getHeight()/(double)2)/((double)(getHeight()+1)/2.0);
			setXY(p,d);
			face.setEmotion(p, a, d, e.getX(), e.getY());
		} 
		paint();
	}
	
	public void mouseDragged(MouseEvent e) {
		
		if (isEnabled()){
			p=((double)e.getX()-(double)getWidth()/(double)2)/((double)(getWidth()+1)/2.0);
			d=-((double)e.getY()-(double)getHeight()/(double)2)/((double)(getHeight()+1)/2.0);
			p*=1.3;
			d*=1.3;
			a=2*Math.pow((Math.abs(p)+Math.abs(d))/2, 2)-1;
			p=(p>1?1:(p<-1?-1:p));
			d=(d>1?1:(d<-1?-1:d));
			face.setEmotion(p, a, d, e.getX(), e.getY());
		}
		
		paint();
		
	}	
	public void mouseWheelMoved(MouseWheelEvent e) {
		/*
		if (isEnabled()){
			a+=(double)e.getWheelRotation()/(double)3;
	        a=(a>=1?0.9999:a<=-1?-0.9999:a);
	        face.setEmotion(p, a, d, e.getX(), e.getY());
		}
        paint();
        */
    }
	
    public void mousePressed(MouseEvent e) {
    	down=true;
    }

    public void mouseReleased(MouseEvent e) {
    	down=false;
    }

    public void mouseEntered(MouseEvent e) {
    	in=true;
    }

    public void mouseExited(MouseEvent e) {
    	in=false;
    	down=false;
    	paint();
    }
    public double getP(){
    	return p;
    }
    public double getA(){
    	return a;
    }
    public double getD(){
    	return d;
    }
    public void mouseClicked(MouseEvent e) {
    	if (isEnabled()){
	    	for (int i=0;i<listeners.size();i++){
	     	   ((ActionListener)listeners.elementAt(i)).actionPerformed(new AffectButtonActionEvent(this, actionCommand, 1, p, a, d, "no label"));
	        }
	    	viz.clicked(5);
	    	System.out.println(p+","+a+","+d);
    	}
    }
    public void reset(){
    	p=0;a=0;d=0;
    	face.setEmotion(p, a, d);
    }
    public void setEnabled(boolean e){
    	oldState=e;
    	super.setEnabled(e);
    }
    public void blink(boolean e){
    	super.setEnabled(e);
    }
    private class VizThread extends Thread{
    	AffectButtonV31 toDraw;
		public int interval, clickedTicks;
		public VizThread(AffectButtonV31 c, int i){
			toDraw=c;
			interval=i;
		}
		public void run(){
			while (true)
			{ try {sleep(interval);} catch (Exception e){}
				if (toDraw.in && toDraw.isVisible()){
					toDraw.repaint();
					if (clickedTicks==0)
						toDraw.blink(oldState);
					else
						clickedTicks--;
				}
				
			}
		}
		public void clicked(int ticks){
			clickedTicks=ticks;
			toDraw.blink(false);
		}
	}
}
