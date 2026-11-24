package insilico.readacross.type.objects;

import insilico.core.molecule.InsilicoMolecule;
import insilico.readacross.type.interfaces.iRaObjectRootMolecule;
import java.awt.Color;
import java.util.ArrayList;

/**
 * RA object that contains a root (starting) molecule.
 * 
 * @author Alberto Manganaro
 */
public class RaObjectRootMolecule extends RaObject implements iRaObjectRootMolecule {
    
    private InsilicoMolecule Molecule;
    private final ArrayList<RaAdditionalValue> AdditionalValues;
    private Color MolColor;
    private String Prediction;
    
    
    /**
     * Constructor
     */
    public RaObjectRootMolecule() {
        super(RaObject.RA_OBJECT_ROOT_MOLECULE);
        Molecule = null;
        AdditionalValues = new ArrayList<>();
        MolColor = Color.GRAY;
        Prediction = "Overall assessmnet: NOT AVAILABLE";
    }

    @Override
    public void setMolecule(InsilicoMolecule mol) {
        this.Molecule = mol;
    }

    @Override
    public InsilicoMolecule getMolecule() {
        return this.Molecule;
    }
    

    @Override
    public boolean hasAdditionalValues() {
        return !AdditionalValues.isEmpty();        
    }

    @Override
    public void addAdditionalValue(RaAdditionalValue value) {
        AdditionalValues.add(value);
    }

    @Override
    public ArrayList<RaAdditionalValue> getAdditionalValues() {
        return AdditionalValues;
    }
        
    @Override
    public void setMoleculeColor(Color color) {
        this.MolColor = color;
    }

    @Override
    public Color getMoleculeColor() {
        return this.MolColor;
    }

    @Override
    public void setPrediction(String prediction) {
        this.Prediction = prediction;
    }
    
    @Override
    public String getPrediction() {
        return Prediction;
    }
    
}
