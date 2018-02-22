/**
* Author: Isaac Newell
* Description: Nodes are the units of mathematical expressions,
*  and are expressions in themselves.
*/

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

public class Node {
  private String op;
  private Fraction value;
  private Node parent;
  private ArrayList<Node> children;

  private static String[] opTypes = {"fraction",
                                      "radical",
                                      "power",
                                      "plus",
                                      "times",
                                      "recip",
                                      "parens",
                                      "var",
                                      "i"};

  public Node(String op) {
    this.op = op;
    this.children = new ArrayList<Node>();
  }

  public Node() {
    this("fraction");
  }

  public Node(int value) {
    this();
    this.value = new Fraction(value);
  }

  public Node(Fraction value) {
    this();
    this.value = new Fraction(value);
  }

  // Does a deep copy
  public Node clone() {
    if (op.equals("fraction")) {
      if (value == null) {
        return new Node();
      } else {
        return new Node(new Fraction(value));
      }
    } else {
      Node newNode = new Node(op);
      for (Node n : children) {
        Node cloneN = n.clone();
        newNode.children.add(cloneN);
        cloneN.parent = newNode;
      }
      return newNode;
    }
  }

  public void setValue(Fraction value) {
    if (op.equals("fraction")) {
      this.value = new Fraction(value);
    } else {
      //System.out.println("You can't assign a value to an operation node");
    }
  }

  public String getOp() {
    return op;
  }

  public Fraction getValue() {
    return value;
  }

  public Node getParent() {
    return parent;
  }

  public ArrayList<Node> getChildren() {
    return children;
  }

  public boolean addChild(Node child) {
    // remove child from its previous parent's children if applicable
    if (child.parent != null) {
      child.parent.children.remove(child);
    }
    // add child to this node's children
    children.add(child);
    // set this node as child's parent
    child.parent = this;
    return true;
  }

  public boolean remove(Node child) {
    child.parent = null;
    children.remove(child);
    return true;
  }

  public void setParent(Node par) {
    if (par != null)
      par.addChild(this);
    else
      this.parent = null;
  }

  public void setChildren(ArrayList<Node> ch) {
    children = ch;
    for (Node c : ch) {
      c.parent = this;
    }
  }

  public static Node performOp(ArrayList<Node> nlist, String op, boolean cl) {
    Node[] narray = new Node[nlist.size()];
    for (int i = 0; i < narray.length; i++) {
      narray[i] = nlist.get(i);
    }
    return performOp(narray, op, cl);
  }

  // Same as below, but for unary operations, e.g. power_2
  public static Node performOp(Node n, String op, boolean cl) {
    Node[] nlist = new Node[] {n};
    Node ncopy = n.clone();
    Node newNode = new Node(op);
    newNode.addChild(ncopy);
    //System.out.println(newNode);

    if (op.indexOf("power") != -1) {
      // If the child node is a frac, then apply the power to it.
      return Simplify.simplifyPower(newNode);
    } else if (op.indexOf("radical") != -1) {
      //System.out.println("RADICAL FOUND");
      return Simplify.simplifyRadical(newNode);
    }
    else if (op.equals("recip")) {
      return Simplify.simplifyRecip(newNode);
    }
    return newNode;
  }

  // Performs the specified operation on the Node s in nlist.
  public static Node performOp(Node[] nlist, String op, boolean cl) {
    //System.out.println("LTH: " + nlist.length);
    ArrayList<Node> nlistcopy = new ArrayList<Node>();
    for (Node n : nlist) {
      nlistcopy.add(n.clone());
    }
    Node newNode = new Node(op);
    newNode.setChildren(nlistcopy);

    if (op.equals("times")) {
      return Simplify.combineConstantsTimes(Simplify.collapseTimes(newNode));
    } else if (op.equals("plus")) {
      return Simplify.combineConstantsPlus(Simplify.collapsePlus(newNode));
    }

    return newNode;
  }

  public String toString() {
    String s = "";
    if (this.op.equals("fraction")) {
      s = s + " " + this.value + " ";
      return s;
    } else {
      s = s + " " + this.op + "(";
      for (int i = 0; i < this.children.size() - 1; i++) {
        s = s + this.children.get(i).toString();
        s = s + ",";
      }
      if (this.children.size() >= 1)
        s = s + this.children.get(this.children.size()-1).toString();
      s = s + ")";
      return s;
    }
  }

  // Expresses the Node as LaTeX code.
  public String toLatex() {
    String s = "";
    if (this.op.equals("fraction")) {
      if (this.value.getDenom() == 1) {
        s = s + " " + this.value + " ";
      } else {
        if (this.value.getNum() < 0) {
          s = s + "-\\frac{" + (-this.value.getNum()) + "}{" + this.value.getDenom() + "}";
        }
        else {
          s = s + "\\frac{" + this.value.getNum() + "}{" + this.value.getDenom() + "}";
        }
      }
      return s.replaceAll("\\s+", " ");
    }
    else {
        if (this.op.equals("times")) {
          // System.out.println("Doing times: " + this.children.size());
          // boolean fracFound = false;
          // boolean badFound = false;
          // boolean fractionize = false;
          // Node fracNode = null;
          // for (int i = 0; i < this.children.size(); i++) {
          //   String op1 = this.children.get(i).op;
          //   if (op1.equals("fraction") && this.children.get(i).getValue().getDenom() != 1) {
          //     fracNode = this.children.get(i);
          //     fracFound = true;
          //   }
          //   if (!op1.equals("fraction") && !op1.equals("i")) {
          //     if (op1.equals("radical")) {
          //       if (!this.children.get(i).children.get(0).op.equals("fraction")) {
          //         badFound = true;
          //         System.out.println(this.children.get(i));
          //         break;
          //       }
          //     }
          //     else {
          //       badFound = true;
          //       System.out.println(this.children.get(i));
          //       break;
          //     }
          //   }
          // }
          // System.out.println("fracFound: " + fracFound);
          // System.out.println("badFound: " + badFound);
          // System.out.println();
          // if (fracFound && !badFound) {
          //   System.out.println("FRACTIONIZE!!!!!!");
          //   fractionize = true;
          // }
          // if (fractionize) {
          //   s = "\\frac{";
          // }
          // Comparator<Node> comparator = new OpOrderComparator();
          // PriorityQueue<Node> queue = new PriorityQueue<Node>(10, comparator);
          // for (int i = 0; i < this.children.size(); i++) {
          //   //System.out.println("SSSSSSS: " + s);
          //   queue.add(this.children.get(i));
          // }
          // while (queue.size() != 0) {
          //   Node next = queue.remove();
          //   if (fractionize) {
          //     if (next == fracNode) {
          //       int v = fracNode.getValue().getNum();
          //       String body = v + "";
          //       if (v < 0) {
          //         body = "\\left(" + body + "\\right)";
          //       }
          //       s = s + body;
          //       continue;
          //     }
          //   }
          //   if (next.op.equals("plus")) {
          //     s = s + "\\left(" + next.toLatex() + "\\right)";
          //   }
          //   else {
          //     s = s + " " + next.toLatex() + " ";
          //   }
          // }
          for (int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i).op.equals("plus") || (this.children.get(i).op.equals("fraction")) && this.children.get(i).value.getNum() < 0) {
              s = s + "\\left(" + this.children.get(i).toLatex() + "\\right)";
            }
            else {
              s = s + " " + this.children.get(i).toLatex() + " ";
            }
          }
          // if (fractionize) {
          //   System.out.println("ENDING FRACTIONIZE");
          //   s = s + "}{" + fracNode.getValue().getDenom() + "}";
          // }
        }
        else if (this.op.equals("plus")) {
          //System.out.println("Doing plus: " + this.children.size());
          if (this.children.size() > 0) {
            s = s + " " + this.children.get(0).toLatex() + " ";
          }
          for (int i = 1; i < this.children.size(); i++) {
            if (this.children.get(i).op.equals("times")) {
              boolean flipsign = false;
              // search for negative constants - subtract.
              for (Node n : this.children.get(i).children) {
                if (n.op.equals("fraction")) {
                  if (n.value.getNum() < 0) {
                    flipsign = true;
                    break;
                  }
                }
              }
              if (flipsign) {
                Node toSubtract = Node.performOp(new Node[] {this.children.get(i), new Node(-1)}, "times", true);
                s = s + " - " + toSubtract.toLatex() + " ";
                continue;
              }
            }
            s = s + "+" + this.children.get(i).toLatex() + " ";
          }
        }
        else if (this.op.indexOf("power") != -1) {
          int power = Integer.parseInt(this.op.split("_")[1]);
          //System.out.println("Doing power: " + power);
          if (this.children.size() > 0) {
            if (this.children.get(0).op.equals("fraction")) {
              s = s + "{" + this.children.get(0).toLatex() + "}^{" + power + "}";
            }
            else {
              s = s + "{\\left(" + this.children.get(0).toLatex() + "\\right)}^{" + power + "}";
            }
          }
        }
        else if (this.op.indexOf("radical") != -1) {
          int power = Integer.parseInt(this.op.split("_")[1]);
          //System.out.println("Doing radical: " + power);
          if (this.children.size() > 0) {
            String index = (power == 2) ? "" : "[" + power + "]";
            s = s + "\\sqrt" + index + "{" + this.children.get(0).toLatex() + "}";
          }
        }
        else if (this.op.equals("i")) {
          //System.out.println("doing i");
          return "i";
        }
        else if (this.op.equals("recip")) {
          s = "\\frac{1}{" + this.children.get(0).toLatex() + "}";
        }
        else {
          s = this.toString();
        }
        return s.replaceAll("\\s+", " ");
    }
  }

  public Complex evaluate() {
    //System.out.println("GET: " + this);
    if (this.op.equals("fraction")) {
      //System.out.println("RETURN: " + (new Complex((double)(this.getValue().getNum()) / this.getValue().getDenom())));
      return new Complex((double)(this.getValue().getNum()) / this.getValue().getDenom());
    }
    else if (this.op.equals("times")) {
      Complex prod = new Complex(1);
      for (Node n : this.children) {
        prod = prod.multiply(n.evaluate());
      }
      //System.out.println("RETURN: " +prod);
      return prod;
    }
    else if (this.op.equals("plus")) {
      Complex sum = new Complex(0);
      for (Node n : this.children) {
        sum = sum.add(n.evaluate());
      }
      //System.out.println("RETURN: " + sum);
      return sum;
    }
    else if (this.op.indexOf("power") != -1) {
      int power = Integer.parseInt(this.op.split("_")[1]);
      //System.out.println("RETURN: "+this.children.get(0).evaluate().power(power));
      return this.children.get(0).evaluate().power(power);
    }
    else if (this.op.indexOf("radical") != -1) {
      int power = Integer.parseInt(this.op.split("_")[1]);
      //System.out.println("RETURN: "+this.children.get(0).evaluate().radical(power));
      return this.children.get(0).evaluate().radical(power);
    }
    else if (this.op.equals("i")) {
      //System.out.println("RETURN: " + (new Complex(0,1)));
      return new Complex(0,1);
    }
    else if (this.op.equals("recip")) {
      Complex recip = this.children.get(0).evaluate();
      double a = recip.getRe();
      double b = recip.getIm();
      //System.out.println("RETURN: "+recip.conjugate().multiply(new Complex(1/(a*a-b*b),0)));
      return recip.conjugate().multiply(new Complex(1/(a*a-b*b),0));
    }
    else {
      return new Complex(0);
    }
  }
}
