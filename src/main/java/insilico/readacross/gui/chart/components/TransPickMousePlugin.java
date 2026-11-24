package insilico.readacross.gui.chart.components;
    
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import insilico.readacross.database.iDBConnector;
import insilico.core.exception.DatabaseException;
import insilico.readacross.type.interfaces.iRaObject;
import insilico.readacross.type.interfaces.iRaObjectInterpolation;
import insilico.readacross.type.interfaces.iRaObjectRootMolecule;
import insilico.readacross.type.interfaces.iRaObjectRule;
import insilico.readacross.type.interfaces.iRaObjectSimMolecule;
import insilico.readacross.type.objects.RaObject;
import insilico.readacross.type.objects.RaResultsContainer;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;


/** 
 * TranslatingGraphMousePlugin uses a MouseButtonOne press and
 * drag gesture to translate the graph display in the x and y
 * direction. The default MouseButtonOne modifier can be overridden
 * to cause a different mouse gesture to translate the display.
 * 
 * 
 * @author Tom Nelson
 */
public class TransPickMousePlugin extends AbstractGraphMousePlugin
    implements MouseListener, MouseMotionListener{

    private final RaResultsContainer RaResults;
    private Graph<iRaObject,String> subGraph;
    private final Cursor moveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    private iRaObject vertex;
    private final Point framePosition; //starting position of infos frame;
    private final int numberOfClick; 
    
    private final int frameOffset = 10; //horizontal distance between main fraim and infos frame;
    private final int frameDistance = 40; //vertical distance between each infos frame;
    private final Dimension dim;
    private final Set<iRaObjectSimMolecule> duplicateMol;
    private final boolean showDuplicateSelectedMol;
    private final boolean showDuplicateSelectedRule;
    
    private final Set<Point2D> coords;
    private final Set<iRaObject> lastSelectedVertex;
    private final Set<iRaObjectSimMolecule> duplicateMolTemp;

    
    private final StaticLayout<iRaObject, String> layout;
    private final Map<iRaObject,Graph> savedSubTree;
    private final PanelRuleLegend legendViewer;
    private iRaObject selectedMolecules;
    private iRaObject lastSelectedMolecules;
    private boolean clear = true;
    
    private final iDBConnector DBConn;
    
           
    /**
    * create an instance with passed modifier value
    * @param modifiers the mouse event modifier to activate this function
    * @param dim
     * @param singleClick
     * @param Results
     * @param duplicateMol
     * @param showDuplicateSelectedMol
     * @param showDuplicateSelectedRule
     * @param layout
     * @param legendViewer
    */
    public TransPickMousePlugin(iDBConnector DBConn, int modifiers, Dimension dim, boolean singleClick, RaResultsContainer Results, Set<iRaObjectSimMolecule> duplicateMol, boolean showDuplicateSelectedMol, boolean showDuplicateSelectedRule, StaticLayout<iRaObject, String> layout, PanelRuleLegend legendViewer) {
        super(modifiers);
        this.dim = dim;
        this.RaResults = Results;
        if (singleClick) {
            numberOfClick = 1;
        } else {
            numberOfClick = 2;
        }
        int width = Toolkit.getDefaultToolkit().getScreenSize().width / 3 + dim.width / 2; //compute x posizion of infos frame
        int height = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - dim.height / 2;//compute x posizion of infos frame
        framePosition = new Point(width, height);
        this.duplicateMol = duplicateMol;
        this.showDuplicateSelectedMol = showDuplicateSelectedMol;
        this.showDuplicateSelectedRule = showDuplicateSelectedRule;
        coords = new HashSet<>();
        lastSelectedVertex = new HashSet<>();
        this.layout = layout;
        subGraph = new SparseGraph<>();
        savedSubTree = new HashMap<>();
        duplicateMolTemp = new HashSet<>();
        this.legendViewer = legendViewer;
        this.DBConn = DBConn;
    }

  /**
     * Check the event modifiers. Set the 'down' point for later
     * use. If this event satisfies the modifiers, change the cursor
     * to the system 'move cursor '
   * @param e the event
   */
  @Override
  public void mousePressed(MouseEvent e) {
      VisualizationViewer vv = (VisualizationViewer)e.getSource();
      down = e.getPoint();
      GraphElementAccessor<iRaObject,String> pickSupport = vv.getPickSupport();
      vertex = pickSupport.getVertex(vv.getGraphLayout(), e.getX(), e.getY());
      
  }
    
  /**
   * unset the 'down' point and change the cursor back to the system
     * default cursor
     * @param e
   */
  @Override
    public void mouseReleased(MouseEvent e) {
        VisualizationViewer vv = (VisualizationViewer)e.getSource();
        down = null;
        vv.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        
    }
    
    /**
     * check the modifiers. If accepted, translate the graph according
     * to the dragging of the mouse pointer
     * @param e the event
   */
  @Override
    public void mouseDragged(MouseEvent e) {
        VisualizationViewer vv = (VisualizationViewer)e.getSource();
        boolean accepted = checkModifiers(e);
        if((accepted)&&(vertex == null)) {
            MutableTransformer modelTransformer = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
            vv.setCursor(moveCursor);
            try {
                Point2D q = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(down);
                Point2D p = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());
                float dx = (float) (p.getX()-q.getX());
                float dy = (float) (p.getY()-q.getY());
                
                modelTransformer.translate(dx, dy);
                down.x = e.getX();
                down.y = e.getY();
            } catch(RuntimeException ex) {
                System.err.println("down = "+down+", e = "+e);
                throw ex;
            }
        
            e.consume();
            vv.repaint();
        }
    }

  @Override
    public void mouseClicked(MouseEvent e) {
        if ((e.getClickCount() == 1) && (vertex != null)){
            collapseOrExpand(vertex, (VisualizationViewer)e.getSource());
            
        }
         
        if ((e.getClickCount() == numberOfClick) && (vertex != null)) {
            if (vertex.getObjectType() == RaObject.RA_OBJECT_ROOT_MOLECULE) {
                for (Frame f : JFrame.getFrames()) 
                    if (f.getClass() == framePrediction.class)
                        f.dispose();
                   
                framePrediction f = new framePrediction((iRaObjectRootMolecule) vertex);
                f.setVisible(true);
                framePosition.y += frameDistance;
                if ((framePosition.y + f.getHeight()) > Toolkit.getDefaultToolkit().getScreenSize().height) {
                    framePosition.y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - dim.height / 2;
                    framePosition.x += frameOffset * 3;
                }
                f.setLocation(framePosition);
                
            } else if (vertex.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE) {
//                for (Frame f : JFrame.getFrames()) 
//                    if (f.getClass() == frameMolDetails.class)
//                        if ( ((frameMolDetails)f).getSimMolObj().getMolecule().GetId() == ((iRaObjectSimMolecule) vertex).getMolecule().GetId() ) {
//                            f.setVisible(true);
//                            f.toFront();
//                            return;
//                        }
                   
                frameMolDetails f = new frameMolDetails((iRaObjectSimMolecule) vertex, DBConn);
                f.setVisible(true);
                framePosition.y += frameDistance;
                if ((framePosition.y + f.getHeight()) > Toolkit.getDefaultToolkit().getScreenSize().height) {
                    framePosition.y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - dim.height / 2;
                    framePosition.x += frameOffset * 3;
                }
                f.setLocation(framePosition);
                
            } else if (vertex.getObjectType() == RaObject.RA_OBJECT_RULE) {
                
                //// RULE object

                // If is already created, makes it visible
                for (Frame f : JFrame.getFrames()) 
                    if (f.getClass() == frameRuleDetails.class)
                        if ( ((frameRuleDetails)f).getRuleObj().getAlert().getId().equals(((iRaObjectRule) vertex).getAlert().getId()) ) {
                            f.setVisible(true);
                            f.toFront();
                            return;
                        }
                   
                // Else creates a new frame
                frameRuleDetails f = new frameRuleDetails((iRaObjectRule) vertex, RaResults);
                f.setVisible(true);
                framePosition.y += frameDistance;
                if ((framePosition.y + f.getHeight()) > Toolkit.getDefaultToolkit().getScreenSize().height) {
                    framePosition.y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - dim.height / 2;
                    framePosition.x += frameOffset * 3;
                }
                f.setLocation(framePosition);
                
            } else if (vertex.getObjectType() == RaObject.RA_OBJECT_INTERPOLATION) {

                //// INTERPOLATION CHART object
                
                // If is already created, makes it visible
//                for (Frame f : JFrame.getFrames()) 
//                    if (f.getClass() == frameMolDetails.class)
//                        if ( ((frameMolDetails)f).getSimMolObj().getMolecule().GetId() == ((iRaObjectSimMolecule) vertex).getMolecule().GetId() ) {
//                            f.setVisible(true);
//                            f.toFront();
//                            return;
//                        }
                   
                frameInterpolation f = new frameInterpolation((iRaObjectInterpolation) vertex);
                f.setVisible(true);
                framePosition.y += frameDistance;
                if ((framePosition.y + f.getHeight()) > Toolkit.getDefaultToolkit().getScreenSize().height) {
                    framePosition.y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - dim.height / 2;
                    framePosition.x += frameOffset * 3;
                }
                f.setLocation(framePosition);
                
            } 
            
            
        }     

    
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
        VisualizationViewer vv = (VisualizationViewer)e.getSource();
        PickedState<iRaObject> pickedVertexState = vv.getPickedVertexState();
        down = e.getPoint();
        GraphElementAccessor<iRaObject,String> pickSupport = vv.getPickSupport();
        vertex = pickSupport.getVertex(vv.getGraphLayout(), e.getX(), e.getY());
        
        if ((vertex==null)&&(clear)){
            for (iRaObject i : lastSelectedVertex)
                pickedVertexState.pick(i, false);
            updateCoordinates(vv, pickedVertexState.getPicked());
            if(lastSelectedVertex.size() == 1)
                if (((iRaObject)lastSelectedVertex.toArray()[0]).getObjectType() == RaObject.RA_OBJECT_RULE)
                    legendViewer.deleteLastSelected(vv);
            legendViewer.updateCoordinates(vv, pickedVertexState.getPicked());
            lastSelectedVertex.clear();
            clear = false;
        }
        
        if(vertex!=null)
            if (!vertex.equals(lastSelectedMolecules)){
                for (iRaObject i : lastSelectedVertex)
                    pickedVertexState.pick(i, false);
                updateCoordinates(vv, pickedVertexState.getPicked());
                if(lastSelectedVertex.size() == 1)
                    if (((iRaObject)lastSelectedVertex.toArray()[0]).getObjectType() == RaObject.RA_OBJECT_RULE)
                        legendViewer.deleteLastSelected(vv);
                legendViewer.updateCoordinates(vv, pickedVertexState.getPicked());
                lastSelectedVertex.clear();
                clear = false;
            }
          
        if ((vertex!=null)&&(!clear))
        {
            clear = true;
            if (vertex.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE){
                if (showDuplicateSelectedMol)
                    for(iRaObjectSimMolecule sim: duplicateMol){
                        if (sim.equals(vertex)) 
                            continue;
                        try {
//                            if (sim.getMolecule().GetCAS().equals((((iRaObjectSimMolecule)vertex).getMolecule().GetCAS()))){
                            if (sim.getMolecule().Id == ((((iRaObjectSimMolecule)vertex).getMolecule().Id ))){
                                pickedVertexState.pick(sim, true);
                                lastSelectedVertex.add(sim);
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(TransPickMousePlugin.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                selectedMolecules = vertex;
                pickedVertexState.pick(vertex, true);
                lastSelectedVertex.add(vertex);
                updateCoordinates(vv, pickedVertexState.getPicked());
            }
            if (vertex.getObjectType() == RaObject.RA_OBJECT_RULE){
                if(showDuplicateSelectedRule){
                    ;
                    }
                pickedVertexState.pick(vertex, true);
                lastSelectedVertex.add(vertex);
                legendViewer.updateCoordinates(vv, pickedVertexState.getPicked());
                legendViewer.setSelectedEntry((iRaObjectRule)vertex);
            }
        }
        lastSelectedMolecules = vertex;
    }

    private void saveSubGraph(Graph<iRaObject, String> graph, iRaObject vertex) {
        subGraph.addVertex(vertex);
        if(graph.getSuccessorCount(vertex)>0){
            Collection<String> edges = graph.getOutEdges(vertex);
            for (String e : edges){
                //if (!e.contains("Duplicate")){
                    subGraph.addEdge(e, graph.getEndpoints(e), graph.getEdgeType(e));
                    saveSubGraph(graph, graph.getEndpoints(e).getSecond());
                //}
                //else
//                    savedDuplicateEdge.addEdge(e,graph.getEndpoints(e),graph.getEdgeType(e));
            }
        }
    }

    public void collapseOrExpand(iRaObject vertex, VisualizationViewer vv){
        PickedState<iRaObject> pickedVertexState = vv.getPickedVertexState();
        if(vertex.getObjectType() == RaObject.RA_OBJECT_CONNECTOR){
            if(!savedSubTree.containsKey(vertex)){
                Iterator<iRaObject> itr = vertex.getChildren();
                while(itr.hasNext())
                    pickedVertexState.pick(itr.next(),false);
                legendViewer.updateCoordinates(vv, pickedVertexState.getPicked());
                saveSubGraph(layout.getGraph(),vertex);
                savedSubTree.put(vertex,subGraph);
                subGraph = new SparseGraph<>();
                removeSubGraph(layout.getGraph(),vertex);
            }
            else{
                addSubGraph(layout.getGraph(),savedSubTree.get(vertex));
                
                savedSubTree.remove(vertex);
            }
            vv.repaint();
        }
    }
    
    private void removeSubGraph(Graph<iRaObject, String> graph, iRaObject vertex) {
        if(duplicateMol.contains(vertex)){
            duplicateMol.remove(vertex);  
            duplicateMolTemp.add((iRaObjectSimMolecule)vertex);
        }
        if(graph.getSuccessorCount(vertex)>0){
            Collection<String> edges = graph.getOutEdges(vertex);
            for (String e : edges){
  //              if (!e.contains("Duplicate")){
                    removeSubGraph(graph, graph.getEndpoints(e).getSecond());
                    graph.removeVertex(graph.getEndpoints(e).getSecond());
    //            }
            }
        }
    }

    private void addSubGraph(Graph<iRaObject, String> graph, Graph<iRaObject, String> sub) {
        //iRaObject first,second;
        Collection<String> edges = sub.getEdges();
        for (String e : edges){
            graph.addEdge(e, sub.getEndpoints(e),sub.getEdgeType(e));
            if(duplicateMolTemp.contains(sub.getEndpoints(e).getFirst()))
                duplicateMol.add((iRaObjectSimMolecule)sub.getEndpoints(e).getFirst());
            if(duplicateMolTemp.contains(sub.getEndpoints(e).getSecond()))
                duplicateMol.add((iRaObjectSimMolecule)sub.getEndpoints(e).getSecond());
        }
        /*edges = savedDuplicateEdge.getEdges();
        for (String e : edges){
            first = savedDuplicateEdge.getEndpoints(e).getFirst();
            second = savedDuplicateEdge.getEndpoints(e).getSecond();
            if(graph.containsVertex(first)&&graph.containsVertex(second)){
                graph.addEdge(e, savedDuplicateEdge.getEndpoints(e),savedDuplicateEdge.getEdgeType(e));
            }
            if (graph.containsVertex(first))
                duplicateMol.add((iRaObjectSimMolecule)first);
            if (graph.containsVertex(second))
                duplicateMol.add((iRaObjectSimMolecule)second);
        }*/
    }    
    
    public void updateCoordinates(VisualizationViewer vv, Set<iRaObject> sims){
        coords.clear();
        for (iRaObject sim :sims){
            if(sim.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE){
                Point2D p1 = new Point();
                p1.setLocation(layout.getX(sim),layout.getY(sim));
                coords.add(vv.getRenderContext().getMultiLayerTransformer().transform(p1));
            }
        }
    }
    /**
     * 
     * @return 
     */
    public iRaObject getSelectedMolecules(){
        return selectedMolecules;
    }
    /**
     * 
     * @return 
     */
    public Set<Point2D> getCoords() {
        return coords;
    }
}