package org.tudelft.affectbutton;

import java.awt.event.ActionEvent;

public class AffectButtonActionEvent extends ActionEvent{
	double p, a, d;
	String emotion;
	public AffectButtonActionEvent(Object source, String command, int id, double p, double a, double d, String emotion){
		super(source, id, command);
		this.p=p; this.a=a; this.d=d;
		
	}
	public double getPleasure(){
		return p;
	}
	public double getArousal(){
		return a;
	}
	public double getDominance(){
		return d;
	}
	public String getEmotion(){
		
		return emotion;
	}
}

