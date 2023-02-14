package insilico.readacross.calculation.endpoints.utils;

import insilico.core.alerts.*;
import insilico.core.constant.InsilicoConstants;
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
public class SAAromataseConcert extends AlertBlockFromSMARTS implements iAlertBlock {

    public final static String KEY_ALERT_AROMATASE_ACTIVE = "arom_act";
    public final static String KEY_ALERT_AROMATASE_INACTIVE = "arom_inact";

    private Pattern[] SA;

    private final static String ACTIVE = "Active";
    private final static String INACTIVE = "Inactive";

    private final static String[][] SMARTS_ACTIVITY = {
            {"n1c2ccc(OC)cc2sc1N",ACTIVE,"SA1"},
            {"c1ccc(cc1)c1nc(N)sc1",ACTIVE,"SA2"},
            {"n1c(N)sc2cccc(c12)",ACTIVE,"SA3"},
            {"CCCCN",INACTIVE,"SA4"},
            {"O=S(=O)(N)c1ccc(N)cc1",INACTIVE,"SA5"},
            {"n1ccsc1C",INACTIVE,"SA6"},
            {"O=C(O)CC",INACTIVE,"SA7"},
            {"N=C(N)N",INACTIVE,"SA8"},
            {"n1oc(cc1)",INACTIVE,"SA9"},
            {"n1csc(c1C)",INACTIVE,"SA10"},
            {"O=c1oc2cc(ccc2[nH]1)",INACTIVE,"SA11"},
            {"c1c[n+](cn1CCCCCCCC)C",ACTIVE,"SA12"},
            {"c1c(cccc1Cl)Cl",ACTIVE,"SA13"},
            {"c1ccnn1",INACTIVE,"SA14"},
            {"c1cnc(n1C)C",INACTIVE,"SA15"},
            {"O=c1c2c(ncn2C)n(c(=O)n1)C",INACTIVE,"SA16"},
            {"c1nc[nH]c1C",INACTIVE,"SA17"},
            {"O=C(O)C",INACTIVE,"SA18"},
            {"S(=O)c1ccccc1",INACTIVE,"SA19"},
            {"O=c1ccnc([nH]1)",INACTIVE,"SA20"},
            {"O=C(NC)C",INACTIVE,"SA21"},
            {"O=C(OC)c1cncn1",INACTIVE,"SA22"},
            {"N(C)C",INACTIVE,"SA23"},
            {"OC(C)Cn1cnc2c(ncnc12)N",INACTIVE,"SA24"},
            {"C(OCC)C(C)",ACTIVE,"SA25"},
            {"N#C",ACTIVE,"SA26"},
            {"n1nncc1",INACTIVE,"SA27"},
            {"N(CC)C",INACTIVE,"SA28"},
            {"O=S(=O)(N)",INACTIVE,"SA29"}
    };



    public SAAromataseConcert() throws InitFailureException {
        super(100+ RaEngine.DB_VALUE_TYPE_AROMATASE, "Rules for Aromatase activity (CONCERT REACH)");
    }
    
    
    @Override
    protected void BuildSAList() throws InitFailureException {

        int idx = 0;

        for (int i=0; i<SMARTS_ACTIVITY.length; i++) {
            Alert curSA = new Alert(BlockIndex, AlertEncoding.BuildAlertId(BlockIndex, (idx+1)));
            curSA.setName("Aromatase alert " + SMARTS_ACTIVITY[i][2]);
            curSA.setDescription("Structural alert for aromatase " + SMARTS_ACTIVITY[i][1] + " compounds, defined by the SMARTS: " + SMARTS_ACTIVITY[i][0] );
            if (SMARTS_ACTIVITY[i][1].equalsIgnoreCase(ACTIVE))
                curSA.setBoolProperty(KEY_ALERT_AROMATASE_ACTIVE, true);
            if (SMARTS_ACTIVITY[i][1].equalsIgnoreCase(INACTIVE))
                curSA.setBoolProperty(KEY_ALERT_AROMATASE_INACTIVE, true);
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