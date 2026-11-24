package insilico.readacross.calculation.endpoints;

import insilico.core.alerts.builders.SACombaseAlgae;
import insilico.core.alerts.iAlertBlock;
import insilico.core.constant.InsilicoConstants;
import insilico.core.descriptor.Descriptor;
import insilico.core.descriptor.blocks.Constitutional;
import insilico.core.exception.DescriptorNotFoundException;
import insilico.core.exception.InitFailureException;
import insilico.core.exception.PropertyNotFoundException;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.readacross.calculation.RaEngine;
import insilico.readacross.calculation.endpoints.utils.MeylanLogP;
import insilico.readacross.calculation.endpoints.utils.RaColorGradient;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.dbDataType;
import insilico.readacross.database.objects.dbValue;
import insilico.readacross.type.interfaces.iRaObjectRule;
import insilico.readacross.type.interfaces.iRaObjectSimMolecule;
import insilico.readacross.type.objects.RaAdditionalValue;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static insilico.readacross.calculation.endpoints.RaEndpointFishCombase.ConvertValueForGradient;

/**
 *
 * LogP (Octanol/Water partition coefficient) is not a real endpoint used in the application, it is
 * only created to load the logP data in the DB
 *
 * @author User
 */
public class RaEndpointLogP extends RaEndpoint {

    private final DecimalFormat Df3digits;
    private dbDataType EndpointDataType;


    public RaEndpointLogP(iDBConnector DBconn) throws InitFailureException {
        super(DBconn, "LogP");

        this.DatasetTextFile = "/data/ds_logp.txt";

        DecimalFormatSymbols InternationalSymbols = new DecimalFormatSymbols();
        InternationalSymbols.setDecimalSeparator('.');
        Df3digits = new DecimalFormat("0.###", InternationalSymbols);

        try {
            ArrayList<dbValue> buf = DBconn.GetAllValuesForEndpoint(RaEngine.DB_VALUE_TYPE_LOGP);
            EndpointDataType = DBconn.GetDataType(buf.get(0).DataTypeId);
        } catch (Exception e) {
            System.err.println("Unable to init data type");
        }        
    }
    
    
    @Override
    protected void BuildAlertBlocks() throws InitFailureException {
        this.AlertBlocks = new iAlertBlock[0];
        this.AlertBlockEnabled = new boolean[0];
    }

    
    @Override
    protected void BuildValueTypes() throws InitFailureException {
        this.EndpointId = RaEngine.DB_VALUE_TYPE_LOGP;
    }

    @Override
    public Color getColorForMolecule(dbValue ExpValue) {
        return Color.GRAY;
    }
 

    @Override
    public void SortRules(ArrayList<iRaObjectRule> Rules) {
        

        
    }

    @Override
    public short getShapeForRule(iRaObjectRule Rule) {
        return SHAPE_RECTANGLE;
    }

    @Override
    public double getSizeFactorForRule(iRaObjectRule Rule) {
        return 1;
    }

    @Override
    public void setAdditionalValues(iRaObjectSimMolecule Molecule) {
        

    }
    
    @Override
    public Color getColorForRule(iRaObjectRule Rule) {
        return new Color(0.8f, 0.8f, 0.8f);
    }

    @Override
    public Color getColorForRootMolecule() {
        return Color.GRAY;
    }
    
    @Override
    public String FormatValue(double Val) {
        return Df3digits.format(Val) + " " + EndpointDataType.Units;
    }
    
}
