package insilico.readacross.gui.chart.components.interpolation;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Alberto Manganaro
 */
public class InterpolationDataSet {

    private final ArrayList<InterpolationDataPoint> Points;
    private InterpolationChartCoordinates TargetLine;
    
    public Color PointColor;
    public Color PointColorFilling;
    public Color PointColorFillingExp;
    public Color MarkColor;
    public Color TargetLineColor;
    public int MarkSize;

    public double MinX;
    public double MinY;
    public double MaxX;
    public double MaxY;


    public InterpolationDataSet() {
        Points = new ArrayList<>();
        TargetLine = null;
        MinX=0; MinY=0; MaxX=0; MaxX=0;

        // Default settings
        PointColor = Color.BLACK;
        PointColorFilling = Color.LIGHT_GRAY;
        PointColorFillingExp = Color.BLACK;
        MarkColor = Color.BLACK;
        TargetLineColor = Color.BLUE;
        MarkSize = 11;
        
    }

    
    public int Count() {
        return Points.size();
    }

    
    public void Add(InterpolationDataPoint point) {

        Points.add(point);
        CheckDataSetBoundaries();
        
    }

    public InterpolationDataPoint GetPoint(int index) {
        if ((index>=0) && (index<Points.size()))
            return Points.get(index);
        else
            return null;
    }

    public void SetPoint(int index, InterpolationDataPoint point) {
        if ((index>=0) && (index<Points.size()))
            Points.set(index, point);
    }

    public void SetTargetLine(InterpolationChartCoordinates value) {
        this.TargetLine = value;
    }
    
    public InterpolationChartCoordinates GetTargetLine() {
        return this.TargetLine;
    }
    
    private void CheckDataSetBoundaries() {
        
        if (Points.isEmpty()) {
            MinX=0; MaxX=0; MinY=0; MaxY=0;
            return;
        }

        MaxX = Points.get(0).GetCoordinates().X;
        MinX = Points.get(0).GetCoordinates().X;
        MaxY = Points.get(0).GetCoordinates().Y;
        MinY = Points.get(0).GetCoordinates().Y;
        
        for (int i=0; i<Points.size(); i++) {
            InterpolationDataPoint p = Points.get(i);
            if (p.GetCoordinates().X > MaxX) MaxX = p.GetCoordinates().X;
            if (p.GetCoordinates().X < MinX) MinX = p.GetCoordinates().X;
            if (p.GetCoordinates().Y > MaxY) MaxY = p.GetCoordinates().Y;
            if (p.GetCoordinates().Y < MinY) MinY = p.GetCoordinates().Y;
        }
        
        // if target line is available
        if (TargetLine != null) {
            if (TargetLine.X > MaxX) MaxX = TargetLine.X;
            if (TargetLine.X < MinX) MinX = TargetLine.X;            
        }
    }
    
}
