package org.tudelft.affectbutton.beta;

/**
 *
 * @author  broekens
 */
import java.awt.*;

import org.tudelft.affectbutton.Face;

public class PADFace extends Component implements Face{
    int x,y, size, fdx, fdy;
    int sx, sy, slex, sley, srex, srey, smx, smy, baseMW, baseMH, baseEW, baseEH;
    int eh, eyebrowSpace, eyebrowOuter, eyebrowInner, mw, mt, mo;
    int mx, my; //mouse x and mouse y of user to simulate that the face looks at the user
    double pupilRandomness=0;
    double mouthRandomness=0;
    /** Creates a new instance of PADFace */
    public PADFace(int x, int y, int size) {
        setDimensions(x, y, size);
    	setEmotion(0,0,0,0,0);
    }
    public void setEmotion(double p, double a, double d, int mx, int my) //accepts pad between -1 and 1
    {   this.mx=(mx*(size/20))/size-1;//eye tracking of mouse
    	this.my=(my*(size/20))/size-1;//eye tracking of mouse
    	
        eh=(int)(baseEH*(1+a))+2;
        eyebrowSpace=(int)(baseEH*(1+a)/2)+baseEH/2+1;
        eyebrowOuter=-(int)((p>0?0:-p)*d*(baseEH))/2;//outer side of eyebrow up down according to p and d.
        eyebrowInner=(int)((p>0?0:-p)*d*(baseEH))/2;//inner side is moved up and down according to p and d.
        
        //mouth should be small when -p -a and +d (=frustrated)
        mw=(int)(baseMW*(d+1)/6)+baseMW/2;//the mouth width depends on d and a.
        mo=(int)(baseMH*(d+a+2)/6);//the mouth openness depends also on d and a.
        mt=(int)((baseMH*p)/2);//the mouth twist (corners) depends on p. positive twist means happy
        
        /*
        pupilRandomness=Math.max(0, -a)*2;
        if (p<0)
        	mouthRandomness=Math.max(0, -a)*2;
        else
        	mouthRandomness=0;
        	*/
        //fd{x,y} is the start coordinate of the face within the yellow circle (the skin)
        fdx=0;
        fdy=size/10-(int)((d+a)*(size/20));
        fdx=this.mx;
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
        
       
    }
}
