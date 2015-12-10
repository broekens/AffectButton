package org.tudelft.affectbutton.beta;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import org.tudelft.affectbutton.AffectButtonActionEvent;
import org.tudelft.affectbutton.Face;
import org.tudelft.affectbutton.PADFaceMapped;

public class AffectButton3D extends JButton implements MouseListener, MouseWheelListener, MouseMotionListener {
	/*	Copyright Joost Broekens, 2008
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

	The interaction is as follows: move inside the button and use the mouse wheel
	This version uses the Mousewheel to input the Arousal value.
	It has been evaluated in the (Broekens & Brinkman, 2009) ACII Paper 
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
	
	public AffectButton3D(Face face){
		this.face=face;
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
		/*
		emotionEvents[0]="-p-a-d";emotionEvents[9]= "  -a-d";emotionEvents[18]=" p-a-d";
		emotionEvents[1]="-p-a  ";emotionEvents[10]="  -a  ";emotionEvents[19]=" p-a  ";
		emotionEvents[2]="-p-a d";emotionEvents[11]="  -a d";emotionEvents[20]=" p-a d";
		emotionEvents[3]="-p  -d";emotionEvents[12]="    -d";emotionEvents[21]=" p  -d";
		emotionEvents[4]="-p    ";emotionEvents[13]="      ";emotionEvents[22]=" p    ";
		emotionEvents[5]="-p   d";emotionEvents[14]="     d";emotionEvents[23]=" p   d";
		emotionEvents[6]="-p a-d";emotionEvents[15]="   a-d";emotionEvents[24]=" p a-d";
		emotionEvents[7]="-p a  ";emotionEvents[16]="   a  ";emotionEvents[25]=" p a  ";
		emotionEvents[8]="-p a d";emotionEvents[17]="   a d";emotionEvents[26]=" p a d";
		*/
		emotionEvents[0]="miserable";	emotionEvents[9]= "sleepy";		emotionEvents[18]="droopy";
		emotionEvents[1]="bored-annoyed";emotionEvents[10]="  -a  ";	emotionEvents[19]="satisfied";
		emotionEvents[2]="frustrated";	emotionEvents[11]="  -a d";		emotionEvents[20]="relaxed";
		emotionEvents[3]="sad";			emotionEvents[12]="    -d";		emotionEvents[21]="content";
		emotionEvents[4]="worried";		emotionEvents[13]="neutral";	emotionEvents[22]="glad";
		emotionEvents[5]="angry";		emotionEvents[14]="confident";	emotionEvents[23]="happy";
		emotionEvents[6]="affraid";		emotionEvents[15]="astonished";	emotionEvents[24]="expecting";
		emotionEvents[7]="mad";			emotionEvents[16]="   a  ";		emotionEvents[25]="happy-eager";
		emotionEvents[8]="angry-mad";	emotionEvents[17]="   a d";		emotionEvents[26]="excited";
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
	
	//emotion handling and button painting stuff
	public int getEmotionId(double p, double a, double d){
		//assumes p a and d are between -1 and 1, transforms them to 0-3 then calculates the index with p=high a=medium d=low factor
		return (int)((p+1.0)*3.0/2.0)*9+(int)((a+1.0)*3.0/2.0)*3+(int)((d+1.0)*3.0/2.0);
	}
	public static String getEmotionFromId(int id){
		return emotionEvents[id];
	}
	public Vector<PADEmotion> sort(Vector<PADEmotion> in){
		for (PADEmotion e: in)
			e.setDistance(p, a, d);
		Collections.sort(in);
		return in;
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
	
	//mouse handling stuff
	public void mouseMoved(MouseEvent e) {
		if (isEnabled()){
			p=((double)e.getX()-(double)getWidth()/(double)2)/((double)(getWidth()+1)/2.0);
			d=-((double)e.getY()-(double)getHeight()/(double)2)/((double)(getHeight()+1)/2.0);
			face.setEmotion(p, a, d, e.getX(), e.getY());
		}
		paint();
	}
	
	public void mouseDragged(MouseEvent e) {
		if (isEnabled()){
			p=((double)e.getX()-(double)getWidth()/(double)2)/((double)(getWidth()+1)/2.0);
			d=-((double)e.getY()-(double)getHeight()/(double)2)/((double)(getHeight()+1)/2.0);
			face.setEmotion(p, a, d, e.getX(), e.getY());
		}
		paint();
	}	
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (isEnabled()){
			a+=(double)e.getWheelRotation()/(double)3;
	        a=(a>=1?0.9999:a<=-1?-0.9999:a);
	        face.setEmotion(p, a, d, e.getX(), e.getY());
		}
        paint();
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
    	for (int i=0;i<listeners.size();i++){
     	   ((ActionListener)listeners.elementAt(i)).actionPerformed(new AffectButtonActionEvent(this, actionCommand, getEmotionId(p, a, d), p, a, d, emotionEvents[getEmotionId(p,a,d)]));
        }
    	viz.clicked(5);
    }
    public void reset(){
    	p=0;a=0;d=0;
    }
    public void setEnabled(boolean e){
    	oldState=e;
    	super.setEnabled(e);
    }
    public void blink(boolean e){
    	super.setEnabled(e);
    }
    public class VizThread extends Thread{
		AffectButton3D toDraw;
		public int interval, clickedTicks;
		public VizThread(AffectButton3D c, int i){
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
