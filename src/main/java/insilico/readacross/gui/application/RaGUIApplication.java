package insilico.readacross.gui.application;

import insilico.readacross.database.iDBConnector;
//import insilico.readacross.database.implementation.local.DBConnectorLocal;
import insilico.readacross.database.local.DBConnectorLocal;
import insilico.readacross.database.objects.dbEndpoint;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.readacross.calculation.RaEngine;
import insilico.readacross.calculation.endpoints.RaEndpoint;
import insilico.readacross.calculation.endpoints.RaEndpointAlgaeCombase;
import insilico.readacross.calculation.endpoints.RaEndpointBCF;
import insilico.readacross.calculation.endpoints.RaEndpointCarcinogenicityCGX;
import insilico.readacross.calculation.endpoints.RaEndpointDaphniaCombase;
import insilico.readacross.calculation.endpoints.RaEndpointDaphniaVermeer;
import insilico.readacross.calculation.endpoints.RaEndpointFishCombase;
import insilico.readacross.calculation.endpoints.RaEndpointFishVermeer;
import insilico.readacross.calculation.endpoints.RaEndpointHepatotox;
import insilico.readacross.calculation.endpoints.RaEndpointMicrobesCombase;
import insilico.readacross.calculation.endpoints.RaEndpointMicronucleus;
import insilico.readacross.calculation.endpoints.RaEndpointMutagenicity;
import insilico.readacross.calculation.endpoints.RaEndpointNephrotox;
import insilico.readacross.calculation.endpoints.RaEndpointPersistenceSediment;
import insilico.readacross.calculation.endpoints.RaEndpointPersistenceSoil;
import insilico.readacross.calculation.endpoints.RaEndpointPersistenceWater;
import insilico.readacross.calculation.endpoints.RaEndpointReadyBiodegradability;
import insilico.readacross.calculation.endpoints.RaEndpointReprotox;
import insilico.readacross.calculation.endpoints.RaEndpointSkinSensitization;
import insilico.readacross.messagelogger.RaMessageLogger;
import insilico.readacross.type.objects.RaResultsContainer;
import insilico.readacross.utils.PrepareDatabase;

import java.awt.Frame;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author User
 */
public class RaGUIApplication extends JFrame {

    private iDBConnector DBConn;
    private final static String VERSION = "BETA 0.23";
//    private final static String DB_FILE = "/vega-db/db_toxread_7_0.dat";
    private final static String DB_FILE = "/vega-db/db_toxread_prova.dat";

    private final static boolean LOAD_FROM_SERIALIZED_FILE = true;

    
    /**
     * Creates new form RaTestApplication
     */
    public RaGUIApplication() {
        initComponents();
        
        this.setTitle("ToxRead " + VERSION);
        
        // set available endpoints
        DefaultComboBoxModel dcm = new DefaultComboBoxModel();
        for (String s : RaEngine.AVAILABLE_EP)
            dcm.addElement(s);
        jComboBoxEndpoint.setModel(dcm);
        
        DefaultCaret caret = (DefaultCaret)jTextAreaLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);        
        
        // Center the frame
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width/2)-getWidth()/2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height/2)-getHeight()/2;
        setLocation(x,y);        
        
        
        // Inits the database
        jButtonRun.setEnabled(false);
        Load_Textfield.setEnabled(false);
        
        RaMessageLogger.getLogger().SetOutputStreamForComponent(jTextAreaLog);
        RaMessageLogger.getLogger().SendMessage("Initializing database...");
        try {

            if (LOAD_FROM_SERIALIZED_FILE) {

                DBConn = new DBConnectorLocal();
                HashMap<String, String> p = new HashMap<>();
                p.put(DBConnectorLocal.CONN_PARAM_INPUT_SERIAL, DB_FILE);
                DBConn.Connect(p);

            } else {

                PrepareDatabase prepDB = new PrepareDatabase();
                prepDB.BuildDatabase();
                DBConn = prepDB.db;
            }

            RaMessageLogger.getLogger().SendMessage("Database correctly initialized.");
        } catch (Exception e) {
            RaMessageLogger.getLogger().SendMessage("Unable to open database - " + e.getMessage());
        }
        
        // Initial info from db
        try {
            RaMessageLogger.getLogger().SendMessage("");
            RaMessageLogger.getLogger().SendMessage("Available molecules: " + DBConn.GetAllMolecules().size());
            RaMessageLogger.getLogger().SendMessage("Available experimental data: ");
            ArrayList<dbEndpoint> epList = DBConn.GetAllEndpoints();
            for (dbEndpoint ep : epList) {
                int n = DBConn.GetAllValuesForEndpoint(ep.Id).size();
                RaMessageLogger.getLogger().SendMessage("" + ep.ShortName +": " + n);
            }
                
        } catch (Exception e) {
            RaMessageLogger.getLogger().SendMessage("Error while accessing db - " + e.getMessage());
        }
        
        jButtonRun.setEnabled(true);
        Load_Textfield.setEnabled(true);
        
        this.Load_Textfield.setText("c1cc(Cl)c(Br)cc1CCNNCC=O");
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaLog = new javax.swing.JTextArea();
        jButtonRun = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jSpinnerNumMols = new javax.swing.JSpinner();
        jPanelInsert = new javax.swing.JPanel();
        Load_Lbl = new javax.swing.JLabel();
        Load_Textfield = new javax.swing.JTextField();
        jComboBoxEndpoint = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Read-Across");
        setMinimumSize(new java.awt.Dimension(480, 400));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTextAreaLog.setEditable(false);
        jTextAreaLog.setBackground(new java.awt.Color(0, 0, 0));
        jTextAreaLog.setColumns(20);
        jTextAreaLog.setForeground(new java.awt.Color(255, 255, 255));
        jTextAreaLog.setRows(5);
        jScrollPane1.setViewportView(jTextAreaLog);

        jButtonRun.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jButtonRun.setText("Run read-across");
        jButtonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel2.setText("Number of similar molecules:");

        jSpinnerNumMols.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jSpinnerNumMols.setModel(new javax.swing.SpinnerNumberModel(3, 1, 100, 1));

        jPanelInsert.setBackground(new java.awt.Color(255, 255, 255));
        jPanelInsert.setBorder(new javax.swing.border.LineBorder(java.awt.Color.lightGray, 1, true));
        jPanelInsert.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Load_Lbl.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        Load_Lbl.setForeground(new java.awt.Color(61, 69, 76));
        Load_Lbl.setText("Insert SMILES:");
        jPanelInsert.add(Load_Lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 130, 20));

        Load_Textfield.setOpaque(true);
        Load_Textfield.setSelectionColor(new java.awt.Color(144, 188, 231));
        jPanelInsert.add(Load_Textfield, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 460, 30));

        jComboBoxEndpoint.setModel(new DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel1.setText("Endpoint: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelInsert, javax.swing.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinnerNumMols, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonRun, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxEndpoint, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelInsert, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jSpinnerNumMols, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxEndpoint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jButtonRun, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        
        // Closes db before exit
        
        if (DBConn != null) 
            try {
                DBConn.Disconnect(new HashMap<String,String>());
            } catch (Exception e) {}
    }//GEN-LAST:event_formWindowClosing

    
    private void jButtonRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunActionPerformed
        
        // Swing worker for running read-across
        
        class SWRunReadAcross extends SwingWorker<Object, Object> {

            @Override
            protected Object doInBackground() throws Exception {

                RaMessageLogger.getLogger().SendMessage("");
                
                InsilicoMolecule mol = SmilesMolecule.Convert(Load_Textfield.getText());
                if (!mol.IsValid()) {
                    RaMessageLogger.getLogger().SendMessage("Unable to parse SMILES molecule, read-across not performed.");
                }

                try {
                    RaEndpoint endpoint;
                    
                    String selEP = (String)jComboBoxEndpoint.getSelectedItem();

                    if (selEP.equalsIgnoreCase(RaEngine.EP_MUTAGENICITY))
                        endpoint = new RaEndpointMutagenicity(DBConn);
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_BCF))
                        endpoint = new RaEndpointBCF(DBConn);
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_ALGAE_COMBASE))
                        endpoint = new RaEndpointAlgaeCombase(DBConn);  
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_MICROBES_COMBASE))
                        endpoint = new RaEndpointMicrobesCombase(DBConn);    
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_DAPHNIA_COMBASE))
                        endpoint = new RaEndpointDaphniaCombase(DBConn); 
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_FISH_COMBASE))
                        endpoint = new RaEndpointFishCombase(DBConn);   
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_READY_BIO))
                        endpoint = new RaEndpointReadyBiodegradability(DBConn);   
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_PERS_SOIL))
                        endpoint = new RaEndpointPersistenceSoil(DBConn);   
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_PERS_WATER))
                        endpoint = new RaEndpointPersistenceWater(DBConn);   
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_PERS_SEDIMENT))
                        endpoint = new RaEndpointPersistenceSediment(DBConn);   
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_REPROTOX))
                        endpoint = new RaEndpointReprotox(DBConn);   
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_HEPATOTOX))
                        endpoint = new RaEndpointHepatotox(DBConn);   
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_NEPHROTOX))
                        endpoint = new RaEndpointNephrotox(DBConn);   
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_SKIN))
                        endpoint = new RaEndpointSkinSensitization(DBConn);   
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_FISH_VERMEER))
                        endpoint = new RaEndpointFishVermeer(DBConn);   
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_DAPHNIA_VERMEER))
                        endpoint = new RaEndpointDaphniaVermeer(DBConn);   
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_CGX_VERMEER))
                        endpoint = new RaEndpointCarcinogenicityCGX(DBConn);   
                    else if (selEP.equalsIgnoreCase(RaEngine.EP_MICRONUCLEUS_VERMEER))
                        endpoint = new RaEndpointMicronucleus(DBConn);   
                    else
                        endpoint = null;
                    
                    RaEngine RA = new RaEngine(DBConn);
                    RA.setSimilarMoleculesSize((Integer) jSpinnerNumMols.getValue());
                    RaResultsContainer res = RA.Calculate(endpoint, mol);

                    new RaGUIApplicationChart(res).setVisible(true);
                } catch (Exception e) {
                    RaMessageLogger.getLogger().SendMessage("An error has occurred - " + e.getMessage());
                }
                
                return null;
            }

        }

        // Close previous frames
        for (Frame f : JFrame.getFrames()) 
            if (f.getClass() != RaGUIApplication.class)  
                f.dispose();
        
        (new SWRunReadAcross()).execute();           
        
    }//GEN-LAST:event_jButtonRunActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Load_Lbl;
    private javax.swing.JTextField Load_Textfield;
    private javax.swing.JButton jButtonRun;
    private javax.swing.JComboBox<String> jComboBoxEndpoint;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanelInsert;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinnerNumMols;
    private javax.swing.JTextArea jTextAreaLog;
    // End of variables declaration//GEN-END:variables
}
