/**
* Author: Isaac Newell
* Description: Solves the cubic with Lagrange's method.
*/

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.Math;

public class LagrangeCubic extends Solver {
  public Node[] solve(Polynomial problem, boolean verbose) {
    Node[] cfs = problem.getCoeffs();

    Node sigma1 = Node.performOp(new Node[] {new Node(-1),cfs[2]},"times",true);
    Node sigma2 = cfs[1].clone();
    Node sigma3 = Node.performOp(new Node[] {new Node(-1),cfs[0]},"times",true);

    Node twosigma13 = Node.performOp(new Node[] {new Node(2), Node.performOp(sigma1,"power_3",true)}, "times", true);
    Node minus9sigma1sigma2 = Node.performOp(new Node[] {new Node(-9),sigma1,sigma2}, "times", true);
    Node twenty7sigma3 = Node.performOp(new Node[] {new Node(27),sigma3}, "times", true);

    Node bigS = Node.performOp(new Node[] {twosigma13,minus9sigma1sigma2,twenty7sigma3}, "plus", true);

    Node sigma16 = Node.performOp(sigma1,"power_6",true);
    Node minus9sigma14sigma2 = Node.performOp(new Node[] {new Node(-9), Node.performOp(sigma1,"power_4",true), sigma2}, "times", true);
    Node twenty7sigma12sigma22 = Node.performOp(new Node[] {new Node(27), Node.performOp(sigma1,"power_2",true),Node.performOp(sigma2,"power_2",true)},"times",true);
    Node minus27sigma23 = Node.performOp(new Node[] {new Node(-27), Node.performOp(sigma2,"power_3",true)},"times",true);

    Node bigP = Node.performOp(new Node[] {sigma16,minus9sigma14sigma2,twenty7sigma12sigma22,minus27sigma23}, "plus", true);

    Node[] quadcoeffs = new Node[3];
    quadcoeffs[0] = bigP.clone();
    quadcoeffs[1] = Node.performOp(new Node[] {new Node(-1), bigS}, "times", true);
    quadcoeffs[2] = new Node(1);
    Polynomial resolvent = new Polynomial(quadcoeffs);
    Quadratic quadsolver = new Quadratic();
    Node[] quadsols = quadsolver.solve(resolvent);

    Node u3 = quadsols[0];
    Node v3 = quadsols[1];

    Node u = Node.performOp(u3,"radical_3",true);
    Node v = Node.performOp(v3,"radical_3",true);

    Node zetaRe = new Node(new Fraction(-1,2));
    Node zetaIm = Node.performOp(new Node[] {Node.performOp(new Node(3), "radical_2", true), new Node(new Fraction(1,2))},
                                "times",
                                true);
    Node zetaImi = Node.performOp(new Node[] {zetaIm, new Node("i")}, "times", true);
    Node zeta = Node.performOp(new Node[] {zetaRe, zetaImi}, "plus", true);
    Node zeta2 = Node.performOp(zeta, "power_2", true);

    Node uzeta = Node.performOp(new Node[] {zeta, u}, "times", true);
    Node uzeta2 = Node.performOp(new Node[] {zeta2, u}, "times", true);
    Node vzeta = Node.performOp(new Node[] {zeta, v}, "times", true);
    Node vzeta2 = Node.performOp(new Node[] {zeta2, v}, "times", true);

    Node x1times3 = Node.performOp(new Node[] {sigma1,u,v},"plus",true);
    Node x2times3 = Node.performOp(new Node[] {sigma1,uzeta2,vzeta},"plus",true);
    Node x3times3 = Node.performOp(new Node[] {sigma1,uzeta,vzeta2},"plus",true);

    Node[] xs = new Node[3];
    xs[0] = Node.performOp(new Node[] {new Node(new Fraction(1,3)), x1times3}, "times", true);
    xs[1] = Node.performOp(new Node[] {new Node(new Fraction(1,3)), x2times3}, "times", true);
    xs[2] = Node.performOp(new Node[] {new Node(new Fraction(1,3)), x3times3}, "times", true);

    if (getOutFileName() != null) {
      try {
        FileWriter fw = new FileWriter(getOutFileName());
        PrintWriter pw = new PrintWriter(fw);

        pw.println("\\begin{example}");
        pw.println("Solve the cubic equation $" + problem.toLatex() + "=0$.");
        pw.println("\\end{example}");
        pw.println("\\justify");

        pw.print("We begin by using the relation between the coefficients of our cubic and the fundamental symmetric polynomials. ");
        pw.println("If we denote the roots of the cubic by $A$, $B$, and $C$, then we have:");
        pw.println("\\begin{align*}");
        pw.println("\\sigma_1 &= (A+B+C) &= -a_2 &= " + sigma1.toLatex() + "\\\\");
        pw.println("\\sigma_2 &= (AB + AC + BC) &= a_1 &= " + sigma2.toLatex() + "\\\\");
        pw.println("\\sigma_3 &= (ABC) &= -a_0 &= " + sigma3.toLatex());
        pw.println("\\end{align*}");

        pw.println("We now introduce the two auxiliary quantities $U$ and $V$:");
        pw.println("$$U = A + \\zeta B + \\zeta^2 C \\qquad\\text{and}\\qquad V = A + \\zeta^2 B + \\zeta C$$");
        pw.println("Once we find these quantities, we can find $A$, $B$, and $C$.");

        pw.println("Since $U^3 V^3$ and $U^3 + V^3$ are symmetric in $A$, $B$, and $C$, we can express them in terms ");
        pw.println("of the fundamental symmetric polynomials $\\sigma_1$, $\\sigma_2$, and $\\sigma_3$, by the Fundamental Theorem of Symmetric Polynomials. ");
        pw.println("That relation is given as follows: ");

        pw.println("\\begin{align*}");
        pw.println("U^3 + V^3 &= S &= 2\\sigma_1^3 -9\\sigma_1\\sigma_2 + 27\\sigma_3 &= " + bigS.toLatex() + "\\\\");
        pw.println("U^3 V^3 &= P &= \\sigma_1^6 -9\\sigma_1^4\\sigma_2 + 27\\sigma_1^2\\sigma_2^2 -27\\sigma_2^3 &= " + bigP.toLatex());
        pw.println("\\end{align*}");

        pw.println("By Vieta's formulas, $U^3$ and $V^3$ will be the roots of the following quadratic equation:");
        pw.println("$$z^2 - Sz + P = 0$$");
        pw.println("Using the quadratic formula to solve for $U^3$ and $V^3$, we get: ");
        pw.println("\\begin{align}");
        pw.println("U^3 &= " + u3.toLatex() + "&\\implies U &=" + u.toLatex() + "\\\\");
        pw.println("V^3 &= " + v3.toLatex() + "&\\implies V &=" + v.toLatex());
        pw.println("\\end{align}");

        pw.println("Now that we know $U$ and $V$, we can solve for the three roots according to:");
        pw.println("\\[");
        pw.println("\\begin{cases}");
        pw.println("A = \\frac{1}{3}\\left(\\sigma_1+U+V\\right)");
        pw.println("B = \\frac{1}{3}\\left(\\sigma_1+\\zeta^2 U + \\zeta V\\right)");
        pw.println("C = \\frac{1}{3}\\left(\\sigma_1+\\zeta U + \\zeta^2 V\\right)");
        pw.println("\\end{cases}");
        pw.println("\\]");

        pw.println("Thus our three solutions are:");
        pw.println("\\[");
        pw.println("\\begin{cases} ");
        String[] abc = {"A","B","C"};
        for (int i = 1; i <= 3; i++) {
          pw.println(abc[i-1]+" \\quad=\\quad " + xs[i-1].toLatex() + "\\\\");
        }
        pw.println("\\end{cases} ");
        pw.println("\\]");

        pw.close();
      } catch (IOException e) {

      }
    }

    return xs;
  }

  public static void main(String[] args) {
    (new LagrangeCubic()).runInputStream();
  }
}
