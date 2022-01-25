package insilico.readacross.database.local;

import insilico.readacross.database.ItemNotFoundException;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.*;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.core.similarity.ScreeningFingerprint;
import insilico.core.similarity.Similarity;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Alberto
 */
public class DBConnectorLocal implements iDBConnector {

    public final static String CONN_PARAM_INPUT_SERIAL = "in_serial";
    public final static String CONN_PARAM_OUTPUT_SERIAL = "out_serial";
    
    private LocalDataContainer Data;
    
    
    
    ////// Constructor
    
    public DBConnectorLocal() {
        
    }
    
    
    
    ////// Connectors
    
    @Override
    public void Connect(HashMap<String, String> Params) throws Exception {
        
        // Load from serialized data file
        if (Params.containsKey(CONN_PARAM_INPUT_SERIAL)) {
            InputStream url = this.getClass().getResourceAsStream(Params.get(CONN_PARAM_INPUT_SERIAL));
            ObjectInputStream in = new ObjectInputStream(url);
            LocalDataContainer readData = (LocalDataContainer) in.readObject();
            in.close();
            this.Data = readData;
        }

        // No parameters, create new structures
        else {
            Data = new LocalDataContainer();
            Data.Datatypes = new ArrayList<>();
            Data.Datasets = new ArrayList<>();
            Data.Endpoints = new ArrayList<>();
            Data.Molecules = new ArrayList<>();
            Data.References = new ArrayList<>();
            Data.Structures = new ArrayList<>();
            Data.Values = new ArrayList<>();            
        }
    }

    @Override
    public void Disconnect(HashMap<String, String> Params) throws Exception {
        
        // Save to serialized file
        if (Params.containsKey(CONN_PARAM_OUTPUT_SERIAL)) {
            File dest = new File(Params.get(CONN_PARAM_OUTPUT_SERIAL));
            dest.createNewFile();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dest));
            out.writeObject(Data);
            out.close();
        }        

        // Else just discard everything
    }

    
    
    ////// Access to single objects

    @Override
    public long InsertMolecule(dbMolecule molecule) throws Exception {
        long nextId = 0;
        for (dbMolecule item : Data.Molecules) nextId = item.Id>nextId ? item.Id : nextId;
        molecule.Id = ++nextId;
        Data.Molecules.add(molecule);
        return nextId;
    }

    @Override
    public long InsertMoleculeNoAutoId(dbMolecule molecule) throws Exception {
        Data.Molecules.add(molecule);
        return molecule.Id;
    }

    @Override
    public long InsertStructure(dbStructure struct) throws Exception {
        long nextId = 0;
        for (dbStructure item : Data.Structures) nextId = item.Id>nextId ? item.Id : nextId;
        struct.Id = ++nextId;
        Data.Structures.add(struct);
        return nextId;
    }

    @Override
    public long InsertDatatype(dbDataType type) throws Exception {
        long nextId = 0;
        for (dbDataType item : Data.Datatypes) nextId = item.Id>nextId ? item.Id : nextId;
        type.Id = ++nextId;
        Data.Datatypes.add(type);
        return nextId;
    }

    @Override
    public long InsertValue(dbValue value) throws Exception {
        long nextId = 0;
        for (dbValue item : Data.Values) nextId = item.Id>nextId ? item.Id : nextId;
        value.Id = ++nextId;
        Data.Values.add(value);
        return nextId;
    }

    @Override
    public long InsertReference(dbReference reference) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long InsertEndpoint(dbEndpoint endpoint) throws Exception {
        long nextId = 0;
        for (dbEndpoint item : Data.Endpoints) nextId = item.Id>nextId ? item.Id : nextId;
        endpoint.Id = ++nextId;
        Data.Endpoints.add(endpoint);
        return nextId;
    }

    @Override
    public long InsertEndpointNoAutoId(dbEndpoint endpoint) throws Exception {
        Data.Endpoints.add(endpoint);
        return endpoint.Id;
    }

    @Override
    public long InsertDataset(dbDataset dataset) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public dbMolecule GetMolecule(long Id) throws Exception {
        for (dbMolecule item : Data.Molecules)
            if (item.Id == Id)
                return item;
        throw new ItemNotFoundException();
    }

    @Override
    public dbStructure GetStructure(long Id) throws Exception {
        for (dbStructure item : Data.Structures)
            if (item.Id == Id)
                return item;
        throw new ItemNotFoundException();
    }

    @Override
    public dbDataType GetDataType(long Id) throws Exception {
        for (dbDataType item : Data.Datatypes)
            if (item.Id == Id)
                return item;
        throw new ItemNotFoundException();
    }
    
    @Override
    public dbValue GetValue(long Id) throws Exception {
        for (dbValue item : Data.Values)
            if (item.Id == Id)
                return item;
        throw new ItemNotFoundException();
    }

    @Override
    public dbReference GetReference(long Id) throws Exception {
        for (dbReference item : Data.References)
            if (item.Id == Id)
                return item;
        throw new ItemNotFoundException();
    }

    @Override
    public dbEndpoint GetEndpoint(long Id) throws Exception {
        for (dbEndpoint item : Data.Endpoints)
            if (item.Id == Id)
                return item;
        throw new ItemNotFoundException();
    }

    @Override
    public dbDataset GetDataset(long Id) throws Exception {
        for (dbDataset item : Data.Datasets)
            if (item.Id == Id)
                return item;
        throw new ItemNotFoundException();
    }

    
    
    ////// Access to multiple simple objects
    
    @Override
    public ArrayList<dbMolecule> GetAllMolecules() throws Exception {
        return this.Data.Molecules;
    }
        
    @Override
    public ArrayList<dbEndpoint> GetAllEndpoints() throws Exception {
        return this.Data.Endpoints;
    }

    @Override
    public ArrayList<dbDataset> GetAllDatasets() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<dbStructure> GetAllStructures() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<dbValue> GetAllValues() throws Exception {
        return this.Data.Values;
    }

    @Override
    public ArrayList<dbReference> GetAllreferences(long EndpointId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    ////// Specific queries
    
    @Override
    public ArrayList<dbValue> GetAllValuesForEndpoint(long EndpointId) throws Exception {
        ArrayList<dbValue> res = new ArrayList<>();
        for (dbValue val : Data.Values) {
            dbDataType dt = GetDataType(val.DataTypeId);
            if (dt.EndpointId == EndpointId)
                res.add(val);
        }
        return res;    
    }

    @Override
    public ArrayList<dbValue> GetAllValuesForDataset(long DatasetId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<dbValue> GetAllValuesForMolecule(long MoleculeId) throws Exception {
        ArrayList<dbValue> res = new ArrayList<>();
        for (dbValue val : Data.Values) {
            if (val.MoleculeId == MoleculeId)
                res.add(val);
        }
        return res;    
    }

    @Override
    public ArrayList<dbValue> GetAllValuesForMoleculeAndEndpoint(long MoleculeId, long EndpointId) throws Exception {
        ArrayList<dbValue> res = new ArrayList<>();
        for (dbValue val : Data.Values) {
            if (val.MoleculeId == MoleculeId) {
                dbDataType dt = GetDataType(val.DataTypeId);
                if (dt.EndpointId == EndpointId)
                    res.add(val);
            }
        }
        return res;    
    }

    @Override
    public ArrayList<dbValue> GetAllValuesForMoleculeAndDataset(long MoleculeId, long DatasetId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    ////// Similarity and matchin queries
    
    @Override
    public ArrayList<dbMolecule> GetSimilarMolecules(int Number, String SMILES) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public ArrayList<dbMolecule> GetMatchingMolecules(InsilicoMolecule Mol, long ScreeningFP) throws Exception {
        ArrayList<dbMolecule> res = new ArrayList<>();
        
        for (dbMolecule m : this.Data.Molecules) {
            dbStructure st = GetStructure(m.StructureId);
            double sim = ScreeningFingerprint.Tanimoto(st.ScreeningFP, ScreeningFP);
            if (sim == 1) {
                InsilicoMolecule im = SmilesMolecule.Convert(st.SMILES);
                if (Similarity.CheckIsomorphism(Mol.GetStructure(), im.GetStructure()))
                    res.add(m);
            }
        }
        
        return res;
    }

    
    
}
