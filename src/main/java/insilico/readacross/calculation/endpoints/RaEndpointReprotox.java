package insilico.readacross.calculation.endpoints;

import insilico.core.exception.InitFailureException;
import insilico.core.alerts.builders.SAReadyBioIRFMN;
import insilico.core.alerts.builders.SAReprotoxVermeer;
import insilico.core.alerts.iAlertBlock;
import insilico.core.constant.InsilicoConstants;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.dbDataType;
import insilico.readacross.database.objects.dbValue;
import insilico.core.descriptor.Descriptor;
import insilico.core.descriptor.blocks.Constitutional;
import insilico.core.exception.DescriptorNotFoundException;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.readacross.calculation.RaEngine;
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
public class RaEndpointReprotox extends RaEndpoint {

    private final DecimalFormat Df2digits;
    
    private dbDataType EndpointDataType;
    
    
    public RaEndpointReprotox(iDBConnector DBconn) throws InitFailureException {
        super(DBconn, "Reproductive Toxicity");

        this.DatasetTextFile = "/data/ds_reprotox.txt";

        DecimalFormatSymbols InternationalSymbols = new DecimalFormatSymbols();
        InternationalSymbols.setDecimalSeparator('.');         
        Df2digits = new DecimalFormat("0.##", InternationalSymbols);
        
        try {
            ArrayList<dbValue> buf = DBconn.GetAllValuesForEndpoint(RaEngine.DB_VALUE_TYPE_REPROTOX);
            EndpointDataType = DBconn.GetDataType(buf.get(0).DataTypeId);
        } catch (Exception e) {
            System.err.println("Unable to init data type");
        }
    }
    
    
    @Override
    protected void BuildAlertBlocks() throws InitFailureException {

        this.AlertBlocks = new iAlertBlock[1];
        this.AlertBlocks[0] = new SAReprotoxVermeer();
        
        this.AlertBlockEnabled = new boolean[1];
        this.AlertBlockEnabled[0] = true;
        
    }
    
    @Override
    protected void BuildValueTypes() throws InitFailureException {
        this.EndpointId = RaEngine.DB_VALUE_TYPE_REPROTOX;
    }
    
    @Override
    public Color getColorForMolecule(dbValue ExpValue) {
        if (ExpValue.Value == 0)
            return Color.GREEN;
        else if (ExpValue.Value == 1)
            return Color.RED;
        return Color.GRAY;
    }
    
    @Override
    public void SortRules(ArrayList<iRaObjectRule> Rules) {
        
        // foo - is sorting needed for this EP?
        class MutagenRulesComparator implements Comparator<iRaObjectRule> {
            @Override
            public int compare(iRaObjectRule o1, iRaObjectRule o2) {
                return 1; 
            }
        }          
        Collections.sort(Rules, new MutagenRulesComparator());
    }

    @Override
    public short getShapeForRule(iRaObjectRule Rule) {
        if (Rule.getAlert().getBoolProperty(InsilicoConstants.KEY_ALERT_REPRO_TOXIC))
            return SHAPE_TRIANGLE_DOWN;
        else
            return SHAPE_TRIANGLE_UP;
    }

    
    @Override
    public double getSizeFactorForRule(iRaObjectRule Rule) {
        return 1;
    }

    @Override
    public void setAdditionalValues(iRaObjectSimMolecule Molecule) {
        
        // Molecular Weight
        
        double MW = 0;
        
        try {
            String SMILES = DBconn.GetStructure(Molecule.getMolecule().StructureId).SMILES;
            InsilicoMolecule Mol = SmilesMolecule.Convert(SMILES);
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
        
        RaAdditionalValue value = new RaAdditionalValue(RaEngine.ADD_VALUE_TYPE_MW, 
                "Molecular Weight", MW, Df2digits.format(MW));
        Molecule.addAdditionalValue(value);
    }
    
    @Override
    public Color getColorForRule(iRaObjectRule Rule) {
        if ( Rule.getAlert().getBoolProperty(InsilicoConstants.KEY_ALERT_REPRO_TOXIC) )
            return Color.RED;
        if ( Rule.getAlert().getBoolProperty(InsilicoConstants.KEY_ALERT_REPRO_NONTOXIC) )
            return Color.GREEN;
        return Color.GRAY;    
    }
 
    @Override
    public Color getColorForRootMolecule() {
        return Color.GRAY;
    }    
    
    @Override
    public String FormatValue(double Val) {
        return EndpointDataType.ConvertValue(Val);
    }
    
}
