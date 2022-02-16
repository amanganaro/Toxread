package insilico.readacross.calculation;

import insilico.core.alerts.Alert;
import insilico.core.alerts.AlertEncoding;
import insilico.core.alerts.AlertList;
import insilico.core.alerts.builders.SABCFIRFMNThreshold;
import insilico.core.alerts.iAlertBlock;
import insilico.readacross.calculation.endpoints.utils.MeylanLogP;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.dbMolecule;
import insilico.readacross.database.objects.dbValue;
import insilico.core.descriptor.Descriptor;
import insilico.core.descriptor.blocks.Constitutional;
//import insilico.core.descriptor.blocks.logp.MeylanLogP;
import insilico.core.exception.DescriptorNotFoundException;
import insilico.readacross.type.objects.RaObjectRootMolecule;
import insilico.readacross.type.objects.RaObjectSimMolecule;
import insilico.readacross.type.objects.RaObjectRule;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.core.similarity.ScreeningFingerprint;
import insilico.readacross.calculation.endpoints.RaEndpoint;
import insilico.readacross.messagelogger.RaMessageLogger;
import insilico.readacross.type.interfaces.iRaObject;
import insilico.readacross.type.interfaces.iRaObjectConnector;
import insilico.readacross.type.interfaces.iRaObjectRootMolecule;
import insilico.readacross.type.interfaces.iRaObjectRule;
import insilico.readacross.type.interfaces.iRaObjectSimMolecule;
import insilico.readacross.type.objects.RaAdditionalValue;
import insilico.readacross.type.objects.RaObject;
import insilico.readacross.type.objects.RaObjectConnector;
import insilico.readacross.type.objects.RaResultsContainer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Main class for read-Across calculation engine
 * 
 * @author Alberto Manganaro
 */
public class RaEngine {

    // Available endpoints
    public final static String EP_MUTAGENICITY = "Mutagenicity";
    public final static String EP_BCF = "BCF";
    public final static String EP_ALGAE_COMBASE = "Algae Toxicity (biocides)";
    public final static String EP_MICROBES_COMBASE = "Microbes Toxicity (biocides)";
    public final static String EP_DAPHNIA_COMBASE = "Daphnia Magna Toxicity (biocides)";
    public final static String EP_FISH_COMBASE = "Fish Acute Toxicity (biocides)";
    public final static String EP_READY_BIO = "Ready Biodegradability";
    public final static String EP_PERS_SOIL = "Persistence (soil)";
    public final static String EP_PERS_WATER = "Persistence (water)";
    public final static String EP_PERS_SEDIMENT = "Persistence (sediment)";
    public final static String EP_REPROTOX = "Reproductive toxicity";
    public final static String EP_HEPATOTOX = "Hepatotoxicity";
    public final static String EP_NEPHROTOX = "Nephrotoxicity";
    public final static String EP_SKIN = "Skin sensitization";
    public final static String EP_FISH_VERMEER = "Fish Acute Toxicity";
    public final static String EP_DAPHNIA_VERMEER = "Daphnia Magna Toxicity";
    public final static String EP_CGX_VERMEER = "Carcinogenicity (CGX)";
    public final static String EP_MICRONUCLEUS_VERMEER = "In vitro Micro Nucleus assay";
    
    public final static String[] AVAILABLE_EP = {
        EP_MUTAGENICITY,
        EP_BCF,
        EP_ALGAE_COMBASE,
        EP_MICROBES_COMBASE,
        EP_DAPHNIA_COMBASE,
        EP_FISH_COMBASE,
        EP_READY_BIO,
        EP_PERS_SOIL,      
        EP_PERS_WATER,      
        EP_PERS_SEDIMENT,
        EP_REPROTOX,
        EP_HEPATOTOX,
        EP_NEPHROTOX,
        EP_SKIN,
        EP_FISH_VERMEER,
        EP_DAPHNIA_VERMEER,
        EP_CGX_VERMEER,
        EP_MICRONUCLEUS_VERMEER
    };
    
    // Id for value types into the DB - to be modified if the db is modified
    public final static int DB_VALUE_TYPE_MUTAGENICITY = 1;
    public final static int DB_VALUE_TYPE_BCF = 2;
    public final static int DB_VALUE_TYPE_CARCINOGENICITY = 3;
    public final static int DB_VALUE_TYPE_LOGP = 4;
    public final static int DB_VALUE_TYPE_ALGAE_COMBASE = 5;
    public final static int DB_VALUE_TYPE_MICROBES_COMBASE = 6;
    public final static int DB_VALUE_TYPE_DAPHNIA_COMBASE = 7;
    public final static int DB_VALUE_TYPE_FISH_COMBASE = 8;
    public final static int DB_VALUE_TYPE_READY_BIO = 9;
    public final static int DB_VALUE_TYPE_PERS_SOIL = 10;
    public final static int DB_VALUE_TYPE_PERS_WATER = 11;
    public final static int DB_VALUE_TYPE_PERS_SEDIMENT = 12;
    public final static int DB_VALUE_TYPE_REPROTOX = 13;
    public final static int DB_VALUE_TYPE_SKIN = 14;
    public final static int DB_VALUE_TYPE_HEPATOTOX = 15;
    public final static int DB_VALUE_TYPE_NEPHROTOX = 16;
    public final static int DB_VALUE_TYPE_DAPHNIA_VERMEER = 17;
    public final static int DB_VALUE_TYPE_FISH_VERMEER = 18;
    public final static int DB_VALUE_TYPE_CGX_VERMEER = 19;
    public final static int DB_VALUE_TYPE_MICRONUCLEUS_VERMEER = 20;

    // Types for additional values
    public final static short ADD_VALUE_TYPE_MW = 1;
    public final static short ADD_VALUE_TYPE_LOGP_EXP = 2;
    public final static short ADD_VALUE_TYPE_LOGP_PRED = 3;
    
    // Connector to local database
    private final iDBConnector DBConn;
    
    // Number of molecules to be used for RA similarity searching
    private int SimilarMoleculesSize;
    
    // Root node (also target value for interpolation chart)
    private RaObjectRootMolecule Root;
    
    
    
    public RaEngine(iDBConnector DBConn) {
        
        // Sets defaults
        SimilarMoleculesSize = 3;
        
        // Sets db connection
        this.DBConn = DBConn;
        
    }
    
 
    /**
     * Calculates the tree of rules and similar molecules for the given
     * input molecule.
     * 
     * @param endpoint
     * @param mol input molecule to be processed
     * @return the root element of the results
     */
    public RaResultsContainer Calculate(RaEndpoint endpoint, InsilicoMolecule mol) throws Exception {
        
        // Create DB dataset object
        RaMessageLogger.getLogger().SendMessage("Creating dataset from db...");
        RaDataset DS = new RaDataset(DBConn, endpoint.getEndpointId()); 

        // Inits similarity
        RaMessageLogger.getLogger().SendMessage("Calculating similarity values...");
        DS.InitSimilarity(mol);
        
        // Creates root object and calculate its properties
        RaMessageLogger.getLogger().SendMessage("Searching properties for target molecule...");
        Root = new RaObjectRootMolecule();
        Root.setMolecule(mol);
        AddValuesToRootMolecule(Root);
        
        // Gets similar molecules without alerts and creates
        // children nodes for each similar molecule
        RaMessageLogger.getLogger().SendMessage("Processing target molecule...");
        ArrayList<RaDataset.RaSimilarMolecule> Res = DS.GetSimilarMolecules(SimilarMoleculesSize, null);
        for (RaDataset.RaSimilarMolecule curRes : Res) {
            RaObjectSimMolecule curChild = new RaObjectSimMolecule();
            curChild.setMolecule(curRes.getMolecule());
            curChild.setSimilarity(curRes.getSimilarity());
            ArrayList<dbValue> curExpValues = DBConn.GetAllValuesForMoleculeAndEndpoint(curRes.getMolecule().Id, endpoint.getEndpointId());
            curChild.setExperimentalValue(curExpValues.get(0));
            curChild.setFormattedExperimentalValue(endpoint.FormatValue(curExpValues.get(0).Value));
            curChild.setMoleculeColor(endpoint.getColorForMolecule(curExpValues.get(0)));
            endpoint.setAdditionalValues(curChild);
            Root.addChildren(curChild);
        }
        
        // Add interpolation node if available
        if (endpoint.HasInterpolation()) {
            ArrayList<iRaObjectSimMolecule> mols = new ArrayList<>();
            Iterator it = Root.getChildren();
            while (it.hasNext()) {
                iRaObject child = (iRaObject)it.next();
                if (child.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE)
                    mols.add((iRaObjectSimMolecule)child);
            }
            if (!mols.isEmpty())
                Root.addChildren(endpoint.getInterpolationNode(Root, mols));
        }
        
        // Calculates SA for current molecule
        AlertList alerts = new AlertList();
        int count = 0;
        for (iAlertBlock SABuilder : endpoint.getAlertBlocks()) {
            if (endpoint.isAlertBlockEnabled(count)) {
                RaMessageLogger.getLogger().SendMessage("Processing rule-set " + SABuilder.getName() + "...");
                CheckForSpecialParameters(SABuilder);
                AlertList curAlerts = SABuilder.Calculate(mol);
                for (Alert curSA : curAlerts.getSAList())
                    alerts.add(curSA);
            }
            count++;
        }

        
        // Gets similar molecules for each alert found and creates
        // children nodes for each similar molecule
        RaMessageLogger.getLogger().SendMessage("Building rules and molecules tree...");
        ArrayList<iRaObjectRule> ChildrenList = new ArrayList<>();
        for (Alert curAlert : alerts.getSAList()) {

            // Check if this alert is the parent of another alert
            // found - in this case it will be skipped, as it will
            // be added as father to the proper alert
            boolean Skip = false;
            for (Alert a : alerts.getSAList()) {
                if (a.getId().equals(curAlert.getId())) continue;
                String newParentString = a.getParentAlerts();
                if (newParentString.isEmpty()) continue;
                String[] newParents = AlertEncoding.UnpackAlertIds(newParentString);
                for (String p : newParents)
                    if (p.equals(curAlert.getId())) {
                        Skip = true;
                        break;
                    }
            }

            if (Skip)
                continue;
            
            
            RaObjectRule curRule = new RaObjectRule();
            curRule.setAlert(curAlert);
            curRule.setShape(endpoint.getShapeForRule(curRule));
            curRule.setSizeFactor(endpoint.getSizeFactorForRule(curRule));
            curRule.setRuleColor(endpoint.getColorForRule(curRule));
            
            Res = DS.GetSimilarMolecules(SimilarMoleculesSize, curAlert);
            for (RaDataset.RaSimilarMolecule curRes : Res) {
                RaObjectSimMolecule curChild = new RaObjectSimMolecule();
                curChild.setMolecule(curRes.getMolecule());
                curChild.setSimilarity(curRes.getSimilarity());
                ArrayList<dbValue> curExpValues = DBConn.GetAllValuesForMoleculeAndEndpoint(curRes.getMolecule().Id, endpoint.getEndpointId()); 
                curChild.setExperimentalValue(curExpValues.get(0));
                curChild.setFormattedExperimentalValue(endpoint.FormatValue(curExpValues.get(0).Value));
                curChild.setMoleculeColor(endpoint.getColorForMolecule(curExpValues.get(0)));
                curRule.addChildren(curChild);
                endpoint.setAdditionalValues(curChild);
            }
            
            // Add interpolation node if available
            if (endpoint.HasInterpolation()) {
                ArrayList<iRaObjectSimMolecule> mols = new ArrayList<>();
                Iterator it = curRule.getChildren();
                while (it.hasNext()) {
                    iRaObject child = (iRaObject)it.next();
                    if (child.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE)
                        mols.add((iRaObjectSimMolecule)child);
                }
                if (!mols.isEmpty())
                    curRule.addChildren(endpoint.getInterpolationNode(Root, mols));
            }
            
            
            AddParentRules(curRule, alerts, DS, endpoint);
            
            //Root.addChildren(curRule);
            ChildrenList.add(curRule);            
        }
        
        // Sort children rules and add them to the root node
        endpoint.SortRules(ChildrenList);
        for (iRaObjectRule curRule : ChildrenList)
            Root.addChildren(curRule);
        
        
        // If endpoint has RA prediction, performs it
        if (endpoint.HasPrediction()) {
            RaMessageLogger.getLogger().SendMessage("Calculating ReadAcross prediction...");
            String Pred = endpoint.DoPrediction(Root);
            Root.setMoleculeColor(endpoint.getColorForRootMolecule());
            Root.setPrediction(Pred);
            System.out.println(Pred);
        }
        
        RaMessageLogger.getLogger().SendMessage("Done.");
        
        return new RaResultsContainer(DBConn, DS, endpoint, Root);
    }
    
    
    private void AddParentRules(RaObjectRule curRule, AlertList alerts, 
            RaDataset DS, RaEndpoint endpoint) throws Exception {

        // Extract parent alerts as id
        String ParentString = curRule.getAlert().getParentAlerts();
        if (ParentString.isEmpty())
            return;
        
        // Build list of parent alerts to be scanned
        String[] Parents = AlertEncoding.UnpackAlertIds(ParentString);
        ArrayList<Alert> ParentAlerts = new ArrayList<>();
        for (String curParent : Parents) 
            for (Alert curAlert : alerts.getSAList())
                if (curAlert.getId().equals(curParent)) 
                    ParentAlerts.add(curAlert);
        
        iRaObjectConnector Connector = new RaObjectConnector();

        // Iteration on each parent alert
        for (Alert curAlert : ParentAlerts) {

            // Check if this alert is the parent of another alert
            // found - in this case it will be skipped, as it will
            // be added as father to the proper alert
            boolean Skip = false;
            for (Alert a : ParentAlerts) {
                if (a.getId().equals(curAlert.getId())) continue; // same alert, skip

                String newParentString = a.getParentAlerts();
                if (newParentString.isEmpty()) continue;
                String[] newParents = AlertEncoding.UnpackAlertIds(newParentString);
                
                for (String p : newParents)
                    if (p.equals(curAlert.getId())) {
                        Skip = true;
                        break;
                    }
            }
            if (Skip) continue;

            // Otherwise, new rule shoud be added and checked recursively
            // for its parents

            RaObjectRule newRule = new RaObjectRule();
            newRule.setAlert(curAlert);
            newRule.setShape(endpoint.getShapeForRule(newRule));
            newRule.setSizeFactor(endpoint.getSizeFactorForRule(newRule));
            newRule.setRuleColor(endpoint.getColorForRule(newRule));

            ArrayList<RaDataset.RaSimilarMolecule> Res = DS.GetSimilarMolecules(SimilarMoleculesSize, curAlert);
            for (RaDataset.RaSimilarMolecule curRes : Res) {
                RaObjectSimMolecule curChild = new RaObjectSimMolecule();
                curChild.setMolecule(curRes.getMolecule());
                curChild.setSimilarity(curRes.getSimilarity());
                ArrayList<dbValue> curExpValues = DBConn.GetAllValuesForMoleculeAndEndpoint(curRes.getMolecule().Id, endpoint.getEndpointId());
                curChild.setExperimentalValue(curExpValues.get(0));
                curChild.setFormattedExperimentalValue(endpoint.FormatValue(curExpValues.get(0).Value));
                curChild.setMoleculeColor(endpoint.getColorForMolecule(curExpValues.get(0)));
                newRule.addChildren(curChild);
                endpoint.setAdditionalValues(curChild);
            }

            // Add interpolation node if available
            if (endpoint.HasInterpolation()) {
                ArrayList<iRaObjectSimMolecule> mols = new ArrayList<>();
                Iterator it = newRule.getChildren();
                while (it.hasNext()) {
                    iRaObject child = (iRaObject)it.next();
                    if (child.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE)
                        mols.add((iRaObjectSimMolecule)child);
                }
                if (!mols.isEmpty())
                    newRule.addChildren(endpoint.getInterpolationNode(Root, mols));
            }
            
            AddParentRules(newRule, alerts, DS, endpoint);
            Connector.addChildren(newRule);
        }

        // At the end, check if the connector node is populated with some
        // rules, in this case it is added to the current rule
        if (Connector.getChildrenSize() > 0)
            curRule.addChildren(Connector);
        
    }
    
    
    private void AddValuesToRootMolecule(iRaObjectRootMolecule Root) {

        DecimalFormatSymbols InternationalSymbols = new DecimalFormatSymbols();
        InternationalSymbols.setDecimalSeparator('.');         
        DecimalFormat Df2digits = new DecimalFormat("0.##", InternationalSymbols);
        
        // Molecular Weight
        double MW = 0;
        String SMILES = Root.getMolecule().GetSMILES();
        InsilicoMolecule Mol = SmilesMolecule.Convert(SMILES);
        if (Mol.IsValid()) {
            Constitutional Desc = new Constitutional();
            Desc.Calculate(Mol);
            try {
                // Note: MW is calculated on carbon-scaled values
                MW = Desc.GetByName("MW").getValue() * 12.011;
            } catch (DescriptorNotFoundException ex) {
                MW = Descriptor.MISSING_VALUE;
            }
        }
        Root.addAdditionalValue(new RaAdditionalValue(RaEngine.ADD_VALUE_TYPE_MW, 
                "Molecular Weight", MW, Df2digits.format(MW)));
        
        // LogP
        Double ExpLogP = null;
        
        try {
            long ScreeningFP = ScreeningFingerprint.Calculate(Mol.GetStructure());
            ArrayList<dbMolecule> Matches = DBConn.GetMatchingMolecules(Mol, ScreeningFP);
            for (dbMolecule m : Matches) {
                ArrayList<dbValue> vals = DBConn.GetAllValuesForMoleculeAndEndpoint(m.Id, DB_VALUE_TYPE_LOGP);
                if (!vals.isEmpty()) {
                    ExpLogP = vals.get(0).Value;
                    break;
                }
            }
        } catch (Exception e) {
            ExpLogP = null;
        }
        
        if (ExpLogP != null) {
            
            // Experimental is available
            Root.addAdditionalValue(new RaAdditionalValue(RaEngine.ADD_VALUE_TYPE_LOGP_EXP, "LogP (experimental)", ExpLogP, String.valueOf(ExpLogP)));
            
        } else {
            
            // LogP must be predicted
            MeylanLogP LogP = new MeylanLogP();
            LogP.Calculate(Mol);
            try {
                if (LogP.GetByName("LogP").getValue() != Descriptor.MISSING_VALUE)
                    Root.addAdditionalValue(new RaAdditionalValue(RaEngine.ADD_VALUE_TYPE_LOGP_PRED, "LogP (calculated)", LogP.GetByName("LogP").getValue(), LogP.GetByName("LogP").getFormattedValue()));
            } catch (DescriptorNotFoundException ex) {
                //
            }
            
        }        
    }
    
    
    /**
     * Only to check for special alert blocks that needs parameters
     * 
     * @param block 
     */
    private void CheckForSpecialParameters(iAlertBlock block) {
       
        // BCF property-based
        if (block.getClass() == SABCFIRFMNThreshold.class) {
            for (RaAdditionalValue val : Root.getAdditionalValues()) {
                if (val.getType() == RaEngine.ADD_VALUE_TYPE_MW) 
                    ((SABCFIRFMNThreshold)block).setMW(val.getNumericalValue());
                if ((val.getType() == RaEngine.ADD_VALUE_TYPE_LOGP_EXP) || (val.getType() == RaEngine.ADD_VALUE_TYPE_LOGP_PRED)) 
                    ((SABCFIRFMNThreshold)block).setLogP(val.getNumericalValue());
            }
        }
    }
    
    
    public void Close() throws Exception {
        DBConn.Disconnect(new HashMap<String, String>());
    }
    
    
    
    //// Getters and Setters

    /**
     * @return the SimilarMoleculesSize
     */
    public int getSimilarMoleculesSize() {
        return SimilarMoleculesSize;
    }

    /**
     * @param SimilarMoleculesSize the SimilarMoleculesSize to set
     */
    public void setSimilarMoleculesSize(int SimilarMoleculesSize) {
        this.SimilarMoleculesSize = SimilarMoleculesSize;
    }
}
