package insilico.readacross.calculation;

import insilico.core.alerts.Alert;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.dbMolecule;
import insilico.readacross.database.objects.dbStructure;
import insilico.readacross.database.objects.dbValue;
import insilico.core.exception.InitFailureException;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.similarity.Similarity;
import insilico.core.similarity.SimilarityDescriptors;
import insilico.core.similarity.SimilarityDescriptorsBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Dataset connected to local DB with current view of molecules for RA
 * 
 * @author Alberto Manganaro
 */
public class RaDataset {
    
    
    public class RaSimilarMolecule {
        
        private dbMolecule Molecule;
        private double Similarity;
        
        public RaSimilarMolecule() {
            Molecule = null;
            Similarity = 0.0;
        }

        public dbMolecule getMolecule() {
            return Molecule;
        }

        public void setMolecule(dbMolecule Molecule) {
            this.Molecule = Molecule;
        }

        public double getSimilarity() {
            return Similarity;
        }

        public void setSimilarity(double Similarity) {
            this.Similarity = Similarity;
        }
    }
    
    // RaSimilarMolecule comparator
    public class RaSimilarMoleculeComparator implements Comparator<RaSimilarMolecule> {
        @Override
        public int compare(RaSimilarMolecule a, RaSimilarMolecule b) {
            if (b.getSimilarity() > a.getSimilarity())
                return +1;
            else if (a.getSimilarity() == b.getSimilarity())
                return 0;
            else
                return -1;
        }
    }

    
    
    private final iDBConnector DBConn;
    
    private int nMolecules;
    private ArrayList<dbMolecule> Molecules;
    private double[] SimValues;
    
    
    public RaDataset(iDBConnector DBConnection, long Endpoint) throws InitFailureException { 

        this.DBConn = DBConnection;
        
        // Extract the dataset of db molecules with the required 
        // experimental data available
        try {
            ArrayList<dbValue> Values = DBConn.GetAllValuesForEndpoint(Endpoint);
            Molecules = new ArrayList<>();
            for (dbValue v : Values)
                Molecules.add(DBConn.GetMolecule(v.MoleculeId));
        } catch (Exception e) {
            throw new InitFailureException("Unable to init dataset from db - " + e.getMessage());
        }
        nMolecules = Molecules.size();
        SimValues = new double[nMolecules];
        for (int i=0; i<nMolecules; i++) 
            SimValues[i] = 0;
        
        
    }
    

    
    public void InitSimilarity(InsilicoMolecule mol) throws Exception {

        // Calculates similarity descriptors for the molecule
        SimilarityDescriptorsBuilder SimDescBuilder = new SimilarityDescriptorsBuilder();
        SimilarityDescriptors SimDesc = SimDescBuilder.Calculate(mol);
        
        // Calculates similarity values on the current molecule set
        Similarity SimCalculator = new Similarity();
        for (int i=0; i<nMolecules; i++) {
            dbStructure st = DBConn.GetStructure(Molecules.get(i).StructureId);
            SimValues[i] = SimCalculator.CalculateExactMatches(SimDesc, st.SimDescriptors, mol.GetStructure(), st.SMILES);
        }
    }

    
    public ArrayList<RaSimilarMolecule> GetSimilarMolecules(int Size, Alert Alert)
            throws Exception {
        
        // Build list of molecules to be used (depending on alerts)
        ArrayList<RaSimilarMolecule> List = new ArrayList<>();
        for (int i=0; i<nMolecules; i++) {
            if (Alert != null) {
                dbStructure curSt = DBConn.GetStructure(Molecules.get(i).StructureId);
                if ((curSt.Alerts == null) || (!curSt.Alerts.contains(Alert.getId())))
                    continue;
            }
            RaSimilarMolecule curSimMol = new RaSimilarMolecule();
            curSimMol.setMolecule(Molecules.get(i));
            curSimMol.setSimilarity(SimValues[i]);
            List.add(curSimMol);
        }
        
        // Sort list
        Collections.sort(List, new RaSimilarMoleculeComparator());
        
        // Extract molecules to be returned
        ArrayList<RaSimilarMolecule> Res = new ArrayList<>();
        for (RaSimilarMolecule m : List) {
            if (Size == 0) break;
            Res.add(m);
            Size--;
        }
        
        return Res;
    }
    
        
}
