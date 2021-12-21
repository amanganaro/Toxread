package insilico.readacross.database.objects;

import java.io.Serializable;

/**
 *
 * @author Alberto
 */
public class dbReference implements Serializable {

    public final static String REFERENCE_TYPE_ARTICLE = "Article";
    public final static String REFERENCE_TYPE_BOOK = "Book";
    public final static String REFERENCE_TYPE_DB = "Database";
    public final static String REFERENCE_TYPE_SITE = "Website";
    public final static String REFERENCE_TYPE_SW = "Software";
    
    public long Id;

    public String ShortName;
    public String FullReference;
    public int Year;
    public String Type;
    public String Link;
    public String Notes;
    
    
}
