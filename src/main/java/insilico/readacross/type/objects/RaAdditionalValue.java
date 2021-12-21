package insilico.readacross.type.objects;

/**
 *
 * @author Alberto Manganaro (a.manganaro@kode-solutions.net)
 */
public class RaAdditionalValue {

    private final short Type;
    private final String Name;
    private final double NumericalValue;
    private final String FormattedValue;
    
    public RaAdditionalValue(short Type, String Name, double NumericalValue, String FormattedValue) {
        this.Type = Type;
        this.Name = Name;
        this.NumericalValue = NumericalValue;
        this.FormattedValue = FormattedValue;
    }

    /**
     * @return the Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @return the NumericalValue
     */
    public double getNumericalValue() {
        return NumericalValue;
    }

    /**
     * @return the FormattedValue
     */
    public String getFormattedValue() {
        return FormattedValue;
    }

    /**
     * @return the Type
     */
    public short getType() {
        return Type;
    }
}
