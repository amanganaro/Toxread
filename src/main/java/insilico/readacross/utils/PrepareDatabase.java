package insilico.readacross.utils;

import insilico.core.alerts.AlertEncoding;
import insilico.core.alerts.AlertList;
import insilico.core.alerts.builders.SABCFIRFMNThreshold;
import insilico.core.alerts.builders.SAMutagenCRS4;
import insilico.core.alerts.iAlertBlock;
import insilico.core.descriptor.Descriptor;
import insilico.core.descriptor.blocks.Constitutional;
import insilico.core.exception.DescriptorNotFoundException;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.readacross.calculation.RaEngine;
import insilico.readacross.calculation.endpoints.*;
import insilico.readacross.calculation.endpoints.utils.MeylanLogP;
import insilico.readacross.database.local.DBConnectorLocal;
import insilico.readacross.database.objects.dbEndpoint;
import insilico.readacross.database.objects.dbMolecule;
import insilico.readacross.database.objects.dbStructure;
import insilico.readacross.database.objects.dbValue;
import insilico.readacross.type.objects.RaAdditionalValue;

import java.util.ArrayList;
import java.util.HashMap;


public class PrepareDatabase {

    public static void main(String args[]) throws Exception {
        PrepareDatabase p = new PrepareDatabase();

        p.BuildDatabase();

        System.out.println("-----");
        p.CloseAndSave("db_toxread_prova_4.dat");
        p.DbSummary();
    }


    public DBConnectorLocal db;

    public PrepareDatabase() throws Exception {
        db = new DBConnectorLocal();
        db.Connect(new HashMap<>());
    }

    public void BuildDatabase() throws Exception {
        RaEndpoint ep;
        ArrayList<iAlertBlock> alerts = new ArrayList<>();

        ep = new RaEndpointMutagenicity(db);
        for (iAlertBlock a : ep.getAlertBlocks()) {
                alerts.add(a);
        }
        AddEndpointData(ep);

        ep = new RaEndpointSkinSensitization(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointAlgaeCombase(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointCarcinogenicityCGX(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointHepatotox(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointMicrobesCombase(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointMicronucleus(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointNephrotox(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointPersistenceSediment(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointPersistenceSoil(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointPersistenceWater(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointReadyBiodegradability(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointReprotox(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointLogP(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);

        ep = new RaEndpointBCF(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            alerts.add(a);
        AddEndpointData(ep);



        UpdateAllAlerts(alerts);
    }

    public void DbSummary() throws Exception{
        System.out.println("Molecules: " + db.GetAllMolecules().size());
        System.out.println("Endpoints: " + db.GetAllEndpoints().size());
        System.out.println("Exp Values: " + db.GetAllValues().size());
        for (dbEndpoint ep : db.GetAllEndpoints()) {
            System.out.println("EP #" + ep.Id + ": " + ep.ShortName + " - Exp Values: " + db.GetAllValuesForEndpoint(ep.Id).size());
        }
    }

    public void AddEndpointData(RaEndpoint EP) throws Exception {
        EP.BuildAndSaveDataset();
    }

    public void UpdateAllAlerts(ArrayList<iAlertBlock> blocks) throws Exception {

        MeylanLogP LogPDesc = new MeylanLogP();


        for (iAlertBlock AB : blocks) {
            System.out.println("* processing alerts for: " + AB.getName());

            for (dbMolecule mol : db.GetAllMolecules()) {

                dbStructure str = db.GetStructure(mol.StructureId);
                InsilicoMolecule imol = SmilesMolecule.Convert(str.SMILES);

                // special case for BCF - Mw and logP have to be set
                if (AB.getClass() == SABCFIRFMNThreshold.class) {

                    double MW = Descriptor.MISSING_VALUE;
                    double logP = Descriptor.MISSING_VALUE;

                    try {
                        if (imol.IsValid()) {
                            Constitutional Desc = new Constitutional();
                            Desc.Calculate(imol);
                            try {
                                // Note: MW is calculated on carbon-scaled values
                                MW = Desc.GetByName("MW").getValue() * 12.011;
                            } catch (DescriptorNotFoundException ex) {
                                MW = Descriptor.MISSING_VALUE;
                            }
                        }
                    } catch (Exception ex) {
                        //
                    }

                    // LogP

                    Double ExpLogP = null;

                    try {
                        ArrayList<dbValue> res = db.GetAllValuesForMoleculeAndEndpoint(mol.StructureId, RaEngine.DB_VALUE_TYPE_LOGP);
                        if (!res.isEmpty())
                            ExpLogP = res.get(0).Value;
                    } catch (Exception e) {
                        ExpLogP = null;
                    }

                    if (ExpLogP != null) {

                        logP = ExpLogP;

                    } else {

                        // LogP must be predicted
                        if (imol.IsValid()) {
                            LogPDesc.Calculate(imol);
                            try {
                                if (LogPDesc.GetByName("LogP").getValue() != Descriptor.MISSING_VALUE)
                                    logP = LogPDesc.GetByName("LogP").getValue();
                            } catch (DescriptorNotFoundException ex) {
                                //
                            }
                        }
                    }

                    ((SABCFIRFMNThreshold) AB).setLogP(logP);
                    ((SABCFIRFMNThreshold) AB).setMW(MW);
                }

                ArrayList<AlertList> curAlerts = new ArrayList<>();
                curAlerts.add(AB.Calculate(imol));

                String alerts = AlertEncoding.MergeAlertIds(curAlerts);
                str.Alerts = AlertEncoding.MergeAlertIds(alerts, str.Alerts);
            }
        }
    }


    public void CloseAndSave(String DBUrl) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put(DBConnectorLocal.CONN_PARAM_OUTPUT_SERIAL, DBUrl);
        db.Disconnect(params);
    }

}
