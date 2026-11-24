package insilico.readacross.type.interfaces;

import insilico.readacross.gui.chart.components.interpolation.InterpolationDataSet;
import insilico.readacross.type.objects.RaAdditionalValue;


/**
 * Interface for the descriptors interpolation chart RA objects
 * 
 * @author Alberto Manganaro
 */
public interface iRaObjectInterpolation extends iRaObject {
   
   public void setAxisXTitle(String title);
   public String getAxisXTitle();
   public void setAxisYTitle(String title);
   public String getAxisYTitle();
   
   public void AddMolecule(String Name, double ValueX, boolean isValueXExperimental, 
           double ValueY, int Size);
   public void SetTargetValue(RaAdditionalValue value);
   public RaAdditionalValue GetTargetValue();
   public InterpolationDataSet getDataset();
   
}
