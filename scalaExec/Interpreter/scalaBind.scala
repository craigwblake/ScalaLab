
package scalaExec.Interpreter


// pass binding for some variables from the GroovyShell to the Scala Interpreter
object scalaBind {
  
   def bind(varName: String, varValue: AnyRef) = {
     
     if (varValue.isInstanceOf[Double])  {  // bind a double value
       var dv = varValue.asInstanceOf[Double]
       scalaExec.Interpreter.GlobalValues.globalInterpreter.bind(varName, dv)
      }
      else
     if (varValue.isInstanceOf[Float])  {  // bind a float value
       var dv = varValue.asInstanceOf[Double]
       scalaExec.Interpreter.GlobalValues.globalInterpreter.bind(varName, dv)
      }
      else
     if (varValue.isInstanceOf[BigDecimal])  {   // bind a BigDecimal value
       var dv = varValue.asInstanceOf[BigDecimal]
       scalaExec.Interpreter.GlobalValues.globalInterpreter.bind(varName, dv)
      }
      else if (varValue.isInstanceOf[Int]) {  // bind an Int value
        var di = varValue.asInstanceOf[Int]
        scalaExec.Interpreter.GlobalValues.globalInterpreter.bind(varName, di)
      } 
      else if (varValue.isInstanceOf[Long]) {  // bind a Long value
        var dl = varValue.asInstanceOf[Long]
        scalaExec.Interpreter.GlobalValues.globalInterpreter.bind(varName, dl)
      } 
      else if (varValue.isInstanceOf[Short]) {  // bind a Short value
        var ds = varValue.asInstanceOf[Short]
        scalaExec.Interpreter.GlobalValues.globalInterpreter.bind(varName, ds)
      } 
      else if (varValue.isInstanceOf[Char]) {  // bind a Char value
        var dc = varValue.asInstanceOf[Char]
        scalaExec.Interpreter.GlobalValues.globalInterpreter.bind(varName, dc)
      } 
      else if (varValue.isInstanceOf[Array[Double]]) {
        scalaExec.Interpreter.GlobalValues.doubleArrayValForBinding = (varValue.asInstanceOf[Array[Double]]).clone
        var evStr = "var "+varName+" =  scalaExec.Interpreter.GlobalValues.doubleArrayValForBinding "
        scalaExec.Interpreter.GlobalValues.globalInterpreter.eval(evStr)
      }
      else if (varValue.isInstanceOf[Array[Array[Double]]]) {
        scalaExec.Interpreter.GlobalValues.double2DArrayValForBinding = (varValue.asInstanceOf[Array[Array[Double]]]).clone
        var evStr = "var "+varName+" =  scalaExec.Interpreter.GlobalValues.double2DArrayValForBinding "
        scalaExec.Interpreter.GlobalValues.globalInterpreter.eval(evStr)
      }
      // convert a GroovySci Matrix to a ScalaSci RichDouble2DArray
      else if (varValue.isInstanceOf[groovySci.math.array.Matrix]) {
        var array2DRepr = (varValue.asInstanceOf[groovySci.math.array.Matrix]).getArray
        scalaExec.Interpreter.GlobalValues.groovySciMatrixForBinding = new scalaSci.RichDouble2DArray(array2DRepr)
        var evStr = "var "+varName+" =  scalaExec.Interpreter.GlobalValues.groovySciMatrixForBinding "
        scalaExec.Interpreter.GlobalValues.globalInterpreter.eval(evStr)
      }
      // convert a GroovySci Vec to a ScalaSci Vec
      else  if (varValue.isInstanceOf[groovySci.math.array.Vec])  {
        var array1DRepr = (varValue.asInstanceOf[groovySci.math.array.Vec]).getv
        scalaExec.Interpreter.GlobalValues.groovySciVecForBinding  = new scalaSci.Vec(array1DRepr)
        var  evStr = "var "+varName+" =  scalaExec.Interpreter.GlobalValues.groovySciVecForBinding "
        scalaExec.Interpreter.GlobalValues.globalInterpreter.eval(evStr)
      }                                                                                     
      else  if (varValue.isInstanceOf[scalaExec.Interpreter.MatlabComplex])  {
        scalaExec.Interpreter.GlobalValues.matlabComplexForBinding = (varValue.asInstanceOf[scalaExec.Interpreter.MatlabComplex])
        var evStr = "var "+varName+" =  scalaExec.Interpreter.GlobalValues.matlabComplexForBinding "
        scalaExec.Interpreter.GlobalValues.globalInterpreter.eval(evStr)
      }
      else 
         scalaExec.Interpreter.GlobalValues.globalInterpreter.bind(varName, varValue)
   }
}


