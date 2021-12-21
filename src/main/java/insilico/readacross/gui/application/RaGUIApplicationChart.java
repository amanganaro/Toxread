package insilico.readacross.gui.application;

import insilico.readacross.gui.chart.RaChart;
import insilico.readacross.type.objects.RaResultsContainer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author User
 */
public class RaGUIApplicationChart extends JFrame {
    
    
    public RaGUIApplicationChart(RaResultsContainer RaResults) throws Exception{
        
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } 
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println(e);
        }

        //Set the main frame in the left third of the screen
//        int x = (Toolkit.getDefaultToolkit().getScreenSize().width/3)-getWidth()/2;
//        int y = (Toolkit.getDefaultToolkit().getScreenSize().height/2)-getHeight()/2;
//        setLocation(x,y);        
        
        // Builds the chart for the given RA results
        RaChart chart = new RaChart(RaResults);
        setTitle("Read-Across chart - Endpoint: " + RaResults.getEndpoint().getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel graph = chart.CreateView(new Dimension(700,700));
        getContentPane().add(graph,BorderLayout.CENTER);
        pack();
        setName("Read-Across chart");
        
    }
    
}
