

package scalaSci.jeigen

import scalaSci.math.array.DoubleArray
import java.util._
import Jama._

import jeigen.DenseMatrix
import jeigen.Shortcuts._

import scalaSci.Vec
import scalaSci.RichDouble1DArray
import scalaSci.RichDouble2DArray
import scalaSci.Matrix
//import collection.immutable.Vector


// this class provides Matrix operations, using zero-indexed values
class Mat( var dm: jeigen.DenseMatrix)   extends AnyRef with scalaSci.scalaSciMatrix[scalaSci.jeigen.Mat] 
{ 
// getters for size
  var  Nrows =  dm.rows // keeps  the number of rows of the Matrix
  var  Ncols =  dm.cols   //  keeps  the number of columns of the Matrix
     // the default constructor allocates a double array of size n x m
     // therefore, the representation of the data is a two-dimensional Java double array
  
  def  v = getv
  var v1d = dm.values
//Array.ofDim[Double](Nrows, Ncols)
  
  final def numRows() = Nrows
  final def numColumns() = Ncols
  final def length() = Nrows*Ncols
  final def size() = (Nrows, Ncols)
  
  // getv() returns the data representation 
  final def getv1d() = {
    v
}

  // the scalaSci.Mat does not wrap a Matrix class of a specific library, thus return simply the data representation  
final def getNativeMatrixRef() =   v // the scalaSci.Mat does not wrap a Matrix class of a specific library, thus return simply the data representation
final def matFromNative() = new Mat(v)
final def matFromNative(v: Array[Array[Double]]) = new Mat(v)
  
//final def  setv(values: Array[Array[Double]], n: Int, m: Int) = { v = values; Nrows = n; Ncols = m }

import Mat._
  
final def  set(row: Int, column: Int, value: Double) =   {  v(row)(column) = value }

final def  get(row: Int, column: Int) = v(row)(column)

  final def this(Nrows: Int, Ncols: Int) = this(new DenseMatrix(Nrows, Ncols)) 
  final def this(tuple: (Int, Int)) = this(tuple._1,  tuple._2) 

//final def this(v: Array[Double]) = this(new jeigen.DenseMatrix(v))

  final def this(vals: Array[Array[Double]]) = {
     this(vals.length, vals(0).length)  // allocate memory with the final default constructor
    var i=0; var j=0
     while (i<vals.length) {
        j=0
       while (j<vals(0).length) {
          dm.set(i, j, vals(i)(j))
          j+=1
        }
        i+=1
     }
 }

final def this(v: Double) = this(new jeigen.DenseMatrix(Array(Array(v))))  
  
final def this(m: Matrix) = 
        this(m.Nrows, m.Ncols)
    

final def getv = {
  var Nrows = dm.rows; var Ncols = dm.cols
  var dd = Array.ofDim[Double](Nrows, Ncols)
  var r=0; var c = 0
  while (r < Nrows) {
    c = 0
    while (c<Ncols)
      {
        dd(r)(c) = dm.get(r, c)
        c += 1
      }
      r += 1
  }
  dd
}
      
  
 
// indexes the corresponding Matrix element without updating automatically sizes for fast access
  final def apply(n: Int, m: Int): Double = {
        dm.get(n, m)
 }

  final def update(n: Int, m: Int, v: Double) = 
    dm.set(n, m, v)
  
  final def apply(n: Int) = {
      var nr = n/Ncols
      var nc = n - nr*Nrows
      v(nr)(nc)
    }
  
  
  
// construct a Matrix from a zero-indexed double [][] array

  
final def this(jm: jMatrix) = {
  this( jm.getArray.length, jm.getArray()(0).length)
    var i=0; var j=0
    var vals = jm.getArray
    while (i<vals.length) {
        j=0
        while (j<vals(0).length) {
       v(i)(j) = vals(i)(j)
       j+=1
        }
        i+=1
     }
}

final def this(Nrows: Int, Ncols: Int, df: Double) = {   // initializes to a default value
     this(Nrows, Ncols)  // allocate memory with the default constructor
    var i=0; var j=0
    while (i<v.length) {
        j=0
        while (j<v(1).length) {
       v(i)(j) = df
       j+=1
        }
        i+=1
     }
 }

    // construct a Matrix from an one-indexed double [][] array
final def this(vals: Array[Array[Double]], flag: Boolean) = {
    this(1,1)
    Nrows = vals.length
    Ncols = vals(0).length
 }

  // construct a Matrix from a one-indexed double [] array
final def this( vals: Array[Double]) = {
    this(1, vals.length)   // keep the array as a row of the Matrix
    var i=0
    while (i<vals.length)  {
       v(0)(i) = vals(i)
       i+=1
   }
  }
  
  /* e.g.
var v = rand0(3,3)
var vc = v.clone
*/
  override final def clone() = {
    var a = new Mat(this)
    a
  }

  /* e.g.
var v = rand0(3,3)
var vc = v.copy
*/
  final def copy() = {  // same as clone()
    clone()
  }

  // copy to a new matrix, perhaps resizing also matrix
  /* e.g.
var v = rand0(3,3)
var vc = v.copy(4, 6)
*/

  final def copy(newNrows: Int, newNcols: Int)  =  {
    var cpMat = new Mat(newNrows, newNcols)   // create a new Matrix 
    val mnNrows = if (newNrows < Nrows)  newNrows else Nrows
    val mnNcols = if (newNcols < Ncols)   newNcols else Ncols
      // copy the original matrix within
    var r = 0; var c = 0
    while (r < mnNrows) {
      c = 0
      while (c < mnNcols) {
        cpMat(r, c) = this(r, c)
        c += 1
      }
      r += 1
    }
    cpMat
    
  }
  
    // clone a Matrix from another Matrix
final def this(a: Mat) = {
     this(a.Nrows, a.Ncols)  // allocate memory with the  default constructor
    var i = 0; var j = 0
    while (i<a.Nrows) {
        j = 0
        while (j<a.Ncols) {
       v(i)(j) = a.v(i)(j)
       j += 1
        }
        i += 1
     }
 }
    
  
override  final def  toString(): String = {
    scalaSci.RichDouble2DArray.printArray(this.toDoubleArray())
  }
  

  
// Mat * Mat, e.g.
 /* 
var v34 = ones0(3, 4)
var v45 = ones0(4, 5)
var vmul = v34 * v45
 */
 final def * (that: Mat): Mat =  {
   new Mat(this.dm.mmul(that.dm))
 }
 
    /*
    var  rN = this.Nrows;   var rM = this.Ncols;
   var  sN = that.Nrows;  var sM = that.Ncols;
   
   var  v1Colj = new Array[Double](rM)
   var result = new Mat(this.Nrows, that.Ncols)
   var j=0; var k=0;
   while (j < sM)  {
       k=0
      while  (k < rM) {
        v1Colj(k) =that(k, j)
        k += 1
      }

      var i=0;
      while (i<rN) {
        var   Arowi = this.v(i)
        var   s = 0.0;
        k=0
        while (k< rM) {
          s += Arowi(k)*v1Colj(k)
          k += 1
        }
      result(i, j) = s;
      i += 1
      }
 j += 1
   }
 
   
  return result
  }
 */ 
  
// Mat * RichDouble2DArray, e.g.
  /* 
var v34 = ones0(3, 4)
var v45 = ones(4, 5)
var vmul = v34 * v45
   */
 override final def * (that: RichDouble2DArray): Mat =  {
   var  rN = this.Nrows;   var rM = this.Ncols;
   var  sN = that.Nrows;  var sM = that.Ncols;
   
   var  v1Colj = new Array[Double](rM)
   var result = new Mat(this.Nrows, that.Ncols)
   var j=0; var k=0;
   while (j < sM)  {
       k=0
      while  (k < rM) {
        v1Colj(k) =that(k, j)
        k += 1
      }

      var i=0;
      while (i<rN) {
        var   Arowi = this.v(i)
        var   s = 0.0;
        k=0
        while (k< rM) {
          s += Arowi(k)*v1Colj(k)
          k += 1
        }
      result(i, j) = s;
      i += 1
      }
 j += 1
   }
  return result
  }
    

  // solve the system using JLAPACK for overdetermined/undetermined cases
  /* e.g.
  
var A = M0("2.3 0.3 -1.2; 0.2 -0.1 2.1; 0.4 -0.2 0.9")
var b = M0("2 3.2 0.1; -0.9 0.3 -0.3")
b = b~   // tranpose since column vectors  correspond to individual right hand side parts
var solution = A.solve(b)
var x0 = solution(::, 0)   // take solution for first b
// test now
var shouldBeZero0 = A*x0 - b(::, 0)
var x1 = solution(::, 1)  // take solution for second b
var shoulsBeZero1 = A*x1 - b(::, 1)
   
// overdetermined case
var Aoverdetermined = M0("2.3 0.3 -1.2; 0.2 -0.1 2.1; 0.4 -0.2 0.9; 0.4 0.5 -0.4")
var bo = M0("2 3.2 0.1 7")
bo = bo~   // tranpose since column vectors  correspond to individual right hand side parts
var solutionOverdetermined = Aoverdetermined.solve(bo)
   */
  final def solve(b: Mat) =  {
    if (b.numRows() == b.numColumns)  // direct solve
       new Mat(scalaSci.math.LinearAlgebra.LinearAlgebra.solve( this.v, b.v))
   else  // overdetermined/underdetermined case
     new Mat(scalaSci.ILapack.DGELS(this toDoubleArray(), b.toDoubleArray()))
  }
  
  final def \(b: Mat) =  solve(b)     // like Matlab's backslash

  
  // slash or right matrix divide
final def /(B: Mat) = this * B.inv()
  
final def /(B: RichDouble2DArray) = this * B.inv()

final def /(B: Array[Array[Double]]) = this * (new RichDouble2DArray(B)).inv()

final def /(B: RichDouble1DArray) = this *(new RichDouble2DArray(B)).inv()

final def /(B: Array[Double]) = this * (new RichDouble2DArray(B)).inv()

  final def  det() =   scalaSci.math.LinearAlgebra.LinearAlgebra.det(v)
  
  final def trace() = scalaSci.math.LinearAlgebra.LinearAlgebra.trace(v)
  
  final def inv() = new Mat(scalaSci.math.LinearAlgebra.LinearAlgebra.inverse(v)) 
  
  final def pinv(m: scalaSci.Mat): scalaSci.Mat =   m.pinv()

  final def cond() = scalaSci.math.LinearAlgebra.LinearAlgebra.cond(v)
 
 final def rank() =  scalaSci.math.LinearAlgebra.LinearAlgebra.rank(v)
 
  
  final def eig() = {
 // construct an MTJ Matrix
  var mtjMat = new scalaSci.MTJ.Mat(this.toDoubleArray)
  mtjMat.eig()
}

 final def svd() = {
   var S  = scalaSci.ILapack.svd(this.toDoubleArray)
   (new scalaSci.RichDouble2DArray(S._1), new scalaSci.RichDouble1DArray(S._2),  new scalaSci.RichDouble2DArray(S._3))
  }
  
  final def svde() = {
    var S = dm.svd
    S
  }

  final def pinv() =  {
    val ejmlM = new scalaSci.EJML.Mat(this.getv)
    val pejml = ejmlM.pinv
    val nrows = pejml.Nrows
    val ncols = pejml.Ncols
    var pM = new Mat(nrows, ncols)
    for (n<-0 until nrows)
      for (m<-0 until ncols)
        pM(n, m) = pejml(n, m)
    pM
  }
  
  // Reduced-Row Echelon form
  final def rref() = {
    var xd = this.toDoubleArray()
    var exd  = new org.ejml.data.DenseMatrix64F(xd)
    
    var reduced = org.ejml.ops.CommonOps.rref(exd, -1, null)
    new Mat(scalaSci.EJML.StaticMathsEJML.DenseMatrixToDoubleArray(reduced))
    
  }
 } 


// Mat's companion object
  object Mat  {

 var digitsPrecision = 4  // controls pecision in toString()  
 var mxRowsToDisplay = 6  
 var mxColsToDisplay = 6
 
  final def setDigitsPrecision(n: Int) = {digitsPrecision = n}
  final def setRowsToDisplay(nrows: Int) = {mxRowsToDisplay = nrows }
  final def setColsToDisplay(ncols: Int) = {mxColsToDisplay = ncols }
  
  
 
  // a conveniency constructor that allows to construct a matrix e.g. as
  //   var x = Mat(3,7) instead of  var x = new Mat(3, 7)
 final def apply(nrows: Int, ncols: Int) = new Mat(nrows, ncols) 
  
    /* e.g.
var xx = 3.4
var a = Mat( 2, 4,
   3.4, 5.6, -6.7, -xx,
   -6.1,  2.4, -0.5, cos(0.45*xx)) 
*/    

  final def apply(values: Double*)  = {
    val   nrows = values(0).toInt  //   number of rows
    val   ncols = values(1).toInt   // number of cols
    val   dvalues = values.toArray
    var   cpos = 2  // current position in array
    var   sm = new Mat( nrows, ncols)  // create a Mat
    for (r<-0 until nrows)
      for (c<-0 until ncols)
         {
           sm(r, c) = values(cpos)  // copy value
           cpos += 1
         }

    sm  // return the constructed matrix

  }
  
final def   $( values :Any*) = {
    // count number of nulls, number of nulls will be the number of rows 
    var nullCnt = 0
    for (v <- values)  
       if (v == null) nullCnt+=1

    // count number of columns
     var colCnt = 0
     var vl = values.length
     while (colCnt < vl && values(colCnt) != null)
       colCnt += 1
       
        var rowCnt = nullCnt+1  // number of rows iof the new Matrix
        
        // take the first element.
        // It can be either a Matrix or a double number
        var vv = values(0) 
     if (vv.isInstanceOf[scalaSci.scalaSciMatrix[Any]]) { // we synthesize our Matrix from Matrices
           
           // take parameters of the submatrices
         var vv0 = vv.asInstanceOf[scalaSci.scalaSciMatrix[Any]]
         var nrowsSubm = vv0.numRows()
         var ncolsSubm = vv0.numColumns()
         
     // construct the new Matrix
   var nm = new Mat(rowCnt*nrowsSubm, colCnt*ncolsSubm)
   var cpos = 0
   for (r<-0 until rowCnt)
     for (c<-0 until colCnt)
         {
        var cv = values(cpos)
        if (cv == null) cpos+=1
        cv = values(cpos)
        
        var crow = r*nrowsSubm
        var ccol = c*ncolsSubm
              
              cv match {
            case null => 
            case v: scalaSci.scalaSciMatrix[Any] =>
            for ( rs <- 0 until nrowsSubm) 
              for (cs <- 0 until ncolsSubm)
                 nm(crow+rs, ccol+cs) = v(rs, cs)
                 
             case _ => 
             }
                 
         cpos += 1  // next element
         }   
         nm
         }
         else {

     // construct the new Matrix
      var nm = new Mat(rowCnt, colCnt)
   var cpos = 0
   for (r<-0 until rowCnt)
     for (c<-0 until colCnt)
         {
        var cv = values(cpos)
        if (cv == null) cpos+=1
        cv = values(cpos)
        cv match {
            case null => 
            case v: Int => nm(r, c) =  v
            case v: Double => nm(r, c) = v
            case _ =>
           }
                     
      cpos += 1
      }
         
     nm                         
     }
    }
    
  

}

