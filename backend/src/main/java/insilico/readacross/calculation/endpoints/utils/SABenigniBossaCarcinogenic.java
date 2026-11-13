/*
* Wrapper for original BB alerts, excluding those only carcinogenic
*/


package insilico.readacross.calculation.endpoints.utils;

import insilico.core.alerts.Alert;
import insilico.core.alerts.AlertBlock;
import insilico.core.alerts.AlertList;
import insilico.core.alerts.builders.SABenigniBossa;
import insilico.core.constant.InsilicoConstants;
import insilico.core.exception.GenericFailureException;
import insilico.core.exception.InitFailureException;
import insilico.core.exception.InvalidMoleculeException;
import insilico.readacross.calculation.RaEngine;

import static insilico.core.alerts.builders.SABenigniBossa.KEY_BBSA_IS_CARCINOGEN;
import static insilico.core.alerts.builders.SABenigniBossa.KEY_BBSA_IS_MUTAGEN;

/**
 *
 * @author Alberto Manganaro (a.manganaro@kode-solutions.net)
 */
public class SABenigniBossaCarcinogenic extends AlertBlock {

    private SABenigniBossa BB;

    public SABenigniBossaCarcinogenic() throws InitFailureException {
        super(100+ RaEngine.DB_VALUE_TYPE_CARCINOGENICITY,"Benigni/Bossa (from ToxTree 2.6) rule set");
    }
    
    
    @Override
    protected void BuildSAList() throws InitFailureException {
        BB = new SABenigniBossa();
        for (Alert a : BB.getAlerts().getSAList()) {
            if (a.getBoolProperty(KEY_BBSA_IS_CARCINOGEN)) {
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
                if (a.getBoolProperty(KEY_BBSA_IS_CARCINOGEN))
                    Res.add(a);
            
            return Res;
            
        } catch (InvalidMoleculeException ex) {
            throw new GenericFailureException(ex);
        }
        
    }
    
}
