/*
* Wrapper for original BB alerts, excluding those only carcinogenic
*/


package insilico.readacross.calculation.endpoints.utils;

import insilico.core.alerts.Alert;
import insilico.core.alerts.AlertBlock;
import insilico.core.alerts.AlertList;
import insilico.core.alerts.builders.SABenigniBossa;
import static insilico.core.alerts.builders.SABenigniBossa.KEY_BBSA_IS_MUTAGEN;
import insilico.core.constant.InsilicoConstants;
import insilico.core.exception.GenericFailureException;
import insilico.core.exception.InitFailureException;
import insilico.core.exception.InvalidMoleculeException;
import insilico.readacross.calculation.RaEngine;

/**
 *
 * @author Alberto Manganaro (a.manganaro@kode-solutions.net)
 */
public class SABenigniBossaMutagenic extends AlertBlock {

    private final static String[][] BiocideAcc = {
        {"SA16 Alkyl carbamate and thiocarbamate","0.33"},
        {"SA2 Alkyl (C<5) or benzyl ester of sulphonic or phosphonic acid","1"},
        {"SA4 Monohaloalkene","0"},
        {"SA7 Epoxides and aziridines","1"},
        {"SA11 Simple aldehyde","1"},
        {"SA15 Isocyanate and isothiocyanate groups","1"}
    };
    
    private SABenigniBossa BB;
    
    public SABenigniBossaMutagenic() throws InitFailureException {
        super(100+ RaEngine.DB_VALUE_TYPE_MUTAGENICITY,"Benigni/Bossa (from ToxTree 2.6) rule set");
    }
    
    
    @Override
    protected void BuildSAList() throws InitFailureException {
        BB = new SABenigniBossa();
        for (Alert a : BB.getAlerts().getSAList()) {
            if (a.getBoolProperty(KEY_BBSA_IS_MUTAGEN)) {
                for (int i=0; i<BiocideAcc.length; i++)
                    if (a.getName().equals(BiocideAcc[i][0])) {
                        double v = Double.parseDouble(BiocideAcc[i][1]);
                        a.setNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_ACCURACY_BIOCIDES, v);
                    }
                Alerts.add(a);
            }
        }
    }

    @Override
    protected AlertList CalculateSAMatches() throws GenericFailureException {
        
        try {
            
            AlertList OriginalRes =  BB.Calculate(CurMol);
            AlertList Res = new AlertList();
            
            for (Alert a : OriginalRes.getSAList()) 
                if (a.getBoolProperty(KEY_BBSA_IS_MUTAGEN))
                    Res.add(a);
            
            return Res;
            
        } catch (InvalidMoleculeException ex) {
            throw new GenericFailureException(ex);
        }
        
    }
    
}
