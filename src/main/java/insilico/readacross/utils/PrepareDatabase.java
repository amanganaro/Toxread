package insilico.readacross.utils;

import insilico.core.alerts.AlertEncoding;
import insilico.core.alerts.AlertList;
import insilico.core.alerts.builders.SABCFIRFMNThreshold;
import insilico.core.alerts.builders.SAMutagenCRS4;
import insilico.core.alerts.iAlertBlock;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.readacross.calculation.endpoints.*;
import insilico.readacross.database.local.DBConnectorLocal;
import insilico.readacross.database.objects.dbEndpoint;
import insilico.readacross.database.objects.dbMolecule;
import insilico.readacross.database.objects.dbStructure;

import java.util.ArrayList;
import java.util.HashMap;


public class PrepareDatabase {

    public static void main(String args[]) throws Exception {
        PrepareDatabase p = new PrepareDatabase();

        p.BuildDatabase();

        System.out.println("-----");
        p.CloseAndSave("db_toxread_prova_3.dat");
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
            // TODO - sistemare
            if (a.getClass() != SAMutagenCRS4.class)
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

        ep = new RaEndpointBCF(db);
        for (iAlertBlock a : ep.getAlertBlocks())
            // TODO - sistemare
//            if (a.getClass() != SABCFIRFMNThreshold.class)
            alerts.add(a);


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

        for (iAlertBlock AB : blocks) {
            System.out.println("* processing alerts for: " + AB.getName());

            for (dbMolecule mol : db.GetAllMolecules()) {

                dbStructure str = db.GetStructure(mol.StructureId);
                InsilicoMolecule imol = SmilesMolecule.Convert(str.SMILES);

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
