package insilico.readacross.gui.chart;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ViewScalingControl;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import insilico.core.constant.InsilicoConstants;
import insilico.readacross.database.objects.dbStructure;
import insilico.core.exception.PropertyNotFoundException;
import insilico.readacross.calculation.endpoints.utils.RaColorGradient;
import insilico.readacross.calculation.endpoints.RaEndpoint;
import insilico.readacross.gui.chart.components.PanelColorLegend;
import insilico.readacross.gui.chart.components.PanelMolViewer;
import insilico.readacross.gui.chart.components.PanelRuleLegend;
import insilico.readacross.gui.chart.components.PopupMousePlugin;
import insilico.readacross.gui.chart.components.TargetInfosPanel;
import insilico.readacross.gui.chart.components.TransPickMousePlugin;
import insilico.readacross.gui.chart.components.VisViewPaintable;
import insilico.readacross.gui.chart.components.generalShape;
import insilico.readacross.type.interfaces.iRaObject;
import insilico.readacross.type.interfaces.iRaObjectRootMolecule;
import insilico.readacross.type.interfaces.iRaObjectRule;
import insilico.readacross.type.interfaces.iRaObjectSimMolecule;
import insilico.readacross.type.objects.RaAdditionalValue;
import insilico.readacross.type.objects.RaObject;
import insilico.readacross.type.objects.RaResultsContainer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.collections15.Transformer;

/**
 * Class for building and showing the RA chart with JUNG libraries
 *
 * @author Alberto Manganaro
 */
public class RaChart implements Serializable {

    public final static int HORIZONTAL = 0;
    public final static int VERTICAL = 1;
    public final static int DIAGONAL_LEFT = 2;
    public final static int DIAGONAL_RIGHT = 3;

    private final boolean SHOW_SIMILAR_MOLECULES = true;
    private final boolean HIGHLIGHT_IDENTICAL_MOLECULES_DASHED = true;
    private final boolean SET_DASHED_LINK_DUPLICATE = true;
    private final boolean SET_QUAD_CURVE_DUPLICATE = true;
    private final boolean SET_QUAD_CURVE_MOLECULES = true;
    private final boolean SET_QUAD_CURVE_SIMILAR_MOLECULES = true;
    private final boolean SET_FIXED_EDGE_SIMILAR_MOLECULES = true;
    private final boolean SET_SINGLE_CLICK_FOR_INFOS_PANEL = false;
    private final boolean HIGHLIGHT_SELECTED_VERTEX_DIMENSION = false;
    private final boolean HIGHLIGHT_SELECTED_VERTEX_COLOR = false;
    private final boolean HIGHLIGHT_SELECTED_VERTEX_CIRLCE = true;
    private final boolean HIGHLIGHT_SELECTED_RULE_TRIANGLE = true;
    private final boolean SET_GRADIENT_BACKGROUND = true;
    private final boolean SHOW_DUPLICATE_SELECTED_MOLECULE = true;
    private final boolean SHOW_DUPLICATE_SELECTED_RULE = true;
    private final boolean LINK_DUPLICATE_SELECTED_MOLECULE = false;
    private final boolean SHOW_LEVEL_IN_RULE_NAME = true;
    
    private final double edgeLength = 70; //lenght of edges;
    private final double angularDisplacment = 5; //angular displacement in degree for central molecules to avoid overlapping
    private final double edgeScalingFactor = 1.2f; //Scaling factor of edges when graph is full
    private final double ruleScalignFactor = 2.5f;
    private final double connectorScalignFactor = 1.8f;
    
    private final int ruleRadius = 20; //radius of rule nodes
    private final int maxLabelColumnWidth = 50; //width of the first column in legend frame

    private final int direction = HORIZONTAL; //HORIZONTAL, VERTICAL, DIAGONAL_LEFT, DIAGONAL_RIGHT
    private final int maxNumberOfRules = 12; //If number of rules is greater than this graph is full
    
    private final float maxMoleculesAngle = (float) (Math.PI/2.75f);
    private final float rootMoleculeRadius = 18.0f; //radius of root
    private final float maxSimMolRadius = rootMoleculeRadius; //maximum radius of leaves;
    private final float minSimMolRadius = 5.0f; //minimum radius of leaves;
    private final float zoomFactor = 1.1f; //zoom in factor; higher value means faster zoom
    private final float picked_mul = 1.2f; //zoom factor of selected vertex
    private final float edgeStroke = 1.5f; //stroke of edges
    private final float vertexStroke = 1.25f; //stroke of vertex contour
    private final float rootStroke = 2.25f; //stroke of vertex contour for root molecule
    private final float topColorAmount = 0.8f; //percentage of top color
    
    private final Color backgroundColor = Color.WHITE; //graph's background color.
    private final Color backgroundGradientTop = new Color(0xCA, 0xE0, 0xF6); //Color.GRAY; //Top color when gradient backgroun
    private final Color backgroundGradientBottom = new Color(0xF0, 0xF0, 0xF9); //Color.WHITE;//Bottom color when gradient backgroun
    private final Color gradientBarTextColor = new Color(25,25,25,255);
    private final Color edgeColor = Color.GRAY; //color of edges
    private final Color vertexLineColor = new Color(0x33, 0x33, 0x33); //color of vertex contourr
    private final Color moleculesEdgesColor = new Color(0x00, 0xC0, 0xFF); //color of edges connection molecules
    private final Color ruleEdgesColor = new Color(0xC2, 0x9F, 0xFF); //color of edges connecting rules
    private final Color selectedVertexLineColor = Color.BLUE;//Color of vertices' contours when selected
    private final Color rootMoleculeColor = new Color(0x83, 0xE2, 0xF7); //fill color of root
    private final Color highlightMoleculeColor = new Color(0xFF, 0xFF, 0x33, 0xCA); //fill color of root
    //private final Color highlightRuleColor = new Color(0xFF, 0xFF, 0x33, 0xCA); //fill color of root
    
    private static ImageIcon plusIco;
    private static ImageIcon minusIco;
    private static ImageIcon interpolationIco;
    private static ImageIcon zoomPlusIco;
    private static ImageIcon zoomMinusIco;
    private static ImageIcon zoomResetIco;

    private final RaResultsContainer RaResults;
    private final Graph<iRaObject, String> curGraph;
    private int EdgeCount;
    private VisualizationViewer<iRaObject, String> VisServ;
    private final HashMap<iRaObjectRule, String> legend;
    private int ruleNodeNumber;
    private final List<iRaObject> centralMolecule;
    private final Set<iRaObjectSimMolecule> duplicateMol;
    private VisViewPaintable vvPaint;
    private final generalShape myShape;
    private JPanel graphFinal;
    private JSplitPane mainSplit;
    private JPanel rightPanel;
    private final PanelMolViewer MolViewer;
    private final PanelRuleLegend LegendViewer;
    private final TargetInfosPanel targetInfosPanel;
    private final PanelColorLegend ColorLegendViewer;
    private ScalingControl scaler;
    private TransPickMousePlugin tpmp;
    private final Map<iRaObjectSimMolecule, Set<iRaObjectSimMolecule>> duplicateMap;
    
    

    /**
     * Constructor. The root node of the RA results is passed as parameter
     * @param Results
     */
    public RaChart(RaResultsContainer Results){
        this.RaResults = Results;
        curGraph = new SparseGraph<>();
        EdgeCount = 0; 
        ruleNodeNumber = 0;
        legend = new HashMap<>();
        centralMolecule = new ArrayList<>();
        duplicateMol = new HashSet<>();
        myShape = new generalShape(rootMoleculeRadius,ruleRadius,maxSimMolRadius,minSimMolRadius);
        LegendViewer = new PanelRuleLegend(maxLabelColumnWidth, SET_SINGLE_CLICK_FOR_INFOS_PANEL);
        ColorLegendViewer = new PanelColorLegend();
        MolViewer = new PanelMolViewer();
        duplicateMap = new HashMap<>();
        
        // Info for molviewer
        targetInfosPanel = new TargetInfosPanel();
        String TargetInfo = "";
        for (RaAdditionalValue value : ((iRaObjectRootMolecule)Results.getRootNode()).getAdditionalValues()) {
            if (!TargetInfo.isEmpty()) TargetInfo += "\n";
            TargetInfo += value.getName() + ": " + value.getFormattedValue();
        }
        targetInfosPanel.setText(TargetInfo);
        
        // Init icons
        plusIco = new ImageIcon(getClass().getResource("/images/plus2.png"));
        minusIco = new ImageIcon(getClass().getResource("/images/minus2.png"));
        interpolationIco = new ImageIcon(getClass().getResource("/images/graph.png"));
        zoomPlusIco = new ImageIcon(getClass().getResource("/images/plus4.png"));
        zoomMinusIco = new ImageIcon(getClass().getResource("/images/minus4.png"));
        zoomResetIco = new ImageIcon(getClass().getResource("/images/reset5.png"));
        
        
    }

    
    /**
     * Compute the number of rules in the first level.
     *
//     * @param RootNode Node corresponding to the target compound.
     */
    private void ComputeRuleNumber(iRaObject curNode) {
        Iterator<iRaObject> Itr = curNode.getChildren();
        while (Itr.hasNext()) {
            iRaObject childNode = Itr.next();
            if (childNode.getObjectType() == RaObject.RA_OBJECT_RULE) {
                ruleNodeNumber++;
            }
            if ((childNode.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE)
                    || (childNode.getObjectType() == RaObject.RA_OBJECT_INTERPOLATION)) {
                centralMolecule.add(childNode);
            }
        }
    }

    
    /**
     * Create the legend for the rules. Recursive method.
     *
     * @param curNode Current node to be analyzed
     * @param level Current depth of the node
     * @param ruleIndex Current index rule
     * @return Updated index rule
     */
    private int ComputeLegend(iRaObject curNode, int level, int ruleIndex) {
        if (curNode.getChildrenSize() == 0) {
            return ruleIndex;
        }
        Iterator<iRaObject> Itr = curNode.getChildren();
        while (Itr.hasNext()) {
            iRaObject childNode = Itr.next();
            if ((childNode.getObjectType() == RaObject.RA_OBJECT_RULE) || (childNode.getObjectType() == RaObject.RA_OBJECT_CONNECTOR)) {
                if ((childNode.getObjectType() == RaObject.RA_OBJECT_RULE)) {
                    if (!legend.containsKey((iRaObjectRule) childNode)){
                        String name;
                        if (SHOW_LEVEL_IN_RULE_NAME) {
                            name = "R" + Integer.toString(++ruleIndex) + "." + Integer.toString(level);
                        } else {
                            name = "R" + Integer.toString(++ruleIndex);
                        }
                        legend.put((iRaObjectRule)childNode, name);
                    }
                }
                ruleIndex = ComputeLegend(childNode, level + 1, ruleIndex);
            }
        }
        return ruleIndex;
    }

    
    /**
     * Private method for recursively building of the graph starting from the RA
     * results
     *
     * @param curNode Current node to be explored
     * @param layout Layout of the graph.
     * @param level Actual depth of the node
     * @param angle Angular value of the last edge
     */
    private void RecursiveAddNodes(iRaObject curNode, StaticLayout layout, int level, double angle) {
        if (level == 0) {
            curGraph.addVertex(curNode);
            layout.setLocation(curNode, 0, 0);
        }
        if (curNode.getChildrenSize() == 0) {
            return;
        }
        int index = 0;
        double radius;
        int hasConnector = 0;
        Iterator<iRaObject> Itr = curNode.getChildren();
        
        while (Itr.hasNext())
            if (Itr.next().getObjectType() == RaObject.RA_OBJECT_CONNECTOR)
                hasConnector = 1;
        Itr = curNode.getChildren();
        
        while (Itr.hasNext()) {
            iRaObject childNode = Itr.next();
            double actualAngle;//angle 
            if ((level == 0) && (childNode.getObjectType() != RaObject.RA_OBJECT_SIM_MOLECULE)) { //for rule!
                actualAngle = angle + Math.PI * 2 / ruleNodeNumber * (index); //if root node all children all around it
                if (ruleNodeNumber > maxNumberOfRules) {
                    if (index % 2 == 0) {
                        radius = edgeLength *ruleScalignFactor * edgeScalingFactor;
                    } else {
                        radius = edgeLength *ruleScalignFactor;
                    }
                } else {
                    radius = edgeLength*ruleScalignFactor;
                }
            } else {
                if (childNode.getObjectType() != RaObject.RA_OBJECT_CONNECTOR) {
                    if (curNode.getChildrenSize()==1)
                        actualAngle = angle;
                    else
                        actualAngle = angle + maxMoleculesAngle /2 -  maxMoleculesAngle /(curNode.getChildrenSize() - hasConnector -1) * (index);//for other node use only the "lower" half of the node
                    if (index%2== 0)
                        radius = edgeLength * edgeScalingFactor;
                    else
                        radius = edgeLength;
                } else {
                    actualAngle = angle;
                    radius = edgeLength *connectorScalignFactor;
                }
            }
            if ((level != 0) || (childNode.getObjectType() != RaObject.RA_OBJECT_SIM_MOLECULE)) {
                int x = (int) layout.getX(curNode) + (int) (radius * Math.cos(actualAngle));//calculate x from radius and angle
                int y = (int) layout.getY(curNode) + (int) (radius * Math.sin(actualAngle));//calculate y from radius and angle
                index++;
                curGraph.addVertex(childNode);
                layout.setLocation(childNode, new Point2D.Double((double) x, (double) y));
                if (childNode.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE) {
                    curGraph.addEdge("SimEdge " + EdgeCount++, curNode, childNode, EdgeType.DIRECTED);
                } else {
                    curGraph.addEdge("RuleEdge " + EdgeCount++, curNode, childNode, EdgeType.DIRECTED);
                }
            }
            RecursiveAddNodes(childNode, layout, level + 1, actualAngle);
        }
    }

    
    /**
     * Add to the graph all the similar node directly connected to the target
     * compound
     *
     * @param RootNode The node corresponding to the target compound.
     * @param layout The layout of the graph.
     */
    private void AddSimilarMol(iRaObject RootNode, StaticLayout layout) {
        double actualAngle;
        int index = 0;
        double radius;
        Iterator<iRaObject> Itr = centralMolecule.iterator();
        while (Itr.hasNext()) {
            iRaObject childNode = Itr.next();
            if (!SET_FIXED_EDGE_SIMILAR_MOLECULES) {
                radius = ((1 - ((iRaObjectSimMolecule) childNode).getSimilarity()) * (edgeLength - rootMoleculeRadius)) + rootMoleculeRadius;
            } else {
                radius = edgeLength;
            }
            actualAngle = (Math.PI * 2 / centralMolecule.size() * index) - Math.PI / 2 + Math.toRadians(angularDisplacment);
            int x = (int) (radius * Math.cos(actualAngle));//calculate x from radius and angle
            int y = (int) (radius * Math.sin(actualAngle));//calculate y from radius and angle
            index++;
            curGraph.addVertex(childNode);
            layout.setLocation(childNode, new Point2D.Double((double) x, (double) y));
            curGraph.addEdge("RootEdge " + EdgeCount++, RootNode, childNode, EdgeType.DIRECTED);
        }
    }

    
    /**
     * Create a map<Vertex, List<Vertex>> of duplicate molecules.
     */
    private void FindDuplicate() {
        iRaObject[] arr = curGraph.getVertices().toArray(new iRaObject[0]);
        for (iRaObject selectedVertex : arr) {
            if (selectedVertex.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE) {
                Set<iRaObjectSimMolecule> tmp = new HashSet<>();
                for (iRaObject currentVertex : arr) {
                    if (currentVertex.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE) {
                        if (((iRaObjectSimMolecule) selectedVertex).getMolecule().Id == (((iRaObjectSimMolecule) currentVertex).getMolecule().Id)) {
                            if (!selectedVertex.equals(currentVertex)) {
                                if (LINK_DUPLICATE_SELECTED_MOLECULE) {
                                    tmp.add((iRaObjectSimMolecule) currentVertex);
                                }
                                duplicateMol.add((iRaObjectSimMolecule) currentVertex);
                            }
                        }
                    }
                }
                if (LINK_DUPLICATE_SELECTED_MOLECULE) {
                    duplicateMap.put((iRaObjectSimMolecule) selectedVertex, tmp);
                }
            }
        }
    }

    
    /**
     *
     * @return
     */
    private Transformer<String, Stroke> createEdgeStrokeTransformer() {
        Transformer<String, Stroke> trans;
        trans = new Transformer<String, Stroke>() {
            @Override
            public Stroke transform(String edge) {
                if (edge.contains("DuplicateEdge") && SET_DASHED_LINK_DUPLICATE) {
                    return new BasicStroke(edgeStroke * 0.5f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 10.0f, new float[]{10f, 5.0f, 2.0f}, 0.0f);
                } else {
                    return new BasicStroke(edgeStroke);
                }
            }
        };
        return trans;
    }

    
    /**
     *
     * @return
     */
    private Transformer<iRaObject, Stroke> createVertexStrokeTransformer() {
        Transformer<iRaObject, Stroke> trans;
        trans = new Transformer<iRaObject, Stroke>() {
            @Override
            public Stroke transform(iRaObject i) {
                if ((i.getObjectType() == RaObject.RA_OBJECT_ROOT_MOLECULE)) {
                    return new BasicStroke(rootStroke);
                }
                if (i.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE && HIGHLIGHT_IDENTICAL_MOLECULES_DASHED) {
                    if (duplicateMol.contains((iRaObjectSimMolecule) i)) {
                        return new BasicStroke(vertexStroke, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 10.0f, new float[]{5.0f}, 0.0f);
                    }
                }
                return new BasicStroke(vertexStroke);
            }
        };
        return trans;
    }

    
    /**
     * Creates the transformer for vertex labels
     *
     * @return the transformer
     */
    private Transformer<iRaObject, String> createVertexLabelTransformer() {
        Transformer<iRaObject, String> trans;
        trans = new Transformer<iRaObject, String>() {
            int ruleIndex = 0;

            @Override
            public String transform(iRaObject i) {
                if ((i.getObjectType() == RaObject.RA_OBJECT_ROOT_MOLECULE)) {
                    return null;
                }
                if ((i.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE)) {
                    VisServ.getRenderer().getVertexLabelRenderer().setPosition(Position.N);
                    String MolLabel;
                    MolLabel = ((iRaObjectSimMolecule) i).getMolecule().CAS;
                    if (MolLabel.isEmpty()) MolLabel = "(no name)";
                    return MolLabel;
                }
                if ((i.getObjectType() == RaObject.RA_OBJECT_RULE)) {
                    VisServ.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
                    return legend.get(i);
                }
                return null;
            }
        };
        return trans;
    }

    
    /**
     *
     * @return
     */
    private Transformer<iRaObject, Icon> vertexIconTransformer() {
        Transformer<iRaObject, Icon> trans;
        trans = new Transformer<iRaObject, Icon>() {
            @Override
            public Icon transform(iRaObject i) {
                if (i.getObjectType() == RaObject.RA_OBJECT_CONNECTOR) {
                    if (curGraph.getOutEdges(i).isEmpty()) {
                        return plusIco;
                    } else {
                        return minusIco;
                    }
                }
                if (i.getObjectType() == RaObject.RA_OBJECT_INTERPOLATION) {
                    return interpolationIco;
                }
                return null;
            }
        };
        return trans;

    }

    
    /**
     * Creates the transformer for vertex filling brush
     *
     * @return the transformer
     */
    private Transformer<iRaObject, Paint> createVertexFillPaintTransformer() {
        Transformer<iRaObject, Paint> trans;
        trans = new Transformer<iRaObject, Paint>() {
            @Override
            public Paint transform(iRaObject i) {
                if (i.getObjectType() == RaObject.RA_OBJECT_ROOT_MOLECULE) {
                    return ((iRaObjectRootMolecule) i).getMoleculeColor();
                }
                if (i.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE) {
                    return ((iRaObjectSimMolecule) i).getMoleculeColor();
                }
                if (i.getObjectType() == RaObject.RA_OBJECT_RULE) {
                    return ((iRaObjectRule)i).getRuleColor();
                }
                return Color.GRAY; // default on type not recognized               
            }
        };
        return trans;
    }

    
    /**
     * Create the transformer for setting shapes and dimensions of vertices
     *
     * @return The VertexShape transformer
     */
    private Transformer<iRaObject, Shape> createVertexShapeTransformer() {
        Transformer<iRaObject, Shape> trans;
        trans = new Transformer<iRaObject, Shape>() {
            @Override
            public Shape transform(iRaObject i) {
                PickedState<iRaObject> state = VisServ.getPickedVertexState();
                float mul;
                if ((state.isPicked(i) && HIGHLIGHT_SELECTED_VERTEX_DIMENSION)) {
                    mul = picked_mul;
                } else {
                    mul = 1;
                }
                if (i.getObjectType() == RaObject.RA_OBJECT_ROOT_MOLECULE) {
                    return myShape.getShape(generalShape.CIRCLE);
               }
               if (i.getObjectType() == RaObject.RA_OBJECT_RULE) {
                   short Shape = ((iRaObjectRule)i).getShape();
                   float SizeFactor = (float)((iRaObjectRule)i).getSizeFactor();
                   switch (Shape) {
                       case RaEndpoint.SHAPE_TRIANGLE_UP:
                           return myShape.getShape(generalShape.TRIANGLEUP, SizeFactor);
                       case RaEndpoint.SHAPE_TRIANGLE_DOWN:
                           return myShape.getShape(generalShape.TRIANGLEDOWN, SizeFactor);
                       case RaEndpoint.SHAPE_RECTANGLE:
                           return myShape.getShape(generalShape.RECTANGLE, SizeFactor*1.3f);
                       default:
                           return myShape.getShape(generalShape.SQUARE, 1);
                   }
               }
               
               if (i.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE){
                   double sim = ((iRaObjectSimMolecule)i).getSimilarity();
                    return myShape.getShape(generalShape.CIRCLE, sim, mul);
                }
                return myShape.getShape(generalShape.SQUARE, 1);
            }
        };
        return trans;
    }

    
    /**
     *
     * @return
     */
    private Transformer<String, Paint> createEdgeDrawPaintTransformer() {
        Transformer<String, Paint> trans;
        trans = new Transformer<String, Paint>() {
            @Override
            public Paint transform(String i) {
                int alfa = 0;
                if (LINK_DUPLICATE_SELECTED_MOLECULE) {
                    if (i.contains("DuplicateEdge")) {
                        alfa = 120;
                    } else {
                        alfa = 255;
                    }
                } else {
                    alfa = 255;
                }
                if (i.contains("RuleEdge")) {
                    return new Color(ruleEdgesColor.getRed(), ruleEdgesColor.getGreen(), ruleEdgesColor.getBlue(), alfa);
                } else if ((i.contains("SimEdge")) || (i.contains("RootEdge"))) {
                    return new Color(moleculesEdgesColor.getRed(), moleculesEdgesColor.getGreen(), moleculesEdgesColor.getBlue(), alfa);
                }
                return new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), alfa);
            }
        };
        return trans;
    }

    
    /**
     *
     * @return
     */
    private Transformer<Context<Graph<iRaObject, String>, String>, Shape> createEdgeShapeTransformer() {
        Transformer<Context<Graph<iRaObject, String>, String>, Shape> trans;
        trans = new Transformer<Context<Graph<iRaObject, String>, String>, Shape>() {
            @Override
            public Shape transform(Context<Graph<iRaObject, String>, String> context) {
                String e = context.element;
                QuadCurve2D instance = new QuadCurve2D.Float();
                if (e.contains("RootEdge") && SET_QUAD_CURVE_SIMILAR_MOLECULES) {
                    instance.setCurve(0.0f, 0.0f, 0.7f, 40f, 1.0f, 0.0f);
                } else if (e.contains("DuplicateEdge") && SET_QUAD_CURVE_DUPLICATE) {
                    instance.setCurve(0.0f, 0.0f, 0.3f, -80f, 1.0f, 0.0f);
                } else if (e.contains("SimEdge") && SET_QUAD_CURVE_MOLECULES) {
                    instance.setCurve(0.0f, 0.0f, 0.9f, -40f, 1.0f, 0.0f);
                } else {
                    instance.setCurve(0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f); //Straight lines
                }
                return instance;
            }
        };
        return trans;
    }

    
    /**
     * Create the transformer for tool-tip over vertex
     *
     * @return the TooltipVertex transformer
     */
    private Transformer<iRaObject, String> createVertexToolTipTransformer() {
        Transformer<iRaObject, String> trans;
        trans = new Transformer<iRaObject, String>() {
            @Override
            public String transform(iRaObject i) {
                String text;
                if ((i.getObjectType() == RaObject.RA_OBJECT_ROOT_MOLECULE)) {
                    text = "<html>" + "Target molecule" + "<br>" + "Smiles: " + ((iRaObjectRootMolecule) i).getMolecule().GetSMILES() + "<br>" + "ID: " + ((iRaObjectRootMolecule) i).getMolecule().GetId();
                    return text;
                }

                if ((i.getObjectType() == RaObject.RA_OBJECT_CONNECTOR)) {
                    text = "<html>" + "Click to expand or collapse subgraph";
                    return text;
                }

                if ((i.getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE)) {
                    String MolCAS, MolName, MolSMILES, MolExp;
                    MolCAS = ((iRaObjectSimMolecule) i).getMolecule().CAS;
                    if (MolCAS.isEmpty()) MolCAS = "(no CAS)";
                    MolName = ((iRaObjectSimMolecule) i).getMolecule().CommonName;
                    if (MolName.isEmpty()) MolName = "(no Name)";
                    try {
                        dbStructure st = RaResults.getDBConn().GetStructure(((iRaObjectSimMolecule) i).getMolecule().Id);
                        MolSMILES = st.SMILES;
                        if (MolSMILES.isEmpty()) MolSMILES = "(no SMILES)";
                    } catch (Exception e) {
                        MolSMILES = "(no SMILES)";
                    }
                    MolExp = RaResults.getEndpoint().FormatValue(((iRaObjectSimMolecule) i).getExperimentalValue().Value);

                    text = "<html>" + "Molecule " + MolName + "<br>CAS: " + MolCAS
                            + "<br>Smiles: " + MolSMILES + "<br>Similarity: "
                            + String.format("%.4f", ((iRaObjectSimMolecule) i).getSimilarity())
                            + "<br>Exp value: " + MolExp;
                    return text;
                }
                if ((i.getObjectType() == RaObject.RA_OBJECT_RULE)) {
                    text = "<html>" + ((iRaObjectRule) i).getAlert().getDescription();
                    double curAccuracy;
                    try {
                        curAccuracy = ((iRaObjectRule) i).getAlert().getNumericProperty(InsilicoConstants.KEY_ALERT_VALUE_ACCURACY);
                        text += "<br>Exp. Accuracy: " + curAccuracy;
                    } catch (PropertyNotFoundException ex) {
                    }
                    return text;
                }
                return ""; // default on type not recognized
            }
        };

        return trans;
    }

    
    /**
     *
     * @return
     */
    private Transformer<iRaObject, Paint> createDrawVertexPaintTransformer() {
        Transformer<iRaObject, Paint> trans;
        trans = new Transformer<iRaObject, Paint>() {
            @Override
            public Paint transform(iRaObject i) {
                PickedState<iRaObject> state = VisServ.getPickedVertexState();
                if ((state.isPicked(i) && HIGHLIGHT_SELECTED_VERTEX_COLOR)) {
                    return selectedVertexLineColor;
                } else {
                    return vertexLineColor;
                }
            }
        };
        return trans;
    }

    
    /**
     *
     */
    private void createPanel() {
        graphFinal = new JPanel();
        graphFinal.setLayout(new BorderLayout());
        
        
        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(-1);
        mainSplit.setResizeWeight(1);
        graphFinal.add(mainSplit, BorderLayout.CENTER);
        
        JSplitPane rightUpperPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, MolViewer, targetInfosPanel);
        
        rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(2, 1));
        rightPanel.add(rightUpperPanel);
//        rightPanel.add(MolViewer);
//        rightPanel.add(targetInfosPanel);
        rightPanel.add(LegendViewer);
        
        mainSplit.setRightComponent(rightPanel);
        

       
    }

    private void collapseGraph(iRaObjectRootMolecule RootNode) {
        for (String r : curGraph.getOutEdges(RootNode)) {
            iRaObject o = curGraph.getEndpoints(r).getSecond();
            if (o.getObjectType() == RaObject.RA_OBJECT_RULE) {
                for (String r1 : curGraph.getOutEdges(o)) {
                    iRaObject o1 = curGraph.getEndpoints(r1).getSecond();
                    if (o1.getObjectType() == RaObject.RA_OBJECT_CONNECTOR) {
                        tpmp.collapseOrExpand(o1, VisServ);
                    }
                }
            }
        }
    }

    
    /**
     * Create a component resize listener
     *
     * @return The component listener
     */
    private ComponentListener createComponentiListener() {
        ComponentAdapter componentAdapter;
        componentAdapter = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                vvPaint.setVisServDimension(VisServ.getHeight(), VisServ.getWidth());
            }
        };
        return componentAdapter;
    }

    
    /**
     * Create the change listener
     *
     * @return the change listener
     */
    private ChangeListener createViewChangeListener() {
        ChangeListener changeListener;
        changeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                
                MutableTransformer t = (MutableTransformer) e.getSource();
                vvPaint.scaleDiam(t.getScale());
                PickedState<iRaObject> state = VisServ.getPickedVertexState();
                tpmp.updateCoordinates(VisServ, state.getPicked());
                LegendViewer.updateCoordinates(VisServ, state.getPicked());
            }
        };
        return changeListener;
    }

    
    private ItemListener createItemListener() {
        ItemListener itemListener;
        itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(!LINK_DUPLICATE_SELECTED_MOLECULE){
                }
                else
                    if(e.getItem()!=null){
                        iRaObject selectedMolecula = tpmp.getSelectedMolecules();
                        if (((iRaObject) e.getItem()).getObjectType() == RaObject.RA_OBJECT_SIM_MOLECULE) {
                            iRaObjectSimMolecule selectedMol = (iRaObjectSimMolecule) e.getItem();
                            if(selectedMol.equals((iRaObjectSimMolecule)selectedMolecula))
                                if (e.getStateChange() == ItemEvent.SELECTED){
                                    for (iRaObjectSimMolecule duplicateMol : duplicateMap.get(selectedMol)) 
                                        if ((curGraph.containsVertex(selectedMol)) && (curGraph.containsVertex(duplicateMol))){
                                            curGraph.addEdge("DuplicateEdge " + EdgeCount++, selectedMol, duplicateMol, EdgeType.UNDIRECTED);
                                        }
                                }
                                else
                                    for (iRaObjectSimMolecule duplicateMol : duplicateMap.get(selectedMol)) 
                                        curGraph.removeEdge(curGraph.findEdge(selectedMol, duplicateMol));
                            
                        }
                    }
            }
        };
        return itemListener;
    }

    
    private KeyListener createKeyListener() {
        KeyListener klist;
        klist = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    PickedState<iRaObject> state = VisServ.getPickedVertexState();
                    state.clear();
                    tpmp.updateCoordinates(VisServ, state.getPicked());
                    LegendViewer.updateCoordinates(VisServ, state.getPicked());
                }

                if (e.getKeyCode() == KeyEvent.VK_PLUS) 
                    scaler.scale(VisServ, zoomFactor, VisServ.getCenter());
                if (e.getKeyCode() == KeyEvent.VK_MINUS)
                    scaler.scale(VisServ, 1/zoomFactor, VisServ.getCenter());
                if (e.getKeyCode() == KeyEvent.VK_0)
                    VisServ.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setToIdentity();
            }
        };
        return klist;
    }

    
    private ChangeListener createLayoutChangeListener() {
        ChangeListener chList;
        chList = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                PickedState<iRaObject> state = VisServ.getPickedVertexState();
                LegendViewer.updateCoordinates(VisServ, state.getPicked());
            }
        };
        return chList;
    }

    
    private Component createZoomControl() {
        JButton zoomIn = new JButton(zoomPlusIco);
        zoomIn.setBorder(BorderFactory.createEmptyBorder());
        zoomIn.setContentAreaFilled(false);

        zoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scaler.scale(VisServ, zoomFactor, VisServ.getCenter());
            }
        });
        JButton zoomOut = new JButton(zoomMinusIco);
        zoomOut.setBorder(BorderFactory.createEmptyBorder());
        zoomOut.setContentAreaFilled(false);
        zoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scaler.scale(VisServ, 1 / zoomFactor, VisServ.getCenter());
            }
        });
        JButton zoomReset = new JButton(zoomResetIco);
        zoomReset.setBorder(BorderFactory.createEmptyBorder());
        zoomReset.setContentAreaFilled(false);

        zoomReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VisServ.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setToIdentity();
            }
        });

        JPanel zoomControl = new JPanel();
        zoomControl.setOpaque(false);

        zoomControl.setLayout(new BoxLayout(zoomControl, BoxLayout.Y_AXIS));
        zoomControl.add(Box.createVerticalGlue());
        zoomControl.add(zoomIn, BorderLayout.EAST);
        //zoomControl.add(Box.createRigidArea(new Dimension(0,1)));
        zoomControl.add(zoomReset, BorderLayout.EAST);
        //zoomControl.add(Box.createRigidArea(new Dimension(0,1)));
        zoomControl.add(zoomOut, BorderLayout.EAST);
        zoomControl.add(Box.createRigidArea(new Dimension(0, 10)));
        return zoomControl;
    }


    /**
     * Creates the BasicVisualizationServer object that should be used to
     * display the final chart
     *
     * @param dim Dimension of viewer and layout;
     * @return visualization server for the current chart
     */
    public JPanel CreateView(final Dimension dim) {

        createPanel();
        iRaObject RootNode = this.RaResults.getRootNode();
        StaticLayout<iRaObject, String> layout = new StaticLayout<>(curGraph);
        PluggableGraphMouse gm = new PluggableGraphMouse();
        scaler = new ViewScalingControl();
        VisServ = new VisualizationViewer<>(layout);
        tpmp = new TransPickMousePlugin(RaResults.getDBConn(), MouseEvent.BUTTON1_MASK, dim, SET_SINGLE_CLICK_FOR_INFOS_PANEL, RaResults, duplicateMol, SHOW_DUPLICATE_SELECTED_MOLECULE, SHOW_DUPLICATE_SELECTED_RULE, layout, LegendViewer);

        ComputeRuleNumber(RootNode);
        RecursiveAddNodes(RootNode, layout, 0, -Math.PI/2); //start adding the nodes to the graph 
        ComputeLegend(RootNode, 0, 0);
        if (SHOW_SIMILAR_MOLECULES) {
            AddSimilarMol(RootNode, layout); //add all the molecules connected to root node
        }
        FindDuplicate();
        collapseGraph((iRaObjectRootMolecule) RootNode);

        vvPaint = new VisViewPaintable(direction, topColorAmount, backgroundGradientTop, backgroundGradientBottom, highlightMoleculeColor,SET_GRADIENT_BACKGROUND, backgroundColor);
        vvPaint.setHighligthMolecules(HIGHLIGHT_SELECTED_VERTEX_CIRLCE);
        vvPaint.setHighlightRules(HIGHLIGHT_SELECTED_RULE_TRIANGLE);
        vvPaint.setTransPickMouse(tpmp);
        vvPaint.setPanelRuleLegend(LegendViewer);
        vvPaint.setDiameter(maxSimMolRadius);
        
        // Gradient bar
        if (RaResults.getEndpoint().HasGradient()) {
            vvPaint.showGradientBar(true);
            RaColorGradient Gradient = RaResults.getEndpoint().getGradient();
            vvPaint.setGradientBar(Gradient.getMaxTitle(), Gradient.getMidTitle(), Gradient.getMinTitle(), Gradient.getMinColor(), 
                    Gradient.getMidColor(), Gradient.getMaxColor(), gradientBarTextColor);
        } else {
            vvPaint.showGradientBar(false);
        }

        gm.add(tpmp);
        gm.add(new ScalingGraphMousePlugin(scaler, 0, 1 / zoomFactor, zoomFactor));
        gm.add(new PopupMousePlugin());

        VisServ.setPreferredSize(dim);
        VisServ.setName("VisServ");
        VisServ.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dim.height / 2, dim.width / 2);
        VisServ.addPreRenderPaintable(vvPaint);
        VisServ.getRenderContext().setVertexFillPaintTransformer(createVertexFillPaintTransformer());
        VisServ.getRenderContext().setVertexDrawPaintTransformer(createDrawVertexPaintTransformer());
        VisServ.getRenderContext().setVertexStrokeTransformer(createVertexStrokeTransformer());
        VisServ.getRenderContext().setVertexShapeTransformer(createVertexShapeTransformer());
        VisServ.getRenderContext().setEdgeShapeTransformer(createEdgeShapeTransformer());
        VisServ.getRenderContext().setEdgeStrokeTransformer(createEdgeStrokeTransformer());
        VisServ.getRenderContext().setEdgeDrawPaintTransformer(createEdgeDrawPaintTransformer());
        VisServ.getRenderContext().setVertexLabelTransformer(createVertexLabelTransformer());
        VisServ.setVertexToolTipTransformer(createVertexToolTipTransformer());
        VisServ.getRenderContext().setVertexIconTransformer(vertexIconTransformer());
        VisServ.addComponentListener(createComponentiListener());
        VisServ.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).addChangeListener(createViewChangeListener());
        VisServ.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).addChangeListener(createLayoutChangeListener());
        VisServ.addKeyListener(createKeyListener());
        VisServ.getPickedVertexState().addItemListener(createItemListener());
        VisServ.setGraphMouse(gm);
        VisServ.setLayout(new BorderLayout());
        VisServ.add(createZoomControl(), BorderLayout.EAST);

        MolViewer.SetMolecule(((iRaObjectRootMolecule) RootNode).getMolecule().GetSMILES()); // SMILES of the molecule is passed to the viewer
        MolViewer.setPreferredSize(new Dimension(dim.height / 2, dim.height / 2));
        MolViewer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        //MolViewer.addMouseListener(new MolViewerMouseAdapter(((iRaObjectRootMolecule)RootNode).getMolecule().GetSMILES()));

        LegendViewer.setPreferredSize(new Dimension(dim.height / 8 * 2, dim.height / 2));
        LegendViewer.setVisServ(VisServ);
        LegendViewer.setLegend(legend);
        LegendViewer.setLayout(layout);
        ColorLegendViewer.setPreferredSize(new Dimension(dim.height / 8, dim.height / 2));

        
    //    JLayeredPane  layeredPane = new JLayeredPane();
     //   layeredPane.setBounds(0,0,dim.width,dim.height);
      //  layeredPane.setLayout(new LayeredPaneLayout(layeredPane));
      //  layeredPane.add(VisServ,new Integer(0));
       // Point2D p1 = new Point();
        //p1.setLocation(layout.getX(RootNode),layout.getY(RootNode));
        //int x = (int) VisServ.getRenderContext().getMultiLayerTransformer().transform(p1).getX();
        //int y = (int) VisServ.getRenderContext().getMultiLayerTransformer().transform(p1).getY();
        //targetInfosPanel.setLocation(x+10,y+10);
        //layeredPane.add(targetInfosPanel, new Integer(1));
        mainSplit.setLeftComponent(VisServ);

        return graphFinal;
    }
    
    /*
    public class LayeredPaneLayout implements LayoutManager {

        private final Container target;

        public LayeredPaneLayout(final Container target) {
            this.target = target;
        }

        @Override
        public void addLayoutComponent(final String name, final Component comp) {
        }

        @Override
        public void layoutContainer(final Container container) {
            for (final Component component : container.getComponents()) {
                if("VisServ".equals(component.getName()))
                    component.setBounds(new Rectangle(0, 0, target.getWidth(), target.getHeight()));
            }
    }

    @Override
    public Dimension minimumLayoutSize(final Container parent) {
            return preferredLayoutSize(parent);
    }

    @Override
    public Dimension preferredLayoutSize(final Container parent) {
            return target.getBounds().getSize();
    }

    @Override
    public void removeLayoutComponent(final Component comp) {
    }
    }*/

}