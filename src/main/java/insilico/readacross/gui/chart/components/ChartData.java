package insilico.readacross.gui.chart.components;

import insilico.readacross.gui.chart.components.interpolation.InterpolationDataPoint;
import java.awt.Color;
import java.util.ArrayList;

/**
 * Class for setting and passing data to be put on the chart.
 * 
 * @author Alberto Manganaro (a.manganaro@kode-solutions.net)
 */
public class ChartData {
    private String TitleAxisX;
    private String TitleAxisY;
    private double TargetValue;
    private Color ColorBackground;
    private Color ColorPointFilling;
    private Color ColorPointEdge;
    private final ArrayList<InterpolationDataPoint> Points;
    private final ArrayList<Integer> PointSize;

    public ChartData() {
        TitleAxisX = "";
        TitleAxisY = "";
        TargetValue = 0;
        ColorBackground = Color.WHITE;
        ColorPointFilling = Color.RED;
        ColorPointEdge = Color.BLACK;
        Points = new ArrayList<>();
        PointSize = new ArrayList<>();
    }

    public void AddPoint(double X, double Y, String Name, int Width) {
        Points.add(new InterpolationDataPoint(X,Y,Name));
        PointSize.add(Width);
    }

    public int getPointSize() {
        return Points.size();
    }

    public InterpolationDataPoint getPoint(int index) {
        return Points.get(index);
    }

    public int getPointWidth(int index) {
        return PointSize.get(index);
    }

    /**
     * @return the TitleAxisX
     */
    public String getTitleAxisX() {
        return TitleAxisX;
    }

    /**
     * @param TitleAxisX the TitleAxisX to set
     */
    public void setTitleAxisX(String TitleAxisX) {
        this.TitleAxisX = TitleAxisX;
    }

    /**
     * @return the TitleAxisY
     */
    public String getTitleAxisY() {
        return TitleAxisY;
    }

    /**
     * @param TitleAxisY the TitleAxisY to set
     */
    public void setTitleAxisY(String TitleAxisY) {
        this.TitleAxisY = TitleAxisY;
    }

    /**
     * @return the TargetValue
     */
    public double getTargetValue() {
        return TargetValue;
    }

    /**
     * @param TargetValue the TargetValue to set
     */
    public void setTargetValue(double TargetValue) {
        this.TargetValue = TargetValue;
    }

    /**
     * @return the ColorBackground
     */
    public Color getColorBackground() {
        return ColorBackground;
    }

    /**
     * @param ColorBackground the ColorBackground to set
     */
    public void setColorBackground(Color ColorBackground) {
        this.ColorBackground = ColorBackground;
    }

    /**
     * @return the ColorPointFilling
     */
    public Color getColorPointFilling() {
        return ColorPointFilling;
    }

    /**
     * @param ColorPointFilling the ColorPointFilling to set
     */
    public void setColorPointFilling(Color ColorPointFilling) {
        this.ColorPointFilling = ColorPointFilling;
    }

    /**
     * @return the ColorPointEdge
     */
    public Color getColorPointEdge() {
        return ColorPointEdge;
    }

    /**
     * @param ColorPointEdge the ColorPointEdge to set
     */
    public void setColorPointEdge(Color ColorPointEdge) {
        this.ColorPointEdge = ColorPointEdge;
    }
}
    
