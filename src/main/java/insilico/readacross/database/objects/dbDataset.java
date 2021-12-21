package insilico.readacross.database.objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Alberto
 */
public class dbDataset implements Serializable {
    
    public long Id;
    
    public String ShortName;
    public String Description;
    public long EndpointId;
    
    public ArrayList<dbValue> DataTrainingSet;
    public ArrayList<dbValue> DataTestSet;
    public ArrayList<dbValue> DataValidationSet;
    
}
