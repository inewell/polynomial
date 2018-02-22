/**
* Author: Isaac Newell
* Description: Implements Cardano's method for cubics.
*/

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.Math;

public class Cardano extends Solver {
  public Node[] solve(Polynomial problem, boolean verbose) {
    // System.out.println(problem);
    // Depress the quadratic term
    Polynomial dep3 = null;

    boolean needToDepress3 = true;
    Depress depress = new Depress(problem);
    Node c2 = problem.getCoeffs()[2];
    if (c2.getOp().equals("fraction")) {
      if (c2.getValue().getNum() == 0) {
        dep3 = problem;
        needToDepress3 = false;
      }
      else {
        dep3 = depress.depress();
      }
    }
    else {
      dep3 = depress.depress();
    }

    Node p = dep3.getCoeffs()[1];
    Node q = dep3.getCoeffs()[0];

    boolean piszero = false;
    boolean qiszero = false;

    Node y1 = null;
    Node y2 = null;
    Node y3 = null;

    Node q2over4 = Node.performOp(new Node[] {Node.performOp(q, "power_2", true), new Node(new Fraction(1,4))},
                                  "times",
                                  true);
    Node p3over27 = Node.performOp(new Node[] {Node.performOp(p, "power_3", true), new Node(new Fraction(1,27))},
                                  "times",
                                  true);
    Node disc = Node.performOp(new Node[] {q2over4, p3over27}, "plus", true);

    Node rad1 = Node.performOp(disc, "radical_2", true);

    Node minusrad1 = Node.performOp(new Node[] {rad1, new Node(-1)}, "times", true);

    Node minusqover2 = Node.performOp(new Node[] {q, new Node(new Fraction(-1,2))},
                                      "times",
                                      true);
    Node u3 = Node.performOp(new Node[] {minusqover2, rad1}, "plus", true);

    Node v3 = Node.performOp(new Node[] {minusqover2, minusrad1}, "plus", true);

    Node u = Node.performOp(u3, "radical_3", true);

    Node v = Node.performOp(v3, "radical_3", true);

    Node uplusv = Node.performOp(new Node[] {u,v}, "plus", true);

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

    if ((p.getOp().equals("fraction") && p.getValue().getNum() == 0) || Solver.isEquiv(p,0)) {
      piszero = true;
      if ((q.getOp().equals("fraction") && q.getValue().getNum() == 0) || Solver.isEquiv(q,0)) {
        qiszero = true;
        y1 = new Node(0);
        y2 = new Node(0);
        y3 = new Node(0);
      }
      else {
        Node minusq = Node.performOp(new Node[] {new Node(-1),q}, "times", true);
        // System.out.println("MINUSQ " + minusq);
        y1 = Node.performOp(minusq, "radical_3", true);
        // System.out.println(y1);
        y2 = Node.performOp(new Node[] {zeta,y1}, "times", true);
        y3 = Node.performOp(new Node[] {zeta2,y1}, "times", true);
      }
    }
    if ((q.getOp().equals("fraction") && q.getValue().getNum() == 0) || Solver.isEquiv(q,0)) {
      qiszero = true;
      y1 = new Node(0);
      Node[] quadCoeffs1 = new Node[3];
      quadCoeffs1[0] = p.clone();
      quadCoeffs1[1] = new Node(0);
      quadCoeffs1[2] = new Node(1);
      Node[] quadsols1 = (new Quadratic()).solve(new Polynomial(quadCoeffs1));
      y2 = quadsols1[0];
      y3 = quadsols1[1];
    }

    // Now we can get our three solutions
    if (!piszero && !qiszero) {
      y1 = Node.performOp(new Node[] {u,v}, "plus", true);
      y2 = Node.performOp(new Node[] {uzeta, vzeta2}, "plus", true);
      y3 = Node.performOp(new Node[] {uzeta2, vzeta}, "plus", true);
    }

    // Shift back
    Node[] xs = null;
    if (needToDepress3) {
      xs = depress.shiftBack(new Node[] {y1, y2, y3});
    }
    else {
      Node[] x2 = {y1,y2,y3};
      xs = x2;
    }

    if (verbose) {
      for (int i = 0; i < xs.length; i++) {
        System.out.println("x_" + (i+1) + " =   " + xs[i]);
        System.out.println();
      }
    }

    // Write Latex of solution to txt file
    if (getOutFileName() != null) {
      try {
        FileWriter fw = new FileWriter(getOutFileName());
        PrintWriter pw = new PrintWriter(fw);

        pw.println("\\begin{example}");
        pw.println("Solve the cubic equation $" + problem.toLatex() + "=0$.");
        pw.println("\\end{example}");
        pw.println("\\justify");
        pw.println("First, we have to depress the quadratic term.");
        // Check if the quadratic term is already 0:
        String var = "x";
        if (needToDepress3) {
          pw.println("Applying the substitution $x = " + depress.getShiftPol().toLatex("y") + "$, we get");
          pw.println("\\begin{equation}");
          //System.out.println("97 " + dep3.toLatex());
          //System.out.println("98 " + dep3);
          pw.println(dep3.toLatex("y") + "=0");
          pw.println("\\end{equation}");
          var = "y";
        }
        else {
          pw.println("Since the quadratic term is already depressed, we can proceed.");
        }

        pw.print("Let us then write this as $" + var + "^3 + p" + var + "+ q = 0$ where we define ");
        pw.println("$$p \\equiv " + p.toLatex() + "\\quad \\text{and} \\qquad q \\equiv " + q.toLatex() + "$$");

        if (piszero && qiszero) {
          pw.println("Since $p=0$ and $q=0$, the problem is much simpler. All of the roots of the cubic in $"+var+"$ are thus 0");
        }
        else if (piszero && !qiszero) {
          pw.println("Since $p=0$, the problem is much simpler. We know that our three roots in $"+var+"$ are thus $\\sqrt[3]{-p}$, $\\zeta\\sqrt[3]{-p}$, and $\\zeta^2\\sqrt[3]{-p}$ where $\\sqrt[3]{-p} \\in \\mathbb{R}$");
        }
        else if (!piszero && qiszero) {
          pw.println("Since $q=0$, the problem is much simpler. We can thus factor out a $"+var+"$ from our depressed cubic. ");
          pw.println("This means that one $"+var+"_1=0$, and we can just use the quadratic formula on the remaining quadratic factor $"+var+"^2 + p = 0$ to find $"+var+"_1$ and $"+var+"_2$.");
        }
        else {
          pw.print("We now introduce auxiliary quantities $u$ and $v$. When we impose the first constraint $u+v="+var+"$, our equation becomes ");
          pw.println("$$u^3 + v^3 + \\left(3uv+p\\right)\\left(u+v\\right) + q = 0$$");
          pw.println("At this point we impose the second constraint that $3uv+p=0$, which gives us the following:");

          pw.println("\\[");
          pw.println("\\begin{cases}");
          Node[] abcd = {new Node(-1), p3over27};
          Node minusp3over27 = Node.performOp(abcd, "times", true);
          pw.println("u^3 v^3 = -\\frac{p^3}{27} = " + minusp3over27.toLatex() + "\\\\");
          abcd[0] = new Node(-1);
          abcd[1] = q;
          Node minusq = Node.performOp(abcd, "times", true);
          pw.println("u^3 + v^3 = -q = " + minusq.toLatex() + "\\\\");
          pw.println("\\end{cases}");
          pw.println("\\]");

          pw.println("By Vieta's formulas, $u^3$ and $v^3$ will be the roots of the quadratic $z^2 + qz - p^3/27 = 0$, or: ");
          Node onenode = new Node(1);
          Polynomial quad = new Polynomial(new Node[] {minusp3over27, q, onenode});
          pw.println("$$" + quad.toLatex("z") + "=0$$");

          //Node[] quadsols = (new Quadratic()).solve(quad);
          pw.println("Then we can use the quadratic formula to solve for $u^3$ and $v^3$, which gives: ");
          pw.println("\\begin{align}");
          //pw.println("u^3, v^3 = " + quadsols[0].toLatex() + ", " + quadsols[1].toLatex());
          //pw.println("u^3, v^3 = " + u3.toLatex() + ", " + v3.toLatex());
          pw.println("u^3 &= " + u3.toLatex() + "&\\implies u &=" + u.toLatex() + "\\\\");
          pw.println("v^3 &= " + v3.toLatex() + "&\\implies v &=" + v.toLatex());
          pw.println("\\end{align}");

          //Now, look at the discriminant;
          if (disc.getOp().equals("fraction")) {
            if (disc.getValue().getNum() == 0) {
              pw.println("Since the discriminant, $\\frac{q^2}{4} + \\frac{p^3}{27} = 0$, u and v are equal. Therefore, we have 3 real roots; 1 simple root and 1 double root.");
            }
            else if (disc.getValue().getNum() < 0) {
              pw.println("Since the discriminant, $\\frac{q^2}{4} + \\frac{p^3}{27} < 0$, $u^3$ and $v^3$ and thus $u$ and $v$ are complex conjugates. Therefore, we have 3 distinct real roots.");
            }
            else {
              pw.println("Since the discriminant, $\\frac{q^2}{4} + \\frac{p^3}{27} > 0$, we have $u, v, \\in \\mathbb{R}$ and $u \\neq v$. Therefore, we have only one real root; the other 2 are complex conjugates.");
            }
          }
        }

        String depString = needToDepress3 ? (", after undoing the shift $y=" + depress.getShiftPol().toLatex()+"$:") : ":";
        pw.println("Thus our three solutions are" + depString);
        pw.println("\\[");
        pw.println("\\begin{cases} ");
        for (int i = 1; i <= 3; i++) {
          pw.println("x_{" + i + "} \\quad=\\quad " + xs[i-1].toLatex() + "\\\\");
        }
        pw.println("\\end{cases} ");
        pw.println("\\]");

        //Simplify?
        boolean simplify = false;
        if (!piszero && !qiszero) {
          double uplusvEval = uplusv.evaluate().getRe();
          for (int n = 1; n < 200; n++) {
            double test = n*uplusvEval;
            double above = Math.ceil(test);
            double below = Math.floor(test);
            if (Math.abs(test-above) < Solver.ERROR_THRESHOLD) {
              simplify = true;
              uplusv = new Node(new Fraction((int)above,n));
              break;
            }
            else if (Math.abs(test-below) < Solver.ERROR_THRESHOLD) {
              simplify = true;
              uplusv = new Node(new Fraction((int)below,n));
              break;
            }
          }
          if (simplify) {
            y1 = uplusv.clone();
            xs[0] = depress.shiftBack(new Node[] {y1})[0];
            Polynomial quadsimp = problem.divide(xs[0]);
            // System.out.println("QUADSIMP " + quadsimp);
            Node[] quadxs = (new Quadratic()).solve(quadsimp);
            xs[1] = quadxs[0];
            xs[2] = quadxs[1];
            //xs = depress.shiftBack(new Node[] {y1,y2,y3});
            pw.println("This simplifies to:");
            pw.println("\\[");
            pw.println("\\begin{cases} ");
            for (int i = 1; i <= 3; i++) {
              pw.println("x_{" + i + "} \\quad=\\quad " + xs[i-1].toLatex() + "\\\\");
            }
            pw.println("\\end{cases} ");
            pw.println("\\]");
          }
          else {
            pw.println("This evaluates numerically to: ");
            pw.println("\\[");
            pw.println("\\begin{cases} ");
            for (int i = 1; i <= 3; i++) {
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

    return xs;
  }

  public static void main(String[] args) {
    (new Cardano()).runInputStream();
  }
}
