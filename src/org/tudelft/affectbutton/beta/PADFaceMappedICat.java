package org.tudelft.affectbutton.beta;

/**
 *
 * @author  broekens
 */
import java.awt.*;
import java.net.*;
import java.io.*;

import org.tudelft.affectbutton.Face;

public class PADFaceMappedICat extends Component implements Face{
	public static double[] FACE_MIXTURE_DISTANCE; //1.3 for all is the default tested in the 2009 chi study.
	
    int x,y, size, fdx, fdy;
    int sx, sy, slex, sley, srex, srey, smx, smy, baseMW, baseMH, baseEW, baseEH;
    int eh, eyebrowSpace, eyebrowOuter, eyebrowInner, mw, mt, mo, tv;
    int mx, my; //mouse x and mouse y of user to simulate that the face looks at the user
    double pupilRandomness=0;
    double mouthRandomness=0;
    
    double [][] emotions;
    double [] emotion;
    
    //ICat
    private long nextICatCommandTimer=0;
    public static int ICATFRAMERATEMS=100;
    boolean iCat=true;
    int nrOfFeatures=9;
    
    /** Creates a new instance of PADFace */
    public PADFaceMappedICat() {
        setDimensions(0, 0, 1);
        emotions=defineEmotions();
        setEmotion(0,0,0,0,0);
        ICAT.send("set-var iCat.Neck "+60+" -1;"+"set-var iCat.Body "+0+" -1;");
    }
    public PADFaceMappedICat(int x, int y, int size) {
        setDimensions(x, y, size);
        emotions=defineEmotions();
        setEmotion(0,0,0,0,0);
		ICAT.send("set-var iCat.Neck "+60+" -1;"+"set-var iCat.Body "+0+" -1;");

    }
    private double[][] defineEmotions(){
    	double[][] emos=new double[9][12];
    	//0=-p-a-d 	= sad / lonely / bored
    	//1=-p-ad 	= jealous / disdainful
    	//2=-pa-d	= terrified / fearful / humiliated / frustrated
    	//3=-pad	= angry / cruel / hostile
    	//4=p-a-d	= protected / humble
    	//5=p-ad	= leisured / relaxed
    	//6=pa-d	= impressed
    	//7=pad 	= happy / joyful / masterful / excited
    	//8=000 	= neutral
    	//this is the influence sphere of the emotion (used for mixed expressions, standard =1.3)
    	FACE_MIXTURE_DISTANCE=new double[9];
    	FACE_MIXTURE_DISTANCE[0]=1.3;
    	FACE_MIXTURE_DISTANCE[1]=1.3;
    	FACE_MIXTURE_DISTANCE[2]=1.3;
    	FACE_MIXTURE_DISTANCE[3]=1.3;
    	FACE_MIXTURE_DISTANCE[4]=1.3;
    	FACE_MIXTURE_DISTANCE[5]=1.3;
    	FACE_MIXTURE_DISTANCE[6]=1.3;
    	FACE_MIXTURE_DISTANCE[7]=1.3;
    	FACE_MIXTURE_DISTANCE[8]=1.7;
    	
    	//eyes height
    	emos[0][0]=-1;emos[1][0]=-0.3;emos[2][0]=1;emos[3][0]=0.5;emos[4][0]=-1;emos[5][0]=-0.5;emos[6][0]=0.3;emos[7][0]=0.5;emos[8][0]=0;
    	//eyebrowSpace between eyes and brows
    	emos[0][1]=-1;emos[1][1]=0;emos[2][1]=1;emos[3][1]=0;emos[4][1]=-1;emos[5][1]=0;emos[6][1]=1;emos[7][1]=0.5;emos[8][1]=0;
    	//eyebrowOuter height
    	emos[0][2]=-1;emos[1][2]=0;emos[2][2]=-.8;emos[3][2]=.8;emos[4][2]=0;emos[5][2]=0;emos[6][2]=0;emos[7][2]=0;emos[8][2]=0;
    	//eyebrowInner height
    	emos[0][3]=1;emos[1][3]=-1;emos[2][3]=.8;emos[3][3]=-.8;emos[4][3]=0;emos[5][3]=0;emos[6][3]=0;emos[7][3]=0;emos[8][3]=0;
    	//mouth width
    	emos[0][4]=-1;emos[1][4]=-1;emos[2][4]=0;emos[3][4]=1;emos[4][4]=0;emos[5][4]=0;emos[6][4]=-1.5;emos[7][4]=1;emos[8][4]=0;
    	//mouth openness
    	emos[0][5]=-1;emos[1][5]=-0.5;emos[2][5]=0;emos[3][5]=1;emos[4][5]=-1;emos[5][5]=-0.5;emos[6][5]=0.7;emos[7][5]=0.5;emos[8][5]=-0.5;
    	//mouth twist (positive = happy)
    	emos[0][6]=-1;emos[1][6]=-0.5;emos[2][6]=-0.3;emos[3][6]=-1;emos[4][6]=0.7;emos[5][6]=1;emos[6][6]=0.5;emos[7][6]=1;emos[8][6]=0;
    	//teeth visible 
    	emos[0][7]=1;emos[1][7]=1;emos[2][7]=0.5;emos[3][7]=1;emos[4][7]=1;emos[5][7]=1;emos[6][7]=-0.5;emos[7][7]=0.5;emos[8][7]=1;
    	//neck tilt 
    	emos[0][8]=-1;emos[1][8]=-.5;emos[2][8]=0.1;emos[3][8]=0;emos[4][8]=-.5;emos[5][8]=-.5;emos[6][8]=0.5;emos[7][8]=0.2;emos[8][8]=0;
    	//p value of emotions
    	emos[0][nrOfFeatures]=-1;emos[1][nrOfFeatures]=-1;emos[2][nrOfFeatures]=-1;emos[3][nrOfFeatures]=-1;emos[4][nrOfFeatures]=1;emos[5][nrOfFeatures]=1;emos[6][nrOfFeatures]=1;emos[7][nrOfFeatures]=1;emos[8][nrOfFeatures]=0;
    	//a value of emotions
    	emos[0][nrOfFeatures+1]=-1;emos[1][nrOfFeatures+1]=-1;emos[2][nrOfFeatures+1]=1;emos[3][nrOfFeatures+1]=1;emos[4][nrOfFeatures+1]=-1;emos[5][nrOfFeatures+1]=-1;emos[6][nrOfFeatures+1]=1;emos[7][nrOfFeatures+1]=1;emos[8][nrOfFeatures+1]=0;
    	//d value of emotions
    	emos[0][nrOfFeatures+2]=-1;emos[1][nrOfFeatures+2]=1;emos[2][nrOfFeatures+2]=-1;emos[3][nrOfFeatures+2]=1;emos[4][nrOfFeatures+2]=-1;emos[5][nrOfFeatures+2]=1;emos[6][nrOfFeatures+2]=-1;emos[7][nrOfFeatures+2]=1;emos[8][10]=0;
    	return emos;
    }
    public void setEmotion(double p, double a, double d, int mx, int my) //accepts pad between -1 and 1
    {   
    	this.mx=(mx*(size/20))/size-1;//eye tracking of mouse
    	this.my=(my*(size/20))/size-1;//eye tracking of mouse
    	
    	//fdx and fdy are the start coordinate of the face within the yellow circle (the skin)
        fdy=size/10-(int)((d+a)*(size/20));
        fdx=this.mx;
        
        //here a mapping is selected from the predefined 8 emotions in PAD space;
        //and subsequently interpolated.
        emotion=new double[nrOfFeatures];
        for (int j=0;j<nrOfFeatures;j++){
        	double weight=0;
        	for (int i=0;i<9;i++){
        		
        		double featureWeight=FACE_MIXTURE_DISTANCE[i]-Math.min(FACE_MIXTURE_DISTANCE[i], distance(p, a, d, emotions[i]));
        		weight+=featureWeight;
        		emotion[j]+=featureWeight*emotions[i][j];
        	}
        	emotion[j]/=weight;
        }
        eh=(int)(baseEH*(1+emotion[0]))+2;
        eyebrowSpace=(int)(baseEH*(1+emotion[1])/2)+baseEH/2+1;
        eyebrowOuter=(int)(-emotion[2]*baseEH)/2;
        eyebrowInner=(int)(-emotion[3]*baseEH)/2;
        
        mw=(int)(baseMW*(emotion[4]+1)/6)+baseMW/2;
        mo=(int)(baseMH*(emotion[5]+1)/3);
        mt=(int)((baseMH*emotion[6])/2);
        
        //teeth visible
        tv=(int)(baseMH*(emotion[7]-1)/3);
    }
    private double distance(double p, double a, double d, double[] emotion){
    	return Math.sqrt(Math.pow(p-emotion[nrOfFeatures], 2) + Math.pow(a-emotion[nrOfFeatures+1], 2) + Math.pow(d-emotion[nrOfFeatures+2], 2));
    }
    public void setEmotion(double p, double a, double d) //accepts pad between -1 and 1
    {   setEmotion(p, a, d, 0, 0);
    }
    public void setDimensions(int xx, int yy, int size)
    {   sx=xx;sy=yy;
        x=size;y=size;
        this.size=size;
        baseEW=size/4;
        baseEH=baseEW/3;
        baseMW=size/2;
        baseMH=baseMW/3;
        
        slex=fdx+sx+size/2-(int)((double)baseEW*1.4);
        sley=fdy+sy+size/3;
        srex=fdx+sx+size/2+(int)((double)baseEW*0.4);
        //srex=fdx+sx+size*3/4-baseEW/2;
        srey=fdy+sy+size/3;
        smx=fdx+sx+size/2;
        smy=fdy+sy+2*(size/3);
        x+=(sx+20);y+=(sx+20);
    }
    public void paint(Graphics g)
    {   if (g==null)
            return;
        
    	//draw pacpan
        Color pacman=new Color(255,200,0);
    	//Color pacman=new Color(200,100,0);
        g.setColor(pacman);
        g.fillOval(sx+4, sy, size-8, size);
        g.fillOval(sx+4, sy, size-8, (int)((double)size*0.66));
        
        //draw left eye
        int pupilVarX=mx+(int)(pupilRandomness*Math.random());
        int pupilVarY=my+(int)(pupilRandomness*Math.random());
        g.setColor(Color.WHITE);
        g.fillArc(slex, sley-eh/2, baseEW, eh, 0, 360);
        g.setColor(new Color(0,200,200));
        g.fillArc(pupilVarX+slex+baseEW/2-baseEW/4, pupilVarY+sley-baseEW/4, baseEW/2, baseEW/2, 0, 360);
        g.setColor(Color.black);
        g.fillArc(pupilVarX+slex+baseEW/2-baseEW/10, pupilVarY+sley-baseEW/10, baseEW/5, baseEW/5, 0, 360);
        g.setColor(pacman);
        g.fillRect(slex, sley-eh/2-baseEW/2, baseEW, baseEW/2);
        g.fillRect(slex, sley+eh/2, baseEW, baseEW/2);
        
        //left eyebrow
        g.setColor(Color.black);
        g.drawLine(slex, sley-eyebrowSpace+eyebrowOuter, slex+baseEW, sley-eyebrowSpace+eyebrowInner);
        g.drawLine(slex, 1+sley-eyebrowSpace+eyebrowOuter, slex+baseEW, 1+sley-eyebrowSpace+eyebrowInner);
        if (size>70)
        	g.drawLine(slex, 2+sley-eyebrowSpace+eyebrowOuter, slex+baseEW, 2+sley-eyebrowSpace+eyebrowInner);
        
        //draw right eye
        pupilVarX=mx+(int)(pupilRandomness*Math.random());
        pupilVarY=my+(int)(pupilRandomness*Math.random());
        g.setColor(Color.WHITE);
        g.fillArc(srex, srey-eh/2, baseEW, eh, 0, 360);
        g.setColor(new Color(0,200,200));
        g.fillArc(pupilVarX+srex+baseEW/2-baseEW/4, pupilVarY+srey-baseEW/4, baseEW/2, baseEW/2, 0, 360);
        g.setColor(Color.black);
        g.fillArc(pupilVarX+srex+baseEW/2-baseEW/10, pupilVarY+srey-baseEW/10, baseEW/5, baseEW/5, 0, 360);
        g.setColor(pacman);
        g.fillRect(srex, srey-eh/2-baseEW/2, baseEW, baseEW/2);
        g.fillRect(srex, srey+eh/2, baseEW, baseEW/2);
        
        //right eyebrow
        g.setColor(Color.black);
        g.drawLine(srex, srey-eyebrowSpace+eyebrowInner, srex+baseEW, srey-eyebrowSpace+eyebrowOuter);
        g.drawLine(srex, 1+srey-eyebrowSpace+eyebrowInner, srex+baseEW, 1+srey-eyebrowSpace+eyebrowOuter);
        if (size>70)
            g.drawLine(srex, 2+srey-eyebrowSpace+eyebrowInner, srex+baseEW, 2+srey-eyebrowSpace+eyebrowOuter);
        
        //draw mouth
        
        //draw lips
        int mr=(int)(mouthRandomness*Math.random());
        int upperlip, lowerlip, shift;
        upperlip=mt-mo;
        lowerlip=mt+mo;
        shift=-mt;
        
       
        //fill the mouth in the right order
        if (upperlip>0){
        	if (lowerlip>0){
            	g.setColor(Color.white);
            	g.fillArc(smx-mw/2, shift+mr+smy-lowerlip, mw, lowerlip*2, -180, 180);
            }
            else{
            	g.setColor(pacman);
            	g.fillArc(smx-mw/2, shift+mr+smy+lowerlip, mw, -lowerlip*2, 0, 180);
            }
        	g.setColor(pacman);
        	g.fillArc(smx-mw/2, shift+mr+smy-upperlip, mw, upperlip*2, -180, 180);
        }
        else{
        	g.setColor(Color.white);
        	g.fillArc(smx-mw/2, shift+mr+smy+upperlip, mw, -upperlip*2, 0, 180);
        	if (lowerlip>0){
            	g.setColor(Color.white);
            	g.fillArc(smx-mw/2, shift+mr+smy-lowerlip, mw, lowerlip*2, -180, 180);
            }
            else{
            	g.setColor(pacman);
            	g.fillArc(smx-mw/2, shift+mr+smy+lowerlip, mw, -lowerlip*2, 0, 180);
            }
        }
        
        
        //fill mouth hole black
        g.setColor(pacman);
        g.fillRect(smx-mw/2, smy+tv, mw, -tv*2);

//      draw teeth
        g.setColor(pacman);
        for (int i=0;i<6;i++)
        	g.drawLine(smx-baseMW/2+i*baseMW/5, smy-Math.abs(mt)-mo, smx-baseMW/2+i*baseMW/5, smy+Math.abs(mt)+mo);
        g.drawLine(smx-baseMW/2, smy, smx+baseMW/2, smy);
        
        
        //draw red lips
        g.setColor(new Color(200,100,0));
        if (upperlip>0){
        	g.drawArc(smx-mw/2, shift+mr+smy-upperlip, mw, upperlip*2, -180, 180);
        }
        else{
        	g.drawArc(smx-mw/2, shift+mr+smy+upperlip, mw, -upperlip*2, 0, 180);
        }
        
        if (lowerlip>0){
        	g.drawArc(smx-mw/2, shift+mr+smy-lowerlip, mw, lowerlip*2, -180, 180);
        }
        else{
        	g.drawArc(smx-mw/2, shift+mr+smy+lowerlip, mw, -lowerlip*2, 0, 180);
        }
        
        //control the iCat
        //0 eyes height
    	//1 eyebrowSpace between eyes and brows
    	//2 eyebrowOuter height
    	//3 eyebrowInner height
    	//4 mouth width
    	//5 mouth openness
    	//6 mouth twist (positive = happy)
    	//7 teeth visible 
        //8 head tilt
    	//9  p value of emotions
    	//10 a value of emotions
    	//11 d value of emotions
        if (iCat && System.currentTimeMillis()>=nextICatCommandTimer){
        	String command=""+
        	"set-var iCat.Head.UpperLeftLip "+(-emotion[6]*100+(emotion[5]+1)*80)+" -1;"+
			"set-var iCat.Head.UpperRightLip "+(-emotion[6]*100+(emotion[5]+1)*80)+" -1;"+
			"set-var iCat.Head.BottomLeftLip "+(-emotion[6]*100-(emotion[5]+1)*80)+" -1;"+
			"set-var iCat.Head.BottomRightLip "+(-emotion[6]*100-(emotion[5]+1)*80)+" -1;"+
			"set-var iCat.Head.LeftEyeBrow "+((emotion[3]-emotion[2])*50)+" -1;"+
			"set-var iCat.Head.RightEyeBrow "+((emotion[3]-emotion[2])*50)+" -1;"+
			"set-var iCat.Head.LeftEyeLid "+(80+emotion[0]*40)+" -1;"+
			"set-var iCat.Head.RightEyeLid "+(80+emotion[0]*40)+" -1;"+
			"set-var iCat.Head.BothEyesVertical "+(emotion[0]+emotion[1])*30+" -1;"+
        	"set-var iCat.Neck "+(70+30*emotion[8])+" -1;";
        	ICAT.send(command);
        	System.out.println(command);
        	System.out.println(emotion[5]+";"+emotion[6]);
			nextICatCommandTimer=System.currentTimeMillis()+ICATFRAMERATEMS;
    	}
    }
    public void enableIcat(boolean e){
    	iCat=e;
    }
}
