import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Quadratic extends Solver {
  public Node[] solve(Polynomial problem, boolean verbose) {
    Node[] cfs = problem.getCoeffs();

    Node b2 = Node.performOp(cfs[1], "power_2", true);
    Node minus4ac = Node.performOp(new Node[] {new Node(-4), cfs[2], cfs[0]}, "times", true);
    Node disc = Node.performOp(new Node[] {b2, minus4ac}, "plus", true);
    // System.out.println(disc);

    ArrayList<Node> underRadNodes = new ArrayList<Node>();
    underRadNodes.add(disc);
    if (disc.evaluate().getRe() < 0) {
      underRadNodes.add(new Node(-1));
    }
    //System.out.println("UNDERRADNODES "+underRadNodes);
    //System.out.println("QUANTITY2 " + Node.performOp(underRadNodes,"times",true));
    Node rad = Node.performOp(Node.performOp(underRadNodes, "times", true),"radical_2",true);
    if (underRadNodes.size() > 1) {
      rad = Node.performOp(new Node[] {rad, new Node("i")}, "times", true);
    }
    Node minusrad = Node.performOp(new Node[] {new Node(-1), rad}, "times", true);

    Node minusb = Node.performOp(new Node[] {new Node(-1), cfs[1]}, "times", true);

    Node recip2a;
    if (cfs[2].getOp().equals("fraction")) {
      recip2a = new Node(cfs[2].getValue().multiply(2).reciprocal());
    }
    else {
      recip2a = Node.performOp(Node.performOp(new Node[] {new Node(2), cfs[2]}, "times", true), "recip", true);
    }

    Node t1 = Node.performOp(new Node[] {minusb, rad}, "plus", true);
    Node t2 = Node.performOp(new Node[] {minusb, minusrad}, "plus", true);

    Node[] xs = new Node[2];
    xs[0] = Node.performOp(new Node[] {recip2a, t1}, "times", true);
    xs[1] = Node.performOp(new Node[] {recip2a, t2}, "times", true);

    // Write Latex of solution to txt file
    if (getOutFileName() != null) {
      try {
        FileWriter fw = new FileWriter(getOutFileName());
        PrintWriter pw = new PrintWriter(fw);

        pw.println("\\begin{example}");
        pw.println("Solve the quadratic equation $"+ problem.toLatex() +"=0$.");
        pw.println("\\end{example}");
        pw.println("\\justify");
        pw.print("Using the quadratic formula $x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a}$");
        pw.println(" with $a =" + cfs[2].toLatex() + "$, $b =" + cfs[1].toLatex() + "$, and $c =" + cfs[0].toLatex() + "$, we have:");
        pw.println("\\[");
        pw.println("\\begin{cases} ");
        for (int i = 1; i <= 2; i++) {
          pw.println("x_{" + i + "} \\quad=\\quad " + xs[i-1].toLatex() + "\\\\");
        }
        pw.println("\\end{cases} ");
        pw.println("\\]");

        pw.close();
      } catch (IOException e){

      }
    }
    return xs;
  }

  public static void main(String[] args) {
    (new Quadratic()).runInputStream();
  }
}
