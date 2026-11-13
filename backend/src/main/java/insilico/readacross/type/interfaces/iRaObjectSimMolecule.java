package insilico.readacross.type.interfaces;

import insilico.readacross.database.objects.dbMolecule;
import insilico.readacross.database.objects.dbValue;
//import insilico.database.objects.DBMolecule;
//import insilico.database.objects.DBValue;
import insilico.readacross.type.objects.RaAdditionalValue;
import java.awt.Color;
import java.util.ArrayList;

/**
 * Interface for the similar molecules RA objects
 * 
 * @author Alberto Manganaro
 */
public interface iRaObjectSimMolecule extends iRaObject {
   
    /**
     * Sets the molecule object
     * @param mol molecule object to be set
     */
    public void setMolecule(dbMolecule mol);
    
    /**
     * Gets the molecule object
     * @return the current molecule object
     */
    public dbMolecule getMolecule();
    
    /**
     * Sets the similarity value for the molecule [0,1]
     * @param value similarity value
     */
    public void setSimilarity(double value);
    
    /**
     * Gets the current similarity value for the molecule
     * @return the current similarity value
     */
    public double getSimilarity();
    
    /**
     * Set the toxicity value for the molecule
     * @param value toxicity value for molecule [0,1]
     */
    public void setExperimentalValue(dbValue value);
    
    /**
     * Get the current toxicity for the molecule
     * @return the toxicity value of molecule
     */
    public dbValue getExperimentalValue();
    
    /**
     * Set the formatted toxicity value for the molecule
     * @param value toxicity value for molecule [0,1]
     */
    public void setFormattedExperimentalValue(String value);
    
    /**
     * Get the current formatted toxicity for the molecule
     * @return the toxicity value of molecule
     */
    public String getFormattedExperimentalValue();
    
    /**
     * Return wheter additional values are avaialble or not
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
}
