package insilico.readacross.database.utils;

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
public class UpdateAlerts2 {
    
    private final iDBConnector DB;
    private final ArrayList<iAlertBlock> Alerts;
    
    public UpdateAlerts2(iDBConnector DB) throws Exception {
        this.DB = DB;
        Alerts = new ArrayList<>();
        // Needed alert blocks

        // 15 - Daphnia
        Alerts.add( new SADaphniaVermeer() );
        
        // 16 - Fish
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
                
                for (iAlertBlock b : Alerts) 
                    alerts.add(b.Calculate(mol));

                String s = AlertEncoding.MergeAlertIds(alerts);
                String ss = AlertEncoding.MergeAlertIds(s, st.Alerts);

                st.Alerts = ss;
            } catch (Throwable e) {
                //
            }
        }
                      
    } 
    
}
