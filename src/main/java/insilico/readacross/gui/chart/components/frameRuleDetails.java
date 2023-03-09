package insilico.readacross.gui.chart.components;

import com.lowagie.text.Image;
import insilico.core.alerts.Alert;
import insilico.core.constant.InsilicoConstants;
import insilico.core.molecule.InsilicoMolecule;
import insilico.core.molecule.conversion.SmilesMolecule;
import insilico.core.molecule.tools.Depiction;
import insilico.readacross.database.objects.dbStructure;
import insilico.readacross.database.objects.dbValue;
import insilico.core.exception.PropertyNotFoundException;
import insilico.readacross.calculation.RaDataset;
import insilico.readacross.type.interfaces.iRaObjectRule;
import insilico.readacross.type.objects.RaResultsContainer;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author User
 */
public class frameRuleDetails extends javax.swing.JFrame {
    
    private static final int MAX_SIM_MOLECULES = 100;
    
    private final iRaObjectRule RuleObj;
    private final RaResultsContainer RaResults;
    private final PanelImageViewer ImgViewer;
    private boolean SimilarMoleculesDisplayed;
    

    /**
     * Creates new form frameMolDetails
     * @param Rule
     * @param Results
     */
    public frameRuleDetails(iRaObjectRule Rule, RaResultsContainer Results) {
        initComponents();
        
        DecimalFormatSymbols InternationalSymbols =
            new DecimalFormatSymbols();
        InternationalSymbols.setDecimalSeparator('.');         
        java.text.DecimalFormat df3 = new java.text.DecimalFormat("0.###", InternationalSymbols);
        java.text.DecimalFormat df5 = new java.text.DecimalFormat("0.#####", InternationalSymbols);
        
        this.RuleObj = Rule;
        this.RaResults = Results;

        // Set the rule viewer
        ImgViewer = new PanelImageViewer();
        jPanelRuleViewer.setLayout(new GridLayout(1, 1));
        jPanelRuleViewer.add(ImgViewer);
        String ImgURL = RuleObj.getAlert().getImageURL();
        if (ImgURL != null) {
            ImgViewer.SetImage(ImgURL);
        } else {
            // try depict
            try {
                String SMARTS = RuleObj.getAlert().getSMARTS();
                if (SMARTS == null) throw new Exception();
                if (SMARTS.isEmpty()) throw new Exception();

                InsilicoMolecule mol = SmilesMolecule.Convert(SMARTS);
                BufferedImage RuleImage = Depiction.DepictMolecule(mol.GetStructure(), 200, 200);
                ImgViewer.SetImage(RuleImage);
            } catch (Exception e) {
                // do nothing
            }


        }
        
        // Set the additional info
        jTextAreaInfo.setText("");
        Alert alert = RuleObj.getAlert();
        jTextAreaInfo.append("Name: " + alert.getName() + "\n");
        jTextAreaInfo.append("Description: " + alert.getDescription() + "\n");
        try {
            double curAccuracy = alert.getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_ACCURACY);
            jTextAreaInfo.append("Experimental accuracy: " + df3.format(curAccuracy) + "\n");
        } catch (PropertyNotFoundException ex) { }
        try {
            double curAccuracy = alert.getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_ACCURACY_BIOCIDES);
            jTextAreaInfo.append("Experimental accuracy (only on biocide compounds): " + df3.format(curAccuracy) + "\n");
        } catch (PropertyNotFoundException ex) { }
        try {
            double curFisher = alert.getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_FISHER);
            String formattedFisher = df5.format(curFisher);
            if (curFisher < 0.00001)
                formattedFisher = "< 10e-6";
            jTextAreaInfo.append("Fisher test p-value: " + formattedFisher + "\n");
        } catch (PropertyNotFoundException ex) { }        
        
        // Set the title
        String title = "Details of rule " + alert.getName();
        jLabelInfo.setText(title);
        this.setTitle(title);
        
        SimilarMoleculesDisplayed = false;
        jLabelSimilar.setText("List of molecules where the rule applies (max " + MAX_SIM_MOLECULES +")");
        jScrollPaneSimilar.setVisible(false);
    }

    
    private void LoadSimilarMolecules() {
        
        class SWLoadMol extends SwingWorker<Object, Object> {
            
            public java.awt.Frame parent;
            
            @Override
            protected Object doInBackground() {        
                
                parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                try {
                    
                    // Extract molecules
                    RaDataset DS = RaResults.getDataset();
                    ArrayList<RaDataset.RaSimilarMolecule> Mols = DS.GetSimilarMolecules(MAX_SIM_MOLECULES, RuleObj.getAlert());

                    // Visualize them
                    jPanelSimilarMols.setLayout(new GridLayout(Mols.size(), 1));
                    for (RaDataset.RaSimilarMolecule m : Mols) {

                        ArrayList<dbValue> curExpValues = RaResults.getDBConn().
                                GetAllValuesForMoleculeAndEndpoint(m.getMolecule().Id, RaResults.getEndpoint().getEndpointId());
                        dbValue val = curExpValues.get(0);
                        
                        dbStructure st = RaResults.getDBConn().GetStructure(m.getMolecule().Id);
                        
                        jPanelSimilarMols.add(new PanelSimilarMolecule(st.SMILES, m.getMolecule().CAS, m.getSimilarity(), 
                                RaResults.getEndpoint().FormatValue(val.Value) ));
                    }
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent,"An error has occurred!");
                }
                
                parent.setCursor(Cursor.getDefaultCursor());
                return null;
            }
        }
        
        SWLoadMol sw = new SWLoadMol();
        sw.parent = this;
        sw.execute();        
 
    }
    
    
    private void SaveMoleculesToFile() {
        
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save molecules to file");
        int res = fc.showSaveDialog(this);
        if ((res == JFileChooser.CANCEL_OPTION) || (res == JFileChooser.ERROR_OPTION))
            return;
        final File saveFile = fc.getSelectedFile();
        
        class SWSaveFile extends SwingWorker<Object, Object> {
            
            public java.awt.Frame parent;
            
            @Override
            protected Object doInBackground() {        
                
                parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                try {
                    FileWriter outFile = new FileWriter(saveFile);
                    PrintWriter out = new PrintWriter(outFile);
                    out.println("No." + "\t" + "CAS" + "\t" + "SMILES" + "\t" + "Similarity" + 
                            "\t" + "Experimental value");

                    // Extract and print molecules
                    RaDataset DS = RaResults.getDataset();
                    ArrayList<RaDataset.RaSimilarMolecule> Mols = DS.GetSimilarMolecules(MAX_SIM_MOLECULES, RuleObj.getAlert());
                    int idx = 0;
                    for (RaDataset.RaSimilarMolecule m : Mols) {
                        idx++;
                        try {
                            ArrayList<dbValue> curExpValues = RaResults.getDBConn().
                                    GetAllValuesForMoleculeAndEndpoint(m.getMolecule().Id, RaResults.getEndpoint().getEndpointId());
                            dbValue val = curExpValues.get(0);
                            dbStructure st = RaResults.getDBConn().GetStructure(m.getMolecule().Id);
                            out.println(idx + "\t" + m.getMolecule().CAS + "\t" +
                                    st.SMILES + "\t" + m.getSimilarity() + 
                                    "\t" + RaResults.getEndpoint().FormatValue(val.Value));
                        } catch (Exception ex) {
                            out.println(idx + "\tERRROR");
                        }
                    }

                    out.close();
                    JOptionPane.showMessageDialog(parent,"File saved.");
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent,"An error has occurred!");
                }
                
                parent.setCursor(Cursor.getDefaultCursor());
                return null;
            }
        }
        
        SWSaveFile sw = new SWSaveFile();
        sw.parent = this;
        sw.execute();
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanelRuleViewer = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabelInfo = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaInfo = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabelSimilar = new javax.swing.JLabel();
        jButtonSimilar = new javax.swing.JButton();
        jScrollPaneSimilar = new javax.swing.JScrollPane();
        jPanelSimilarMols = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanelRuleViewer.setMinimumSize(new java.awt.Dimension(100, 100));

        javax.swing.GroupLayout jPanelRuleViewerLayout = new javax.swing.GroupLayout(jPanelRuleViewer);
        jPanelRuleViewer.setLayout(jPanelRuleViewerLayout);
        jPanelRuleViewerLayout.setHorizontalGroup(
            jPanelRuleViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 383, Short.MAX_VALUE)
        );
        jPanelRuleViewerLayout.setVerticalGroup(
            jPanelRuleViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jSplitPane1.setTopComponent(jPanelRuleViewer);

        jSplitPane2.setDividerLocation(180);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel1.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel1.setPreferredSize(new java.awt.Dimension(385, 260));

        jLabelInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelInfo.setText("jLabel1");

        jTextAreaInfo.setEditable(false);
        jTextAreaInfo.setColumns(20);
        jTextAreaInfo.setRows(5);
        jScrollPane2.setViewportView(jTextAreaInfo);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                    .addComponent(jLabelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane2.setTopComponent(jPanel1);

        jPanel2.setMinimumSize(new java.awt.Dimension(150, 150));

        jLabelSimilar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelSimilar.setText("jLabelSimilar");

        jButtonSimilar.setText("Load molecules");
        jButtonSimilar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSimilarActionPerformed(evt);
            }
        });

        jScrollPaneSimilar.setMinimumSize(new java.awt.Dimension(100, 100));

        jPanelSimilarMols.setMinimumSize(new java.awt.Dimension(100, 100));

        javax.swing.GroupLayout jPanelSimilarMolsLayout = new javax.swing.GroupLayout(jPanelSimilarMols);
        jPanelSimilarMols.setLayout(jPanelSimilarMolsLayout);
        jPanelSimilarMolsLayout.setHorizontalGroup(
            jPanelSimilarMolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 363, Short.MAX_VALUE)
        );
        jPanelSimilarMolsLayout.setVerticalGroup(
            jPanelSimilarMolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 184, Short.MAX_VALUE)
        );

        jScrollPaneSimilar.setViewportView(jPanelSimilarMols);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelSimilar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButtonSimilar)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPaneSimilar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelSimilar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSimilar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneSimilar, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane2.setRightComponent(jPanel2);

        jSplitPane1.setRightComponent(jSplitPane2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSimilarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSimilarActionPerformed
        if (!SimilarMoleculesDisplayed) {
            // Load molecules into the scroll panel
            jScrollPaneSimilar.setVisible(true);
            LoadSimilarMolecules();
            jButtonSimilar.setText("Export molecules");
            SimilarMoleculesDisplayed = true;
        } else {
            // Export to file
            SaveMoleculesToFile();
        }
    }//GEN-LAST:event_jButtonSimilarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonSimilar;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JLabel jLabelSimilar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelRuleViewer;
    private javax.swing.JPanel jPanelSimilarMols;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneSimilar;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTextArea jTextAreaInfo;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the SimMolObj
     */
    public iRaObjectRule getRuleObj() {
        return RuleObj;
    }
}
