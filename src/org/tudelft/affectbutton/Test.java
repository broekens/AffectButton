package org.tudelft.affectbutton;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.RandomAccessFile;


public class Test extends Frame implements ActionListener, WindowListener{
	private int nrOfClicks;
	private RandomAccessFile raf;
	private AffectButton ab;
	
	public Test(String recordingLabel){
		setLayout(null);
		//Adding and initing the AffectButton, just basic button stuff
		ab=new AffectButton(new PADFaceMapped(0,0,1));
		ab.setBounds(50, 50, 100, 100);
		ab.setActionCommand(recordingLabel); //not really needed but handy if you have multiple buttons handled by the same ActionEventListener, so you know which one fired an event
		ab.addActionListener(this);
		addWindowListener(this);
		ab.setVisible(true);
		add(ab);
		nrOfClicks=1;
		try {
    		raf=new RandomAccessFile("AfffectButtonOutput.txt", "rw");
    		raf.seek(raf.length());
    	} catch (Exception exc){raf=null;exc.printStackTrace();}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Make a button
		Test test;
		if (args.length>0)
			test=new Test(args[0]);
		else
			test=new Test(""+System.currentTimeMillis());
		test.setVisible(true);
		test.setBounds(400, 400, 200, 200);
		
	}
	public void actionPerformed(ActionEvent e) {
		System.out.println(e);
		try {
			if (raf!=null)
				raf.writeBytes(((AffectButtonActionEvent)e).getActionCommand()+","+System.currentTimeMillis()+","+nrOfClicks+","+((AffectButtonActionEvent)e).getPleasure()+","+((AffectButtonActionEvent)e).getArousal()+","+((AffectButtonActionEvent)e).getDominance()+"\r\n");
		} catch (Exception exc){exc.printStackTrace();}
		nrOfClicks++;
    }
	
	public void windowActivated(WindowEvent w){
	}
	public void windowDeactivated(WindowEvent w){
	}
	public void windowOpened(WindowEvent w){
	}
	public void windowClosed(WindowEvent w){
	}
	public void windowClosing(WindowEvent w){
		try {if (raf!=null) raf.close();}
		catch (Exception e){}
		System.exit(0);
	}
	public void windowDeiconified(WindowEvent w){
	}
	public void windowIconified(WindowEvent w){
	}
}
