package insilico.readacross.database.local;

import insilico.readacross.database.objects.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Alberto
 */
public class LocalDataContainer implements Serializable {

        private static final long serialVersionUID = 1L;

        public ArrayList<dbDataType> Datatypes;
        public ArrayList<dbDataset> Datasets;
        public ArrayList<dbEndpoint> Endpoints;
        public ArrayList<dbMolecule> Molecules;
        public ArrayList<dbReference> References;
        public ArrayList<dbStructure> Structures;
        public ArrayList<dbValue> Values;
    
}
