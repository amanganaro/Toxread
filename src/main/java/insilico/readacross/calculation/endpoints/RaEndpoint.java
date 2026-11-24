
package insilico.readacross.calculation.endpoints;

import insilico.core.alerts.AlertBlock;
import insilico.core.alerts.AlertEncoding;
import insilico.core.alerts.AlertList;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.core.similarity.ScreeningFingerprint;
import insilico.core.similarity.SimilarityDescriptorsBuilder;
import insilico.core.tools.utils.GeneralUtilities;
import insilico.readacross.calculation.endpoints.utils.RaColorGradient;
import insilico.core.alerts.iAlertBlock;
import insilico.readacross.database.DBConnector;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.dbDataType;
import insilico.readacross.database.objects.dbEndpoint;
import insilico.readacross.database.objects.dbValue;
import insilico.readacross.database.objects.dbMolecule;
import insilico.readacross.database.objects.dbStructure;
import insilico.core.exception.GenericFailureException;
import insilico.core.exception.InitFailureException;
import insilico.readacross.type.interfaces.iRaObjectInterpolation;
import insilico.readacross.type.interfaces.iRaObjectRootMolecule;
import insilico.readacross.type.interfaces.iRaObjectRule;
import insilico.readacross.type.interfaces.iRaObjectSimMolecule;
import insilico.readacross.type.objects.RaObjectRootMolecule;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author User
 */
public abstract class RaEndpoint {
    
    public final static short SHAPE_UNDEFINED = 0;
    public final static short SHAPE_TRIANGLE_UP = 1;
    public final static short SHAPE_TRIANGLE_DOWN = 2;
    public final static short SHAPE_RECTANGLE = 3;
    
    protected iDBConnector DBconn;
    
    private final String Name;
    protected iAlertBlock[] AlertBlocks;
    protected boolean[] AlertBlockEnabled;
    protected long EndpointId;
    protected boolean Interpolation;
    protected RaColorGradient Gradient;
    protected boolean HasPrediction;

    protected String DatasetTextFile;
    
    
    public RaEndpoint(iDBConnector DBconn, String Name) throws InitFailureException{
        this.DBconn = DBconn;
        this.Name = Name;
        this.Interpolation = false;
        this.Gradient = null;
        this.HasPrediction = false;
        BuildAlertBlocks();
        BuildValueTypes();
    }
    
    protected abstract void BuildAlertBlocks() throws InitFailureException;
    protected abstract void BuildValueTypes() throws InitFailureException;
    
    public abstract Color getColorForRootMolecule();
    public abstract Color getColorForMolecule(dbValue ExpValue);
    public abstract short getShapeForRule(iRaObjectRule Rule);
    public abstract double getSizeFactorForRule(iRaObjectRule Rule);
    public abstract Color getColorForRule(iRaObjectRule Rule);

    
    public abstract void setAdditionalValues(iRaObjectSimMolecule Molecule);
    
    public abstract void SortRules(ArrayList<iRaObjectRule> Rules);
    
    /**
     * To be overridden if prediction for root molecule is available.
     * 
     * @param Root
     * @return true if a prediction has made
     */
    public String DoPrediction(RaObjectRootMolecule Root)  throws GenericFailureException {
        return "Overall assessmnet: NOT AVAILABLE";
    }

    /**
     * To be overridden if interpolation is available;
     * @param TargetMolecule
     * @param molecules molecules for the interpolation
     * @return 
     */
    public iRaObjectInterpolation getInterpolationNode(iRaObjectRootMolecule TargetMolecule, ArrayList<iRaObjectSimMolecule> molecules) {
        return null;
    }
    
    /**
     * @return the AlertBlocks
     */
    public iAlertBlock[] getAlertBlocks() {
        return AlertBlocks;
    }

    /**
     * @param index
     * @return the AlertBlockEnabled
     */
    public boolean isAlertBlockEnabled(int index) {
        return AlertBlockEnabled[index];
    }

    /**
     * @param index
     * @param value
     */
    public void setAlertBlockEnabled(int index, boolean value) {
        AlertBlockEnabled[index] = value;
    }

    /**
     * @param value 
     */
    public void setAllAlertBlockEnabled(boolean value) {
        for (int i=0; i<AlertBlockEnabled.length; i++)
            AlertBlockEnabled[i] = value;
    }
    
    /**
     * @return the ValueTypes
     */
    public long getEndpointId() {
        return EndpointId;
    }

    /**
     * @return the Name
     */
    public String getName() {
        return Name;
    }
    
    /**
     * Get if the endpoint has interpolation charts or not
     * @return 
     */
    public boolean HasInterpolation() {
        return this.Interpolation;
    }

    public boolean HasGradient() {
        return this.Gradient!=null;
    }
    
    public RaColorGradient getGradient() {
        return this.Gradient;
    }
    
    public boolean HasPrediction() {
        return this.HasPrediction;
    }
    
    public abstract String FormatValue(double Val);


    //// methods to build dataset and store it in db
    public void BuildAndSaveDataset() throws Exception {

        DataInputStream in;
        BufferedReader br;
        URL DsURL = getClass().getResource(this.DatasetTextFile);
        br = new BufferedReader(new InputStreamReader(new DataInputStream(DsURL.openStream())));

        // 1 - dataset name
        String DsName = GeneralUtilities.TrimString(br.readLine());
        System.out.println("* Processing: " + DsName);

        // 2 - qualitative or quantitative
        boolean DsQuantitative = Boolean.parseBoolean(GeneralUtilities.TrimString(br.readLine().split("\t")[1]));

        // 3 - units
        String[] units = br.readLine().split("\t");
        String DsUnits = units.length > 1 ? GeneralUtilities.TrimString(units[1]) : "";

        // 4 - label mapping
        String str;
        HashMap<Double, String> DsLabels = new HashMap<>();
        while ( (str=br.readLine())!= null ) {
            str = GeneralUtilities.TrimString(str);
            if (str.isEmpty())
                continue;
            if (!str.split("\t")[0].equalsIgnoreCase("label"))
                break;
            DsLabels.put(Double.valueOf(str.split("\t")[1]), str.split("\t")[2]);
        }

        // build and store datatype and endpoint
        dbEndpoint EP = new dbEndpoint();
        EP.ShortName = this.getName();
        EP.Id = this.getEndpointId();
        DBconn.InsertEndpointNoAutoId(EP);

        dbDataType DT = new dbDataType();
        DT.ClassLabels = DsLabels;
        DT.EndpointId = this.getEndpointId();
        DT.Id = this.getEndpointId();
        DT.IsQuantitative = DsQuantitative;
        DT.Units = DsUnits;
        DBconn.InsertDatatype(DT);


        // 5 - molecules
        SimilarityDescriptorsBuilder SimDescEngine = new SimilarityDescriptorsBuilder();
        if (str.equalsIgnoreCase("Molecules")) {
            while ( (str=br.readLine())!= null ) {
                str = GeneralUtilities.TrimString(str);
                if (str.isEmpty())
                    continue;
                long Id = Long.parseLong(str.split("\t")[0]);
                String CAS = str.split("\t")[1];
                String SMI = str.split("\t")[2];
                double Exp = Double.parseDouble(str.split("\t")[3]);

                // check structure and calculate screening fp + alerts
                InsilicoMolecule imol = SmilesMolecule.Convert(SMI);
                if (!imol.IsValid()) {
                    System.out.println("* Unable to parse molecule #" + Id + ": " + SMI);
                    continue;
                }
                long sFP = ScreeningFingerprint.Calculate(imol.GetStructure());

                ArrayList<dbMolecule> matches = DBconn.GetMatchingMolecules(imol, sFP);

                // check if new molecule should be inserted
                // create new mol if needed
                if (matches.isEmpty()) {
                    dbStructure STR = new dbStructure();
                    STR.SMILES = SMI;
                    STR.Alerts = "";
                    STR.ScreeningFP = sFP;
                    STR.SimDescriptors = SimDescEngine.Calculate(imol);
                    STR.Id = DBconn.InsertStructure(STR);

                    dbMolecule M = new dbMolecule();
                    M.CAS = CAS;
                    M.StructureId = STR.Id;
                    M.Id = DBconn.InsertMolecule(M);
                    Id = M.Id;
                } else {
                    System.out.println("* found existing mol for mol #" + Id);
                    Id = matches.get(0).Id;
                }

                dbValue VAL = new dbValue();
                VAL.DataTypeId = DT.Id;
                VAL.MoleculeId = Id;
                VAL.Operator = dbValue.OPERATOR_TYPE_EQ;
                VAL.Reliability = dbValue.RELIABILITY_4;
                VAL.Value = Exp;
                VAL.Id = DBconn.InsertValue(VAL);

            }
        }

    }
    
}
