
package insilico.readacross.calculation.endpoints;

import insilico.readacross.calculation.endpoints.utils.RaColorGradient;
import insilico.core.alerts.iAlertBlock;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.dbValue;
import insilico.core.exception.GenericFailureException;
import insilico.core.exception.InitFailureException;
import insilico.readacross.type.interfaces.iRaObjectInterpolation;
import insilico.readacross.type.interfaces.iRaObjectRootMolecule;
import insilico.readacross.type.interfaces.iRaObjectRule;
import insilico.readacross.type.interfaces.iRaObjectSimMolecule;
import insilico.readacross.type.objects.RaObjectRootMolecule;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public abstract class RaEndpoint {
    
    public final static short SHAPE_UNDEFINED = 0;
    public final static short SHAPE_TRIANGLE_UP = 1;
    public final static short SHAPE_TRIANGLE_DOWN = 2;
    public final static short SHAPE_RECTANGLE = 3;
    
    protected iDBConnector DBconn;
    
    private final String Name;
    protected iAlertBlock[] AlertBlocks;
    protected boolean[] AlertBlockEnabled;
    protected long EndpointId;
    protected boolean Interpolation;
    protected RaColorGradient Gradient;
    protected boolean HasPrediction;
    
    
    public RaEndpoint(iDBConnector DBconn, String Name) throws InitFailureException{
        this.DBconn = DBconn;
        this.Name = Name;
        this.Interpolation = false;
        this.Gradient = null;
        this.HasPrediction = false;
        BuildAlertBlocks();
        BuildValueTypes();
    }
    
    protected abstract void BuildAlertBlocks() throws InitFailureException;
    protected abstract void BuildValueTypes() throws InitFailureException;
    
    public abstract Color getColorForRootMolecule();
    public abstract Color getColorForMolecule(dbValue ExpValue);
    public abstract short getShapeForRule(iRaObjectRule Rule);
    public abstract double getSizeFactorForRule(iRaObjectRule Rule);
    public abstract Color getColorForRule(iRaObjectRule Rule);

    
    public abstract void setAdditionalValues(iRaObjectSimMolecule Molecule);
    
    public abstract void SortRules(ArrayList<iRaObjectRule> Rules);
    
    /**
     * To be overridden if prediction for root molecule is available.
     * 
     * @param Root
     * @return true if a prediction has made
     */
    public String DoPrediction(RaObjectRootMolecule Root)  throws GenericFailureException {
        return "Overall assessmnet: NOT AVAILABLE";
    }

    /**
     * To be overridden if interpolation is available;
     * @param TargetMolecule
     * @param molecules molecules for the interpolation
     * @return 
     */
    public iRaObjectInterpolation getInterpolationNode(iRaObjectRootMolecule TargetMolecule, ArrayList<iRaObjectSimMolecule> molecules) {
        return null;
    }
    
    /**
     * @return the AlertBlocks
     */
    public iAlertBlock[] getAlertBlocks() {
        return AlertBlocks;
    }

    /**
     * @param index
     * @return the AlertBlockEnabled
     */
    public boolean isAlertBlockEnabled(int index) {
        return AlertBlockEnabled[index];
    }

    /**
     * @param index
     * @param value
     */
    public void setAlertBlockEnabled(int index, boolean value) {
        AlertBlockEnabled[index] = value;
    }

    /**
     * @param value 
     */
    public void setAllAlertBlockEnabled(boolean value) {
        for (int i=0; i<AlertBlockEnabled.length; i++)
            AlertBlockEnabled[i] = value;
    }
    
    /**
     * @return the ValueTypes
     */
    public long getEndpointId() {
        return EndpointId;
    }

    /**
     * @return the Name
     */
    public String getName() {
        return Name;
    }
    
    /**
     * Get if the endpoint has interpolation charts or not
     * @return 
     */
    public boolean HasInterpolation() {
        return this.Interpolation;
    }

    public boolean HasGradient() {
        return this.Gradient!=null;
    }
    
    public RaColorGradient getGradient() {
        return this.Gradient;
    }
    
    public boolean HasPrediction() {
        return this.HasPrediction;
    }
    
    public abstract String FormatValue(double Val);
    
}
