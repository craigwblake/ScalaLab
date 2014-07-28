
package scalaExec.Interpreter;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;


/* buffering import statements as text, is useful for the execution of Groovy scripts with the GroovyShell,
 since GroovyShell does not remember previously compiled classes and
 import statements, as the Scala Interpreter does.
This class allows to edit buffered code conveniently using an RSyntaxArea component.
*/
public class editBufferedImportsAction extends AbstractAction {

    public editBufferedImportsAction() {
      super("Edit the buffered imports");
    }
   
    
    
                        @Override
                        public void actionPerformed(ActionEvent e) {
    JFrame dframe = new JFrame("Edit the contents of the buffered imports");
    final RSyntaxTextArea jt = new RSyntaxTextArea();
        
     jt.setFont(new Font(GlobalValues.paneFontName, Font.PLAIN, GlobalValues.paneFontSize));
      
      jt.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
      jt.setCodeFoldingEnabled(true);
      jt.setText(scalaExec.Interpreter.ControlGroovy.bufferingImports);
        
      RTextScrollPane   jp = new RTextScrollPane(jt);
    JButton updateButton = new JButton("Update the buffered imports");
    updateButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          scalaExec.Interpreter.ControlGroovy.bufferingImports  = jt.getText();
        }
    });
    
    dframe.setLayout(new BorderLayout());
    dframe.add(jp, BorderLayout.NORTH);
    dframe.add(updateButton, BorderLayout.SOUTH);
    dframe.pack();
    dframe.setLocation(200, 200);
    dframe.setVisible(true);
            }
       
        
}
    

