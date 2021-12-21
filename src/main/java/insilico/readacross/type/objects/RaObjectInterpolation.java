package insilico.readacross.type.objects;

import insilico.readacross.gui.chart.components.interpolation.InterpolationChartCoordinates;
import insilico.readacross.gui.chart.components.interpolation.InterpolationDataPoint;
import insilico.readacross.gui.chart.components.interpolation.InterpolationDataSet;
import insilico.readacross.type.interfaces.iRaObjectInterpolation;

/**
 * RA object that contains descriptors interpolation chart data.
 *
 * @author Alberto Manganaro
 */
public class RaObjectInterpolation extends RaObject implements iRaObjectInterpolation {

    private final InterpolationDataSet dataset;
    private String AxisXTitle;
    private String AxisYTitle;
    private RaAdditionalValue TargetValue;
    
    public RaObjectInterpolation() {
        super(RaObject.RA_OBJECT_INTERPOLATION);
        
        this.AxisXTitle = "";
        this.AxisYTitle = "";
        this.TargetValue = null;
        dataset = new InterpolationDataSet();
    }

    @Override
    public void setAxisXTitle(String title) {
        this.AxisXTitle = title;
    }

    @Override
    public String getAxisXTitle() {
        return this.AxisXTitle;
    }

    @Override
    public void setAxisYTitle(String title) {
        this.AxisYTitle = title;
    }

    @Override
    public String getAxisYTitle() {
        return this.AxisYTitle;
    }

    @Override
    public void AddMolecule(String Name, double ValueX, boolean isValueXExperimental, double ValueY, int Size) {
        InterpolationDataPoint p = new InterpolationDataPoint(ValueX, ValueY, Name, Size);
        p.setExperimental(isValueXExperimental);
        dataset.Add(p);        
    }

    @Override
    public InterpolationDataSet getDataset() {
        return this.dataset;
    }

    @Override
    public void SetTargetValue(RaAdditionalValue value) {
        this.TargetValue = value;
        dataset.SetTargetLine(new InterpolationChartCoordinates(value.getNumericalValue(), 0));
    }
    
    @Override
    public RaAdditionalValue GetTargetValue() {
        return this.TargetValue;
    }
    
    
}
