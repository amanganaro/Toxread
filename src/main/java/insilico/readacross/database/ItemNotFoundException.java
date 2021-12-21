package insilico.readacross.database;

/**
 * Default class for database exceptions
 * 
 * @author Alberto Manganaro (a.manganaro@kode-solutions.net)
 */
public class ItemNotFoundException extends Exception {
    
    private static final long serialVersionUID = 1L;

    
    public ItemNotFoundException() {
            // TODO Auto-generated constructor stub
    }

    public ItemNotFoundException(String message) {
            super(message);
            // TODO Auto-generated constructor stub
    }

    public ItemNotFoundException(Throwable cause) {
            super(cause);
            // TODO Auto-generated constructor stub
    }

    public ItemNotFoundException(String message, Throwable cause) {
            super(message, cause);
            // TODO Auto-generated constructor stub
    }

}
