package insilico.readacross.gui.chart.components.interpolation;

/**
 *
 * @author Alberto Manganaro
 */
public class InterpolationChartCoordinates {

    public double X;
    public double Y;
    
    public int canvasX;
    public int canvasY;
    
    
    public InterpolationChartCoordinates() {
        X=0; Y=0;
        canvasX=0; canvasY=0;
    }
    
    public InterpolationChartCoordinates(double x, double y) {
        this();
        X=x;
        Y=y;
    }
    
}
