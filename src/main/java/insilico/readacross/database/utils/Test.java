package insilico.readacross.database.utils;

import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.local.DBConnectorLocal;
import insilico.readacross.database.objects.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Alberto
 */
public class Test {
    
    public static void main(String[] args) throws Exception {
        
        if (false) {
            iDBConnector DB = new DBConnectorLocal();
            DB.Connect(new HashMap<>());

            TransferToxReadData tr = new TransferToxReadData(DB);
            
            HashMap<String, String> p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_OUTPUT_SERIAL, "prova_serial.dat");
            DB.Disconnect(p);
        }
        
        
        // mostra tutti i dati nel db serializzato
        if (false) {
            ShowDatabase("db_toxread_orig_comb_ver1.dat", true, true);
        }
             
        
        // prende file originale toxread, importa nuovi dati di combase e 
        // salva su dat nuovo - senza update alert
        if (false) {
            iDBConnector DB = new DBConnectorLocal();
            HashMap<String, String> p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_INPUT_SERIAL, "db_toxread_orig.dat");
            DB.Connect(p);
            
            ImportCombaseData comb = new ImportCombaseData(DB);
            comb.Import("combase_data.txt");
            
            p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_OUTPUT_SERIAL, "db_toxread_orig_comb.dat");
            DB.Disconnect(p);            
        }
        
        
        // prende file originale toxread + combase, importa nuovi dati di 
        // vermeer 1 e salva su dat nuovo - senza update alert
        if (false) {
            iDBConnector DB = new DBConnectorLocal();
            HashMap<String, String> p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_INPUT_SERIAL, "db_toxread_orig_comb.dat");
            DB.Connect(p);
            
            ImportVermeer1Data comb = new ImportVermeer1Data(DB);
            comb.Import("vermeer_1_data.txt");
            
            p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_OUTPUT_SERIAL, "db_toxread_orig_comb_ver1.dat");
            DB.Disconnect(p);            
        }
        
        // aggiorna gli alert per combase + vermeer 1
        if (false) {
            iDBConnector DB = new DBConnectorLocal();
            HashMap<String, String> p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_INPUT_SERIAL, "db_toxread_orig_comb_ver1.dat");
            DB.Connect(p);
            
            UpdateAlerts al = new UpdateAlerts(DB);
            al.Update();
            
            p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_OUTPUT_SERIAL, "db_toxread_orig_comb_ver1_alerts.dat");
            DB.Disconnect(p);            
        }
        
        // prende file originale toxread + combase +vermeer 1, importa nuovi dati di 
        // vermeer 2 e salva su dat nuovo - poi riapre e salva alert nuovi
        if (false) {
            iDBConnector DB = new DBConnectorLocal();
            HashMap<String, String> p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_INPUT_SERIAL, "db_toxread_orig_comb_ver1_alerts.dat");
            DB.Connect(p);
            
            ImportVermeer2Data comb = new ImportVermeer2Data(DB);
            comb.Import("vermeer_2_data.txt");
            
            p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_OUTPUT_SERIAL, "db_toxread_orig_comb_ver2.dat");
            DB.Disconnect(p);            

            DB = new DBConnectorLocal();
            p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_INPUT_SERIAL, "db_toxread_orig_comb_ver2.dat");
            DB.Connect(p);
            
            UpdateAlerts2 al = new UpdateAlerts2(DB);
            al.Update();
            
            p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_OUTPUT_SERIAL, "db_toxread_orig_comb_ver2_alerts.dat");
            DB.Disconnect(p);            
        }        
        
        // prende file originale toxread + combase +vermeer 2, importa nuovi dati di 
        // vermeer 3 e salva su dat nuovo - poi riapre e salva alert nuovi
        if (false) {
            iDBConnector DB = new DBConnectorLocal();
            HashMap<String, String> p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_INPUT_SERIAL, "db_toxread_orig_comb_ver2_alerts.dat");
            DB.Connect(p);
            
            ImportVermeer3Data comb = new ImportVermeer3Data(DB);
            comb.Import("vermeer_3_data.txt");
            
            p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_OUTPUT_SERIAL, "db_toxread_orig_comb_ver3.dat");
            DB.Disconnect(p);            

            DB = new DBConnectorLocal();
            p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_INPUT_SERIAL, "db_toxread_orig_comb_ver3.dat");
            DB.Connect(p);
            
            UpdateAlerts3 al = new UpdateAlerts3(DB);
            al.Update();
            
            p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_OUTPUT_SERIAL, "db_toxread_orig_comb_ver3_alerts.dat");
            DB.Disconnect(p);            
        }     
        
        
        // prende file originale toxread + combase +vermeer 2, importa nuovi dati di 
        // vermeer 3 e salva su dat nuovo - poi riapre e salva alert nuovi
        if (false) {
            iDBConnector DB = new DBConnectorLocal();
            HashMap<String, String> p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_INPUT_SERIAL, "db_toxread_orig_comb_ver3_alerts.dat");
            DB.Connect(p);
            
            UpdateCAS cas = new UpdateCAS(DB);
            cas.Import("mn_cas.txt");
            
            p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_OUTPUT_SERIAL, "db_toxread_orig_comb_ver3_alerts_updcas.dat");
            DB.Disconnect(p);         
        }          
        
        // aggiorna solo due alert prima non matchati
        if (true) {
            iDBConnector DB = new DBConnectorLocal();
            HashMap<String, String> p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_INPUT_SERIAL, "db_toxread_orig_comb_ver3_alerts_updcas.dat");
            DB.Connect(p);
            
            UpdateAlertsDaphniaFish upd = new UpdateAlertsDaphniaFish(DB);
            upd.Update();
            
            p = new HashMap<>();
            p.put(DBConnectorLocal.CONN_PARAM_OUTPUT_SERIAL, "db_toxread_orig_comb_ver3_alerts_updcas_upd2.dat");
            DB.Disconnect(p);         
        }          

    }
 


    
    private final static void ShowDatabase(String dbPath, boolean EP, boolean Mols) throws Exception{
       
        iDBConnector DB = new DBConnectorLocal();
        HashMap<String, String> p = new HashMap<>();
        p.put(DBConnectorLocal.CONN_PARAM_INPUT_SERIAL, dbPath);
        DB.Connect(p);

        if (EP) {
            System.out.println("Endpoints");
            ArrayList<dbEndpoint> ep = DB.GetAllEndpoints();
            for (dbEndpoint e : ep) {
                System.out.println(e.Id + "\t" + e.ShortName + "\t" + e.Description);
                dbDataType dt = DB.GetDataType(e.Id);
                if (dt != null)
                    for (Double k : dt.ClassLabels.keySet())
                        System.out.println("  " + k + " - " + dt.ClassLabels.get(k));
            }
            System.out.println();
        }
        
        if (Mols) {
            System.out.println("Molecules");
            ArrayList<dbMolecule> mols = DB.GetAllMolecules();
            for (dbMolecule m : mols) {
                dbStructure st = DB.GetStructure(m.StructureId);
                System.out.print(m.Id + "\t" + m.CAS + "\t" + st.SMILES + "\t" + (st.Alerts==null?"NA":st.Alerts)  );
                ArrayList<dbValue> values = DB.GetAllValuesForMolecule(m.Id);
                for (dbValue v : values) {
                    dbDataType dt = DB.GetDataType(v.DataTypeId);
                    dbEndpoint e = DB.GetEndpoint(dt.EndpointId);
                    System.out.print("\t" + e.ShortName + ": " + v.Value);
                }
                System.out.println();
            }
            System.out.println();            

        }
        
    }
}
