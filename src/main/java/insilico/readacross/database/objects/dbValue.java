package insilico.readacross.database.objects;

import java.io.Serializable;

/**
 *
 * @author Alberto
 */
public class dbValue implements Serializable {

    public final static String OPERATOR_TYPE_EQ = "=";
    public final static String OPERATOR_TYPE_LT = "<";
    public final static String OPERATOR_TYPE_GT = ">";
    public final static String OPERATOR_TYPE_LET = "<=";
    public final static String OPERATOR_TYPE_GET = ">=";
    
    public final static String RELIABILITY_0 = "0";
    public final static String RELIABILITY_1 = "1";
    public final static String RELIABILITY_2 = "2";
    public final static String RELIABILITY_3 = "3";
    public final static String RELIABILITY_4 = "4";
    
    public long Id;
    
    public double Value;
    public String Operator;
    public String Reliability;
    public String Notes;
    
    public long MoleculeId;
    public long ReferenceId;
    public long DataTypeId;
    
}
