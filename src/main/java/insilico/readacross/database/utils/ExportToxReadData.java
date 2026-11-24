//package insilico.readacross.database.utils;
//
//import insilico.readacross.database.DBConnector;
//import insilico.readacross.database.objects.DBAccessTools;
//import insilico.readacross.database.objects.DBMolecule;
//import insilico.readacross.database.objects.DBValue;
//import insilico.readacross.database.objects.DBValueType;
//
//import java.util.ArrayList;
//
///**
// *
// * @author Alberto
// */
//public class ExportToxReadData {
//
//    public ExportToxReadData() throws Exception{
//
//        DBConnector DBConn = new DBConnector("vega-db", DBConnector.OPEN_EXISTING_LOCAL);
//
//        ArrayList<DBValueType> vt = DBAccessTools.GetAllValueTypes(DBConn);
//        for (DBValueType dvt : vt)
//            System.out.println(dvt.GetId() + "\t" + dvt.GetName() + "\t" + dvt.GetUnits() + "\t" + dvt.GetEndpoint().GetId() + "\t" + dvt.GetEndpoint().GetName() + "\t" + dvt.GetEndpoint().GetDescription());
//
//
//        System.out.println();
//
//        ArrayList<DBMolecule> mols = DBAccessTools.GetAllMolecules(DBConn);
//        for (DBMolecule m : mols)
//            System.out.println(m.GetId() + "\t" + "\t" + m.GetSMILES() + "\t" + m.GetAlerts());
//
//        System.out.println();
//
//        for (DBMolecule m : mols) {
//            ArrayList<DBValue> vv = DBAccessTools.GetValues(DBConn, m, vt);
//            for (DBValue v : vv)
//                System.out.println(m.GetId() + "\t" + m.GetSMILES() + "\t" + v.GetValueType().GetName() + "\t" + v.GetValue() + "\t" + v.GetFormattedValue());
//        }
//
//    }
//}
