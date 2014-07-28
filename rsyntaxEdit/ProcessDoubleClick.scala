
package rsyntaxEdit

import scalaExec.Interpreter.GlobalValues

// processes a word over which we double click with the mouse
// this will generally display information about the variable,
// that however depends on whether the variable is ScalaSci's or GroovySci's'
object ProcessDoubleClick {

  // process the double click over the editor's text word wd
def processDoubleClick(wd: String) = {
  var wordAtCursor = wd
    
  if (scalaExec.Interpreter.GlobalValues.inScalaMode)  {  // use Scala interpreter to retrieve variable's contents'
    

     var sI = scalaExec.Interpreter.GlobalValues.globalInterpreter   // the global Scala interpreter
  
       // e.g. if the wordAtCursor variable, is: var aa =10, wordAtCursor=="aa" 
     var  typeOfId = sI.typeOfTerm(wordAtCursor).toString()
          
  if (typeOfId != "<notype>") {
  
      typeOfId = typeOfId filter (_ != '(' ) filter ( _ != ')')   // remove some strange parenthesis the interpreter returns before type
      
      
      if (typeOfId.contains(":")==false) 
       {  // not a function: avoid displaying values for functions
       // take in $$dummy synthertic variable the identifier as a string, e.g. for : var aa = 10, is  $$dymmy = "aa"
      //var $$dummy = ""+wordAtCursor  
      // construct command to extract the value of the variable, e.g. var $$dummy = aa
      //var execString = "var $$dummy = "+$$dummy 
      //sI.quietRun(execString)  // execute quitely, the required value is assigned to the synthetic variable $$dummy

     // var valueOfId = scalaExec.Interpreter.GlobalValues.globalInterpreter.valueOfTerm("$$dummy").getOrElse("none")
       var valueOfId =      scalaExec.Interpreter.GlobalValues.globalInterpreter.eval(wordAtCursor)
       
       if (GlobalValues.getValuesForAllJSyntax == true) {
       if (valueOfId != "none")  
          println(wordAtCursor+" [ "+ typeOfId+" ]  " +valueOfId)   
         else
          println(wordAtCursor+" [ "+ typeOfId+" ]  ")
       
      }
      
      else {  // values for controlled types only
     var isPrimitiveType =  (( typeOfId.contains("Double") || typeOfId.contains("Int") || typeOfId.contains("Long")
           || typeOfId.contains("Char") || typeOfId.contains("Short") || typeOfId.contains("Boolean")
 || typeOfId.contains("String"))  && (typeOfId.contains("[")==false ))
     
        // for scalaSci types we have the provision to cut large strings at toString
     var isScalaSciType = typeOfId.contains("scalaSci")  

          
      if (valueOfId != "none")   {
        
            // display also size for scalaSci types
        if (isScalaSciType) {
         val  sizeOfId =      scalaExec.Interpreter.GlobalValues.globalInterpreter.eval(wordAtCursor+".size")
         wordAtCursor = wordAtCursor+ " "+"("+sizeOfId+") " 
        }
        
            if (isScalaSciType == false && isPrimitiveType == false) {  // not a scalaSci or primitive type, avoid displaying value
            println(wordAtCursor+" ["+ typeOfId+"]  " )   
                  }  
                  else  {
       //    var valueOfId = scalaExec.Interpreter.GlobalValues.globalInterpreter.valueOfTerm("$$dummy").getOrElse("none")
           println(wordAtCursor+" ["+ typeOfId+"]  " +valueOfId)   
                  }
                    
        } // valueOfId != "none"
        else
          println(wordAtCursor+" ["+ typeOfId+"]  ")
        
       }  // values for controlled types only 
     }     // not a function: avoid displaying values for functions
     else 
       println(wordAtCursor+" ["+ typeOfId+"] ")
      }   // <notype>                   

      
      else
             println("")
    
        }
     
     else {  // use GroovyShell
       
          var  sI = gExec.Interpreter.GlobalValues.GroovyShell   // the global GroovyShell
          var  groovyShellDefinedVar = sI.getVariable(wordAtCursor)  // get the variable from GroovyShell

          if (groovyShellDefinedVar!=null) {
            var  currVariable = scalaExec.Interpreter.ControlGroovy.execWithGroovyShell(wordAtCursor)
      
          if (currVariable != null)  {
          println(currVariable.toString())
          currVariable = null;
          wordAtCursor = ""
            
          }   // groovyShellDefinedVar != null
       } // wordAtCursor.length() > 0
     
          else
      println("");

     } // use GroovyShell
 }       
}
