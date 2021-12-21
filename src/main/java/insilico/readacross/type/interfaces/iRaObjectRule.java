package insilico.readacross.type.interfaces;

import insilico.core.alerts.Alert;
import java.awt.Color;



/**
 * Interface for the Rule RA object
 *
 * TO DO - for now rule object is just a string
 * 
 * @author Alberto Manganaro
 */
public interface iRaObjectRule extends iRaObject {
   
    public void setAlert(Alert alert);
    public Alert getAlert();
    
    public void setShape(short Shape);
    public short getShape();
    
    public void setSizeFactor(double Factor);
    public double getSizeFactor();   
    
    public void setRuleColor(Color color);
    public Color getRuleColor();
    
}
