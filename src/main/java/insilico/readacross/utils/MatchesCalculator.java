
package insilico.readacross.utils;

import insilico.core.alerts.Alert;
import insilico.core.alerts.AlertList;
import insilico.core.alerts.iAlertBlock;
//import insilico.core.database.iDBConnector;
//import insilico.core.database.objects.dbEndpoint;
//import insilico.core.database.objects.dbMolecule;
//import insilico.core.database.objects.dbStructure;
//import insilico.core.database.objects.dbValue;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.dbMolecule;
import insilico.readacross.database.objects.dbStructure;
import insilico.readacross.database.objects.dbValue;

import java.util.ArrayList;

/**
 *
 * @author Alberto
 */
public class MatchesCalculator {

    private final iDBConnector DBConn;
    
    public MatchesCalculator(iDBConnector DBConn) {
        this.DBConn = DBConn;
    }
    
    public void Calculate(long EndPointId, iAlertBlock SA) throws Exception {
                
        ArrayList<dbValue> Values = DBConn.GetAllValuesForEndpoint(EndPointId);
        ArrayList<dbMolecule> Molecules = new ArrayList<>();
        for (dbValue v : Values)
            Molecules.add(DBConn.GetMolecule(v.MoleculeId));
        
        AlertList AllSA = SA.getAlerts();
        System.out.print("No.\tMolecule\tExp");
        for (Alert a : AllSA.getSAList())
            System.out.print("\t" + a.getName());
        System.out.println();
        
        int idx = 0;
        for (dbMolecule m : Molecules) {
            dbStructure st = DBConn.GetStructure(m.StructureId);
            InsilicoMolecule mol = SmilesMolecule.Convert(st.SMILES);
            AlertList sas = SA.Calculate(mol);
            double val = Values.get(idx).Value;
            
            idx++;
            System.out.print(idx + "\t" + st.SMILES + "\t" + val);
            for (Alert a : AllSA.getSAList()) {
                boolean found = false;
                for (Alert aa : sas.getSAList()) {
                    if (a.getId() == aa.getId()) {
                        found = true; break;
                    }
                }
                if (found)
                    System.out.print("\tYES");
                else
                    System.out.print("\tno");
            }
            System.out.println();
            
        }
                
        
    }
    
    
}
