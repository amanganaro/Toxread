package insilico.readacross.database.objects;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Alberto
 */
public class dbDataType implements Serializable {
    
    public final static String LABEL_MISSING_VALUE = "N.A.";
    
    public long Id;

    public boolean IsQuantitative;
    public String Units;
    public HashMap<Double, String> ClassLabels;
    
    public long EndpointId;
    
    public dbDataType() {
        ClassLabels = new HashMap<>();
    }
    
    public String ConvertValue(Double val) {
        if (ClassLabels.containsKey(val)) {
            return ClassLabels.get(val);
        } else
            return LABEL_MISSING_VALUE;
    }
}
