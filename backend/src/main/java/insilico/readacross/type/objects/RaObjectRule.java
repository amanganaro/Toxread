package insilico.readacross.type.objects;

import insilico.core.alerts.Alert;
import insilico.readacross.calculation.endpoints.RaEndpoint;
import insilico.readacross.type.interfaces.iRaObjectRule;
import java.awt.Color;

/**
 * RA object that contains a rule object
 * 
 * @author Alberto Manganaro
 */
public class RaObjectRule extends RaObject implements iRaObjectRule {

    private Alert Alert;
    private short Shape;
    private double SizeFactor;
    private Color RuleColor;
    
    /**
     * Constructor
     */
    public RaObjectRule() {
        super(RaObject.RA_OBJECT_RULE);
        this.Alert = null;
        this.Shape = RaEndpoint.SHAPE_UNDEFINED;
        this.SizeFactor = 1;
    }
    
    @Override
    public void setAlert(Alert alert) {
        this.Alert = alert;
    }

    @Override
    public Alert getAlert() {
        return this.Alert;
    }

    @Override
    public void setShape(short Shape) {
        this.Shape = Shape;
    }

    @Override
    public short getShape() {
        return this.Shape;
    }

    @Override
    public void setSizeFactor(double Factor) {
        this.SizeFactor = Factor;
    }

    @Override
    public double getSizeFactor() {
        return this.SizeFactor;
    }

    @Override
    public void setRuleColor(Color color) {
        this.RuleColor = color;
    }

    @Override
    public Color getRuleColor() {
        return this.RuleColor;
    }

    
}
