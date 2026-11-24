package insilico.readacross.gui.chart.components;

import insilico.core.exception.InvalidMoleculeException;
import insilico.core.molecule.conversion.SmilesMolecule;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import insilico.core.molecule.tools.Depiction;
import org.openscience.cdk.interfaces.IAtomContainer;
//import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.RingGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;

/**
 *
 * @author User
 */
public class PanelMolViewer extends javax.swing.JPanel {

    private String CurMol; // as smiles
    private IAtomContainer CurMolWithCoord;

    
    /**
     * Creates new form PanelMolViewer
     */
    public PanelMolViewer() {
        CurMol = null;
        initComponents();
    }


    public void SetMolecule(String mol) {
        CurMol = mol;
        
        // Generates structure
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        try {
            sdg.setMolecule(SmilesMolecule.Convert(CurMol).GetStructure());
        } catch (InvalidMoleculeException ex) {
            return;
        }
        CurMolWithCoord = null;
        try {
            sdg.generateCoordinates();
            CurMolWithCoord = sdg.getMolecule();
        } catch (Exception e) { 
            CurMolWithCoord = null;
        }
        
        this.repaint();
    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (CurMol == null) {
            
            try {
                Graphics2D g2D = (Graphics2D) g;
                // the call to 'setup' only needs to be done on the first paint
                Rectangle drawArea = new Rectangle(this.getWidth(), this.getHeight());
                // Builds the Image object
                g2D.setBackground(new Color(240,230,210));
                g2D.clearRect(0, 0, this.getWidth(), this.getHeight());
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }            
            return;
        }
        
        try {
            // Generates structure
//            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
//            sdg.setMolecule(SmilesMolecule.Convert(CurMol).GetCDKMolecule());
//            try {
//                sdg.generateCoordinates();
//            } catch (Exception e) { 
//                throw new Exception("Unable to generate coordinates for molecule");        
//            }
//
//            IMolecule mol = sdg.getMolecule();

            // Create image generators
            List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
            generators.add(new BasicSceneGenerator());
            generators.add(new RingGenerator());
            generators.add(new BasicAtomGenerator());

            // the renderer needs to have a toolkit-specific font manager
            AtomContainerRenderer renderer = new AtomContainerRenderer(generators, new AWTFontManager());

            Graphics2D g2D = (Graphics2D) g;

            // the call to 'setup' only needs to be done on the first paint
            Rectangle drawArea = new Rectangle(this.getWidth(), this.getHeight());
            renderer.setup(CurMolWithCoord, drawArea);

            Rectangle diagramBounds = renderer.calculateDiagramBounds(CurMolWithCoord);
            renderer.setZoomToFit(drawArea.width, drawArea.height, diagramBounds.width, diagramBounds.height);

            // Builds the Image object
            g2D.drawImage(Depiction.DepictMolecule(CurMolWithCoord, this.getWidth(), this.getHeight()), 0, 0 , null);
            g2D.setBackground(Color.WHITE);
//            g2D.clearRect(0, 0, this.getWidth(), this.getHeight());
            g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2D.dispose();

            renderer.paint(CurMolWithCoord, new AWTDrawVisitor(g2D));

        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
        
    }    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMaximumSize(new Dimension(2000, 2000));
        setMinimumSize(new Dimension(100, 100));
        setPreferredSize(new Dimension(100, 100));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
