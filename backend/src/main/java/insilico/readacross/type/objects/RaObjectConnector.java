package insilico.readacross.type.objects;

import insilico.readacross.type.interfaces.iRaObjectConnector;

/**
 * RA object that acts a simple connector for further rules.
 * 
 * @author Alberto Manganaro (a.manganaro@kode-solutions.net)
 */
public class RaObjectConnector extends RaObject implements iRaObjectConnector {
    
    private boolean Expanded;
    
    public RaObjectConnector() {
        super(RaObject.RA_OBJECT_CONNECTOR);
        this.Expanded = false;
    }

    @Override
    public void setExpanded(boolean value) {
        Expanded = value;
    }

    @Override
    public boolean isExpanded() {
        return Expanded;
    }
    
}
