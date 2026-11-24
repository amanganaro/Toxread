package insilico.readacross.type.interfaces;

import java.util.Iterator;

/**
 * Generic interface for objects to be displayed in the RA chart,
 * implemented as a list.
 * 
 * @author Alberto Manganaro
 */
public interface iRaObject {

    /**
     * Gets the type of the object
     * @return type of the object
     */
    public int getObjectType();

    /**
     * Gets the number of current children
     * @return number of children
     */
    public int getChildrenSize();
    
    /**
     * Gets the i-th child, or null on wrong index
     * @param index index of the child
     * @return the index-th child
     */
    public iRaObject getChild(int index);
    
    /**
     * Gets the Iterator object for all children
     * @return Iterator on children
     */
    public Iterator<iRaObject> getChildren();
    
    /**
     * Adds a new child
     * @param obj child to be added
     */
    public void addChildren(iRaObject obj);
    
    /**
     * Gets the current father, or null if no father is set
     * @return the father
     */
    public iRaObject getFather();
    
}
