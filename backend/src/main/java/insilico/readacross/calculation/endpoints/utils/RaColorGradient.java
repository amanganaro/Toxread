package insilico.readacross.calculation.endpoints.utils;

import java.awt.Color;

/**
 *
 * @author Alberto Manganaro (a.manganaro@kode-solutions.net)
 */
public class RaColorGradient {

    private final Color MinColor;
    private final Color MaxColor;
    private final Color MidColor;
    
    private final String MaxTitle;
    private final String MidTitle;
    private final String MinTitle;
    
    
    public RaColorGradient(Color Min, Color Mid, Color Max, String MinName, String MidName, String MaxName) {
        MinColor = Min;
        MidColor = Mid;
        MaxColor = Max;
        MaxTitle = MaxName;
        MidTitle = MidName;
        MinTitle = MinName;
    }

    
    public Color Calculate(double Value) {
        
        int R,G,B;
        
        if (Value < 0.5) {
            Value /= 0.5;
            R = CalculateSingleColor(MinColor.getRed(), MidColor.getRed(), Value);
            G = CalculateSingleColor(MinColor.getGreen(), MidColor.getGreen(), Value);
            B = CalculateSingleColor(MinColor.getBlue(), MidColor.getBlue(), Value);
        } else {
            Value = (Value-0.5) / 0.5;
            R = CalculateSingleColor(MidColor.getRed(), MaxColor.getRed(), Value);
            G = CalculateSingleColor(MidColor.getGreen(), MaxColor.getGreen(), Value);
            B = CalculateSingleColor(MidColor.getBlue(), MaxColor.getBlue(), Value);            
        }
        
        // Check boundaries
        R = (R<0?0:(R>255?255:R));
        G = (G<0?0:(G>255?255:G));
        B = (B<0?0:(B>255?255:B));
        
        return new Color(R, G, B);
    }
    
    
    private int CalculateSingleColor(int Start, int End, double Value) {
        if (Start < End) 
            return Start + (int)Math.round(Value * (End-Start));
        else 
            return Start - (int)Math.round(Value * (Start-End));
    }
    
    /**
     * @return the MinColor
     */
    public Color getMinColor() {
        return MinColor;
    }

    /**
     * @return the MaxColor
     */
    public Color getMaxColor() {
        return MaxColor;
    }

    /**
     * @return the MidColor
     */
    public Color getMidColor() {
        return MidColor;
    }

    /**
     * @return the MaxTitle
     */
    public String getMaxTitle() {
        return MaxTitle;
    }

    /**
     * @return the MinTitle
     */
    public String getMinTitle() {
        return MinTitle;
    }

    /**
     * @return the MidTitle
     */
    public String getMidTitle() {
        return MidTitle;
    }
    
}
