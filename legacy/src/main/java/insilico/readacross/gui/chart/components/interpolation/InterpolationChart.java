package insilico.readacross.gui.chart.components.interpolation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * InterpolationChart Component
 * 
 * @author Alberto Manganaro
 */
public class InterpolationChart extends JPanel {

    public boolean DrawMarks;
    public boolean DrawGrids;
    public boolean DrawAxisLegend; 

    public Color BackColorGeneral;
    public Color BackColorChartArea;
    public Color GridColor;
    public Color GridColorOnZeros;

    public ArrayList<InterpolationDataSet> DataSeries;
    public InterpolationChartAxis ChartAxis;

    private int clientWidth;
    private int clientHeight;
    private Rectangle ChartArea;
    private int curFontSize;
    
    private double BoundYMax;
    private double BoundYMin;
    
    private final static float DashSize[] = {8.0f};
    private final static BasicStroke StrokeDashed =
        new BasicStroke(1.0f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER,
                        10.0f, DashSize, 0.0f);
    
    private final DecimalFormat Df2digits;
    
    
    public InterpolationChart(int GraphicsWidth, int GraphicsHeight) {
        super();
        
        clientWidth = GraphicsWidth;
        clientHeight = GraphicsHeight;
        curFontSize = 8;
        BoundYMax = 0;
        BoundYMin = 0;

        DataSeries = new ArrayList<>();
        ChartAxis = new InterpolationChartAxis();

        DrawMarks = true;
        DrawGrids = true;
        DrawAxisLegend = true;
        BackColorGeneral = new Color(0xFF, 0xFF, 0xDD);
        BackColorChartArea = Color.WHITE;
        GridColor = new Color(0xEE, 0xEE, 0xEE);
        GridColorOnZeros = new Color(0xBE, 0xBE, 0xBE);
        
        DecimalFormatSymbols InternationalSymbols = new DecimalFormatSymbols();
        InternationalSymbols.setDecimalSeparator('.');         
        Df2digits = new DecimalFormat("0.##", InternationalSymbols);
        
    }
    
    
    public InterpolationChart() {
        this(400,300);
    }
    
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintChart((Graphics2D)g);
    }    

    
    public void paintChart(Graphics2D g) {

        if (DataSeries.isEmpty())
            return;

        AdaptAxisScaleToDataset();
        
        // definition of chartarea 
        Rectangle d = g.getClipBounds();
        clientWidth = d.width;
        clientHeight = d.height;

        // calculation of font
        long TotSize = clientWidth*clientHeight;
        if (TotSize > 360000)
            curFontSize = 14;
        else if (TotSize > 160000)
            curFontSize = 12;
        else if (TotSize > 40000)
            curFontSize = 10;
        else
            curFontSize = 8;
        
        ChartArea = new Rectangle(45, 30, clientWidth-45-30, clientHeight-45-30);
        
        // color of the background
        g.setColor(BackColorGeneral);
        g.fillRect(0, 0, clientWidth, clientHeight);
        g.setColor(BackColorChartArea);
        g.fillRect(ChartArea.x, ChartArea.y, ChartArea.width, ChartArea.height);
        
        // draws axis
        DrawAxis(g);

        // draws all dataseries
        for (int i=0; i<DataSeries.size(); i++) {
            
            InterpolationDataSet ds = DataSeries.get(i);
            
            // transforms dataset points to canvas points
            TransformDatasetPoints(ds);

            // draws chart points (and marks if needed
            DrawDataSetPoints(ds, g);
            DrawTargetLine(ds, g);
            if (DrawMarks)
                DrawDataSetMarks(ds, g);
        }

    }


    private void AdaptAxisScaleToDataset() {
        double minX=0, minY=0, maxX=0, maxY=0;
        boolean first=true;
        
        if (DataSeries != null) {
            
            for (int i=0; i<DataSeries.size(); i++) {
                InterpolationDataSet ds = DataSeries.get(i);
                if (ds != null) {
                    if (first) {
                        minX = ds.MinX;
                        maxX = ds.MaxX;
                        minY = ds.MinY;
                        maxY = ds.MaxY;
                        first = false;
                    } else {
                        if (ds.MinX < minX)
                            minX = ds.MinX;
                        if (ds.MaxX > maxX)
                            maxX = ds.MaxX;
                        if (ds.MinY < minY)
                            minY = ds.MinY;
                        if (ds.MaxY > maxY)
                            maxY = ds.MaxY;
                    }                    
                }
            }

            double lagX = 0;
            double diam = maxX-minX;
            if (diam < 1)
                lagX = 0.1;
            else if (diam < 10)
                lagX = 0.5;
            else if (diam < 50)
                lagX = 1;
            else
                lagX = 5;

            double lagY = 0;
            diam = maxY-minY;
            if (diam < 1)
                lagY = 0.1;
            else if (diam < 10)
                lagY = 0.5;
            else if (diam < 50)
                lagY = 1;
            else
                lagY = 5;
            
            ChartAxis.SetRangeX(minX - lagX, maxX + lagX);
            ChartAxis.SetRangeY(minY - lagY, maxY + lagY);
            
            BoundYMin = minY;
            BoundYMax = maxY;
        }
    }

    
    /**
     * Scale a X point from data metric to final canvas metric
     * 
     */
    private int ScaleXPoint(double x) {
        
        // scaling from data metrics to relative chart metrics [0,1]
        x = (x - ChartAxis.GetMinX()) / (ChartAxis.GetMaxX() - ChartAxis.GetMinX());

        // from chart metrics to canvas metrics with the right offset
        x = ChartArea.getX() + (x * ChartArea.getWidth());

        return (int)x;
    }
    

    /**
     * Scale a Y point from data metric to final canvas metric
     * 
     */
    private int ScaleYPoint(double y) {
        
        // scaling from data metrics to relative chart metrics [0,1]
        y = (y - ChartAxis.GetMinY()) / (ChartAxis.GetMaxY() - ChartAxis.GetMinY());

        // from chart metrics to canvas metrics with the right offset
        y = ChartArea.getY() + ((1-y) * ChartArea.getHeight());

        return (int)y;
    }
    

    private boolean TransformDatasetPoints(InterpolationDataSet ds) {

        for (int i=0; i<ds.Count(); i++) {

            InterpolationDataPoint p = ds.GetPoint(i);
            
            p.GetCoordinates().canvasX = ScaleXPoint(p.GetCoordinates().X);
            p.GetCoordinates().canvasY = ScaleYPoint(p.GetCoordinates().Y);
            ds.SetPoint(i, p);
            
        }

        if (ds.GetTargetLine() != null)
            ds.GetTargetLine().canvasX = ScaleXPoint(ds.GetTargetLine().X);
            
        return true;
    }
    
    
    
    //// DRAWING of elements ///////////////////////////////////////////
    
    
    private void DrawDataSetPoints(InterpolationDataSet ds, Graphics2D g) {

        if (ds == null) return;
        
        g.setStroke(new BasicStroke());

        for (int i=0; i<ds.Count(); i++) {
            
            InterpolationDataPoint p = ds.GetPoint(i);
            if (p == null) continue;

            int size = p.getSize();
            if (p.isExperimental())
                g.setColor(ds.PointColorFillingExp);
            else
                g.setColor(ds.PointColorFilling);
            g.fillOval(p.GetCoordinates().canvasX - size/2, 
                       p.GetCoordinates().canvasY - size/2, size, size);
            g.setColor(ds.PointColor);
            g.drawOval(p.GetCoordinates().canvasX - size/2, 
                       p.GetCoordinates().canvasY - size/2, size, size);
            
        }
    }


    private void DrawTargetLine(InterpolationDataSet ds, Graphics2D g) {
        
        // Line
        g.setColor(ds.TargetLineColor);
        g.setStroke(StrokeDashed);
        int x = ds.GetTargetLine().canvasX;
        int y0 = ChartArea.y;
        int y1 = ChartArea.y + ChartArea.height;
        g.drawLine(x,y0 + 5,x,y1 - 5);
        
        // Legend
        Font CurFont = new Font("serif", Font.PLAIN, curFontSize);
        g.setFont(CurFont);
        g.setColor(ds.MarkColor);

        int offsetX = 4;
        int y = y0 + 12;
        String msg = "Target logP = " + Df2digits.format(ds.GetTargetLine().X);

        g.drawString(msg, x + offsetX, y);
        
    }
    
    
    private void DrawDataSetMarks(InterpolationDataSet ds, Graphics2D g) {

        if (ds == null) return;

        g.setStroke(new BasicStroke());

        Font CurFont = new Font("serif", Font.PLAIN, curFontSize);
        g.setFont(CurFont);
        g.setColor(ds.MarkColor);
        FontMetrics fm = g.getFontMetrics(CurFont);
        
        for (int i=0; i<ds.Count(); i++) {
            InterpolationDataPoint p = ds.GetPoint(i);
            if (p == null) continue;
            int offsetX = p.getSize()/2 + 3;
            int offsetY = p.getSize()/2 + 3;
            g.drawString(p.GetMark(), p.GetCoordinates().canvasX + offsetX,
                         p.GetCoordinates().canvasY - offsetY);
        }

    }
    
    
    private void DrawAxis(Graphics2D g) {
    
        DrawGridMarksAndTicks(g);
        DrawBoundaries(g);
        DrawXAxis(g);
        DrawYAxis(g);
        
    }
    
    
    private void DrawBoundaries(Graphics2D g) {

        g.setColor(new Color(128, 0, 0, 50));
        int x0 = ChartArea.x;
        int x1 = ChartArea.x + ChartArea.width;

        int y1 = ScaleYPoint(BoundYMax);
        int y2 = ChartArea.y;
        g.fillRect(x0, y2, (x1-x0), (y1-y2));
//        g.drawLine(x0,y,x1,y);

        y1 = ScaleYPoint(BoundYMin);
        y2 = ChartArea.y + ChartArea.height;
        g.fillRect(x0, y1, (x1-x0), (y2-y1));
//        g.drawLine(x0,y,x1,y);
    }
    
    
    private void DrawXAxis(Graphics2D g) {
        
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke());
        int x0 = ChartArea.x;
        int x1 = x0 + ChartArea.width;
        int y = ChartArea.y + ChartArea.height;
        g.drawLine(x0,y,x1,y);
        
        if (DrawAxisLegend) {
            Font CurFont = new Font("arial", Font.PLAIN, curFontSize);
            g.setFont(CurFont);
            g.setColor(Color.BLUE);
            FontMetrics fm = g.getFontMetrics(CurFont);
            g.drawString(ChartAxis.GetNameX(), x1-fm.stringWidth(ChartAxis.GetNameX()),
                         y+6+2*fm.getHeight());
        }
        
    }
    
    
    private void DrawYAxis(Graphics2D g) {
        
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke());
        int y0 = ChartArea.y + ChartArea.height;
        int y1 = ChartArea.y;
        int x = ChartArea.x;
        g.drawLine(x,y0,x,y1);

        if (DrawAxisLegend) {
            Font CurFont = new Font("arial", Font.PLAIN, curFontSize);
            g.setFont(CurFont);
            g.setColor(Color.BLUE);
            FontMetrics fm = g.getFontMetrics(CurFont);
            g.drawString(ChartAxis.GetNameY(), 10, fm.getHeight());
        }
        
    }
    
    
    private void DrawGridMarksAndTicks(Graphics2D g) {
        
        double CurTick;
        String CurTickStr;
        int w, h;

        DecimalFormatSymbols InternationalSymbols = new DecimalFormatSymbols();
        InternationalSymbols.setDecimalSeparator('.');         
        DecimalFormat df_float = new DecimalFormat("0.#", InternationalSymbols);
        
        
        g.setStroke(new BasicStroke());
        Font CurFont = new Font("arial", Font.PLAIN, curFontSize);
        g.setFont(CurFont);
        FontMetrics fm = g.getFontMetrics(CurFont);
        h = fm.getHeight();
        
        for (int i=0; i<ChartAxis.GetTicksX().size(); i++) {
            CurTick = ChartAxis.GetTicksX().get(i);
            if (CurTick == (int)CurTick)
                CurTickStr = Integer.valueOf((int)CurTick).toString();
            else
                CurTickStr = df_float.format(CurTick);
            w = fm.stringWidth(CurTickStr);
            
            int y0 = ChartArea.y + ChartArea.height;
            int y1 = ChartArea.y;
            int x = ScaleXPoint(CurTick);
            
            if (DrawGrids) {
                if (CurTick == 0) 
                    g.setColor(GridColorOnZeros);
                else
                    g.setColor(GridColor);
                g.drawLine(x,y0,x,y1);
            }
            
            g.setColor(Color.BLACK);
            g.drawLine(x,y0,x,y0+2);
            g.drawString(CurTickStr, x-(int)(w/2), y0+6+h);
        }
                
        for (int i=0; i<ChartAxis.GetTicksY().size(); i++) {
            CurTick = ChartAxis.GetTicksY().get(i);
            if (CurTick == (int)CurTick)
                CurTickStr = Integer.valueOf((int)CurTick).toString();
            else
                CurTickStr = df_float.format(CurTick);
            w = fm.stringWidth(CurTickStr);
            
            int x0 = ChartArea.x;
            int x1 = x0 + ChartArea.width;
            int y = ScaleYPoint(CurTick);
            
            if (DrawGrids) {
                if (CurTick == 0) 
                    g.setColor(GridColorOnZeros);
                else
                    g.setColor(GridColor);
                g.drawLine(x0,y,x1,y);
            }
            g.setColor(Color.BLACK);
            g.drawLine(x0-2,y,x0,y);
            g.drawString(CurTickStr, x0-6-w, y+(int)(h/2));
                
        }
                
    }
    
    
}
