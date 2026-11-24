package insilico.readacross.gui.chart.components.interpolation;

import java.util.ArrayList;

/**
 *
 * @author Alberto Manganaro
 */
public class InterpolationChartAxis {

    private String NameX;
    private String NameY;
    
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    
    private ArrayList<Double> ticksX;
    private ArrayList<Double> ticksY;
    
    
    public InterpolationChartAxis() {
        
        NameX = "";
        NameY = "";
        
        SetRangeX(0, 100);
        SetRangeY(0, 100);
        
    }

    
    public void SetRangeX(double minValue, double maxValue) {
        minX = minValue;
        maxX = maxValue;
        CalculateTicksX(); 
    }
    
    
    public void SetRangeY(double minValue, double maxValue) {
        minY = minValue;
        maxY = maxValue;
        CalculateTicksY(); 
    }
    

    private void CalculateTicksX() {

        ticksX = new ArrayList<Double>();

        // calculates ticks for X axis
        int t = (int) (maxX-minX);
        double step=0;
        if (t<1)
            step = 0.1;
        else if (t<5)
            step = 0.5;
        else if (t<20)
            step = 1;
        else 
            step = 5;
        
        boolean cond=true;

        int bufTick = (int)Math.floor(minX) * 10;
        while (cond) {
            if ((bufTick>=(minX*10)) && (bufTick%(step*10)==0))
                cond = false;
            else 
                bufTick += 1;
        }
        double curTick = bufTick/10.0;
        
        cond = true;
        
        while (cond) {
            ticksX.add(curTick);
            curTick += step;
            if (curTick>maxX)
                cond = false;
        }
    }
    
    
    private void CalculateTicksY() {
        
        ticksY = new ArrayList<Double>();
        
        // calculates ticks for Y axis
        int t = (int) (maxY-minY);
        double step=0;
        if (t<1)
            step = 0.1;
        else if (t<5)
            step = 0.5;
        else if (t<20)
            step = 1;
        else 
            step = 5;
        
        boolean cond=true;

        int bufTick = (int)Math.floor(minY) * 10;
        while (cond) {
            if ((bufTick>=(minY*10)) && (bufTick%(step*10)==0))
                cond = false;
            else 
                bufTick += 1;
        }
        double curTick = bufTick/10.0;
        
        cond = true;
        
        while (cond) {
            ticksY.add(curTick);
            curTick += step;
            if (curTick>maxY)
                cond = false;
        }
        
    }
    
    
    public double GetMinX() {
        return minX;
    }
            
    
    public double GetMinY() {
        return minY;
    }
            
    
    public double GetMaxX() {
        return maxX;
    }
            
    
    public double GetMaxY() {
        return maxY;
    }
           
    
    public String GetNameX() {
        return NameX;
    }
    
    
    public String GetNameY() {
        return NameY;
    }
    
    
    public void SetNameX(String Name) {
        NameX = Name;
    }
    
    
    public void SetNameY(String Name) {
        NameY = Name;
    }

    
    public ArrayList<Double> GetTicksX() {
        return ticksX;
    }

    
    public ArrayList<Double> GetTicksY() {
        return ticksY;
    }
    
}
