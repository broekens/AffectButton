package org.tudelft.affectbutton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class ShowAllFaces extends Frame implements ActionListener {
	AffectButton buttons[][];
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ShowAllFaces();
	}
	public ShowAllFaces(){
		this.setLayout(null);
		this.setVisible(true);
		this.setBounds(0, 0, 1200, 1000);
		Button b=new Button("show");
		this.add(b);
		b.setBounds(50, 600, 100, 50);
		b.addActionListener(this);
		buttons=new AffectButton[4][10];
		createButtons();
	}
				
	public void createButton(int x, int y){
		AffectButton ab=new AffectButton(new PADFaceMapped(0,0,1));
		ab.setBounds(50+x*100, 50+y*100, 100, 100);
		ab.setVisible(true);
		add(ab);
		buttons[y][x]=ab;
	}
	public void createButtons(){
		for (int x=0;x<10;x++){
			for (int y=0;y<4;y++)
			createButton(x,y);
		}
	}
	public void setButtons(){
		for (int x=0;x<10;x++){
			buttons[0][x].showState(50-x*5, 50-x*5);
		}
		for (int x=0;x<10;x++){
			buttons[1][x].showState(50+x*5, 50-x*5);
		}
		for (int x=0;x<10;x++){
			buttons[2][x].showState(50-x*5, 50+x*5);
		}
		for (int x=0;x<10;x++){
			buttons[3][x].showState(50+x*5, 50+x*5);
		}
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		setButtons();
	}
}
