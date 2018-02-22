/**
* Author: Isaac Newell
* Description: Depresses a Polynomial object using the shift x=y-a_{n-1}/n
*/

public class Depress {
  private Polynomial p;
  private Polynomial shift;

  public Depress(Polynomial p) {
    this.p = p;
  }

  public Polynomial depress() {
    Node[] c = p.getCoeffs();
    int deg = p.degree();
    this.shift = new Polynomial(new Node[] {new Node(c[deg-1].getValue().divide(-deg)), new Node(1)});

    Polynomial dep = new Polynomial(deg);

    for (int i = 0 ; i <= deg; i++) {
      Polynomial ci = new Polynomial(new Node[] {c[i]});
      dep = dep.add(ci.multiply(shift.power(i)));
    }

    return dep;
  }

  public Node[] shiftBack(Node[] ys) {
    Node[] xs = new Node[ys.length];
    for (int i = 0; i < ys.length; i++) {
      xs[i] = shift.getValue(ys[i]);
    }
    return xs;
  }

  public Polynomial getShiftPol() {
    return shift;
  }
}
