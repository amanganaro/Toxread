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
public class SAReprotoxConcert extends AlertBlockFromSMARTS implements iAlertBlock {

    public final static String KEY_ALERT_EYE_REPRO_ACTIVE = "repro_act";
    public final static String KEY_ALERT_EYE_REPRO_INACTIVE = "repro_inact";
    public final static String KEY_ALERT_EYE_REPRO_REL_MAX = "repro_rel_max";
    public final static String KEY_ALERT_EYE_REPRO_REL_MIN = "repro_rel_min";

    private Pattern[] SA;

    private final static String ACTIVE = "Reprotox";
    private final static String INACTIVE = "NON Reprotox";
    private final static String MAX = "good reliability";
    private final static String MIN = "low reliability";

    private final static String[][] SMARTS_ACTIVITY = {
            {"CCCCCF",ACTIVE,MAX},
            {"CCCCC(C)COCCCCCCO",ACTIVE,MAX},
            {"CCCCCCOCc1ccccc1C",ACTIVE,MAX},
            {"CCCC(CC)c1ccc(O)cc1",ACTIVE,MAX},
            {"NC(CCC=O)C(O)=O",ACTIVE,MAX},
            {"CCn1cc(C(O)=O)c(=O)c2cc(F)c(cc12)N1CCNCC1",ACTIVE,MAX},
            {"CN1CCC(CC1)OC(=O)Cc1ccccc1",ACTIVE,MAX},
            {"OCCCC(=O)c1ccccc1",ACTIVE,MAX},
            {"CCN(C)C(=O)c1ccccc1",ACTIVE,MAX},
            {"CC(C)C=CCCCCC=O",ACTIVE,MAX},
            {"COCCc1ccc(O)cc1",ACTIVE,MAX},
            {"Fc1ccc(C=O)cc1",ACTIVE,MAX},
            {"Oc1ccc(C=Cc2ccccc2)cc1",ACTIVE,MAX},
            {"O=Cc1cccc(c1)S(=O)=O",ACTIVE,MAX},
            {"CCC(O)C(=O)OC",ACTIVE,MAX},
            {"CN(N=O)C(N)=O",ACTIVE,MAX},
            {"O=C1CCC(=O)N1",ACTIVE,MAX},
            {"CNC(=O)CS",ACTIVE,MAX},
            {"ClC(Cl)C=O",ACTIVE,MAX},
            {"CC(N)C1CCC(N)C(OC2C(N)CCCC2O)O1",ACTIVE,MAX},
            {"Clc1ccccc1Cc1ccccc1",ACTIVE,MAX},
            {"Cc1nccc(-c2ccccc2)c1C",ACTIVE,MAX},
            {"CCCCOCc1cccc(O)c1",ACTIVE,MAX},
            {"OCCNCc1cccc(Cl)c1",ACTIVE,MAX},
            {"Nc1nc(N)c2ccccc2n1",ACTIVE,MAX},
            {"Nc1ncnc2ncnc12",ACTIVE,MAX},
            {"CC1OCCC1(C)O",ACTIVE,MAX},
            {"CN(C)C(=O)C=C",ACTIVE,MAX},
            {"FC(F)CCl",ACTIVE,MAX},
            {"CCOC(=O)C1=C(C)NC(C)=C(C1c1ccccc1)C(=O)OC",ACTIVE,MAX},
            {"CC(=O)OC1CCC2C3CCC4=CC(=O)CCC4C3CCC12C",ACTIVE,MAX},
            {"CCC1C(=O)N(N(C1=O)c1ccccc1)c1ccccc1",ACTIVE,MAX},
            {"C(C(c1ccccc1)c1ccccc1)c1ccccc1",ACTIVE,MAX},
            {"Oc1ccc(CCNCCc2ccccc2)cc1",ACTIVE,MAX},
            {"OCCCCCCCC1CCCC1C=CCO",ACTIVE,MAX},
            {"O1c2ccccc2C=Nc2ccccc12",ACTIVE,MAX},
            {"CCCN1CCN(CC1)c1ccccc1",ACTIVE,MAX},
            {"NC(Cc1ccc(O)c(O)c1)C(O)=O",ACTIVE,MAX},
            {"Cc1ccc(C)c2ccccc12",ACTIVE,MAX},
            {"OC(=O)Cc1ccc(O)cc1",ACTIVE,MAX},
            {"NC(=O)OCCCOC(N)=O",ACTIVE,MAX},
            {"COc1cc(Cl)c(Cl)cc1Cl",ACTIVE,MAX},
            {"CCCNCCC(C)(C)CC",ACTIVE,MAX},
            {"CC(C)Oc1ccccc1Cl",ACTIVE,MAX},
            {"CCOP(O)(=S)OCC",ACTIVE,MAX},
            {"O=CCc1cccs1",ACTIVE,MAX},
            {"COP(=S)(OC)SC",ACTIVE,MAX},
            {"CCOC(=O)CC=O",ACTIVE,MAX},
            {"Nc1ccnnc1",ACTIVE,MAX},
            {"CCOS(C)(=O)=O",ACTIVE,MAX},
            {"c1cnncn1",ACTIVE,MAX},
            {"CCNC(S)=S",ACTIVE,MAX},
            {"C1CNC=NC1",ACTIVE,MAX},
            {"CCCC(=O)NCCc1ccccc1",INACTIVE,MAX},
            {"CC(C)(C)NCCc1ccccc1",INACTIVE,MAX},
            {"CCCCOc1ccc(C)cc1",INACTIVE,MAX},
            {"CCCCOCC(C)OC(C)=O",INACTIVE,MAX},
            {"Cc1ccccc1OCCN",INACTIVE,MAX},
            {"NCC1OC(CCC1O)OC1C(N)CC(N)C(O)C1O",INACTIVE,MAX},
            {"CCCCCCCCN(C)CCO",INACTIVE,MAX},
            {"Cc1cc(N)ccc1O",INACTIVE,MAX},
            {"CCCOP(O)(O)=O",INACTIVE,MAX},
            {"Clc1cc2oc3cc(Cl)c(Cl)cc3c2cc1Cl",INACTIVE,MAX},
            {"CN(C)CC(c1ccccc1)c1ccccc1",INACTIVE,MAX},
            {"C(c1ccccc1)n1cnc2ccccc12",INACTIVE,MAX},
            {"CCN(CC)CCNc1ccccc1C",INACTIVE,MAX},
            {"CNCC(C)NC(=O)c1ccccc1",INACTIVE,MAX},
            {"CCC(C)NC(C)Cc1ccccc1",INACTIVE,MAX},
            {"O=Cc1cnc2ccccc12",INACTIVE,MAX},
            {"c1ccc2nccnc2c1",INACTIVE,MAX},
            {"OCNc1ccccc1",INACTIVE,MAX},
            {"Sc1ccc(C=O)cc1",INACTIVE,MAX},
            {"N#Cc1ccccc1",INACTIVE,MAX},
            {"CN(C)C(N)=S",INACTIVE,MAX},
            {"BrCBr",INACTIVE,MAX},
            {"CCCCCCCCCCCCCCC(=O)OCCC",INACTIVE,MAX},
            {"CCOC(=O)C(CCc1ccccc1)NC(C)C=O",INACTIVE,MAX},
            {"OC1CCOC(OC2COC(O)C(O)C2O)C1O",INACTIVE,MAX},
            {"CCCN1c2ccccc2Sc2ccc(S)cc12",INACTIVE,MAX},
            {"CCN(CC)CCNCc1ccc(Cl)cc1",INACTIVE,MAX},
            {"Cc1ccccc1N=Nc1ccccc1",INACTIVE,MAX},
            {"CCN(CC)CCOCc1ccccc1",INACTIVE,MAX},
            {"C(Oc1ccccc1)c1ccccc1",INACTIVE,MAX},
            {"Clc1ccc(Cc2ccccn2)cc1",INACTIVE,MAX},
            {"Nc1ccc(cc1)S(=O)(=O)NC=O",INACTIVE,MAX},
            {"COC(=O)C1C(C=C(C)C)C1(C)C",INACTIVE,MAX},
            {"CCNc1ccc(cc1)S(N)(=O)=O",INACTIVE,MAX},
            {"CNCCC(O)C(O)C(O)CO",INACTIVE,MAX},
            {"OCCCc1ccc(Cl)cc1",INACTIVE,MAX},
            {"CC(C=O)=Cc1ccccc1",INACTIVE,MAX},
            {"Cc1ccc2ccccc2n1",INACTIVE,MAX},
            {"COC(=CC=O)C(=O)C(C)=C",INACTIVE,MAX},
            {"Brc1cc(Br)c(Br)c(Br)c1Br",INACTIVE,MAX},
            {"c1nncn1-c1ccccc1",INACTIVE,MAX},
            {"CCc1cccc(CN)c1",INACTIVE,MAX},
            {"CC1CCC(CN)CC1",INACTIVE,MAX},
            {"CCc1cccc(S)c1",INACTIVE,MAX},
            {"CCCCCSCCN",INACTIVE,MAX},
            {"Nc1cnccn1",INACTIVE,MAX},
            {"CN(C)NC=O",INACTIVE,MAX},
            {"CNC(=N)NC",INACTIVE,MAX},
            {"CCOP(C)=O",INACTIVE,MAX},
            {"CCNNCC",INACTIVE,MAX},
            {"c1cn[nH]c1",INACTIVE,MAX},
            {"OCC=O",INACTIVE,MIN},
            {"c1ccc2ccccc2c1",INACTIVE,MIN},
            {"CC=CCN",INACTIVE,MIN},
            {"CCCCCCCCCCC(C)CC(C)CC",INACTIVE,MIN},
            {"CCCCCCOC",INACTIVE,MIN},
            {"OCc1ccccc1",INACTIVE,MIN},
            {"CCS",INACTIVE,MIN},
            {"COCC=O",INACTIVE,MIN},
            {"NCc1ccccc1",INACTIVE,MIN},
            {"CC=CC(O)=O",INACTIVE,MIN},
            {"c1nc2ccccc2[nH]1",INACTIVE,MIN},
            {"FCF",INACTIVE,MIN},
            {"c1ccc(cc1)-c1ccccc1",INACTIVE,MIN},
            {"CCNCCC(C)CO",INACTIVE,MIN},
            {"c1ccocc1",INACTIVE,MIN},
            {"CCC(C)C(C)N",INACTIVE,MIN},
            {"O=CCC=O",INACTIVE,MIN},
            {"Oc1ccc(C=O)cc1",INACTIVE,MIN},
            {"NS(=O)(=O)c1ccccc1",INACTIVE,MIN},
            {"Cc1ccccc1Cc1ccccc1",INACTIVE,MIN},
            {"Clc1ccc(Cl)c(Cl)c1",INACTIVE,MIN},
            {"Nc1ccc(N)cc1",INACTIVE,MIN},
            {"Cc1ccsc1",INACTIVE,MIN},
            {"CC(C)COC(C)=O",INACTIVE,MIN},
            {"CC[N+](C)(C)C",INACTIVE,MIN},
            {"COC(c1ccccc1)c1ccccc1",INACTIVE,MIN},
            {"C#N",ACTIVE,MIN},
            {"CN(C)CCc1ccccc1",ACTIVE,MIN},
            {"CCCCCCN",ACTIVE,MIN},
            {"OCCCO",ACTIVE,MIN},
            {"CCOCCO",ACTIVE,MIN},
            {"CCCCCCCCC=O",ACTIVE,MIN},
            {"NCCc1cccc(O)c1",ACTIVE,MIN},
            {"Cc1ccc(N)cc1",ACTIVE,MIN},
            {"CCCCCCNCC",ACTIVE,MIN},
            {"CC(C)OCCO",ACTIVE,MIN},
            {"CP(O)=O",ACTIVE,MIN},
            {"CCCNc1ccccc1",ACTIVE,MIN},
            {"CCNP(=O)NCC",ACTIVE,MIN},
            {"CCCNC(=O)NCC",ACTIVE,MIN},
            {"CC1(CC=O)CCCCC1",ACTIVE,MIN},
            {"Nc1ccnc(N)n1",ACTIVE,MIN},
            {"CCCCNC(C)=O",ACTIVE,MIN},
            {"COCCCOC",ACTIVE,MIN},
            {"CN(C)CCNCCO",ACTIVE,MIN},
            {"CCCNC(C)CN",ACTIVE,MIN},
            {"NC(N)=S",ACTIVE,MIN},
            {"CCCN(CCC)CCO",ACTIVE,MIN},
            {"c1nc[nH]n1",ACTIVE,MIN}
    };



    public SAReprotoxConcert() throws InitFailureException {
        super(100+ RaEngine.DB_VALUE_TYPE_REPROTOX_CONCERT, "Rules for Reproductive Toxicity (CONCERT REACH)");
    }
    
    
    @Override
    protected void BuildSAList() throws InitFailureException {

        int idx = 0;

        for (int i=0; i<SMARTS_ACTIVITY.length; i++) {
            Alert curSA = new Alert(BlockIndex, AlertEncoding.BuildAlertId(BlockIndex, (idx+1)));
            curSA.setName("Reprotox alert no. " + (i+1));
            curSA.setDescription("Structural alert for " + SMARTS_ACTIVITY[i][1] + " compounds (" + SMARTS_ACTIVITY[i][2] + "), defined by the SMARTS: " + SMARTS_ACTIVITY[i][0] );
            curSA.setSMARTS(SMARTS_ACTIVITY[i][0]);
            if (SMARTS_ACTIVITY[i][1].equalsIgnoreCase(ACTIVE))
                curSA.setBoolProperty(KEY_ALERT_EYE_REPRO_ACTIVE, true);
            if (SMARTS_ACTIVITY[i][1].equalsIgnoreCase(INACTIVE))
                curSA.setBoolProperty(KEY_ALERT_EYE_REPRO_INACTIVE, true);
            if (SMARTS_ACTIVITY[i][2].equalsIgnoreCase(MAX))
                curSA.setBoolProperty(KEY_ALERT_EYE_REPRO_REL_MAX, true);
            if (SMARTS_ACTIVITY[i][2].equalsIgnoreCase(MIN))
                curSA.setBoolProperty(KEY_ALERT_EYE_REPRO_REL_MIN, true);
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