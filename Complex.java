/**
* Author: Isaac Newell
* Description: Implements complex numbers and their associated math.
*/

import java.lang.Math;

public class Complex {
  private double a;
  private double b;
  private static final int numDecimals = 4;

  public Complex(double a, double b) {
    this.a = a;
    this.b = b;
  }

  public Complex(int a, int b) {
    this.a = (double)a;
    this.b = (double)b;
  }

  public Complex(double a) {
    this(a,0);
  }

  public Complex(int a) {
    this((double)a, 0);
  }

  public double getRe() {
    return a;
  }

  public double getIm() {
    return b;
  }

  public Complex conjugate() {
    return new Complex(a, -b);
  }

  public Complex add(Complex other) {
    return new Complex(a + other.a, b + other.b);
  }

  public Complex multiply(Complex other) {
    return new Complex(a*other.a-b*other.b, b*other.a+a*other.b); //(a+bi)(c+di) = ac-bd + (bc+ad)i
  }

  public double magnitude() {
    return Math.sqrt(a*a + b*b);
  }

  // Gets the argument of the number in polar form.
  public double theta() {
    // System.out.println(a + ", " + b);
    if (Math.abs(a) < Solver.ERROR_THRESHOLD) {
      if (b < 0) {
        return -Math.PI/2;
      }
      else {
        return Math.PI/2;
      }
    }
    if (Math.abs(b) < Solver.ERROR_THRESHOLD) {
      //System.out.println("NO B");
      if (a < 0) {
        //System.out.println("RETURNING PI");
        return Math.PI;
      }
      else {
        return 0;
      }
    }
    return Math.atan(b/a);
  }

  // Raises a number to the n power.
  public Complex power(int n) {
    if (Math.abs(b) < Solver.ERROR_THRESHOLD) {
      //real
      return new Complex(Math.pow(a,n),0);
    }
    double r = magnitude();
    double t = theta();

    double newR = Math.pow(r,n);
    double newT = t * n;

    return new Complex(newR*Math.cos(newT), newR*Math.sin(newT));
  }

  // Takes the nth root of a complex number, using De Moivre's Theorem.
  public Complex radical(int n) {
    if (Math.abs(b) < Solver.ERROR_THRESHOLD) {
      //real
      //System.out.println("REAL");
      if (a > 0) return new Complex(Math.pow(a,1.0/n));
      else {
        if (n % 2 == 1) {
          return new Complex(-Math.pow(-a,1.0/n));
        }
      }
    }
    double r = magnitude();
    double t = theta();

    double newR = Math.pow(r,1.0/n);
    double newT = t / n;

    double inc = 2*Math.PI/n;

    if (newT > 0) {
      for (int i = 0 ; i < n; i++) {
        if (newT - inc > 0) {
          newT -= inc;
        }
        else {
          break;
        }
      }
    }

    else if (newT < 0) {
      for (int i = 0 ; i < n; i++) {
        if (newT + inc < 0) {
          newT += inc;
        }
        else {
          break;
        }
      }
    }

    return new Complex(newR*Math.cos(newT), newR*Math.sin(newT));
  }

  public static double round(double d) {
    return Math.round(d*Math.pow(10,numDecimals))/(Math.pow(10,numDecimals));
  }

  public String toString() {
    String s = "";
    boolean showedRe = false;
    if (Math.abs(a) > Math.pow(10,-numDecimals)) {
      s += round(a);
      showedRe = true;
    }
    if (Math.abs(b) > Math.pow(10,-numDecimals)) {
      if (showedRe) {
        if (b > 0) {
          s += " + ";
        }
      }
      s += round(b) + " i";
    }
    if (s.equals("")) {
      return "0";
    }
    return s;
  }

  public String toLatex() {
    return toString();
  }
}
