
package insilico.readacross.database.utils;

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
public class ImportCombaseData {
    
    private final iDBConnector DB;
    private final SimilarityDescriptorsBuilder SimDescEngine;
    
    
    public ImportCombaseData(iDBConnector DB) {
        this.DB = DB;
        SimDescEngine = new SimilarityDescriptorsBuilder();
    }
    
    
    public void Import(String InputFile) throws Exception {
        
        //// add new endpoints (and datatypes)
        
        // 5 - Algae
        dbEndpoint de = new dbEndpoint();
        de.ShortName = "Algae acute toxicity";
        de.Description = "Algae acute toxicity (EC50)";
        DB.InsertEndpoint(de);

        dbDataType dt = new dbDataType();
        dt.IsQuantitative = true;
        dt.Units = "mg/L";
        dt.EndpointId = de.Id;
        DB.InsertDatatype(dt);
            
        // 6 - Sludge
        de = new dbEndpoint();
        de.ShortName = "Microbes toxicity";
        de.Description = "Microbes toxicity (EC50)";
        DB.InsertEndpoint(de);

        dt = new dbDataType();
        dt.IsQuantitative = true;
        dt.Units = "mg/L";
        dt.EndpointId = de.Id;
        DB.InsertDatatype(dt);
            
        // 7 - Daphnia
        de = new dbEndpoint();
        de.ShortName = "Daphnia Magna acute toxicity";
        de.Description = "Daphnia Magna acute toxicity (EC50)";
        DB.InsertEndpoint(de);

        dt = new dbDataType();
        dt.IsQuantitative = true;
        dt.Units = "mg/L";
        dt.EndpointId = de.Id;
        DB.InsertDatatype(dt);
            
        // 8 - Fish
        de = new dbEndpoint();
        de.ShortName = "Fish acute toxicity";
        de.Description = "Fish acute toxicity (EC50)";
        DB.InsertEndpoint(de);

        dt = new dbDataType();
        dt.IsQuantitative = true;
        dt.Units = "mg/L";
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
//        SACombaseAlgae sa_algae = new SACombaseAlgae();
//        SACombaseMicrobes sa_micro = new SACombaseMicrobes();
//        SACombaseDaphnia sa_daphnia = new SACombaseDaphnia();
//        SACombaseFish sa_fish = new SACombaseFish();
//        
//        for (dbMolecule m : DB.GetAllMolecules()) {
//            System.out.println("Mol no. " + m.Id);
//            
//            try {
//                dbStructure st = DB.GetStructure(m.StructureId);
//                InsilicoMolecule mol = SmilesMolecule.Convert(st.SMILES);
//
//                ArrayList<AlertList> alerts = new ArrayList<>();
//                alerts.add(sa_algae.Calculate(mol));
//                alerts.add(sa_micro.Calculate(mol));
//                alerts.add(sa_daphnia.Calculate(mol));
//                alerts.add(sa_fish.Calculate(mol));
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
