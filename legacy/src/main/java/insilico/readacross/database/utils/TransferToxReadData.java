package insilico.readacross.database.utils;

import insilico.readacross.database.DBConnector;
import insilico.readacross.database.entities.DBAccessTools;
import insilico.readacross.database.entities.DBMolecule;
import insilico.readacross.database.entities.DBValue;
import insilico.readacross.database.entities.DBValueType;
import insilico.readacross.database.iDBConnector;
import insilico.readacross.database.objects.*;
//import insilico.database.DBConnector;
//import insilico.database.objects.DBAccessTools;
//import insilico.database.objects.DBMolecule;
//import insilico.database.objects.DBValue;
//import insilico.database.objects.DBValueType;

import java.util.ArrayList;

/**
 *
 * @author Alberto
 */
public class TransferToxReadData {

    public TransferToxReadData(iDBConnector DB) throws Exception{
        
        DBConnector DBConn = new DBConnector("vega-db", DBConnector.OPEN_EXISTING_LOCAL);
        
        ArrayList<DBValueType> vt = DBAccessTools.GetAllValueTypes(DBConn);
        for (DBValueType dvt : vt) {
            System.out.println(dvt.GetId() + "\t" + dvt.GetName() + "\t" + dvt.GetUnits() + "\t" + dvt.GetEndpoint().GetId() + "\t" + dvt.GetEndpoint().GetName() + "\t" + dvt.GetEndpoint().GetDescription()); 

            dbEndpoint de = new dbEndpoint();
            de.ShortName = dvt.GetEndpoint().GetName();
            de.Description = "";
            DB.InsertEndpoint(de);

            dbDataType dt = new dbDataType();
            
            dt.IsQuantitative = dvt.GetIsNumeric();
            dt.Units = dvt.GetUnits();
            if (!dt.IsQuantitative) {
                dt.ClassLabels.put(0.0, dvt.GetClassForValue(0));
                dt.ClassLabels.put(1.0, dvt.GetClassForValue(1));
                dt.ClassLabels.put(2.0, dvt.GetClassForValue(2));
            }
            dt.EndpointId = de.Id;
            DB.InsertDatatype(dt);
            
        }
        
        System.out.println();
        
        ArrayList<DBMolecule> mols = DBAccessTools.GetAllMolecules(DBConn);
        for (DBMolecule m : mols) {
            System.out.println(m.GetId() + "\t" + "\t" + m.GetSMILES() + "\t" + m.GetAlerts());
            
            dbMolecule mol = new dbMolecule();
            dbStructure st = new dbStructure();

            st.SMILES = m.GetSMILES();
            st.Type = dbStructure.STRUCT_TYPE_ORIGINAL;
            st.Alerts = m.GetAlerts();
            st.SimDescriptors = m.GetDescriptors();
            st.ScreeningFP = m.GetScreeningFP();
            DB.InsertStructure(st);

            mol.CAS = m.GetCAS();
            mol.CommonName = "";
            mol.Notes = "";
            mol.StructureId = st.Id;
            DB.InsertMolecule(mol);            
            
            ArrayList<DBValue> vv = DBAccessTools.GetValues(DBConn, m, vt);
            for (DBValue v : vv) {
                System.out.println(v.GetValueType().GetName() + "\t" + v.GetFormattedValue());

                dbValue val = new dbValue();
                val.Value = v.GetValue();
                val.Operator = dbValue.OPERATOR_TYPE_EQ;
                val.Reliability = dbValue.RELIABILITY_4;
                val.DataTypeId = v.GetValueType().GetId() + 1;
                val.MoleculeId = mol.Id;
                DB.InsertValue(val);
            }
            
        }
        System.out.println();
        
        
        
    }
}
