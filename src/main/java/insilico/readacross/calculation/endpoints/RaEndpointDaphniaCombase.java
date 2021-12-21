package insilico.readacross.calculation.endpoints;

import insilico.readacross.calculation.endpoints.utils.MeylanLogP;
import insilico.readacross.calculation.endpoints.utils.RaColorGradient;
import insilico.core.alerts.builders.SACombaseDaphnia;
import insilico.core.exception.InitFailureException;
import insilico.core.alerts.iAlertBlock;
import insilico.core.constant.InsilicoConstants;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.dbDataType;
import insilico.readacross.database.objects.dbValue;
import insilico.core.descriptor.Descriptor;
import insilico.core.descriptor.blocks.Constitutional;
import insilico.core.exception.DescriptorNotFoundException;
import insilico.core.exception.PropertyNotFoundException;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.readacross.calculation.RaEngine;
import static insilico.readacross.calculation.endpoints.RaEndpointFishCombase.ConvertValueForGradient;
import insilico.readacross.type.interfaces.iRaObjectRule;
import insilico.readacross.type.interfaces.iRaObjectSimMolecule;
import insilico.readacross.type.objects.RaAdditionalValue;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author User
 */
public class RaEndpointDaphniaCombase extends RaEndpoint {

    private final static double DAP_MIN = -1; // 0.1 mg/L
    private final static double DAP_MID = 1; // 10 mg/L
    private final static double DAP_MAX = 2; // 100 mg/L
    private final DecimalFormat Df2digits;
    private final DecimalFormat Df3digits;
    
    private final MeylanLogP LogP;
    
    private dbDataType EndpointDataType;
    
    
    public RaEndpointDaphniaCombase(iDBConnector DBconn) throws InitFailureException {
        super(DBconn, "Daphnia Magna");
        Interpolation = false;

        LogP = new MeylanLogP();
        
        Gradient = new RaColorGradient(new Color(0,255,0), Color.YELLOW, new Color(255,0, 0), (Math.pow(10, DAP_MAX) + " mg/L"), (Math.pow(10, DAP_MID) + " mg/L"), (Math.pow(10, DAP_MIN) + " mg/L"));
        
        DecimalFormatSymbols InternationalSymbols = new DecimalFormatSymbols();
        InternationalSymbols.setDecimalSeparator('.');         
        Df2digits = new DecimalFormat("0.##", InternationalSymbols);
        Df3digits = new DecimalFormat("0.###", InternationalSymbols);
        
        try {
            ArrayList<dbValue> buf = DBconn.GetAllValuesForEndpoint(RaEngine.DB_VALUE_TYPE_DAPHNIA_COMBASE);
            EndpointDataType = DBconn.GetDataType(buf.get(0).DataTypeId);
        } catch (Exception e) {
            throw new InitFailureException("Unable to init data type");
        }        
    }
    
    
    @Override
    protected void BuildAlertBlocks() throws InitFailureException {

        this.AlertBlocks = new iAlertBlock[1];
        this.AlertBlocks[0] = new SACombaseDaphnia();
        
        this.AlertBlockEnabled = new boolean[1];
        this.AlertBlockEnabled[0] = true;
                
    }

    
    @Override
    protected void BuildValueTypes() throws InitFailureException {
        this.EndpointId = RaEngine.DB_VALUE_TYPE_DAPHNIA_COMBASE;
    }

    @Override
    public Color getColorForMolecule(dbValue ExpValue) {
        try {
            double Value = ConvertValueForGradient(DAP_MIN, DAP_MID, DAP_MAX, ExpValue.Value);
            return Gradient.Calculate(Value);
        } catch (Exception e) {
            return Color.GRAY;
        }
    }
 

    @Override
    public void SortRules(ArrayList<iRaObjectRule> Rules) {
        
        class BCFRulesComparator implements Comparator<iRaObjectRule> {
            @Override
            public int compare(iRaObjectRule o1, iRaObjectRule o2) {
                try {
                    double acc1 = o1.getAlert().getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_MEAN);
                    double acc2 = o2.getAlert().getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_MEAN);
                    if (acc1==acc2)
                        return 0;
                    else                                
                        return (acc1>acc2?1:-1);
                } catch (PropertyNotFoundException ex) {
                  return 0;
            }
            }
}        
        Collections.sort(Rules, new BCFRulesComparator());
        
    }

    @Override
    public short getShapeForRule(iRaObjectRule Rule) {
        if (Rule.getAlert().getBoolProperty(InsilicoConstants.KEY_ALERT_IS_UPPER_THRESHOLD))
            return SHAPE_TRIANGLE_DOWN;
        else if (Rule.getAlert().getBoolProperty(InsilicoConstants.KEY_ALERT_IS_LOWER_THRESHOLD))
            return SHAPE_TRIANGLE_UP;
        else
            return SHAPE_RECTANGLE;
    }

    @Override
    public double getSizeFactorForRule(iRaObjectRule Rule) {
        return 1;
    }

    @Override
    public void setAdditionalValues(iRaObjectSimMolecule Molecule) {
        
        // Molecular Weight
        
        double MW = 0;
        InsilicoMolecule Mol = null;
        
        try {
            String SMILES = DBconn.GetStructure(Molecule.getMolecule().StructureId).SMILES;
            Mol = SmilesMolecule.Convert(SMILES);
            if (Mol.IsValid()) {
                Constitutional Desc = new Constitutional();
                Desc.Calculate(Mol);
                try {
                    // Note: MW is calculated on carbon-scaled values
                    MW = Desc.GetByName("MW").getValue() * 12.011;
                } catch (DescriptorNotFoundException ex) {
                    MW = Descriptor.MISSING_VALUE;
                }
            }
        } catch (Exception ex) {
            MW = 0;
        }
        
        Molecule.addAdditionalValue(new RaAdditionalValue(RaEngine.ADD_VALUE_TYPE_MW, 
                "Molecular Weight", MW, Df2digits.format(MW)));

        
        // LogP

        Double ExpLogP = null;
        
        try {
            ArrayList<dbValue> res = DBconn.GetAllValuesForMoleculeAndEndpoint(Molecule.getMolecule().Id, RaEngine.DB_VALUE_TYPE_LOGP);
            if (!res.isEmpty())
                ExpLogP = res.get(0).Value;
        } catch (Exception e) {
            ExpLogP = null;
        }
        
        if (ExpLogP != null) {
            
            // Experimental is available
            Molecule.addAdditionalValue(new RaAdditionalValue(RaEngine.ADD_VALUE_TYPE_LOGP_EXP, "LogP (experimental)", ExpLogP, String.valueOf(ExpLogP)));
            
        } else {
            
            // LogP must be predicted
            if (Mol != null) {
                LogP.Calculate(Mol);
                try {
                    if (LogP.GetByName("LogP").getValue() != Descriptor.MISSING_VALUE)
                        Molecule.addAdditionalValue(new RaAdditionalValue(RaEngine.ADD_VALUE_TYPE_LOGP_PRED, "LogP (calculated)", LogP.GetByName("LogP").getValue(), LogP.GetByName("LogP").getFormattedValue()));
                } catch (DescriptorNotFoundException ex) {
                    //
                }
            }
        }
        
    }
    
    @Override
    public Color getColorForRule(iRaObjectRule Rule) {
        try {
            double Value = ConvertValueForGradient(DAP_MIN, DAP_MID, DAP_MAX, (Rule.getAlert().getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_MEAN)));
            return Gradient.Calculate(Value);
        } catch (PropertyNotFoundException ex) {
            return new Color(0.8f, 0.8f, 0.8f); 
        }
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
