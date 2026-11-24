package insilico.readacross.database;

import insilico.readacross.database.objects.*;
import insilico.core.molecule.InsilicoMolecule;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Alberto Manganaro
 */
public interface iDBConnector {

    //
    // Connection and disconnection methods.
    //
    abstract public void Connect(HashMap<String, String> Params) throws Exception;
    abstract public void Disconnect(HashMap<String, String> Params) throws Exception;
    
    //
    // Direct insert methods for all db basic object types.
    // All methods return the database id (auto-increment) of the added record.
    // Also the database id in the db object is updated with the db id.
    //
    abstract public long InsertMolecule(dbMolecule molecule) throws Exception;
    abstract public long InsertMoleculeNoAutoId(dbMolecule molecule) throws Exception;
    abstract public long InsertStructure(dbStructure struct) throws Exception;
    abstract public long InsertDatatype(dbDataType type) throws Exception;
    abstract public long InsertValue(dbValue value) throws Exception;
    abstract public long InsertReference(dbReference reference) throws Exception;
    abstract public long InsertEndpoint(dbEndpoint endpoint) throws Exception;
    abstract public long InsertEndpointNoAutoId(dbEndpoint endpoint) throws Exception;
    abstract public long InsertDataset(dbDataset dataset) throws Exception;
    
    //
    // Direct methods to retrieve a single db basic object type given its id.
    //
    abstract public dbMolecule GetMolecule(long Id) throws Exception;
    abstract public dbStructure GetStructure(long Id) throws Exception;
    abstract public dbDataType GetDataType(long Id) throws Exception;
    abstract public dbValue GetValue(long Id) throws Exception;
    abstract public dbReference GetReference(long Id) throws Exception;
    abstract public dbEndpoint GetEndpoint(long Id) throws Exception;
    abstract public dbDataset GetDataset(long Id) throws Exception;

    //
    // Methods for querying all available records for a given db basic object
    //
    abstract public ArrayList<dbMolecule> GetAllMolecules() throws Exception;
    abstract public ArrayList<dbStructure> GetAllStructures() throws Exception;
    abstract public ArrayList<dbValue> GetAllValues() throws Exception;
    abstract public ArrayList<dbReference> GetAllreferences(long EndpointId) throws Exception;
    abstract public ArrayList<dbEndpoint> GetAllEndpoints() throws Exception;
    abstract public ArrayList<dbDataset> GetAllDatasets() throws Exception;
    
    //
    // Methods for specific queries
    //
    
    // Retrieve all available values for a specific endpoint
    abstract public ArrayList<dbValue> GetAllValuesForEndpoint(long EndpointId) throws Exception;
    
    // Retrieve all available values for a specific dataset
    abstract public ArrayList<dbValue> GetAllValuesForDataset(long DatasetId) throws Exception;
    
    // Retrieve all available values for a specific molecule
    abstract public ArrayList<dbValue> GetAllValuesForMolecule(long MoleculeId) throws Exception;
    
    // Retrieve all available values for a specific endpoint for a specific molecule
    abstract public ArrayList<dbValue> GetAllValuesForMoleculeAndEndpoint(long MoleculeId, long EndpointId) throws Exception;
    
    // Retrieve all available values for a specific dataset for a specific molecule
    abstract public ArrayList<dbValue> GetAllValuesForMoleculeAndDataset(long MoleculeId, long DatasetId) throws Exception;
    
    //
    // Methods for similarity/matching structure queries
    //
    abstract public ArrayList<dbMolecule> GetSimilarMolecules(int Number, String SMILES) throws Exception;
    abstract public ArrayList<dbMolecule> GetMatchingMolecules(InsilicoMolecule Mol, long ScreeningFP) throws Exception;
    
}
