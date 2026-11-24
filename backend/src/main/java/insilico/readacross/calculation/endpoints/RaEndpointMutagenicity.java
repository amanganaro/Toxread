package insilico.readacross.calculation.endpoints;

import insilico.mutagenicity_consensus.ismcMutagenicity;
import insilico.readacross.calculation.endpoints.utils.SABenigniBossaMutagenic;
import insilico.core.exception.InitFailureException;
import insilico.core.alerts.builders.SAMutagenCRS4;
import insilico.core.alerts.builders.SAMutagenIRFMN;
import insilico.core.alerts.builders.SAMutagenSarpy;
import insilico.core.alerts.iAlertBlock;
import insilico.core.constant.InsilicoConstants;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.dbDataType;
import insilico.readacross.database.objects.dbValue;
import insilico.core.descriptor.Descriptor;
import insilico.core.descriptor.blocks.Constitutional;
import insilico.core.exception.DescriptorNotFoundException;
import insilico.core.exception.GenericFailureException;
import insilico.core.exception.PropertyNotFoundException;
import insilico.core.model.InsilicoModelConsensusOutput;
import insilico.core.model.iInsilicoModel;
import insilico.core.model.iInsilicoModelConsensus;
import insilico.core.model.runner.InsilicoModelRunnerByMolecule;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
//import insilico.model.mutagenicity.consensus.ismcMutagenicity;
import insilico.readacross.calculation.RaEngine;
import static insilico.readacross.calculation.RaEngine.DB_VALUE_TYPE_MUTAGENICITY;
import insilico.readacross.type.interfaces.iRaObject;
import insilico.readacross.type.interfaces.iRaObjectRule;
import insilico.readacross.type.interfaces.iRaObjectSimMolecule;
import insilico.readacross.type.objects.RaAdditionalValue;
import insilico.readacross.type.objects.RaObjectRootMolecule;
import insilico.readacross.type.objects.RaObjectRule;
import insilico.readacross.type.objects.RaObjectSimMolecule;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author User
 */
public class RaEndpointMutagenicity extends RaEndpoint {

    private final DecimalFormat Df2digits;
    
    private final static short PRED_NA = -999;
    private final static short PRED_CONFLICT = -1;
    private final static short PRED_NON_MUTA = 0;
    private final static short PRED_MUTA = 1;
    
    // Prediction of target molecule
    private short OverallPrediction;
    private double RaScoreMuta;
    private double RaScoreNonMuta;
    private InsilicoModelConsensusOutput QSARConsensus;
    
    private dbDataType EndpointDataType;
    
    
    
    public RaEndpointMutagenicity(iDBConnector DBconn) throws InitFailureException {
        super(DBconn, "Mutagenicity");

        this.DatasetTextFile = "/data/ds_mutagenicity.txt";

        DecimalFormatSymbols InternationalSymbols = new DecimalFormatSymbols();
        InternationalSymbols.setDecimalSeparator('.');         
        Df2digits = new DecimalFormat("0.##", InternationalSymbols);
        this.HasPrediction = true;
        
        RaScoreMuta = 0.0; RaScoreNonMuta = 0.0; 
        OverallPrediction = PRED_NA; QSARConsensus = null;
        
        try {
            ArrayList<dbValue> buf = DBconn.GetAllValuesForEndpoint(DB_VALUE_TYPE_MUTAGENICITY);
            EndpointDataType = DBconn.GetDataType(buf.get(0).DataTypeId);
        } catch (Exception e) {
            System.err.println("Unable to init data type");
        }
    }
    
    
    @Override
    protected void BuildAlertBlocks() throws InitFailureException {

        this.AlertBlocks = new iAlertBlock[4];
        this.AlertBlocks[0] = new SABenigniBossaMutagenic();
        this.AlertBlocks[1] = new SAMutagenSarpy();
        this.AlertBlocks[2] = new SAMutagenIRFMN();
        this.AlertBlocks[3] = new SAMutagenCRS4();
        
        this.AlertBlockEnabled = new boolean[4];
        this.AlertBlockEnabled[0] = true;
        this.AlertBlockEnabled[1] = true;
        this.AlertBlockEnabled[2] = true;
        this.AlertBlockEnabled[3] = true;
        
    }

    
    @Override
    protected void BuildValueTypes() throws InitFailureException {
        this.EndpointId = DB_VALUE_TYPE_MUTAGENICITY;
    }

    @Override
    public Color getColorForRootMolecule() {
        switch (OverallPrediction) {
            case PRED_NA:
                return Color.GRAY;
            case PRED_CONFLICT:
                return Color.YELLOW;
            case PRED_MUTA:
                return new Color(1f, 0, 0, 1f);
            case PRED_NON_MUTA:
                return new Color(0, 1f, 0, 1f);
        }
        return Color.GRAY;
    }    
    
    @Override
    public Color getColorForMolecule(dbValue ExpValue) {
        try {
            double safeness = 1 - ExpValue.Value;
            if (safeness < 0.5f)
                return new Color(1, (float)(2*safeness), 0,1f);
            else
                return new Color((float)(2-2*safeness), 1, 0,1f);        
        } catch (Exception e) {
            return Color.GRAY;
        }
    }

    @Override
    public void SortRules(ArrayList<iRaObjectRule> Rules) {
        
        class MutagenRulesComparator implements Comparator<iRaObjectRule> {
            @Override
            public int compare(iRaObjectRule o1, iRaObjectRule o2) {
                try {
                    double acc1 = o1.getAlert().getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_ACCURACY);
                    double acc2 = o2.getAlert().getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_ACCURACY);
                    if (!o1.getAlert().getBoolProperty(InsilicoConstants.KEY_ALERT_IS_TOXIC))
                        acc1 = 1-acc1;
                    if (!o2.getAlert().getBoolProperty(InsilicoConstants.KEY_ALERT_IS_TOXIC))
                        acc2 = 1-acc2;
                    if (acc1==acc2)
                        return 0;
                    else                                
                        return (acc1>acc2?1:-1);
                } catch (PropertyNotFoundException ex) {
                    return 0;
                }
            }
}        
        Collections.sort(Rules, new MutagenRulesComparator());
    }

    @Override
    public short getShapeForRule(iRaObjectRule Rule) {
        boolean IsToxic = Rule.getAlert().getBoolProperty(InsilicoConstants.KEY_ALERT_IS_TOXIC);
        if (IsToxic)
            return SHAPE_TRIANGLE_DOWN;
        else
            return SHAPE_TRIANGLE_UP;
    }

    @Override
    public double getSizeFactorForRule(iRaObjectRule Rule) {
        try {
            double PValue = (float)Rule.getAlert().getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_FISHER);
            double Factor = (float)(1 - Math.pow(PValue, 2));
            if (Factor < 0.5) Factor = 0.5f;
            return Factor;
        } catch (PropertyNotFoundException e) {
            return 0;
        }
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
        double curDegree;
        try {
            curDegree = Rule.getAlert().getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_ACCURACY);
        } catch (PropertyNotFoundException ex) {
            return new Color(0.8f, 0.8f, 0.8f); 
        }
        boolean IsToxic = Rule.getAlert().getBoolProperty(InsilicoConstants.KEY_ALERT_IS_TOXIC);
        float val = (float)Math.sqrt(1-curDegree);
        if (IsToxic)
            return new Color(1, val, val);
        else
            return new Color(val, 1, val);
    }
 
    
    @Override
    public String DoPrediction(RaObjectRootMolecule Root) throws GenericFailureException {
        
        ArrayList<iRaObjectSimMolecule> Mols = new ArrayList<>();
        HashMap<Long, Integer> MolCardinality = new HashMap <>();
        
        // Iterates on all first level rules
        for (int i=0; i<Root.getChildrenSize(); i++) {
            iRaObject o = Root.getChild(i);
            
            // If it is a first levele molecule, add it to the list
            if (o.getClass() == RaObjectSimMolecule.class)  {
                
                iRaObjectSimMolecule CurMol = (iRaObjectSimMolecule)o;
                
                // Add molecule to list
                boolean toAdd = true;
                for (iRaObjectSimMolecule m : Mols)
                    if (m.getMolecule().Id == CurMol.getMolecule().Id) {
                        toAdd = false; 
                        break;
                    }
                if (toAdd)
                    Mols.add(CurMol);
                
                // Update molecule cardinality
                int CurCard = 0;
                if (MolCardinality.containsKey(CurMol.getMolecule().Id))
                    CurCard = MolCardinality.get(CurMol.getMolecule().Id);
                CurCard++;
                MolCardinality.put(CurMol.getMolecule().Id, CurCard);
            }
                
            // Check if child is some other object (to be skipped)
            if (o.getClass() != RaObjectRule.class) 
                continue;
            
            iRaObjectRule CurRule = (iRaObjectRule)o;
            // Iterates on all sim molecules for the current rule
            for (int k=0; k<CurRule.getChildrenSize(); k++) {
                iRaObject oo = CurRule.getChild(k);
                if (oo.getClass() != RaObjectSimMolecule.class) 
                    continue;
                iRaObjectSimMolecule CurMol = (iRaObjectSimMolecule)oo;
                
                // Check if molecule agrees with SA
                boolean RuleMuta = CurRule.getAlert().getBoolProperty(InsilicoConstants.KEY_ALERT_IS_TOXIC);
                boolean MolMuta;
                try { 
                    ArrayList<dbValue> curExpValues = DBconn.GetAllValuesForMoleculeAndEndpoint(CurMol.getMolecule().Id, getEndpointId());
                    MolMuta = (curExpValues.get(0).Value == 1);
                } catch (Exception ex) { continue; }
                if (RuleMuta != MolMuta)
                    continue;
                
                // Add molecule to list
                boolean toAdd = true;
                for (iRaObjectSimMolecule m : Mols)
                    if (m.getMolecule().Id == CurMol.getMolecule().Id) {
                        toAdd = false; 
                        break;
                    }
                if (toAdd)
                    Mols.add(CurMol);
                
                // Update molecule cardinality
                int CurCard = 0;
                if (MolCardinality.containsKey(CurMol.getMolecule().Id))
                    CurCard = MolCardinality.get(CurMol.getMolecule().Id);
                CurCard++;
                MolCardinality.put(CurMol.getMolecule().Id, CurCard);
            }
        }
        
        // Calculate final RA scores
        RaScoreMuta = 0;
        RaScoreNonMuta = 0;
        double ScoreTot = 0;
        for (iRaObjectSimMolecule m : Mols) {
            int card = MolCardinality.get(m.getMolecule().Id);
            try { 
                boolean curMuta = ( DBconn.GetAllValuesForMoleculeAndEndpoint(m.getMolecule().Id, getEndpointId()).get(0).Value == 1 );
                if (curMuta)
                    RaScoreMuta += card * m.getSimilarity();
                else
                    RaScoreNonMuta += card * m.getSimilarity();
                ScoreTot += card * m.getSimilarity();
            } catch (Exception ex) { continue; }
        }
        RaScoreMuta /= ScoreTot; // normalize
        RaScoreNonMuta /= ScoreTot; // normalize
        
        // Calculate QSAR consensus model
        QSARConsensus = null;
            iInsilicoModelConsensus Cons;
        try {
            Cons = new ismcMutagenicity();
            InsilicoModelRunnerByMolecule Runner = new InsilicoModelRunnerByMolecule();
            for (iInsilicoModel requiredModel : Cons.GetRequiredModels())
                Runner.AddModel(requiredModel, false);
            Runner.AddModel(Cons);
            
            Runner.Run(Root.getMolecule());
            
            QSARConsensus = Runner.GetModelConsensusWrapper(ismcMutagenicity.class).getResult().get(0);
        } catch (InitFailureException ex) {
            throw new GenericFailureException(ex);
        }
        
        // Set overall prediction
        if ( (QSARConsensus != null) && (RaScoreMuta+RaScoreNonMuta > 0)) {
            if (QSARConsensus.getMainResultValue() == 0) {
                if (RaScoreNonMuta > RaScoreMuta)
                    OverallPrediction = PRED_NON_MUTA;
                else
                    OverallPrediction = PRED_CONFLICT;
            } else {
                if (RaScoreNonMuta <= RaScoreMuta)
                    OverallPrediction = PRED_MUTA;
                else
                    OverallPrediction = PRED_CONFLICT;                
            }
        } else {
            OverallPrediction = PRED_NA;
        }
            
        // Build prediction string
        String Pred = "Overall assessment: ";
        switch (OverallPrediction) {
            case PRED_NA: Pred += "NOT AVAILABLE"; break;
            case PRED_CONFLICT: Pred += "CONFLICTING"; break;
            case PRED_MUTA: Pred += "MUTAGENIC"; break;
            case PRED_NON_MUTA: Pred += "NON-MUTAGENIC"; break;
        }
        
        Pred += "\n\n";
        Pred += "Read-Across assessment: ";
        if (RaScoreMuta > RaScoreNonMuta)
            Pred += "Mutagenic\n";
        else
            Pred += "Non-Mutagenic\n";
        Pred += "Read-Across Mutagenic score = " + Df2digits.format(RaScoreMuta) + "\n";
        Pred += "Read-Across Non-Mutagenic score = " + Df2digits.format(RaScoreNonMuta) + "\n";

        Pred += "\n";
        Pred += "QSAR consensus assessment: " + QSARConsensus.getAssessment() + "\n";
        int idx = 0;
        for (String s : QSARConsensus.getResults()) {
            Pred += Cons.GetResultsName()[idx] + " = " + s + "\n";
            idx++;
        }
        
        return Pred;
    }
    
    @Override
    public String FormatValue(double Val) {
        return EndpointDataType.ConvertValue(Val);
    }
    
}
