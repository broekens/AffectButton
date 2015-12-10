package org.tudelft.affectbutton.beta;

public class PADEmotion implements Comparable<PADEmotion>{
	public double p, a, d;
	public String name;
	public double distance;
	
	public PADEmotion(double p, double a, double d, String name){
		this.p=p;this.a=a;this.d=d;
		this.name=new String(name);
	}
	public double setDistance(double p, double a, double d){
		distance=Math.sqrt(Math.pow(p-this.p, 2)+Math.pow(a-this.a, 2)+Math.pow(d-this.d,2));
		return distance;
	}
	public int compareTo(PADEmotion e){
		return (this.distance<e.distance?-1:this.distance>e.distance?+1:0);
	}
}
