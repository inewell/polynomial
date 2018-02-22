/**
* Author: Isaac Newell
* Description: Implements Ferrari's method to solve quartics.
*/

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class Ferrari extends Solver {
  public Node[] solve(Polynomial problem, boolean verbose) {
    // Depress the quartic
    Polynomial dep4 = null;
    boolean needToDepress4 = true;
    Depress depress = new Depress(problem);

    Node c3 = problem.getCoeffs()[3];
    if (c3.getOp().equals("fraction")) {
      if (c3.getValue().getNum() == 0) {
        dep4 = problem;
        needToDepress4 = false;
      }
      else {
        dep4 = depress.depress();
      }
    }
    else {
      dep4 = depress.depress();
    }
    // This will hold the values of y
    Node[] ys = new Node[4];

    // Get the cubic equation for \alpha
    Node p = dep4.getCoeffs()[2];
    Node q = dep4.getCoeffs()[1];
    Node r = dep4.getCoeffs()[0];

    //-q^2/8, (p^2/4 - r), p, 1
    Node[] cubCoeffs = new Node[4];

    cubCoeffs[0] = Node.performOp(new Node[] {Node.performOp(q, "power_2", true), new Node(new Fraction(-1,8))},
                                  "times",
                                  true);
    Node p2over4 = Node.performOp(new Node[] {Node.performOp(p, "power_2", true), new Node(new Fraction(1,4))},
                          "times",
                          true);
    Node minusr = Node.performOp(new Node[] {r, new Node(-1)}, "times", true);

    cubCoeffs[1] = Node.performOp(new Node[] {p2over4, minusr}, "plus", true);

    cubCoeffs[2] = p;

    cubCoeffs[3] = new Node(1);

    Polynomial cubic = new Polynomial(cubCoeffs);

    // System.out.println("50 " + cubic);
    // System.out.println("51 " + cubic.toLatex());

    Cardano cardano = new Cardano();

    Node[] alphas = cardano.solve(cubic);
    Node alpha = alphas[0];
    if (Math.abs(alpha.evaluate().magnitude()) < Solver.ERROR_THRESHOLD) {
      alpha = alphas[1];
    }

    Node twoalpha = Node.performOp(new Node[] {new Node(2), alpha}, "times", true);
    Node root2alpha = Node.performOp(twoalpha, "radical_2", true);
    Node minusroot2alpha = Node.performOp(new Node[] {new Node(-1),root2alpha}, "times", true);
    Node twop = Node.performOp(new Node[] {new Node(2),p},"times",true);
    Node reciproot2alpha = Node.performOp(root2alpha,"recip",true);
    Node twoqreciproot2alpha = Node.performOp(new Node[] {new Node(2),q,reciproot2alpha},"times",true);
    Node minus2qreciproot2alpha = Node.performOp(new Node[] {new Node(-1),twoqreciproot2alpha},"times",true);
    Node s1 = Node.performOp(new Node[] {twoalpha,twop,twoqreciproot2alpha},"plus",true);
    Node s2 = Node.performOp(new Node[] {twoalpha,twop,minus2qreciproot2alpha},"plus",true);
    Node minuss1 = Node.performOp(new Node[] {new Node(-1),s1},"times",true);
    Node minuss2 = Node.performOp(new Node[] {new Node(-1),s2},"times",true);
    Node r1 = Node.performOp(minuss1,"radical_2",true);
    Node r2 = Node.performOp(minuss2,"radical_2",true);

    Node halfroot2alpha = Node.performOp(new Node[] {new Node(new Fraction(1,2)),root2alpha},"times",true);
    Node halfminusroot2alpha = Node.performOp(new Node[] {new Node(new Fraction(1,2)),minusroot2alpha},"times",true);
    Node halfr1 = Node.performOp(new Node[] {new Node(new Fraction(1,2)),r1},"times",true);
    Node minushalfr1 = Node.performOp(new Node[] {new Node(new Fraction(-1,2)),r1},"times",true);
    Node halfr2 = Node.performOp(new Node[] {new Node(new Fraction(1,2)),r2},"times",true);
    Node minushalfr2 = Node.performOp(new Node[] {new Node(new Fraction(-1,2)),r2},"times",true);

    Node[] ys1 = new Node[2];
    Node[] ys2 = new Node[2];
    ys1[0] = Node.performOp(new Node[] {halfroot2alpha,halfr1}, "plus",true);
    ys1[1] = Node.performOp(new Node[] {halfroot2alpha,minushalfr1},"plus",true);
    ys2[0] = Node.performOp(new Node[] {halfminusroot2alpha,halfr2},"plus",true);
    ys2[1] = Node.performOp(new Node[] {halfminusroot2alpha,minushalfr2},"plus",true);
    //
    // ys1[0] = Node.performOp(new Node[] {new Node(new Fraction(1,2)),ys1_0},"times",true);
    // ys1[1] = Node.performOp(new Node[] {new Node(new Fraction(1,2)),ys1_1},"times",true);
    // ys2[0] = Node.performOp(new Node[] {new Node(new Fraction(1,2)),ys2_0},"times",true);
    // ys2[1] = Node.performOp(new Node[] {new Node(new Fraction(1,2)),ys2_1},"times",true);


    // Node a01 = Node.performOp(new Node[] {pover2,alpha,qrecip2root2alpha}, "plus", true);
    // Node a02 = Node.performOp(new Node[] {pover2,alpha,minusqrecip2root2alpha}, "plus", true);

    // Polynomial quad1 = new Polynomial(new Node[] {a01, minusroot2alpha, new Node(1)});
    // Polynomial quad2 = new Polynomial(new Node[] {a02, root2alpha, new Node(1)});
    //
    Quadratic quadsolver = new Quadratic();
    //
    // Node[] ys1 = quadsolver.solve(quad1);
    // Node[] ys2 = quadsolver.solve(quad2);

    //Node[] ys1 = Node.performOp(new Node[] {twoalpha,})

    boolean qiszero = false;

    if ((q.getOp().equals("fraction") && q.getValue().getNum() == 0) || Solver.isEquiv(q,0)) {
      qiszero = true;
      // We have a biquadratic
      Polynomial biquad = new Polynomial(new Node[] {r,p,new Node(1)});
      // System.out.println("BIQUAD " + biquad);
      Node[] ysquareds = quadsolver.solve(biquad);
      Node y1 = Node.performOp(ysquareds[0],"radical_2",true);
      Node y2 = Node.performOp(ysquareds[1],"radical_2",true);
      ys[0] = y1;
      ys[1] = Node.performOp(new Node[] {new Node(-1),y1},"times",true);
      ys[2] = y2;
      ys[3] = Node.performOp(new Node[] {new Node(-1),y2},"times",true);
    }
    else {
      ys[0] = ys1[0];
      ys[1] = ys1[1];
      ys[2] = ys2[0];
      ys[3] = ys2[1];
    }

    // Shift back
    Node[] xs = null;
    if (needToDepress4) {
      xs = depress.shiftBack(ys);
    }
    else {
      xs = ys;
    }


    // Node[] xs = depress.shiftBack(ys);

    // for (int i = 1; i <= 3; i++) {
    //   System.out.println("x_" + i + " =    " + xs[i-1]);
    //   System.out.println();
    // }

    // Write Latex of solution to txt file
    if (getOutFileName() != null) {
      try {
        FileWriter fw = new FileWriter(getOutFileName());
        PrintWriter pw = new PrintWriter(fw);

        pw.println("\\begin{example}");
        pw.println("Solve the quartic equation $"+ problem.toLatex() +"=0$.");
        pw.println("\\end{example}");
        pw.println("\\justify");
        pw.println("First, we have to depress the cubic term.");
        // Check if the cubic term is already 0:
        String var = "x";
        if (needToDepress4) {
          pw.println("Applying the substitution $x = " + depress.getShiftPol().toLatex("y") + "$, we get");
          pw.println("\\begin{equation*}");
          pw.println(dep4.toLatex("y") + "=0");
          pw.println("\\end{equation*}");
          var = "y";
        }
        else {
          pw.println("Since the cubic term is already depressed, we can proceed.");
        }

        pw.println("Let us then write this as $" + var + "^4 + p" + var + "^2 + q" + var + "+ r = 0$ where we define ");
        pw.println("$$p \\equiv " + p.toLatex() + "\\quad \\text{and} \\qquad q \\equiv " + q.toLatex() + "\\quad \\text{and} \\qquad r \\equiv " + r.toLatex() + "$$");

        String depString = needToDepress4 ? (", after undoing the shift $y=" + depress.getShiftPol().toLatex()+"$:") : ":";

        if (qiszero) {
          pw.print("Since $q=0$, this is a much simpler problem; we have a biquadratic equation. ");
          pw.print("That is, by making the substitution $z="+var+"^2$ we have a quadratic, which is easily solvable with the quadratic formula. ");
          pw.println("We have: ");
          pw.println("$$z^2 + pz + r = 0$$");
          pw.println("Applying the quadratic formula we solve for z:");
          pw.println("$$z = \\frac{-p \\pm \\sqrt{p^2-4r}}{2}$$");
          pw.println("Since $z = "+var+"^2$, we have $"+var+"=\\pm z_1, \\pm z_2$.");
          pw.println("This gives us the following four solutions for $x$" + depString);
          pw.println("\\[");
          pw.println("\\begin{cases}");
          for (int i = 1; i <= 4; i++) {
            pw.println("x_{" + i + "} \\quad=\\quad " + xs[i-1].toLatex() + "\\\\");
          }
          pw.println("\\end{cases}");
          pw.println("\\]");
        }
        else {
          pw.println("Inserting an auxiliary variable $\\alpha$, we rearrange our depressed quartic as follows:");
          pw.println("\\begin{equation*}");
          pw.println("\\left(y^2 + \\frac{p}{2} + \\alpha \\right)^2 = 2\\alpha y^2 - qy + \\alpha^2 + p \\alpha + \\frac{p^2}{4} - r");
          pw.println("\\end{equation*}");

          pw.print("The RHS of the above is quadratic in $"+var+"$. ");
          pw.print("We next set $\\alpha$ such that RHS is a perfect square, by setting the discriminant of that quadratic to zero. ");
          pw.println("Thus we are left with the following cubic in $\\alpha$:");
          pw.println("$$\\alpha^3 + p\\alpha^2 + \\left(\\frac{p^2}{4} - r\\right)\\alpha - \\frac{q^2}{8} = 0$$");
          pw.println("$$\\therefore " + cubic.toLatex("\\alpha")+"=0$$");

          pw.println("We can now solve for $\\alpha$ using Cardano's method. We will need $\\alpha \\neq 0$. We get:");
          pw.println("$$\\alpha = "+ alpha.toLatex() +"$$");

          pw.println("Now that both sides of our equation for $" + var + "$ are perfect squares, we move the RHS over to the left and factor as follows:");
          pw.println("\\begin{equation}");
          pw.println("\\left(y^2 - \\sqrt{2\\alpha} y + \\frac{p}{2} + \\alpha + \\frac{q}{2\\sqrt{2\\alpha}}\\right)\\left(y^2 + \\sqrt{2\\alpha} y + \\frac{p}{2} + \\alpha - \\frac{q}{2\\sqrt{2\\alpha}}\\right) = 0");
          pw.println("\\end{equation}");

          pw.println("Since we have two quadratic factors, we now solve both with the quadratic formula. ");
          pw.println("Thus our four solutions are" + depString);

          int length = xs[0].toLatex().length();
          String mod = "";
          //System.out.println("LENGTH: " + length);
          if (length > 200) {
            mod = "horrendous ";
            pw.println("\\tiny");
            for (int i = 1; i <= 4; i++) {
              int count = 0;
              String curr = xs[i-1].toLatex();
              int idx = curr.lastIndexOf("\\frac{1}{2} \\sqrt{\\left(");
              curr = idx > 0 ? curr = curr.substring(0,idx-2) + "\\\\" + curr.substring(idx-2) : curr;
              pw.println("\\begin{multline*}");
              pw.println("x_{" + i + "} \\quad=\\quad " + curr + "");
              pw.println("\\end{multline*}");
            }
          }
          else {
            pw.println("\\[");
            pw.println("\\begin{cases}");
            for (int i = 1; i<= 4; i++) {
              pw.println("x_{" + i + "} \\quad=\\quad " + xs[i-1].toLatex() + "\\\\");
            }
            pw.println("\\end{cases}");
            pw.println("\\]");
          }
          if (!xs[0].getOp().equals("fraction")) {
            pw.println("These " + mod + "expressions evaluate numerically to: ");
            pw.println("\\[");
            pw.println("\\begin{cases} ");
            for (int i = 1; i <= 4; i++) {
              pw.println("x_{" + i + "} \\quad=\\quad " + xs[i-1].evaluate().toLatex() + "\\\\");
            }
            pw.println("\\end{cases} ");
            pw.println("\\]");
          }
        }

        pw.close();
      } catch (IOException e){

      }
    }

    return alphas;
  }

  public static void main(String[] args) {
    (new Ferrari()).runInputStream();
  }
}
