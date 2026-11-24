package insilico.readacross.database.local;

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
public class TextImport {

    private final iDBConnector DB;
    private final SimilarityDescriptorsBuilder SimDescEngine;
    
    
    public TextImport(iDBConnector DB) {
        this.DB = DB;
        SimDescEngine = new SimilarityDescriptorsBuilder();
    }
    
    
    public void ImportEndpoint(String FilePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(FilePath)));       
        String str;
        br.readLine(); // skip header row
        while ( (str=br.readLine())!= null ) {
            String[] parsedStr = GeneralUtilities.TrimString(str).split("\t");
            
            dbEndpoint de = new dbEndpoint();
            de.ShortName = parsedStr[1];
            de.Description = parsedStr[2];
            
            DB.InsertEndpoint(de);
        }
        br.close();
    }
    
    public void ImportDataType(String FilePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(FilePath)));       
        String str;
        br.readLine(); // skip header row
        while ( (str=br.readLine())!= null ) {
            String[] parsedStr = GeneralUtilities.TrimString(str).split("\t");
            
            dbDataType dt = new dbDataType();
            dt.IsQuantitative = Integer.valueOf(parsedStr[1])==1;
            dt.Units = parsedStr[2];
            if (!dt.IsQuantitative) {
                String[] labels = parsedStr[3].split("#");
                for (String s : labels) {
                    String[] buf = s.split("\\|");
                    dt.ClassLabels.put(Double.valueOf(buf[0]), buf[1]);
                }
            }
            dt.EndpointId = Long.valueOf(parsedStr[4]);
            
            DB.InsertDatatype(dt);
        }
        br.close();
    }

    public void ImportMolecules(String FilePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(FilePath)));       
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
            
            st.SMILES = parsedStr[4];
            st.Type = parsedStr[5];
            
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
                mol.CommonName = parsedStr[2];
                mol.Notes = parsedStr[3];
                mol.StructureId = st.Id;
                DB.InsertMolecule(mol);

                val.Value = Double.valueOf(parsedStr[6]);
                val.Operator = parsedStr[7];
                val.Reliability = parsedStr[8];
                val.DataTypeId = Long.valueOf(parsedStr[9]);
                val.MoleculeId = mol.Id;
                DB.InsertValue(val);
            
            } else {
            
                for (dbMolecule matchMol : matches) {
                    System.out.println("* Found matching mol already in DB: " + matchMol.Id + " " + matchMol.CAS + "; added only exp values.");
                    val = new dbValue();
                    val.Value = Double.valueOf(parsedStr[6]);
                    val.Operator = parsedStr[7];
                    val.Reliability = parsedStr[8];
                    val.DataTypeId = Long.valueOf(parsedStr[9]);
                    val.MoleculeId = matchMol.Id;
                    DB.InsertValue(val);
                }
                
            }
            
        }
        br.close();
    }
    
    public void ImportAlerts(String FilePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(FilePath)));       
        String str;
        br.readLine(); // skip header row
        int idx = 0;
        while ( (str=br.readLine())!= null ) {
            idx++;
            System.out.println("* Mol no. " + idx + "...");
            String[] parsedStr = GeneralUtilities.TrimString(str).split("\t");
            
            if (parsedStr.length>2) {
            
                String SMI = parsedStr[1];
                String Alerts = parsedStr[2];

                // Check if molecule is ok
                InsilicoMolecule imol = SmilesMolecule.Convert(SMI);
                if (!imol.IsValid()) {
                    System.out.println("* Unable to parse molecule at line " + idx + ": " + SMI);
                    continue;
                }

                // Calculate properties for the molecule
                long FP = ScreeningFingerprint.Calculate(imol.GetStructure());

                // Check if already in DB
                ArrayList<dbMolecule> matches = DB.GetMatchingMolecules(imol, FP);

                if (!matches.isEmpty()) {

                    for (dbMolecule matchMol : matches) {
                        System.out.println("* Found matching mol already in DB: " + matchMol.Id + " " + matchMol.CAS + "; added alerts.");

                        dbStructure st = DB.GetStructure(matchMol.Id);
                        st.Alerts = Alerts;
                    }

                }
                
            }
            
            
        }
        br.close();
    }    
    
}
