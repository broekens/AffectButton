package org.tudelft.affectbutton;

import java.awt.Color;
import java.awt.Graphics;

public interface Face {
	public void setEmotion(double p, double a, double d, int mx, int my);
    public void setEmotion(double p, double a, double d);
    public void setDimensions(int xx, int yy, int size);
    public void paint(Graphics g);
}
