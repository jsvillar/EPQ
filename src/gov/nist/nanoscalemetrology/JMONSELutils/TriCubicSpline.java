/**
 * Class for performing an interpolation on the tabulated function y =
 * f(x1,x2,x3) using a natural tricubic spline Assumes second derivatives at end
 * points = 0 (natural spine) </p>
 * <p>
 * This version is based heavily upon the CubicSpline class of Dr. Michael
 * Thomas Flanagan, May 2002, updated 29 April 2005, 17 February 2006, 21
 * September 2006, with minor modifications by John Villarrubia.
 * </p>
 * <p>
 * Copyright: Flanagan's original code was accompanied by this copyright notice:
 * "Permission to use, copy and modify this software and its documentation for
 * NON-COMMERCIAL purposes is granted, without fee, provided that an
 * acknowledgement to the author, Michael Thomas Flanagan at
 * www.ee.ucl.ac.uk/~mflanaga, appears in all copies. Dr Michael Thomas Flanagan
 * makes no representations about the suitability or fitness of the software for
 * any or for a particular purpose. Michael Thomas Flanagan shall not be liable
 * for any damages suffered as a result of using, modifying or distributing this
 * software or its derivatives." Modifications by John Villarrubia, are,
 * pursuant to title 17 Section 105 of the United States Code, not subject to
 * copyright protection and are in the public domain.
 * </p>
 * 
 * @author Dr Michael Thomas Flanagan, modified by Dr John Villarrubia
 * @version 1.0
 */

// Modification history:
// 7/30/2007
package gov.nist.nanoscalemetrology.JMONSELutils;

public class TriCubicSpline {

   private int nPoints = 0; // no. of x1 tabulated points
   private int mPoints = 0; // no. of x2 tabulated points
   private int lPoints = 0; // no. of x3 tabulated points
   private double[][][] y = null; // y=f(x1,x2) tabulated function
   private double[] x1 = null; // x1 in tabulated function f(x1,x2,x3)
   private double[] x2 = null; // x2 in tabulated function f(x1,x2,x3)
   private double[] x3 = null; // x3 in tabulated function f(x1,x2,x3)
   private BiCubicSpline[] bcsn = null;// nPoints array of BiCubicSpline
   // instances
   private CubicSpline csm = null; // CubicSpline instance
   private double[][][] d2ydx2inner = null; // inner matrix of second
   // derivatives
   private boolean derivCalculated = false; // = true when the called bicubic

   // spline derivatives have been
   // calculated

   // Constructor
   public TriCubicSpline(double[] x1, double[] x2, double[] x3, double[][][] y) {
      this.nPoints = x1.length;
      this.mPoints = x2.length;
      this.lPoints = x3.length;
      if(this.nPoints != y.length)
         throw new IllegalArgumentException("Arrays x1 and y-row are of different length " + this.nPoints + " " + y.length);
      if(this.mPoints != y[0].length)
         throw new IllegalArgumentException("Arrays x2 and y-column are of different length " + this.mPoints + " "
               + y[0].length);
      if(this.lPoints != y[0][0].length)
         throw new IllegalArgumentException("Arrays x3 and y-column are of different length " + this.mPoints + " "
               + y[0][0].length);
      if((this.nPoints < 3) || (this.mPoints < 3) || (this.lPoints < 3))
         throw new IllegalArgumentException("The tabulated 3D array must have a minimum size of 3 X 3 X 3");

      this.csm = new CubicSpline(this.nPoints);
      this.bcsn = BiCubicSpline.oneDarray(this.nPoints, this.mPoints, this.lPoints);
      this.x1 = new double[this.nPoints];
      this.x2 = new double[this.mPoints];
      this.x3 = new double[this.lPoints];
      this.y = new double[this.nPoints][this.mPoints][this.lPoints];
      this.d2ydx2inner = new double[this.nPoints][this.mPoints][this.lPoints];
      for(int i = 0; i < this.nPoints; i++)
         this.x1[i] = x1[i];
      for(int j = 0; j < this.mPoints; j++)
         this.x2[j] = x2[j];
      for(int j = 0; j < this.lPoints; j++)
         this.x3[j] = x3[j];
      for(int i = 0; i < this.nPoints; i++)
         for(int j = 0; j < this.mPoints; j++)
            for(int k = 0; k < this.lPoints; k++)
               this.y[i][j][k] = y[i][j][k];
   }

   // Constructor with data arrays initialised to zero
   // Primarily for use by QuadriCubicSpline
   public TriCubicSpline(int nP, int mP, int lP) {
      this.nPoints = nP;
      this.mPoints = mP;
      this.lPoints = lP;
      if((this.nPoints < 3) || (this.mPoints < 3) || (this.lPoints < 3))
         throw new IllegalArgumentException("The data matrix must have a minimum size of 3 X 3 X 3");

      this.csm = new CubicSpline(this.nPoints);
      this.bcsn = BiCubicSpline.oneDarray(this.nPoints, this.mPoints, this.lPoints);
      this.x1 = new double[this.nPoints];
      this.x2 = new double[this.mPoints];
      this.x3 = new double[this.lPoints];
      this.y = new double[this.nPoints][this.mPoints][this.lPoints];
      this.d2ydx2inner = new double[this.nPoints][this.mPoints][this.lPoints];
   }

   // Returns a new TriCubicSpline setting internal array size to nP x mP x lP
   // and all array values to zero with natural spline default
   // Primarily for use in this.oneDarray for QuadriCubicSpline
   public static TriCubicSpline zero(int nP, int mP, int lP) {
      if((nP < 3) || (mP < 3) || (lP < 3))
         throw new IllegalArgumentException("A minimum of three x three x three data points is needed");
      final TriCubicSpline aa = new TriCubicSpline(nP, mP, lP);
      return aa;
   }

   // Create a one dimensional array of TriCubicSpline objects of length nP each
   // of internal array size mP x lP xkP
   // Primarily for use in quadriCubicSpline
   public static TriCubicSpline[] oneDarray(int nP, int mP, int lP, int kP) {
      if((mP < 3) || (lP < 3) || (kP < 3))
         throw new IllegalArgumentException("A minimum of three x three x three data points is needed");
      final TriCubicSpline[] a = new TriCubicSpline[nP];
      for(int i = 0; i < nP; i++)
         a[i] = TriCubicSpline.zero(mP, lP, kP);
      return a;
   }

   // METHODS
   // Resets the x1, x2, x3, y data arrays
   // Primarily for use in QuadriCubicSpline
   public void resetData(double[] x1, double[] x2, double[] x3, double[][][] y) {
      if(x1.length != y.length)
         throw new IllegalArgumentException("Arrays x1 and y row are of different length");
      if(x2.length != y[0].length)
         throw new IllegalArgumentException("Arrays x2 and y column are of different length");
      if(x3.length != y[0][0].length)
         throw new IllegalArgumentException("Arrays x3 and y column are of different length");
      if(this.nPoints != x1.length)
         throw new IllegalArgumentException("Original array length not matched by new array length");
      if(this.mPoints != x2.length)
         throw new IllegalArgumentException("Original array length not matched by new array length");
      if(this.lPoints != x3.length)
         throw new IllegalArgumentException("Original array length not matched by new array length");

      for(int i = 0; i < this.nPoints; i++)
         this.x1[i] = x1[i];

      for(int i = 0; i < this.mPoints; i++)
         this.x2[i] = x2[i];

      for(int i = 0; i < this.lPoints; i++)
         this.x3[i] = x3[i];

      for(int i = 0; i < this.nPoints; i++)
         for(int j = 0; j < this.mPoints; j++)
            for(int k = 0; k < this.lPoints; k++)
               this.y[i][j][k] = y[i][j][k];
   }

   // Returns an interpolated value of y for values of x1, x2 and x3
   // from a tabulated function y=f(x1,x2,x3)
   public double interpolate(double xx1, double xx2, double xx3) {

      final double[][] yTempml = new double[this.mPoints][this.lPoints];
      for(int i = 0; i < this.nPoints; i++) {
         for(int j = 0; j < this.mPoints; j++)
            for(int k = 0; k < this.lPoints; k++)
               yTempml[j][k] = y[i][j][k];
         this.bcsn[i].resetData(x2, x3, yTempml);
      }
      final double[] yTempm = new double[nPoints];

      for(int i = 0; i < nPoints; i++) {
         if(this.derivCalculated)
            this.bcsn[i].setDeriv(this.d2ydx2inner[i]);
         yTempm[i] = this.bcsn[i].interpolate(xx2, xx3);
         if(!this.derivCalculated)
            this.d2ydx2inner[i] = this.bcsn[i].getDeriv();
      }
      derivCalculated = true;

      this.csm.resetData(x1, yTempm);

      return this.csm.interpolate(xx1);
   }

   // Get inner matrix of derivatives
   // Primarily used by QuadriCubicSpline
   public double[][][] getDeriv() {
      return this.d2ydx2inner;
   }

   // Set inner matrix of derivatives
   // Primarily used by QuadriCubicSpline
   public void setDeriv(double[][][] d2ydx2) {
      this.d2ydx2inner = d2ydx2;
      this.derivCalculated = true;
   }
}
