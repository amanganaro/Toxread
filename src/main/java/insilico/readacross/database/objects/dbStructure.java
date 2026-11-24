package insilico.readacross.database.objects;

import insilico.core.similarity.SimilarityDescriptors;

import java.io.Serializable;

/**
 *
 * @author Alberto
 */
public class dbStructure implements Serializable {
    
    public final static String STRUCT_TYPE_ORIGINAL = "Original";
    public final static String STRUCT_TYPE_NEUTRALIZED = "Neutralized";
    public final static String STRUCT_TYPE_ISOMER = "Isomer";
    public final static String STRUCT_TYPE_OTHER = "Other";
    
    public long Id;
    
    public String SMILES;
    public String Type;
    public String INCHI;
    public String INCHIKey;

    public SimilarityDescriptors SimDescriptors;
    public long ScreeningFP;
    public String Alerts;
    
}
