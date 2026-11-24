    package insilico.readacross.gui.chart.components;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ViewScalingControl;
import insilico.readacross.type.interfaces.iRaObject;
import insilico.readacross.type.objects.RaObject;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author Frank
 */
public class PopupMousePlugin extends AbstractPopupGraphMousePlugin {

    private final JPopupMenu pMenuGeneric = new JPopupMenu();
    //private HashMap<String, String> legend;
    private VisualizationViewer<iRaObject,String> vv;
    private Point p;
    private iRaObject vertex;
    private Transformer<iRaObject,String> oldLabelTransf;
    private ScalingControl scaler;
  //  private PanelRuleLegend LegendViewer;
//    private PanelColorLegend ColorLegendViewer;
//    private String frameTitle; //title of infos frame;
//    private Dimension legendDimension; //dimension of legend frame;
//    private Point legendPosition; //position of legend frame;
//    private VisViewPaintable vvPaint;
    
//    private final boolean showLegendInFrame;
    
    /**
    *
    */
    public PopupMousePlugin(){
        
        
//        this.showLegendInFrame = showLegendInFrame;
  //      this.vvPaint = vvPaint;
        scaler = new ViewScalingControl();
//        ColorLegendViewer = new PanelColorLegend();

        //LegendViewer.setLegend(legend);
//        ColorLegendViewer.setLabel(topLabel, bottomLabel);
//        vvPaint.setLegend(legend);
        
//        legendDimension = new Dimension(500,400);
//        int width = Toolkit.getDefaultToolkit().getScreenSize().width/3-dim.width/2; //compute x posizion of infos frame
//        int height = Toolkit.getDefaultToolkit().getScreenSize().height/2-dim.height/2;//
//        if (width - legendOffset < 0)
//            width = 0;
//        else
//            width -= legendOffset;
//        legendPosition = new Point(width,height);
        
        JMenuItem item;
        pMenuGeneric.add(item = new JMenuItem("Save"));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vv.setDoubleBuffered(false);
                BufferedImage im = new BufferedImage(vv.getWidth(), vv.getHeight(), BufferedImage.TYPE_INT_ARGB);
                vv.paint(im.getGraphics());

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
                fileChooser.setFileFilter(filter);
                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    String fn = file.getAbsolutePath();
                    if (fn.split("[.]").length<2){
                        fn = fn.concat(".png");
                        file = new File(fn);
                    }                    
                    try {
                        ImageIO.write(im, "PNG", file);
                    } catch (IOException ex) {
                        // on error?
                    }
                }        
                vv.setDoubleBuffered(true);
            }
        });
        pMenuGeneric.add(item = new JMenuItem("Copy to clipboard"));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vv.setDoubleBuffered(false);
                BufferedImage im = new BufferedImage(vv.getWidth(), vv.getHeight(), BufferedImage.TYPE_INT_ARGB);
                vv.paint(im.getGraphics());
                ImageSelection ims = new ImageSelection(im);
                try{
                    Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                    c.setContents(ims, null);
                    JOptionPane.showMessageDialog(vv, "Copied to clipboard!","Attention", JOptionPane.INFORMATION_MESSAGE);
                }catch(HeadlessException ex){
                    JOptionPane.showMessageDialog(vv, "Error in coping to clipboard!"+ex,"Attention", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        pMenuGeneric.add(item = new JCheckBoxMenuItem("Show label",true));
        item.addActionListener(new ActionListener() {
            @SuppressWarnings("empty-statement")
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
                if (item.isSelected())
                    vv.getRenderContext().setVertexLabelTransformer(oldLabelTransf);
                else{
                    oldLabelTransf = vv.getRenderContext().getVertexLabelTransformer();
                    vv.getRenderContext().setVertexLabelTransformer(new Transformer<iRaObject, String>() {
                        @Override
                        public String transform(iRaObject i) {
                            if (i.getObjectType() != RaObject.RA_OBJECT_RULE)
                                return "";
                            else 
                                return oldLabelTransf.transform(i);
                        }
                    });
                }
                vv.repaint();
            }
        });
        pMenuGeneric.add(item = new JMenuItem("Zoom in"));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.2f, p);
            }
        });
        pMenuGeneric.add(item = new JMenuItem("Zoom out"));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 0.8f, p);
            }
        });
//        pMenuGeneric.add(item = new JMenuItem("Background"));
//        item.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                
//            }
//        });
        /*if (!showLegendInFrame){
            pMenuGeneric.add(item = new JCheckBoxMenuItem("Show legend"));
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
                            vvPaint.showLegend(item.isSelected());
                            
                }
            });
        }
        else{
            pMenuGeneric.add(item = new JMenuItem("Show legend"));
             item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    frameTitle = "Legend";
                    //Check whether info frame already exists. if so brings it up, otherwise create new
                    JFrame frameLegend=null;
                    boolean frameExist = false;
                    Frame[] frameList = JFrame.getFrames();
                    for(Frame f:frameList)
                           if (f.getTitle().equals(frameTitle)){
                              frameExist = true;
                              frameLegend = (JFrame)f;
                              break;
                           }
                    if(frameExist){
                        frameLegend.setVisible(true);
                        frameLegend.toFront();
                    }
                    else{
                        frameLegend = new JFrame(frameTitle);
                        frameLegend.setLocation(legendPosition);
                        frameLegend.setPreferredSize(legendDimension);
                        frameLegend.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                        frameLegend.setResizable(true);
                        frameLegend.setLayout(new BorderLayout());
                        frameLegend.getContentPane().add(LegendViewer,BorderLayout.CENTER);
                        frameLegend.getContentPane().add(ColorLegendViewer,BorderLayout.EAST);
                        frameLegend.pack();
                        frameLegend.setVisible(true); 
                    }
                }
                 
             });
        }*/
               
            
    }
        
    @Override
    protected void handlePopup(MouseEvent e) {
        vv = (VisualizationViewer<iRaObject,String>)e.getSource();
        p = e.getPoint();
        
        GraphElementAccessor<iRaObject,String> pickSupport = vv.getPickSupport();
        vertex = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
        if (vertex == null)
            pMenuGeneric.show(vv,(int)p.getX(),(int)p.getY());
    }
    
    // This class is used to hold an image while on the clipboard.
    static class ImageSelection implements Transferable
    {
        private final Image image;

        public ImageSelection(Image image)
        {
            this.image = image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!DataFlavor.imageFlavor.equals(flavor))
            {
                throw new UnsupportedFlavorException(flavor);
            }
            return image;
        }
       
    }
}
