/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package insilico.readacross.gui.chart.components;

import edu.uci.ics.jung.visualization.VisualizationServer;
import static insilico.readacross.gui.chart.RaChart.DIAGONAL_LEFT;
import static insilico.readacross.gui.chart.RaChart.DIAGONAL_RIGHT;
import static insilico.readacross.gui.chart.RaChart.HORIZONTAL;
import static insilico.readacross.gui.chart.RaChart.VERTICAL;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Set;

/**
 *
 * @author Frank
 */
public class VisViewPaintable implements VisualizationServer.Paintable{
    private final int direction;
    private final float topColorAmount;
    private Color topColor;
    private Color bottomColor;
    private Color mediumColor;
    private int width;
    private int height;
    private boolean highlightMolecules;
    private double initialDiameter,scale;
    private PanelRuleLegend prl;
    private boolean highlightRules;
    private Set<Point2D> moleculesCoord;
    private Set<Point2D> ruleCoord;
    private final Color flatBackgroundColor;
    private final Boolean gradientBackGround;
    private Color textColor = Color.RED;
    private String topLabel = "Max";
    private String midLabel = "Mid";
    private String bottomLabel = "Min";
    private TransPickMousePlugin tpmp;
    private final Color highlightMoleculeColor;
    private boolean showGradientBar = false;
    private Color gradientBottomColor;
    private Color gradientTopColor;
    private Color gradientMidColor;
    
    /**
     *
     * @param diam
     */
    public void setDiameter(double diam) {
        this.initialDiameter = diam;
        scaleDiam(1);
    }
    
    /**
     * 
     * @param scale 
     */
    public void scaleDiam(double scale) {
        this.scale = scale;
    }
   
    /**
     * 
     * @param direction
     * @param topColorAmount
     * @param topColor
     * @param bottomColor
     * @param highlightMoleculeColor
     * @param flatBackground
     * @param flatBackgroundColor 
     */
    public VisViewPaintable(int direction, float topColorAmount, Color topColor, Color bottomColor, Color highlightMoleculeColor, boolean flatBackground, Color flatBackgroundColor) {
        this.direction = direction;
        this.topColorAmount = topColorAmount;
        this.topColor = topColor;
        this.bottomColor = bottomColor;
        this.highlightMoleculeColor = highlightMoleculeColor;
        this.flatBackgroundColor = flatBackgroundColor;
        this.gradientBackGround = flatBackground;
    }
    
    /**
     * 
     * @param topLabel
     * @param bottomLabel
     * @param minColor
     * @param medColor
     * @param maxColor 
     * @param textColor 
     */
    public void setGradientBar(String topLabel, String midLabel, String bottomLabel, Color minColor, Color medColor, Color maxColor, Color textColor ){
        this.topLabel = topLabel;
        this.midLabel = midLabel;
        this.bottomLabel = bottomLabel;
        this.gradientBottomColor = minColor;
        this.gradientTopColor = maxColor;
        this.gradientMidColor = medColor;
        this.textColor = textColor;
    }
    
    /**
    * 
    * @param g 
    */ 
    @Override
    public void paint(Graphics g) {
        
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        GradientPaint paint = null;
        Graphics2D g2d = (Graphics2D) g;
        
        Font curFont = new Font("Arial", Font.PLAIN, 11);
        g.setFont(curFont);
        
        if(gradientBackGround){
            switch( direction )
            {
                case HORIZONTAL :
                {
                    paint = new GradientPaint( 0, height, topColor, width*topColorAmount, height, bottomColor, false );
                    break;
                }
                case VERTICAL :
                {
                    paint = new GradientPaint( width, 0, topColor, width, height*topColorAmount, bottomColor, false );
                    break;
                }
                case DIAGONAL_LEFT :
                {   
                    paint = new GradientPaint( 0, 0, topColor, width*topColorAmount, height*topColorAmount, bottomColor,false);
                    break;
                }
                case DIAGONAL_RIGHT :
                {
                    paint = new GradientPaint( width*topColorAmount, 0, topColor, 0, height*topColorAmount, bottomColor, false );
                    break;
                }
            } 
            g2d.setRenderingHints(rh);
            g2d.setPaint(paint);
            g2d.fillRect(0, 0, width, height);
        }
        else{
            g2d.setColor(flatBackgroundColor);
            g2d.fillRect(0, 0, width, height);
            
        }
                    /*if (showLeg)
                    {
                        String str = "Legend";
                        Color textColor = Color.BLACK;
                        g.setFont(new Font("Arial", Font.BOLD, 15));
                        g.setColor(textColor);
                        g.drawString(str,5,25);
                        
                        g.setFont(new Font("Arial", Font.PLAIN, 15));
                        int row = 25+25;
                        Iterator<iRaObjectRule> keySetIterator = legend.keySet().iterator();
                        while(keySetIterator.hasNext()){
                            iRaObjectRule rule = keySetIterator.next();
                            str = legend.get(rule)+ " - "+ rule.getAlert().getDescription();
                            if (shortString)
                                str = str.substring(0, 20) + "...";
                            g.drawString(str,5,row);
                            row+=20;
                        }
      */     
        if(showGradientBar){
            int h = 200;
            int w = 20;
            
            // Background box for black outline
            g.setColor(Color.BLACK);
            g2d.fillRect(5-1, 5-1, w+2, (h/2)+2);
            g2d.fillRect(5-1, (h/2)-1, w+2, (h/2)+2);
            
            // Gradient filling
            GradientPaint primary = new GradientPaint(5, 5, gradientTopColor, w, h/2, gradientMidColor);
            g2d.setPaint(primary);
            g2d.fillRect(5, 5, w, h/2);
            primary = new GradientPaint(5, h/2, gradientMidColor, w, h, gradientBottomColor);
            g2d.setPaint(primary);
            g2d.fillRect(5, h/2, w, h/2);

            // Text
            FontMetrics metrics = g.getFontMetrics(curFont);
            g.setColor(textColor);
            g.drawString(topLabel,35,metrics.getHeight());
            g.drawString(midLabel,35,h/2);
            g.drawString(bottomLabel,35,h);
        }

        
        if (highlightMolecules){
            moleculesCoord = tpmp.getCoords();
            for (Point2D coord1 : moleculesCoord) {
                Ellipse2D.Double circle = new Ellipse2D.Double(coord1.getX() - (initialDiameter*scale*1.2), coord1.getY() - (initialDiameter*scale*1.2), initialDiameter*scale*1.2*2, initialDiameter*scale*1.2*2);
                g2d.setColor(highlightMoleculeColor);
                g2d.setStroke(new BasicStroke((float) (5*scale)));
             g2d.draw(circle);
            }
        }
        
        
        if (highlightRules){
            ruleCoord = prl.getCoords();
            for (Point2D coord1 : ruleCoord) {
                Ellipse2D.Double circle = new Ellipse2D.Double(coord1.getX() - (initialDiameter*scale*1.2), coord1.getY() - (initialDiameter*scale*1.2), initialDiameter*scale*1.2*2, initialDiameter*scale*1.2*2);
                g2d.setColor(highlightMoleculeColor);
                g2d.setStroke(new BasicStroke((float) (5*scale)));
                g2d.draw(circle);
            }
        }
    }

    /**
     * 
     * @return 
     */
    @Override
    public boolean useTransform() {
        return false;
    }
    
    /**
     * 
     * @param height
     * @param width 
     */
    public void setVisServDimension(int height, int width){
        this.height = height;
        this.width = width;
    }
    
//    public void showLegend(boolean showLeg){
//        this.showLeg = showLeg;
//    }

    
//    public void setLegend(HashMap<iRaObjectRule, String> legend){
//        this.legend = legend;
//    }
    
    /**
     * 
     * @param showGradientBar 
     */
    public void showGradientBar(boolean showGradientBar){
      this.showGradientBar = showGradientBar;
    }
    
    /**
     *
     * @param setHiglightMolecules
     */
    public void setHighligthMolecules(boolean setHiglightMolecules){
        this.highlightMolecules = setHiglightMolecules;
    }
    
    public void setHighlightRules(boolean h){
        this.highlightRules = h;
    }

    public void setTransPickMouse(TransPickMousePlugin tpmp) {
        this.tpmp = tpmp;
    }
    
    public void setPanelRuleLegend(PanelRuleLegend prl){
        this.prl = prl;
    }
}
