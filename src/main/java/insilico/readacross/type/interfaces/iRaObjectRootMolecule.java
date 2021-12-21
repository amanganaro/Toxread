package insilico.readacross.type.interfaces;

import insilico.core.molecule.InsilicoMolecule;
import insilico.readacross.type.objects.RaAdditionalValue;
import java.awt.Color;
import java.util.ArrayList;

/**
 * Interface for the Root Molecule RA object
 * 
 * @author Alberto Manganaro
 */
public interface iRaObjectRootMolecule extends iRaObject {
   
    /**
     * Sets the molecule object
     * @param mol molecule object to be set
     */
    public void setMolecule(InsilicoMolecule mol);
    
    /**
     * Gets the molecule object
     * @return the current molecule object
     */
    public InsilicoMolecule getMolecule();
    
    /**
     * Return whether additional values are available or not
     * @return 
     */
    public boolean hasAdditionalValues();
    
    /**
     * Add one additional value
     * @param value 
     */
    public void addAdditionalValue(RaAdditionalValue value);
    
    /**
     * Get the array list of available additional values
     * @return 
     */
    public ArrayList<RaAdditionalValue> getAdditionalValues();
    
    /**
     * Set the color that should be used to paint this molecule object
     * @param color 
     */
    public void setMoleculeColor(Color color);

    /**
     * Get the color that should be used to paint this molecule object
     * @return the color of the molecule
     */
    public Color getMoleculeColor();  
    
    /**
     * Set the prediction (as text) for the root molecule
     * @param prediction 
     */
    public void setPrediction(String prediction);
    
    /**
     * Get the prediction (as text)
     * @return the color of the molecule
     */
    public String getPrediction();
    
}
