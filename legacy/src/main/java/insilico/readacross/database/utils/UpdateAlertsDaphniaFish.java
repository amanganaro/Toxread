package insilico.readacross.database.utils;

import insilico.core.alerts.Alert;
import insilico.core.alerts.AlertEncoding;
import insilico.core.alerts.AlertList;
import insilico.core.alerts.builders.SADaphniaVermeer;
import insilico.core.alerts.builders.SAFishVermeer;
import insilico.core.alerts.iAlertBlock;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.dbMolecule;
import insilico.readacross.database.objects.dbStructure;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;

import java.util.ArrayList;

/**
 *
 * @author Alberto
 */
public class UpdateAlertsDaphniaFish {
    
    private final iDBConnector DB;
    private final ArrayList<iAlertBlock> Alerts;
    
    public UpdateAlertsDaphniaFish(iDBConnector DB) throws Exception {
        this.DB = DB;
        Alerts = new ArrayList<>();
        
        // Needed alert blocks
        // solo per correggere errore precedente nei due blocchi

        Alerts.add( new SADaphniaVermeer() );
        Alerts.add( new SAFishVermeer() );
        
    }


    public void Update() throws Exception {
        
//// Update ALL alerts matching for all molecules in DB
        System.out.println();
        System.out.println("Updating alerts");
        
        int counter = 0;
        for (dbMolecule m : DB.GetAllMolecules()) {
            counter++;
            if (counter%25==0)
                System.out.println("Mol no. " + counter);
            
            try {
                dbStructure st = DB.GetStructure(m.StructureId);
                InsilicoMolecule mol = SmilesMolecule.Convert(st.SMILES);

                ArrayList<AlertList> alerts = new ArrayList<>();
                AlertList alertsToFix = new AlertList();
                
                for (iAlertBlock b : Alerts) {
                    AlertList curAlerts = b.Calculate(mol);
                    for (Alert a : curAlerts.getSAList()) {
                        if ( a.getId().equalsIgnoreCase("0x08") ) // daphnia 8
                            alertsToFix.add(a);
                        if ( a.getId().equalsIgnoreCase("0w02") ) // fish 2
                            alertsToFix.add(a);
                    }
                    alerts.add(b.Calculate(mol));
                }
                
                String s = AlertEncoding.MergeAlertIds(alerts);
                String ss = AlertEncoding.MergeAlertIds(s, st.Alerts);

                st.Alerts = ss;
            } catch (Throwable e) {
                //
            }
        }
                      
    } 
    
}
