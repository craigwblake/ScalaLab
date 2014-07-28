package scalaSci;

import static scalaSci.PrintFormatParams.*;
        
// for accumulating eigenanalysis results in JLAPACK related routines

public class EigResults {
  public double [] realEvs;
  public  double [] imEvs;
  public  double [][] leftEvecs;
  public double [][] rightEvecs;
  
  public final int  length() { return realEvs.length+imEvs.length+2*(leftEvecs.length*leftEvecs[0].length); }
  public final int  size() { return length(); }
  

  
  @Override
  public  String toString() {
      StringBuilder sb = new StringBuilder();
      String realEvsStr = "real eigenvalues: "+ printArray(realEvs);
      String imEvsStr = "imaginary eigenvalues: "+ printArray(realEvs);
      String leftEvecsStr = "left eigenvectors: "+ printArray(leftEvecs);
      String rightEvecsStr = "right eigenvectors: "+printArray(rightEvecs);
      
      sb.append(realEvsStr); sb.append(imEvsStr); sb.append(leftEvecsStr); sb.append(rightEvecsStr);
      return sb.toString();
  }
  
}
