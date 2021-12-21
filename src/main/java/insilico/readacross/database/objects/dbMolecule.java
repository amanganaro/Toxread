package insilico.readacross.database.objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Alberto
 */
public class dbMolecule implements Serializable {
    
    public long Id;
    
    public String CAS;
    public String CommonName;
    public String Notes;
    
    public long StructureId;
    public ArrayList<Long> AdditionalStructuresId;
    
}
