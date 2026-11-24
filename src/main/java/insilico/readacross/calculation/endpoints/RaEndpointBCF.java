package insilico.readacross.calculation.endpoints;

import insilico.readacross.calculation.endpoints.utils.MeylanLogP;
import insilico.readacross.calculation.endpoints.utils.RaColorGradient;
import insilico.core.alerts.builders.SABCFIRFMN;
import insilico.core.alerts.builders.SABCFIRFMNThreshold;
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
import insilico.readacross.type.interfaces.iRaObjectInterpolation;
import insilico.readacross.type.interfaces.iRaObjectRootMolecule;
import insilico.readacross.type.interfaces.iRaObjectRule;
import insilico.readacross.type.interfaces.iRaObjectSimMolecule;
import insilico.readacross.type.objects.RaAdditionalValue;
import insilico.readacross.type.objects.RaObjectInterpolation;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static insilico.readacross.calculation.RaEngine.DB_VALUE_TYPE_BCF;
import static insilico.readacross.calculation.RaEngine.DB_VALUE_TYPE_MUTAGENICITY;

/**
 *
 * @author User
 */
public class RaEndpointBCF extends RaEndpoint {

    private final static double BCF_MIN = 0;
    private final static double BCF_MID = 3;
    private final static double BCF_MAX = 6;
    private final DecimalFormat Df2digits;
    private final DecimalFormat Df3digits;
    
    private final MeylanLogP LogP;

    private dbDataType EndpointDataType;


    public RaEndpointBCF(iDBConnector DBconn) throws InitFailureException {
        super(DBconn, "BCF");

        this.DatasetTextFile = "/data/ds_bcf.txt";

        Interpolation = true;

        LogP = new MeylanLogP();
        
        Gradient = new RaColorGradient(new Color(0,255,0), Color.YELLOW, new Color(255,0, 0), (BCF_MIN + " log units"), (BCF_MID + " log units"), (BCF_MAX + " log units"));
        
        DecimalFormatSymbols InternationalSymbols = new DecimalFormatSymbols();
        InternationalSymbols.setDecimalSeparator('.');         
        Df2digits = new DecimalFormat("0.##", InternationalSymbols);
        Df3digits = new DecimalFormat("0.###", InternationalSymbols);

        try {
            ArrayList<dbValue> buf = DBconn.GetAllValuesForEndpoint(DB_VALUE_TYPE_BCF);
            EndpointDataType = DBconn.GetDataType(buf.get(0).DataTypeId);
        } catch (Exception e) {
            System.err.println("Unable to init data type");
        }
        
    }
    
    
    @Override
    protected void BuildAlertBlocks() throws InitFailureException {

        this.AlertBlocks = new iAlertBlock[2];
        this.AlertBlocks[0] = new SABCFIRFMN();
        this.AlertBlocks[1] = new SABCFIRFMNThreshold();
        
        this.AlertBlockEnabled = new boolean[2];
        this.AlertBlockEnabled[0] = true;
        this.AlertBlockEnabled[1] = true;
                
    }

    
    @Override
    protected void BuildValueTypes() throws InitFailureException {
        this.EndpointId = RaEngine.DB_VALUE_TYPE_BCF;
    }

    @Override
    public Color getColorForMolecule(dbValue ExpValue) {
        try {
            double Value = (ExpValue.Value - BCF_MIN) / (BCF_MAX - BCF_MIN);
            Value = (Value<0?0:(Value>1?1:Value));
            return Gradient.Calculate(Value);
        } catch (Exception e) {
            return Color.GRAY;
        }
    }
 
    @Override
    public iRaObjectInterpolation getInterpolationNode(iRaObjectRootMolecule TargetMolecule, ArrayList<iRaObjectSimMolecule> molecules) {
        
        iRaObjectInterpolation IntNode = new RaObjectInterpolation();
        
        IntNode.setAxisXTitle("LogP");
        IntNode.setAxisYTitle("BCF");
        
        // Target value
        ArrayList<RaAdditionalValue> values = TargetMolecule.getAdditionalValues();
        for (RaAdditionalValue v : values)
            if ( (v.getType()==RaEngine.ADD_VALUE_TYPE_LOGP_EXP) || (v.getType()==RaEngine.ADD_VALUE_TYPE_LOGP_PRED) )
                IntNode.SetTargetValue(v);
        
        for (iRaObjectSimMolecule mol : molecules) {

            try {
                Double LogPValue = null; 
                boolean isExp = false;
                for (RaAdditionalValue val : mol.getAdditionalValues()) {
                    if (val.getType() == RaEngine.ADD_VALUE_TYPE_LOGP_EXP) {
                        LogPValue = val.getNumericalValue();
                        isExp = true;
                        break;
                    }
                    if (val.getType() == RaEngine.ADD_VALUE_TYPE_LOGP_PRED) {
                        LogPValue = val.getNumericalValue();
                        isExp = false;
                    }
                }
                double BCF = mol.getExperimentalValue().Value;

                if (LogPValue != null) {
                    int PointSize = (int)Math.round(14 * mol.getSimilarity());
                    PointSize = PointSize<6?6:PointSize;
                    IntNode.AddMolecule(mol.getMolecule().CAS, LogPValue, isExp, BCF, PointSize);
                }
            } catch (Exception e) {
                //
            }
        }
        
        return IntNode;
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
        Boolean Threshold = Rule.getAlert().getBoolProperty(InsilicoConstants.KEY_ALERT_IS_UPPER_THRESHOLD);
        if (Threshold)
            return SHAPE_TRIANGLE_DOWN;
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
            double Value = (Rule.getAlert().getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_MEAN) - BCF_MIN) / (BCF_MAX - BCF_MIN);
            Value = (Value<0?0:(Value>1?1:Value));
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
        return Df3digits.format(Val);
    }
    
}
