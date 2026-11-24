package insilico.readacross.calculation.endpoints.utils;

import insilico.core.alerts.*;
import insilico.core.exception.GenericFailureException;
import insilico.core.exception.InitFailureException;
import insilico.core.exception.InvalidMoleculeException;
import insilico.readacross.calculation.RaEngine;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.smarts.SmartsPattern;

/**
 *
 * @author User
 */
public class SAEyeIrritationConcert extends AlertBlockFromSMARTS implements iAlertBlock {

    public final static String KEY_ALERT_EYE_IRRITATION_ACTIVE = "eyeirr_act";
    public final static String KEY_ALERT_EYE_IRRITATION_INACTIVE = "eyeirr_inact";

    private Pattern[] SA;

    private final static String ACTIVE = "Irritant";
    private final static String INACTIVE = "NOT Irritant";

    private final static String[][] SMARTS_ACTIVITY = {
        {"CC(CC)S(=O)(=O)O",ACTIVE},
        {"CCCCCCCCCN(CCO)",ACTIVE},
        {"C(C)CCNCCNC",ACTIVE},
        {"CC(=O)CC(C)O",ACTIVE},
        {"CCC(C)(O)C#C",ACTIVE},
        {"ClC=C(Cl)",ACTIVE},
        {"O=[N+]([O-])c1ccc(O)cc1",ACTIVE},
        {"CCCCCCCCC(=O)N(C)",ACTIVE},
        {"CCCCCCCCCN(C)C",ACTIVE},
        {"CCCCCCCCN(C)C",ACTIVE},
        {"COC(=O)CCO",ACTIVE},
        {"CC(C)OCC(C)O",ACTIVE},
        {"C(C)OCC(C)OCCO",ACTIVE},
        {"C(=O)CCl",ACTIVE},
        {"C(O)CCl",ACTIVE},
        {"CCN(CC)CCC",ACTIVE},
        {"BrCC",ACTIVE},
        {"C(CS)C",ACTIVE},
        {"OCCOc1ccccc1",ACTIVE},
        {"CCl",ACTIVE},
        {"CCNCN",ACTIVE},
        {"Oc1c(O)cccc1",ACTIVE},
        {"C(COc1ccccc1)",ACTIVE},
        {"Nc1ccc(O)cc1",ACTIVE},
        {"C(=O)CO",ACTIVE},
        {"OCCO",ACTIVE},
        {"N(C)C",ACTIVE},
        {"S(=O)(=O)O",ACTIVE},
        {"CCC#N",ACTIVE},
        {"CC(=O)O",ACTIVE},
        {"CO",ACTIVE},
        {"OC(=O)C=C",ACTIVE},
        {"CCC(C)O",ACTIVE},
        {"CC=CC=O",ACTIVE},
        {"CC",ACTIVE},
        {"CC(=O)Nc1ccccc1",INACTIVE},
        {"CCCC(CCC)CCC",INACTIVE},
        {"CCCC(C)COCC",INACTIVE},
        {"CCCCOC(=O)c1ccc(cc1)",INACTIVE},
        {"CCc2cc(C)cc(c2)C(C)",INACTIVE},
        {"Nc1c2ccccc2ccc1",INACTIVE},
        {"c2nc(N)nc(N)n2",INACTIVE},
        {"Cc1ccc(NC)cc1",INACTIVE},
        {"COOC(C)(C)C",INACTIVE},
        {"CCC(=CC)C",INACTIVE},
        {"CCCCCCCCCCCCC=C",INACTIVE},
        {"CO[Si]",INACTIVE},
        {"CC(C)(c1ccccc1)C(C)",INACTIVE},
        {"CC(=C)C(=O)OCCO",INACTIVE},
        {"NN",INACTIVE},
        {"Cc1ccccc1C",INACTIVE},
        {"c1c(cccc1)c2ccccc2",INACTIVE},
        {"c1ncc[nH]1",INACTIVE},
        {"Nc1c(C)cccc1",INACTIVE},
        {"C(=O)OCCCCCCCC",INACTIVE},
        {"CC(C)CCCCCCCC",INACTIVE},
        {"COC(=O)c1cc(ccc1)",INACTIVE},
        {"P(Oc1ccccc1)",INACTIVE},
        {"c1c(Cl)cccc1",INACTIVE},
        {"OC=C",INACTIVE},
        {"CC(N)C(=O)",INACTIVE},
        {"[Si](C)(C)",INACTIVE},
        {"c1cccnc1",INACTIVE},
        {"C(=C)c1ccccc1",INACTIVE},
        {"C(=O)CC(=O)",INACTIVE},
        {"OCCC(CC)C(=O)O",INACTIVE},
        {"C(F)(F)C(F)(F)",INACTIVE},
        {"CCCOCC=C",INACTIVE},
        {"CCCCCCCCCCCCCCC",INACTIVE},
        {"CC(C)CCCCCC",INACTIVE},
        {"CNCC=C",INACTIVE},
        {"CCCOC(=O)CCCCC",INACTIVE},
        {"CC(C)COC",INACTIVE},
        {"C(N)C(=O)",INACTIVE},
        {"CCCCCCCCCCCCCCC(=O)",INACTIVE},
        {"C(C)CCC(C)C",INACTIVE},
        {"CC(C=C)",INACTIVE},
        {"CC(=O)N",INACTIVE},
        {"C(=S)(N)",INACTIVE},
        {"C(c1ccccc1)c2ccccc2",INACTIVE},
        {"CCC(C)C",INACTIVE},
        {"c1ccccc1",INACTIVE},
        {"CC(COCCCO)",INACTIVE},
        {"CCCOC",INACTIVE},
        {"CCCCCCCC",INACTIVE},
        {"P(O)",INACTIVE},
        {"Oc1ccccc1",INACTIVE},
        {"c1ccncn1",INACTIVE},
        {"CCc1ccccc1",INACTIVE},
        {"CCN",INACTIVE},
        {"OCCCO",INACTIVE},
        {"OC(=O)O",INACTIVE},
        {"CC(C)OC(=O)",INACTIVE}
    };



    public SAEyeIrritationConcert() throws InitFailureException {
        super(100+ RaEngine.DB_VALUE_TYPE_EYE_IRRITATION, "Rules for Eye Irritation (CONCERT REACH)");
    }
    
    
    @Override
    protected void BuildSAList() throws InitFailureException {

        int idx = 0;

        for (int i=0; i<SMARTS_ACTIVITY.length; i++) {
            Alert curSA = new Alert(BlockIndex, AlertEncoding.BuildAlertId(BlockIndex, (idx+1)));
            curSA.setName("Eye Irritation alert no. " + (i+1));
            curSA.setDescription("Structural alert for eye " + SMARTS_ACTIVITY[i][1] + " compounds, defined by the SMARTS: " + SMARTS_ACTIVITY[i][0] );
            curSA.setSMARTS(SMARTS_ACTIVITY[i][0]);
            if (SMARTS_ACTIVITY[i][1].equalsIgnoreCase(ACTIVE))
                curSA.setBoolProperty(KEY_ALERT_EYE_IRRITATION_ACTIVE, true);
            if (SMARTS_ACTIVITY[i][1].equalsIgnoreCase(INACTIVE))
                curSA.setBoolProperty(KEY_ALERT_EYE_IRRITATION_INACTIVE, true);
            Alerts.add(curSA);
            idx++;
        }


    }
    
    
    @Override
    protected void InitSMARTS() throws InitFailureException {

        int nFragments = SMARTS_ACTIVITY.length;
        SA = new Pattern[nFragments];

        for (int i=0; i<SMARTS_ACTIVITY.length; i++) {
            String s = SMARTS_ACTIVITY[i][0];
            SA[i] = SmartsPattern.create(s, DefaultChemObjectBuilder.getInstance()).setPrepare(false);
        }

    }

    
    @Override
    protected AlertList CalculateSAMatches() throws GenericFailureException {
        AlertList Res = new AlertList();
        
        try {

            int nFragments = SMARTS_ACTIVITY.length;
            
            for (int i=0; i<nFragments; i++) 
                if (SA[i].matches(CurMol.GetStructure()))
                    Res.add((Alert)Alerts.get(i).clone());
            
        } catch (CloneNotSupportedException | InvalidMoleculeException e) {
            return null;
        }
        
        return Res; 
    }


    public void SaveSmartsPNG() {

    }    
}