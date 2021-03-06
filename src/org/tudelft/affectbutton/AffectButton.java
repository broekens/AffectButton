package org.tudelft.affectbutton;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

public class AffectButton extends JButton implements MouseListener, MouseMotionListener {
	/*	Copyright Joost Broekens, 2008-2011
	When using this, please cite the (Broekens & Brinkman, 2013) IJHCS Paper.
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
	Current version v3.3, experimental (but pretty safe to use) code in which I changed arousal scale behaviour to be more responsive in the low arousal area.
	Current version v3.2, cleaned up code and fixed dragging bug, and decided on 0.55 border size and 1.1 sensitivity, as based on latest large scale studies, same as Python code. 
	Version 3.2 is reported upon in (Broekens & Brinkman, 2013) IJHCS Paper. 
	*/
	private String actionCommand;
	private String label;
	private Vector listeners;
	private Face face;      
	private double p, a, d;
	private boolean in=false, down=false;
	private Graphics buffer;
	private Image im;
	private VizThread viz;
	
	private boolean oldState=true;
	private boolean INVERSE_AD=false;
	
	public static final int OS=0;
	public static final int CUSTOM=1;
	
	public AffectButton(){
		this.face=new PADFaceMapped();
		init();
	}
	public AffectButton(Face face){
		this.face=face;
		init();
	}
	private void init(){
		viz=new VizThread(this, 50);
		viz.start();
		p=0;a=0;d=0;
		addMouseListener(this);
		addMouseMotionListener(this);
		listeners=new Vector();
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
	}
	//button funtcions;
	public void setStyle(int style){
		//deprecated
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
	}
	public void paint(){
		paint(this.getGraphics());
	}
	public boolean isActive(){
		return in;
	}
	public void setXY(double x, double y){
		//not validated
		//x and y between -1 and 1
		double sigmoidSteepness, sigmoidZero, baseLength;
		double sensitivity=1.1; //evaluated with set to 1.1: a sensitivity of 1 means the complete space of the button is used. 2 means only the middle part is used, making it more sensitive and more easy to get to extreme arousal (from a CHI perspective).
		p=x*sensitivity;
		d=y*sensitivity;
		baseLength=Math.max(Math.abs(p),Math.abs(d)); //MAX (P, D) base
		//baseLength=Math.sqrt(Math.pow(p,2)+Math.pow(d,2))/Math.sqrt(2.0); //euclid P,D base
		
		//now do the mapping to arousal.
		//Arousal is controlled simple by using a Sigmoid function based on MAX (P, D) vector. This best emulates the linear arousal behaviour in setXYOld() (that has been tested extensively) and resolves the non responsiveness of arousal in the middle part of the button.
		sigmoidSteepness=11;
		sigmoidZero=8.5;
		a=2.1/(1+Math.exp(-(sigmoidSteepness*baseLength-sigmoidZero)))-1;
		
		//Arousal is controlled simply by using a quadratic function that has 0,0,0 as middle point, then drops to -1, then to 1 based MIN (P,D) length (mimics better original linear)
		//sigmoidSteepness=6;
		//sigmoidZero=0.35;
		//a=sigmoidSteepness*Math.pow(Math.max(Math.abs(p),Math.abs(d))-sigmoidZero, 2)-1;
		
		
		p=(p>1?1:(p<-1?-1:p));
		d=(d>1?1:(d<-1?-1:d));
		a=(a>1?1:(a<-1?-1:a));
		if (INVERSE_AD)
		{	double temp=a;
			a=d;d=temp;
		}
	}
	public void setXYOld(double x, double y){
		//x and y between -1 and 1
		double factor=0.55;//old factor used in acii, affine, mpref: 0.66, now it is 0.55 to increase responsiveness of arousal, tested in many different studies
		double sensitivity=1.1; //evaluated with set to 1.1: a sensitivity of 1 means the complete space of the button is used. 2 means only the middle part is used, making it more sensitive and more easy to get to extreme arousal (from a CHI perspective).
		p=x*sensitivity;
		d=y*sensitivity;
		//now do the mapping to arousal.
		//Arousal is controlled in the outer ring of the button.
		//The inner ring is a square of size <factor>% of the button in which p and d are controlled (see <factor> below).
		//Arousal is controlled in the outer ring (between <factor>% and the edge of the button).
		//Arousal is based on the position of the mouse pointer in the outer ring.
		//So at the edge, a = 1, and within the <factor>% of the inner square, a=-1.
		
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
		
		p=(p>1?1:(p<-1?-1:p));
		d=(d>1?1:(d<-1?-1:d));
		a=(a>1?1:(a<-1?-1:a));
	}
	public void showState(int mousex, int mousey){
		p=((double)mousex-(double)getWidth()/(double)2)/((double)(getWidth()+1)/2.0);
		d=-((double)mousey-(double)getHeight()/(double)2)/((double)(getHeight()+1)/2.0);
		setXY(p,d);
		face.setEmotion(p, a, d, mousex, mousey);
		paint();
		this.getGraphics().drawLine(mousex-3, mousey, mousex+3, mousey);
		this.getGraphics().drawLine(mousex, mousey-3, mousex, mousey+3);
		
	}
	//mouse handling stuff
	public void mouseClicked(MouseEvent e) {
    	if (isEnabled()){
	    	for (int i=0;i<listeners.size();i++){
	     	   ((ActionListener)listeners.elementAt(i)).actionPerformed(new AffectButtonActionEvent(this, actionCommand, 1, p, a, d, "no label"));
	        }
	    	viz.clicked(5);
	    	System.out.println(p+","+a+","+d);
    	}
    }
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
		mouseMoved(e);
		
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
		AffectButton toDraw;
		public int interval, clickedTicks;
		public VizThread(AffectButton c, int i){
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
