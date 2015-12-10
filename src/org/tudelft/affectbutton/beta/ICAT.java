package org.tudelft.affectbutton.beta;

import java.io.PrintStream;
import java.net.Socket;

public class ICAT {
	private Socket iCatSocket;
    private static PrintStream iCat;
    private static boolean useICat=true;

    public ICAT(){
    	try {
        	iCatSocket=new Socket("127.0.0.1", 11111);
        	iCat=new PrintStream(iCatSocket.getOutputStream(), true);
        	useICat=true;
        	iCat.print("show-info off");
        } catch (Exception e){useICat=false;System.out.println("Cannot open connection to iCat, button output only");e.printStackTrace(); }
    }
    
    public static void send(String s){
    	if (iCat==null)
    		new ICAT();
    	if (useICat)
    		iCat.print(s);
    }
}
