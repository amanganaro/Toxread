package insilico.readacross.database.utils;

import insilico.core.alerts.AlertEncoding;
import insilico.core.alerts.AlertList;
import insilico.core.alerts.builders.*;
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
public class UpdateAlerts {
    
    private final iDBConnector DB;
    private final ArrayList<iAlertBlock> Alerts;
    
    public UpdateAlerts(iDBConnector DB) throws Exception {
        this.DB = DB;
        Alerts = new ArrayList<>();
        
        // Needed alert blocks

        // skip original alerts for muta+bcf
        // starts with new alerts for imported data for COMBASE+VERMEER
        
        // 1 - Muta
        // 2 - BCF
        
        // 3 - Algae
        Alerts.add( new SACombaseAlgae() );
                
        // 4 - Microbes
        Alerts.add( new SACombaseMicrobes() );
        
        // 5 - Daphnia
        Alerts.add( new SACombaseDaphnia() );
        
        // 6 - Fish
        Alerts.add( new SACombaseFish() );
        
        // 7 - RB
        Alerts.add( new SAReadyBioIRFMN() );
        
        // 8 - P soil
        Alerts.add( new SAPersistenceSoil() );
        
        // 9 - P water
        Alerts.add( new SAPersistenceWater() );
        
        // 10 - P sediment
        Alerts.add( new SAPersistenceSediment() );
        
        // 11 - Reprotox
        Alerts.add( new SAReprotoxVermeer() );
        
        // 12 - Hepatotox
        Alerts.add( new SAHepatotoxicity() );
        
        // 13 - Nephro
        Alerts.add( new SANephroVermeer() );
        
        // 14 - Skin
        Alerts.add( new SASkinVermeer() );
        
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
