package insilico.readacross.type.objects;

import insilico.readacross.database.iDBConnector;
import insilico.core.molecule.InsilicoMolecule;
//import insilico.database.DBConnector;
import insilico.readacross.calculation.RaDataset;
import insilico.readacross.calculation.endpoints.RaEndpoint;
import insilico.readacross.type.interfaces.iRaObject;

/**
 *
 * @author User
 */
public class RaResultsContainer {

    private final iDBConnector DBConn;
    private final RaDataset Dataset;
    private final RaEndpoint Endpoint;
    private final iRaObject RootNode;
 
    
    public RaResultsContainer(iDBConnector DBConn, RaDataset Dataset, RaEndpoint Endpoint, iRaObject RootNode) {
        this.DBConn = DBConn;
        this.Dataset = Dataset;
        this.Endpoint = Endpoint;
        this.RootNode = RootNode;
    }

    
    /**
     * @return the DBConn
     */
    public iDBConnector getDBConn() {
        return DBConn;
    }

    /**
     * @return the Endpoint
     */
    public RaEndpoint getEndpoint() {
        return Endpoint;
    }

    /**
     * @return the RootNode
     */
    public iRaObject getRootNode() {
        return RootNode;
    }

    /**
     * @return the Dataset
     */
    public RaDataset getDataset() {
        return Dataset;
    }
    
}
