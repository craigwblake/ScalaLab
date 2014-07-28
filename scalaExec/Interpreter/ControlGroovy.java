
package scalaExec.Interpreter;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import scalalab.JavaGlobals;

// this class controls the embedded in ScalaLab, GroovyLab engine that offers  alternative scientific scripting
// GroovySci the Groovy based DSL of GroovyLab, shares the same Java scientific and plotting libraries with ScalaSci, 
// and has a similar potential, although a different syntax and style
public class ControlGroovy {

    // prepares the default import statements for the GroovyShell 
   static public org.codehaus.groovy.control.customizers.ImportCustomizer globallmportCustomizer;

     // settings for embedded GroovyLab. These are saved and restored at the next session
   static public Properties  groovyLabSettings = loadGlabSettings();

   // GroovyShell does not remember imported statements and previously compiled classes
   // Thus, we buffer the source of them. The following variables are used to implement the buffering task.
   static public String bufferingImports ="";
   static public String bufferingCode="";
   static public RSyntaxTextArea  bufferedImportsTextArea;
   static public RSyntaxTextArea  bufferedCodeTextArea;
   static public RTextScrollPane   bufferedImportsScrollPane;
   static public RTextScrollPane   bufferedCodeScrollPane;
    
   // loads configuration settings for GroovyLab from "Glabs.props" file and returns them with a Java Properties object
   static public Properties loadGlabSettings() {
       Properties settings = null; 
       try
        {  
           settings = new Properties();
       
           FileInputStream in = null;
        
             // the GroovyLab's configuration file
           String configFileName = scalaExec.Interpreter.GlobalValues.workingDir+File.separatorChar+"Glab.props";
           File configFile = new File(configFileName);
           if (configFile.exists())   {  // configuration file exists
                  in = new FileInputStream(configFile);
                  settings.load(in);   // load the settings
                  gExec.Interpreter.GlobalValues.gLabPropertiesFile = configFileName;
                  }
           }
            catch (IOException e) 
        {
           e.printStackTrace();
        }
    
        return settings;

   }
    
    // execute an expression with the Groovy shell
    public static String  execWithGroovyShell(String expression)
    {
            boolean displayAnswer = true;

            if (expression.endsWith(";"))
               displayAnswer = false;    // surpresses the display of the last script result
        
           // set the Groovy's compiler configuration to have the list of the specified .jar files in its classpath
        if (gExec.Interpreter.GlobalValues.GroovyShell == null)  {  // construct a properly inited GroovyShell
            
            warmUpGroovy();   // init GroovyLab
            
        } // construct a properly inited GroovyShell
            

         
    expression  =  scalaExec.Interpreter.ControlGroovy.bufferingImports+"\n"+ scalaExec.Interpreter.ControlGroovy.bufferingCode+"\n"+expression;
    expression = expression.trim();
       

           Object  grResult=null;
       // evaluate the command with the Groovy shell
           gExec.Interpreter.GlobalValues.currentExpression = expression;
           try {
               grResult =  gExec.Interpreter.GlobalValues.GroovyShell.evaluate(expression);
               gExec.Interpreter.GlobalValues.groovyBinding = gExec.Interpreter.GlobalValues.GroovyShell.getContext();  // update Groovy context
            }
           catch (Exception e) {
                      String excText = e.getMessage();
                        if (excText.isEmpty()==false) {
                           StackTraceElement []  st = e.getStackTrace();
                        StringBuilder exText = new StringBuilder();
                        for (StackTraceElement el: st) 
                            exText.append(el.toString()+"\n");
                        
                        
                        System.out.println("\nCompilation Error: \n"+excText.toString());
                        
                        return null;
                    }  // excText not empty
              }   // exception in compile with GroovyShell   

           
       if (displayAnswer==true)  {
           if (grResult instanceof  double [])  {
if (groovySci.PrintFormatParams.verboseFlag == true)               
 return "double["+((double [])grResult).length+"] = \n "+groovySci.math.array.Matrix.printArray((double[])grResult);
else
return "";
           }
                   
else if  (grResult instanceof  double [][]) {
if (groovySci.PrintFormatParams.verboseFlag == true)               
    return "double["+((double [][])grResult).length+"] ["+((double [][])grResult)[0].length+ "] = \n"+
        groovySci.math.array.Matrix.printArray((double[][])grResult);
     else
    return "";
            }
           else
           
    if (grResult instanceof  int []) 
return "int["+((int [])grResult).length+"] = \n "+groovySci.math.array.DoubleArray.toString((int[])grResult);
else if  (grResult instanceof  int [][])
return "int["+((int [][])grResult).length+"] ["+((int [][])grResult)[0].length+ "] = \n"+
        groovySci.math.array.DoubleArray.toString((int[][])grResult);
                       else 
           if (grResult != null)
               {
          String typeOfResult = groovySciCommands.BasicCommands.typeOf(grResult);
          return typeOfResult+":\n"+grResult.toString();
           
         }
        
           else
               return "";
       }
       else return "";
    }
    
 public static void groovyLabClose(Properties groovyLabSettings) {
     passPropertiesFromWorkspaceToSettings(groovyLabSettings);
     File outPropFile = new File(scalaExec.Interpreter.GlobalValues.workingDir+File.separator+"Glab.props");
    try {
   FileOutputStream outFile = new FileOutputStream(outPropFile);
   groovyLabSettings.store(outFile, "Saved GroovyLab global conf parameters");
   outFile.close();
    }
    catch (Exception fnfe) {
        JOptionPane.showMessageDialog(null, "Cannot write configuration file. Perhaps you do not have access rights for write, try making a shortcut to gLab using a proper \"Start in\" directory ","Cannot write configuration file", JOptionPane.INFORMATION_MESSAGE);
        System.out.println("error opening file for writing configuration");
        fnfe.printStackTrace();
        }
      }
 
    
    // initializes a new Groovy Shell with a classpath consisted of the default libraries plus the array of additionalToolboxes      
 public  static void  warmUpGroovy() {

            
            java.util.LinkedList<String>  GroovyShellPathsList = new java.util.LinkedList<>() ;  // the current paths list with which the GroovyShell's classloader is inited
          
            GroovyShellPathsList.clear();
            GroovyShellPathsList.add(".");  // current directory
            GroovyShellPathsList.add(".");  // current directory
            GroovyShellPathsList.add(GlobalValues.workingDir);
            GroovyShellPathsList.add(JavaGlobals.jarFilePath);
            
            GroovyShellPathsList.add(JavaGlobals.jsciFile);
            GroovyShellPathsList.add(JavaGlobals.mtjColtSGTFile);
            GroovyShellPathsList.add(JavaGlobals.ejmlFile);
            GroovyShellPathsList.add(JavaGlobals.jblasFile);
            GroovyShellPathsList.add(JavaGlobals.numalFile);
            GroovyShellPathsList.add(JavaGlobals.ApacheCommonsFile);
      
            GroovyShellPathsList.add(JavaGlobals.LAPACKFile);
            GroovyShellPathsList.add(JavaGlobals.ARPACKFile);
            GroovyShellPathsList.add(JavaGlobals.JASFile);
            GroovyShellPathsList.add(JavaGlobals.groovyJarFile);
            GroovyShellPathsList.add(JavaGlobals.groovyLabFile);
            
            
            System.out.println("appending to GroovyShell path:  "+GlobalValues.workingDir); 
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.jarFilePath);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.jsciFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.mtjColtSGTFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.ejmlFile);
            System.out.println("appending to GroovyShell path: "+JavaGlobals.jblasFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.numalFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.ApacheCommonsFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.LAPACKFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.ARPACKFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.JASFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.groovyJarFile);
            System.out.println("appending to GroovyShell path:  "+JavaGlobals.groovyLabFile);
          
            Vector <String> classpath = GlobalValues.ScalaSciClassPathComponents;
           for (int k=0; k<classpath.size(); k++) {
              String clsp = classpath.elementAt(k).trim();
              GroovyShellPathsList.add(clsp);  
              System.out.println("appending to GroovyShell toolbox:  "+ clsp);
          }
          
        
        
     // set the compiler configuration to have the list of the specified .jar files in its classpath
            CompilerConfiguration cf = new CompilerConfiguration();
                       
            if (gExec.Interpreter.GlobalValues.CompileDecimalsToDoubles)  {    // convert BigDecimalsToDoubles for efficiency
            
              cf.addCompilationCustomizers(new CompilationCustomizer(CompilePhase.CONVERSION) {
                
                    @Override
                    public void call(final SourceUnit source, final  GeneratorContext context, final ClassNode classNode)   {
                        new expandRunTime.ConstantTransformer(source).visitClass(classNode);
                    }
                });
            }
            
            cf.setTargetBytecode(gExec.Interpreter.GlobalValues.jdkTarget);
            if (gExec.Interpreter.GlobalValues.CompileIndy == true)  {
               cf.getOptimizationOptions().put("indy", true);
               cf.getOptimizationOptions().put("int", false);
            }
            else             {
               cf.getOptimizationOptions().put("indy", false);
               cf.getOptimizationOptions().put("int", true);
            }
            
            if (gExec.Interpreter.GlobalValues.useAlwaysDefaultImports) {
               prepareImports();
               cf.addCompilationCustomizers(globallmportCustomizer);
            }
               
            cf.setRecompileGroovySource(true);
            
            cf.setClasspathList(GroovyShellPathsList);
            final ClassLoader parentClassLoader = GlobalValues.scalalabMainFrame.getClass().getClassLoader();
           
            gExec.Interpreter.GlobalValues.GroovyShell = new GroovyShell(parentClassLoader, new Binding(), cf);
       
            // expand the runtime of Groovy with GroovyLab's extensions
            String expandGroovyCode = " expG = new expandRunTime.expandGroovy();  expG.run(); " ;

            gExec.Interpreter.GlobalValues.GroovyShell .evaluate(expandGroovyCode);
            
           
       }
 
 public static void prepareImports() {
if (globallmportCustomizer == null) {
    globallmportCustomizer  = new ImportCustomizer();
            
    globallmportCustomizer.addStaticStars("groovySci.math.array.BasicDSP", 
            "groovySci.math.array.Vec", "groovySci.math.array.Matrix",
            "groovySci.math.array.CCMatrix", "groovySci.math.array.Sparse",
            "groovySci.math.array.JILapack", "groovySci.math.plot.plot",
            "groovySci.math.plot.plotAdaptiveFunctional", 
            "groovySci.math.array.MatrixConvs",
            "groovySci.FFT.FFTNR",
             "jplot.jFigure", "jplot.jPlot",
            "groovySci.math.io.MatIO",
            "groovySciCommands.BasicCommands",
            "groovySci.math.array.DoubleArray",
            "groovySci.FFT.ApacheFFT",
            "groovySci.FFT.FFTCommon",
            "NR.gaussj", 
            "java.lang.Math"          
            
            );
    globallmportCustomizer.addImports("groovySci.math.array.Vec", "groovySci.math.array.Matrix",
            "groovySci.math.array.CCMatrix", "groovySci.math.array.Sparse",
            "groovySci.math.array.JILapack", "groovySci.math.plot.PlotController",
            "groovy.swing.SwingBuilder", "java.text.DecimalFormat",
            "groovySci.FFT.ApacheFFT"
            );
    
    globallmportCustomizer.addStarImports("Jama", "numal",
            "jplot",  
            "java.awt",  "javax.swing", "java.awt.event",
            "JSci.maths", "JSci.maths.wavelet", "JSci.maths.wavelet.daubechies2",
            "groovySci.math.array", 
            "NR", "com.nr.sp"
            );
  }

 }
 
 
 
 
// pass properties readed from settings Property String to the gLab workspace structures
    public static void passPropertiesFromSettingsToWorkspace(Properties settings)
     {
         
         // whether to compile with the invokedynamic support
    String indyProperty = settings.getProperty("indyProp");
    if (indyProperty != null)   
         if (indyProperty.equalsIgnoreCase("true"))
             gExec.Interpreter.GlobalValues.CompileIndy = true;
         else gExec.Interpreter.GlobalValues.CompileIndy = false;
       
        //whether to transform BigDecimals to doubles with an AST Transformation for efficiency
     String compileDecimalsToDoublesProperty = settings.getProperty("compileDecimalsToDoublesProp");
     if (compileDecimalsToDoublesProperty != null)
         if (compileDecimalsToDoublesProperty.equalsIgnoreCase("true"))
             gExec.Interpreter.GlobalValues.CompileDecimalsToDoubles = true;
         else gExec.Interpreter.GlobalValues.CompileDecimalsToDoubles = false;
         
      String useAlwaysDefaultImportsProp = settings.getProperty("useAlwaysDefaultImportsProp");
if (useAlwaysDefaultImportsProp != null)
         if (useAlwaysDefaultImportsProp.equalsIgnoreCase("true"))
             gExec.Interpreter.GlobalValues.useAlwaysDefaultImports = true;
         else gExec.Interpreter.GlobalValues.useAlwaysDefaultImports = false;
         
         
    } 
    
// pass properties from the gLab workspace structures to the settings Property String to 
    public static void passPropertiesFromWorkspaceToSettings(Properties settings)
     {
         
        settings.setProperty("indyProp", Boolean.toString(gExec.Interpreter.GlobalValues.CompileIndy));
        settings.setProperty("compileDecimalsToDoublesProp", Boolean.toString(gExec.Interpreter.GlobalValues.CompileDecimalsToDoubles));
        
        settings.setProperty("useAlwaysDefaultImportsProp", Boolean.toString(gExec.Interpreter.GlobalValues.useAlwaysDefaultImports));
     }
 
}



