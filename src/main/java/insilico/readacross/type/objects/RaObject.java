package insilico.readacross.type.objects;

import insilico.readacross.type.interfaces.iRaObject;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Ancestor object for all RA objects, implementing the 
 * iRaObject interface.
 * 
 * @author Alberto Manganaro
 */
public class RaObject implements iRaObject {

    public static int RA_OBJECT_ROOT_MOLECULE = 1;
    public static int RA_OBJECT_RULE = 2;
    public static int RA_OBJECT_SIM_MOLECULE = 3;
    public static int RA_OBJECT_CONNECTOR = 4;
    public static int RA_OBJECT_INTERPOLATION = 5;
    
    private final int ObjectType;
    private ArrayList<iRaObject> Children;
    private iRaObject Father;
    

    /**
     * Constructor of the object, the type (integer) of the instance
     * should be passed as parameter.
     * @param CurObjectType type of the instance object
     */
    public RaObject(int CurObjectType) {
        this.ObjectType = CurObjectType;
        Children = new ArrayList<iRaObject>();
        Father = null;
    }
    
    @Override
    public int getObjectType() {
        return ObjectType;
    }

    @Override
    public int getChildrenSize() {
        return this.Children.size();
    }

    @Override
    public iRaObject getChild(int index) {
        if ((index>=0) && (index<this.getChildrenSize()))
            return this.Children.get(index);
        else
            return null;
    }

    @Override
    public Iterator<iRaObject> getChildren() {
        return this.Children.iterator();
    }

    @Override
    public void addChildren(iRaObject obj) {
        this.Children.add(obj);
    }

    @Override
    public iRaObject getFather() {
        return this.Father;
    }
    
}
