/**
* Author: Isaac Newell
* Description: Superclass for all the specific solving methods. Primarily for handling IO.
*/

import java.util.Scanner;

public abstract class Solver {
  private String outFileName;
  public static final double ERROR_THRESHOLD = Math.pow(10,-8);

  public String getOutFileName() {
    return outFileName;
  }

  public abstract Node[] solve(Polynomial problem, boolean verbose);

  public Node[] solve(Polynomial problem) {
    return solve(problem, false);
  }

  public static boolean isEquiv(Node n, Complex c) {
    Complex numN = n.evaluate();
    double[] vals = {numN.getRe(), numN.getIm()};

    for (int i = 0; i < vals.length; i++) {
      double test = vals[i];
      double actual = (i==0)? c.getRe() : c.getIm();
      if (Math.abs(test-actual) > Solver.ERROR_THRESHOLD) {
        // System.out.println("2525252525   " + n + " FALSE");
        return false;
      }
    }
    // System.out.println("333333333   " + n + " TRUE");
    return true;
  }

  public static boolean isEquiv(Node n, double d) {
    return isEquiv(n,new Complex(d,0));
  }

  public static boolean isEquiv(Node n, int i) {
    return isEquiv(n,new Complex(i,0));
  }

  public void runInputStream() {
    Scanner kb = new Scanner(System.in);

    while (true) {
      System.out.println("POLYNOMIAL SOLVER");
      System.out.println("Enter rational coefficients, separated by commas.");
      System.out.println("Put the coefficients in order from a_0 to a_n.");
      System.out.println("Use '/' to denote fractions.");

      String inp = kb.nextLine();
      String[] cfs = inp.replaceAll("\\s+", "").split(",");
      Node[] c1 = new Node[cfs.length];
      for (int i = 0; i < cfs.length; i++) {
        c1[i] = new Node(new Fraction(cfs[i]));
      }

      System.out.println("Enter the output file name (should be .txt): ");
      this.outFileName = kb.nextLine();

      Polynomial pol = new Polynomial(c1);
      Node[] solutions = this.solve(pol, true);


      System.out.println();
      System.out.println("Solve another polynomial? (y/n)");
      String res = kb.nextLine();
      if (res.equals("y")) {
        continue;
      } else if (res.equals("n")) {
        break;
      }
    }
  }
}
