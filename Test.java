import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

public class Test {
  private String s;

  public Test(String s) {
    this.s = s;
  }

  public void changeS() {
    s = "HERBERT";
  }

  public static void main(String[] args) {
    int[] cfsi = {12,4,3,1};
    Node[] cfs = new Node[4];
    for (int i = 0; i < cfsi.length; i++) {
      cfs[i] = new Node(cfsi[i]);
    }
    Polynomial p1 = new Polynomial(cfs);
    Node r = new Node(-3);
    Polynomial p2 = p1.divide(r);
    System.out.println(p2);

    // Node n1 = new Node(1);
    // Node n2 = new Node(2);
    // Node n3 = Node.performOp(new Node[] {n1, n2}, "times", true);
    // Node n4 = new Node(new Fraction(5,8));
    // Node n5 = Node.performOp(new Node[] {n3, n4}, "plus", true);
    // Node n6 = Node.performOp(new Node(3), "radical_2", true);
    // System.out.println("n5: " + n5);
    // System.out.println("n6: " + n6);
    // Node n7 = Node.performOp(new Node[] {n5, n6}, "plus", true);
    // Node n8 = Node.performOp(n7, "power_3", true);

    // System.out.println(n8);
    // System.out.println(n8.toLatex());
    //
    // String s1 = "ISAAC";
    // Test t1 = new Test(s1);
    // t1.changeS();
    // System.out.println(s1);
    // System.out.println(t1.s);
    //
    // ArrayList<Integer> al1 = new ArrayList<Integer>();
    // for (int i = 0; i < 10; i++) {
    //   al1.add(i);
    // }
    // for (int i = 0; i < al1.size(); i++) {
    //   if (al1.get(i) % 2 == 0) {al1.remove(al1.get(i));}
    // }
    // System.out.println(al1);
    //
    // System.out.println("p".indexOf("power"));
    // System.out.println("power_3".split("_")[1]);
    // Node[] coeffs = new Node[4];
    // for (int i = 0; i < 4; i++) {
    //   coeffs[i] = new Node(1);
    // }
    // Polynomial p = new Polynomial(coeffs);
    // System.out.println("POL: " + p);
    // Cardano cardano = new Cardano();
    // Node[] xs = cardano.solve(p);
    //
    // System.out.println("BEGINNING THE TEST");
    // System.out.println("ACTUAL: " + xs[0]);
    // System.out.println("LATEX:  " + xs[0].toLatex());

    //
    // Fraction f1 = new Fraction(25, 8);
    // for (Fraction f : f1.simplifyRadical(2)) {
    //   System.out.println(f);
    // }
    // Fraction f2 = new Fraction(-125, 3);
    // for (Fraction f : f2.simplifyRadical(2)) {
    //   System.out.println(f);
    // }
  }
}
