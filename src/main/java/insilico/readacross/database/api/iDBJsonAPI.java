package insilico.readacross.database.api;

import insilico.core.molecule.InsilicoMolecule;

/**
 *
 * @author Alberto
 */
public interface iDBJsonAPI {
    
    abstract public String GetAllEndpoints() throws Exception;
    abstract public String GetAllDatasets() throws Exception;
    
    // molecule and structure
    abstract public String GetMolecule(long Id) throws Exception;

    // value, datatype and reference
    abstract public String GetValue(long Id) throws Exception;

    abstract public String GetEndpoint(long Id) throws Exception;
    abstract public String GetDataset(long Id) throws Exception;

    abstract public String GetAllValuesForEndpoint(long EndpointId) throws Exception;
    abstract public String GetAllValuesForDataset(long DatasetId) throws Exception;
    abstract public String GetAllValuesForMolecule(long MoleculeId) throws Exception;
    abstract public String GetAllValuesForMoleculeAndEndpoint(long MoleculeId, long EndpointId) throws Exception;
    abstract public String GetAllValuesForMoleculeAndDataset(long MoleculeId, long DatasetId) throws Exception;

    abstract public String GetSimilarMolecules(int Number, String SMILES) throws Exception;
    abstract public String GetMatchingMolecules(InsilicoMolecule Mol, long ScreeningFP) throws Exception;
    
}
