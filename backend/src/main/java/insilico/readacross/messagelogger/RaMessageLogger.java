package insilico.readacross.messagelogger;

import java.io.PrintStream;
import javax.swing.text.JTextComponent;

/**
 *
 * @author User
 */
public class RaMessageLogger {
    
    private static RaMessageLogger MessageLogger = new RaMessageLogger();
    
    private PrintStream OutStream;
    private JTextComponent OutComponent;
    
    
    public RaMessageLogger() {
        OutStream = System.out;
        OutComponent = null;
    }
    
    public static RaMessageLogger getLogger() {
        return MessageLogger;
    }
    
    
    public void SetOutputStream(PrintStream OutputStream) {
        OutStream = OutputStream;
        OutComponent = null;
    }
    
    public void SetOutputStreamForComponent(JTextComponent OutputComponent) {
        OutStream = null;
        OutComponent = OutputComponent;
    }
    
    
    public void SendMessage(String msg) {
        if ((OutStream != null) && (OutComponent == null))
            OutStream.println(msg);
        if ((OutStream == null) && (OutComponent != null))
            OutComponent.setText(OutComponent.getText() + msg + "\n");
    }
}
