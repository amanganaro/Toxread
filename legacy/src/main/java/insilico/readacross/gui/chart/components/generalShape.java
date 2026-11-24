package insilico.readacross.gui.chart.components;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 *
 * @author Frank
 */
public class generalShape{
    
    private final float rootRadius;
    private final int ruleRadius;
    private final float maxSimRadius;
    private final float minSimRadius;
    private double radius;

    public static int RECTANGLE = 1;
    public static int CIRCLE = 2;
    public static int ELLIPSE = 3;
    public static int TRIANGLEUP = 4;
    public static int TRIANGLEDOWN = 5;
    public static int SQUARE = 6;
    
    
        private Shape getTriangle(double length,double a,double b,double c, boolean up ){
         if (up){
                double x1=-length/2,y1=-Math.sin(Math.toRadians(-a))*length/2,x2,y2,x3,y3;
                x2=x1+length;
                y2=y1;
                x3=(x1+Math.cos(Math.toRadians(-a))*length);
                y3=(y1+Math.sin(Math.toRadians(-a))*length);
                GeneralPath gp=new GeneralPath();
                gp.moveTo(x1,y1);
                gp.lineTo(x2,y2);
                gp.lineTo(x3,y3);
                gp.closePath();
                return gp;
         }
         else{
                double x1=-length/2,y1=Math.sin(Math.toRadians(-a))*length/2,x2,y2,x3,y3;
                x2=x1+length;
                y2=y1;
                x3=(x1+Math.cos(Math.toRadians(-a))*length);
                y3=(y1-Math.sin(Math.toRadians(-a))*length);
                GeneralPath gp=new GeneralPath();
                gp.moveTo(x1,y1);
                gp.lineTo(x2,y2);
                gp.lineTo(x3,y3);
                gp.closePath();
                return gp;
         }
     }    
    
    public generalShape(float rootRadius, int ruleRadius, float maxSimRadius, float minSimRadius) {
        this.rootRadius = rootRadius;
        this.ruleRadius = ruleRadius;
        this.maxSimRadius = maxSimRadius;
        this.minSimRadius = minSimRadius;
    }
    /**
     * Select the shape for root!
     * @param type which figure
     * @param mul dimension multiplier
     * @return the selected shape
     */
    public Shape getShape(int type, float mul){
            switch(type){
            case 1:
                return new Rectangle(-(int)(rootRadius*mul),-(int)(ruleRadius*0.6f*mul),(int)(ruleRadius*2*mul),(int)(ruleRadius*1.2f*mul));
            case 2:
                return new Ellipse2D.Float(-(int)(rootRadius*mul), -(int)(rootRadius*mul), (int)(rootRadius*mul*2), (int)(rootRadius*mul*2));
            case 3:
                return new Ellipse2D.Float(-(int)(rootRadius*mul), -(int)(rootRadius*mul/12*5), (int)(rootRadius*mul*2), (int)(rootRadius*mul/6*5));
            case 4:
                return getTriangle((int)(rootRadius*mul*2), 60, 60, 60, true);
            case 5:
                return getTriangle((int)(rootRadius*mul*2), 60, 60, 60, false);
            case 6:
                return new Rectangle(-(int)(rootRadius*1.2*mul),-(int)(rootRadius*1.2*mul),(int)(rootRadius*1.2*mul*2),(int)(rootRadius*1.2*mul*2));
            default:
                    return null;
        }
    }
    
    /**
     * Select the shape for molecules
     * @param type which shape to use
     * @param similarity value of similarity
     * @param mul dimension multiplier
     * @return 
     */
    public Shape getShape(int type, double similarity, float mul){
        radius =  Math.pow(similarity, 2) * maxSimRadius;
        if (radius < minSimRadius)
            radius = minSimRadius;
        //System.out.println("Radius: "+radius*mul);
         switch(type){
            case 1:
                return new Rectangle(-(int)(radius*mul),-(int)(radius/12*5*mul),(int)(radius*2*mul),(int)(radius/6*5*mul));
            case 2:
                return new Ellipse2D.Float(-(float)(radius*mul), -(float)(radius*mul), (float)(radius*mul*2), (float)(radius*mul*2));
            case 3:
                return new Ellipse2D.Float(-(float)(radius*mul), -(float)(ruleRadius/12*5*mul), (float)(radius*mul*2), (float)(ruleRadius/6*5*mul));
            case 4:
                return getTriangle(radius*2*mul, 60, 60, 60, true);
            case 5:
                return getTriangle(radius*2*mul, 60, 60, 60, false);
            case 6:
                return new Rectangle(-(int)(radius*mul),-(int)(radius*mul),-(int)(radius*mul*2),-(int)(radius*mul*2));
            default:
                    return null;
        }          
    }
    /**
     * Select the shape for rule!
     * @param type which shape to use
     * @return 
     */
    public Shape getShape(int type){
        switch(type){
            case 1:
                return new Rectangle(-ruleRadius,-ruleRadius/12*5,ruleRadius*2,ruleRadius/6*5);
            case 2:
                return new Ellipse2D.Float(-ruleRadius, -ruleRadius, ruleRadius*2, ruleRadius*2);
            case 3:
                return new Ellipse2D.Float(-ruleRadius, -ruleRadius/12*5, ruleRadius*2, ruleRadius/6*5);
            case 4:
                return getTriangle(ruleRadius*2, 60, 60, 60, true);
            case 5:
                return getTriangle(ruleRadius*2, 60, 60, 60, false);
            case 6:
                return new Rectangle(-ruleRadius,-ruleRadius,ruleRadius*2,ruleRadius*2);
            default:
                    return null;
        }
        
    }
    
    public double getRadius() {
        return radius;
    }
}
    