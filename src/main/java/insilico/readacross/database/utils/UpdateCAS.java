
package insilico.readacross.database.utils;

import insilico.core.tools.utils.GeneralUtilities;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.dbMolecule;
import insilico.readacross.database.objects.dbStructure;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.core.similarity.ScreeningFingerprint;
import insilico.core.similarity.SimilarityDescriptorsBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author Alberto
 */
public class UpdateCAS {
    
    private final iDBConnector DB;
    private final SimilarityDescriptorsBuilder SimDescEngine;
    
    
    public UpdateCAS(iDBConnector DB) throws Exception {
        this.DB = DB;
        SimDescEngine = new SimilarityDescriptorsBuilder();
    }
    
    
    public void Import(String InputFile) throws Exception {
        
        //// Read molecules
        //// format of input file:
        //// no. [tab] CAS [tab] SMILES
        
        BufferedReader br = new BufferedReader(new FileReader(new File(InputFile)));       
        String str;
        br.readLine(); // skip header row
        int idx = 0;
        while ( (str=br.readLine())!= null ) {
            idx++;
            System.out.print("* Mol no. " + idx + "... ");
            String[] parsedStr = GeneralUtilities.TrimString(str).split("\t");
            
            dbMolecule mol = new dbMolecule();
            dbStructure st = new dbStructure();
            
            String CAS = parsedStr[1];
            st.SMILES = parsedStr[2];
            st.Type = dbStructure.STRUCT_TYPE_ORIGINAL;
            st.Alerts = "";
            st.INCHI = "";
            st.INCHIKey = "";
            
            // Check if molecule is ok
            InsilicoMolecule imol = SmilesMolecule.Convert(st.SMILES);
            if (!imol.IsValid()) {
                System.out.println("Unable to parse molecule at line " + idx + ": " + st.SMILES);
                continue;
            }
            
            // Calculate properties for the molecule
            st.SimDescriptors = SimDescEngine.Calculate(imol);
            st.ScreeningFP = ScreeningFingerprint.Calculate(imol.GetStructure());
            
            // Check if already in DB
            ArrayList<dbMolecule> matches = DB.GetMatchingMolecules(imol, st.ScreeningFP);
            
            if (matches.isEmpty()) {
                
                System.out.println("Molecule not found! line " + idx + ", smiles: " + st.SMILES);
            
            } else {
            
                for (dbMolecule matchMol : matches) {
                    
                    if (matchMol.CAS.equalsIgnoreCase("-")) {
                        System.out.println("found match with no CAS, set to " + CAS);
                        matchMol.CAS = CAS;
                    } else {
                        System.out.println("found match already with a CAS");
                    }
                    
                }
            }
        }
        br.close();  
                        
    }
    
}
