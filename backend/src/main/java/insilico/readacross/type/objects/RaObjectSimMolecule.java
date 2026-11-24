package insilico.readacross.type.objects;

//import insilico.core.database.objects.dbMolecule;
//import insilico.core.database.objects.dbValue;
import insilico.readacross.database.objects.dbMolecule;
import insilico.readacross.database.objects.dbValue;
import insilico.readacross.type.interfaces.iRaObjectSimMolecule;
import java.awt.Color;
import java.util.ArrayList;

/**
 * RA object that contains a similar molecule.
 *
 * @author Alberto Manganaro
 */
public class RaObjectSimMolecule extends RaObject implements iRaObjectSimMolecule {
    
    private dbMolecule Molecule;
    private double Similarity;
    private dbValue ExpValue;
    private String ExpValueFormatted;
    private Color MolColor;
    private final ArrayList<RaAdditionalValue> AdditionalValues;
    
    /**
     * Constructor
     */
    public RaObjectSimMolecule() {
        super(RaObject.RA_OBJECT_SIM_MOLECULE);
        Molecule = null;
        Similarity = 0;
        MolColor = Color.GRAY;
        ExpValueFormatted = "";
        AdditionalValues = new ArrayList<>();
    }

    @Override
    public void setMolecule(dbMolecule mol) {
        this.Molecule = mol;
    }

    @Override
    public dbMolecule getMolecule() {
        return this.Molecule;
    }

    @Override
    public void setSimilarity(double value) {
        this.Similarity = value;
    }

    @Override
    public double getSimilarity() {
        return this.Similarity;
    }

    @Override
    public void setExperimentalValue(dbValue value) {
        this.ExpValue = value;
    }

    @Override
    public dbValue getExperimentalValue() {
        return this.ExpValue;
    }

    @Override
    public void setFormattedExperimentalValue(String value) {
        this.ExpValueFormatted = value;
    }
    
    @Override
    public String getFormattedExperimentalValue() {
        return this.ExpValueFormatted;
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
    
    
}
