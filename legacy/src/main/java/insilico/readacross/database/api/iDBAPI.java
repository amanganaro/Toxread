package insilico.readacross.database.api;

import insilico.readacross.database.objects.*;
import insilico.core.molecule.InsilicoMolecule;

import java.util.ArrayList;

/**
 *
 * @author Alberto
 */
public interface iDBAPI {

    abstract public ArrayList<dbEndpoint> GetAllEndpoints() throws Exception;
    abstract public ArrayList<dbDataset> GetAllDatasets() throws Exception;
    
    abstract public dbMolecule GetMolecule(long Id) throws Exception;
    abstract public dbStructure GetStructure(long Id) throws Exception;
    abstract public dbDataType GetDataType(long Id) throws Exception;
    abstract public dbValue GetValue(long Id) throws Exception;
    abstract public dbReference GetReference(long Id) throws Exception;
    abstract public dbEndpoint GetEndpoint(long Id) throws Exception;
    abstract public dbDataset GetDataset(long Id) throws Exception;

    abstract public ArrayList<dbValue> GetAllValuesForEndpoint(long EndpointId) throws Exception;
    abstract public ArrayList<dbValue> GetAllValuesForDataset(long DatasetId) throws Exception;
    abstract public ArrayList<dbValue> GetAllValuesForMolecule(long MoleculeId) throws Exception;
    abstract public ArrayList<dbValue> GetAllValuesForMoleculeAndEndpoint(long MoleculeId, long EndpointId) throws Exception;
    abstract public ArrayList<dbValue> GetAllValuesForMoleculeAndDataset(long MoleculeId, long DatasetId) throws Exception;

    abstract public ArrayList<dbMolecule> GetSimilarMolecules(int Number, String SMILES) throws Exception;
    abstract public ArrayList<dbMolecule> GetMatchingMolecules(InsilicoMolecule Mol, long ScreeningFP) throws Exception;
    
}
