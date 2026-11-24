
package insilico.readacross.database.utils;

import insilico.core.alerts.builders.SAPersistenceSediment;
import insilico.core.alerts.builders.SAPersistenceSoil;
import insilico.core.alerts.builders.SAPersistenceWater;
import insilico.core.alerts.builders.SAReadyBioIRFMN;
import insilico.core.alerts.iAlertBlock;
import insilico.core.tools.utils.GeneralUtilities;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.*;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.core.similarity.ScreeningFingerprint;
import insilico.core.similarity.SimilarityDescriptorsBuilder;
//import insilico.core.tools.GeneralUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author Alberto
 */
public class ImportVermeer2Data {
    
    private final iDBConnector DB;
    private final SimilarityDescriptorsBuilder SimDescEngine;
    private final ArrayList<iAlertBlock> Alerts;
    
    
    public ImportVermeer2Data(iDBConnector DB) throws Exception {
        this.DB = DB;
        SimDescEngine = new SimilarityDescriptorsBuilder();
        Alerts = new ArrayList<>();
        
        // Needed alert blocks
        Alerts.add( new SAReadyBioIRFMN() );
        Alerts.add( new SAPersistenceSoil() );
        Alerts.add( new SAPersistenceWater() );
        Alerts.add( new SAPersistenceSediment() );
    }
    
    
    public void Import(String InputFile) throws Exception {
        
        //// add new endpoints (and datatypes)
        //// (last ep was 8 - Fish)
        
        // 17 - Daphnia magna
        dbEndpoint de = new dbEndpoint();
        de.ShortName = "Daphnia Magna acute toxicity";
        de.Description = "Daphnia Magna acute toxicity";
        DB.InsertEndpoint(de);

        dbDataType dt = new dbDataType();
        dt.IsQuantitative = true;
        dt.Units = "mg/l";
        dt.EndpointId = de.Id;
        DB.InsertDatatype(dt);
            
        // 18 - Fish tox
        de = new dbEndpoint();
        de.ShortName = "Fish acute toxicity";
        de.Description = "Fish acute toxicity";
        DB.InsertEndpoint(de);

        dt = new dbDataType();
        dt.IsQuantitative = true;
        dt.Units = "mg/l";
        dt.EndpointId = de.Id;
        DB.InsertDatatype(dt);
            
        
        
        //// Read molecules
        //// format of input file:
        //// no. [tab] CAS [tab] SMILES [tab] value [tab] endpoint_id
        
        BufferedReader br = new BufferedReader(new FileReader(new File(InputFile)));       
        String str;
        br.readLine(); // skip header row
        int idx = 0;
        while ( (str=br.readLine())!= null ) {
            idx++;
            System.out.println("* Mol no. " + idx + "...");
            String[] parsedStr = GeneralUtilities.TrimString(str).split("\t");
            
            dbMolecule mol = new dbMolecule();
            dbStructure st = new dbStructure();
            dbValue val = new dbValue();
            
            st.SMILES = parsedStr[2];
            st.Type = dbStructure.STRUCT_TYPE_ORIGINAL;
            st.Alerts = "";
            st.INCHI = "";
            st.INCHIKey = "";
            
            // Check if molecule is ok
            InsilicoMolecule imol = SmilesMolecule.Convert(st.SMILES);
            if (!imol.IsValid()) {
                System.out.println("* Unable to parse molecule at line " + idx + ": " + st.SMILES);
                continue;
            }
            
            // Calculate properties for the molecule
            st.SimDescriptors = SimDescEngine.Calculate(imol);
            st.ScreeningFP = ScreeningFingerprint.Calculate(imol.GetStructure());
            
            // Check if already in DB
            ArrayList<dbMolecule> matches = DB.GetMatchingMolecules(imol, st.ScreeningFP);
            
            if (matches.isEmpty()) {
                DB.InsertStructure(st);

                mol.CAS = parsedStr[1];
                mol.CommonName = "";
                mol.Notes = "";
                mol.StructureId = st.Id;
                DB.InsertMolecule(mol);

                val.Value = Double.valueOf(parsedStr[3]);
                val.Operator = dbValue.OPERATOR_TYPE_EQ;
                val.Reliability = dbValue.RELIABILITY_4;
                val.DataTypeId = Long.valueOf(parsedStr[4]);
                val.MoleculeId = mol.Id;
                DB.InsertValue(val);
            
            } else {
            
                for (dbMolecule matchMol : matches) {
                    System.out.println("* Found matching mol already in DB: " + matchMol.Id + " " + matchMol.CAS + "; added only exp values.");
                    val = new dbValue();
                    val.Value = Double.valueOf(parsedStr[3]);
                    val.Operator = dbValue.OPERATOR_TYPE_EQ;
                    val.Reliability = dbValue.RELIABILITY_4;
                    val.DataTypeId = Long.valueOf(parsedStr[4]);
                    val.MoleculeId = matchMol.Id;
                    DB.InsertValue(val);
                }
            }
        }
        br.close();  
        
        
        //// Update ALL alerts matching for all molecules in DB
//        System.out.println();
//        System.out.println("updating alerts");
//        
//        int counter = 0;
//        for (dbMolecule m : DB.GetAllMolecules()) {
//            counter++;
//            if (counter%20==0)
//                System.out.println("Mol no. " + counter);
//            
//            try {
//                dbStructure st = DB.GetStructure(m.StructureId);
//                InsilicoMolecule mol = SmilesMolecule.Convert(st.SMILES);
//
//                ArrayList<AlertList> alerts = new ArrayList<>();
//                
//                for (iAlertBlock b : Alerts) 
//                    alerts.add(b.Calculate(mol));
//
//                String s = AlertEncoding.MergeAlertIds(alerts);
//                String ss = AlertEncoding.MergeAlertIds(s, st.Alerts);
//
//                st.Alerts = ss;
//            } catch (Throwable e) {
//                //
//            }
//        }
                
    }
    
}
