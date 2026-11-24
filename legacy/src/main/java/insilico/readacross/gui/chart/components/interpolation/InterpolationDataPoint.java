package insilico.readacross.gui.chart.components.interpolation;

/**
 *
 * @author Alberto Manganaro
 */
public class InterpolationDataPoint {

    private InterpolationChartCoordinates Coordinates;
    private int Size;
    private String Mark;
    private boolean Experimental;

    
    public InterpolationDataPoint() {
        Coordinates = new InterpolationChartCoordinates();
        Size = 10;
        Mark = "";
    }

    public InterpolationDataPoint(double x, double y) {
        this();
        Coordinates.X = x; Coordinates.Y = y;
    }

    public InterpolationDataPoint(double x, double y, String PointMark) {
        this(x, y);
        this.Mark = PointMark;
    } 
    
    public InterpolationDataPoint(double x, double y, String PointMark, int size) {
        this(x, y, PointMark);
        this.Size = size;
    } 
    
    
    public InterpolationChartCoordinates GetCoordinates() {
        return Coordinates;
    }
    
    public void SetCoordinates(InterpolationChartCoordinates Coord) {
        Coordinates = Coord;
    }

    public String GetMark() {
        return Mark;
    }
    
    public void SetMark(String PointMark) {
        Mark = PointMark;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int Size) {
        this.Size = Size;
    }

    /**
     * @return the Experimental
     */
    public boolean isExperimental() {
        return Experimental;
    }

    /**
     * @param Experimental the Experimental to set
     */
    public void setExperimental(boolean Experimental) {
        this.Experimental = Experimental;
    }

}
