/**
* Author: Isaac Newell
* Description: Implements the functionality of polynomial math.
*/

import java.lang.Math;

public class Polynomial
{
  //a
  private Node[] array;

  public Polynomial(Node[] a)
  {
    // for (Node n : a) {
    //   System.out.print(n + ", ");
    // }
    // System.out.println();
    array = new Node[a.length];
    for (int i = 0; i < a.length; i++) {
      array[i] = a[i].clone();
    }
  }

  public Polynomial(int deg) {
    array = new Node[deg+1];
    for (int i = 0; i < deg+1; i++) {
      array[i] = new Node(0);
    }
  }

  public int degree()
  {
    return array.length - 1;
  }

  // Evaluates the polynomial at x.
  public Node getValue(Node x)
  {
    Node[] terms = new Node[array.length];
    if (array.length > 0) {
      terms[0] = array[0].clone();
    }
    if (array.length > 1) {
      terms[1] = Node.performOp(new Node[] {array[1], x}, "times", true);
    }
    for (int i = 2; i < array.length; i++) {
      String op = "power_" + i;
      Node[] lst = {x};
      Node pwrNode = Node.performOp(lst, op, true);
      terms[i] = Node.performOp(new Node[] {array[i], pwrNode}, "times", true);
    }
    Node total = Node.performOp(terms, "plus", false);
    return total;
  }

  // Prints the polynomial in a readable format
  public String toString()
  {
    String s = "";
    for (int i = 0; i < array.length - 1; i++) {
      s += array[i].toString() + "x^" + i + " + ";
    }
    s += array[array.length - 1].toString() + "x^" + (array.length - 1);
    return s;
  }

  // Writes the polynomial as LaTeX code.
  public String toLatex(String var) {
    String s = "";
    for (int i = array.length-1; i >= 0; i--) {
      boolean addself = true;
      boolean signcovered = false;
      if (array[i].getOp().equals("fraction")) {
        if (array[i].getValue().getNum() == 1 && array[i].getValue().getDenom() == 1) {
          if (i > 0) addself = false;
        }
        else if (array[i].getValue().getNum() == 0 && array[i].getValue().getDenom() == 1) {
          continue;
        }
        else if (array[i].getValue().getNum() < 0) {
          if (array[i].getValue().getNum() == -1 && array[i].getValue().getDenom() == 1) {
            if (i > 0) {
              addself = false;
              s += "-";
            }
          }
          signcovered = true;
        }
      }
      if (!signcovered) {
        if (i < array.length-1) {
          s += "+";
        }
      }
      if (addself)
        s += array[i].toLatex();
      if (i == 0) {
        break;
      }
      else if (i == 1) {
        s += var;
      }
      else {
        s += "{"+var+"}^{" + i + "}";
      }
    }
    return s;
  }

  public String toLatex() {
    return this.toLatex("x");
  }
  public Node[] getCoeffs()
  {
    return array;
  }

  // Adds to another polynomial
  public Polynomial add(Polynomial other) {
    Polynomial bigger;
    int smalldeg;
    int bigdeg;
    Node[] p;
    if (this.degree() > other.degree()) {
      bigger = this;
      bigdeg = this.degree();
      smalldeg = other.degree();
      p = new Node[this.degree() + 1];
    } else {
      bigger = other;
      bigdeg = other.degree();
      smalldeg = this.degree();
      p = new Node[other.degree() + 1];
    }
    for (int i = 0; i <= smalldeg; i++) {
      Node[] lst = {array[i], other.getCoeffs()[i]};
      p[i] = Node.performOp(lst, "plus", true);
    }
    for (int i = smalldeg+1; i <= bigdeg; i++) {
      p[i] = bigger.getCoeffs()[i];
    }
    return new Polynomial(p);
  }

  // Performs polynomial multiplication. Uses the formula that
  // sum_{k=0}^{n} {a_k x^k} * sum_{k=0}^{m} {b_k x^k} = sum_{k=0}^{n+m} {sum{i+j=k}a_i b_j x^k}
  public Polynomial multiply(Polynomial other)
  {
    Node[] p = new Node[array.length + other.degree()];
    for (int i = 0; i < array.length; i++) {
      for (int j = 0; j < other.degree() + 1; j++) {
        Node[] lst = {array[i], other.getCoeffs()[j]};
        Node prod = Node.performOp(lst, "times", true);
        if (p[i+j] == null) {
          p[i+j] = prod;
        } else {
          p[i+j] = Node.performOp(new Node[] {p[i+j], prod}, "plus", false);
        }
      }
    }
    Polynomial product = new Polynomial(p);
    return product;
  }

  // Raises a polynomial to a power.
  public Polynomial power(int n) {
    if (n == 0) {
      return new Polynomial(new Node[] {new Node(1)});
    } else {
      Polynomial p = this;
      for (int i = 1; i < n; i++) {
        p = this.multiply(p);
      }
      return p;
    }
  }

  // Divides out (x-root) from the polynomial; only works when root divides evenly.
  public Polynomial divide(Node root) {
    Node[] cfs = new Node[this.degree()];
    // for (int i = 0; i < cfs.length; i++) {
    //   Node total = new Node(0);
    //   for (int j = i+1; j <= cfs.length; j++) {
    //     Node prod = Node.performOp(new Node[] {array[j],Node.performOp(root,"power_"+(j-1),true)},"times", true);
    //     total = Node.performOp(new Node[] {total,prod}, "plus", true);
    //   }
    //   cfs[i] = total;
    // }
    for (int i = cfs.length-1; i >= 0; i--) {
      Node total = new Node(0);
      for (int j = cfs.length; j > i; j--) {
        Node power = Node.performOp(root, "power_"+(j-i-1),true);
        Node prod = Node.performOp(new Node[] {array[j],power},"times",true);
        total = Node.performOp(new Node[] {total,prod},"plus",true);
      }
      cfs[i] = total;
    }
    return new Polynomial(cfs);
  }

  public static void main(String[] args)
  {
    Node[] cfs1 = {new Node(-3), new Node(1)};
    Node[] cfs2 = {new Node(5), new Node(2)};

    Polynomial p1 = new Polynomial(cfs1);
    Polynomial p2 = new Polynomial(cfs2);

    Polynomial p3 = p1.multiply(p2);

    System.out.println(p3);
    System.out.println();

    Node[] cfs3 = {new Node(1), new Node(1)};
    Polynomial p4 = new Polynomial(cfs3);
    Polynomial p5 = p4.power(2);

    System.out.println(p4);
    System.out.println();

    System.out.println(p5);
    System.out.println();
    System.out.println(p4);
    System.out.println();

    Polynomial p6 = p5.add(p1);
    System.out.println(p6);
  }
}
