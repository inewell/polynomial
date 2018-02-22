/**
* Author: Isaac Newell
* Description: Contains all the simplification methods used after performing operations.
*/

import java.util.List;
import java.util.ArrayList;

public class Simplify {
  // Combine all constants under plus operation
  public static Node combineConstantsPlus(Node newNode) {
    if (newNode.getOp().equals("fraction")) {
      return newNode;
    }
    // combine all fraction nodes being added
    Node fracNode = null;
    int i = 0;
    boolean removeSelf = true;
    while (i < newNode.getChildren().size()){
      Node n = newNode.getChildren().get(i);
      if (n.getOp().equals("fraction")) {
        if (fracNode == null) {
          fracNode = n;
          i ++;
        } else {
          fracNode.setValue(fracNode.getValue().add(n.getValue()));
          newNode.getChildren().remove(i);
        }
      } else {
        removeSelf = false;
        i ++;
      }
    }
    if (removeSelf) {
      return fracNode;
    }
    else {
      if (fracNode != null && fracNode.getValue().getNum() == 0) {
        newNode.remove(fracNode);
      }
    }
    if (newNode.getChildren().size() == 1) {
      return newNode.getChildren().get(0);
    }
    return newNode;
  }

  public static Node collapsePlus(Node newNode) {
    ArrayList<Node> toAdd = new ArrayList<Node>();
    int i = 0;
    while (i < newNode.getChildren().size()) {
      Node n = newNode.getChildren().get(i);
      if (n.getOp().equals("plus")) {
        for (Node n2 : n.getChildren()) {
          toAdd.add(n2);
        }
        newNode.getChildren().remove(i);
      }
      else {
        i++;
      }
    }
    for (Node n : toAdd) {
      newNode.addChild(n);
    }
    if (newNode.getChildren().size() == 1) {
      return newNode.getChildren().get(0);
    }
    return newNode;
  }

  // Combine all constants under times operation
  public static Node combineConstantsTimes(Node newNode) {
    if (newNode.getOp().equals("fraction")) {
      return newNode;
    }
    Node fracNode = null;
    int i = 0;
    boolean removeSelf = true;
    int nonfracct = 0;
    while (i < newNode.getChildren().size()) {
      Node n = newNode.getChildren().get(i);
      if (n.getOp().equals("fraction")) {
        if (n.getValue().getNum() == 0) {
          return new Node(0);
        }
        if (fracNode == null) {
          fracNode = n;
          i ++;
        } else {
          fracNode.setValue(fracNode.getValue().multiply(n.getValue()));
          newNode.getChildren().remove(i);
        }
      } else {
        nonfracct ++;
        removeSelf = false;
        i ++;
      }
    }
    if (removeSelf) {
      //System.out.println("Returning fracNode with " + fracNode.getValue());
      return fracNode;
    }
    if (fracNode != null) {
      if (fracNode.getValue().getNum() == 1 && fracNode.getValue().getDenom() == 1) {
        if (nonfracct > 1) {
          newNode.remove(fracNode);
          return newNode;
        }
        else {
          //nfc = 1
          newNode.remove(fracNode);
          Node newTop = newNode.getChildren().get(0);
          newTop.setParent(null);
          return newTop;
        }
      }
    }
    return newNode;
  }

  public static Node collapseTimes(Node newNode) {
    ArrayList<Node> toAdd = new ArrayList<Node>();
    int i = 0;
    while (i < newNode.getChildren().size()) {
      Node n = newNode.getChildren().get(i);
      if (n.getOp().equals("times")) {
        for (Node n2 : n.getChildren()) {
          toAdd.add(n2);
        }
        newNode.getChildren().remove(i);
      }
      else {
        i++;
      }
    }
    for (Node n : toAdd) {
      newNode.addChild(n);
    }
    if (newNode.getChildren().size() == 1) {
      return newNode.getChildren().get(0);
    }
    return newNode;
  }

  public static Node simplifyPower(Node newNode) {
    int power = Integer.parseInt(newNode.getOp().split("_")[1]);
    Node child = newNode.getChildren().get(0);
    if (child.getOp().equals("fraction")) {
      if (child.getValue().getNum() == 0) {
        return new Node(0);
      }
      child.setValue(child.getValue().power(power));
      child.setParent(null);
      return child;
    }
    return newNode;
  }

  public static Node simplifyRadical(Node newNode) {
    //System.out.println("SIMPLIFYING RADICAL");
    int power = Integer.parseInt(newNode.getOp().split("_")[1]);
    Node child = newNode.getChildren().get(0);
    if (child.getOp().equals("fraction")) {
      if (child.getValue().getNum() == 0) {
        return new Node(0);
      }
      Fraction ncv0 = child.getValue();
      Fraction[] sr = ncv0.simplifyRadical(power);
      // System.out.println("SRSRSRSR: " + sr[0] + ", " + sr[1]);
      Node n_o = new Node(sr[0].getNum());
      Node n_i_d_i = new Node(sr[1].getNum() * sr[1].getDenom());
      Node d_o_d_i = new Node(sr[0].getDenom()*sr[1].getDenom());
      Node iNode = new Node("i");

      ArrayList<Node> prodNodes = new ArrayList<Node>();
      if (n_o.getValue().getDenom() != 1 || n_o.getValue().getNum() != 1) {
        prodNodes.add(n_o);
      }
      if (n_i_d_i.getValue().getDenom() != 1 || n_i_d_i.getValue().getNum() != 1) {
        Node rootnidi = new Node(newNode.getOp());
        rootnidi.addChild(n_i_d_i);
        prodNodes.add(rootnidi);
        // System.out.println(rootnidi);
      }
      if (ncv0.getNum() < 0) {
        if (power == 2) {
          prodNodes.add(iNode);
        }
        else if (power == 3) {
          prodNodes.add(new Node(-1));
        }
      }
      if (d_o_d_i.getValue().getDenom() != 1 || d_o_d_i.getValue().getNum() != 1) {
        Node den = new Node(new Fraction(d_o_d_i.getValue().getDenom(), d_o_d_i.getValue().getNum()));
        prodNodes.add(den);
      }

      // System.out.println("SIZE"+prodNodes.size());
      if (prodNodes.size() == 0) {
        return new Node(1);
      }
      if (prodNodes.size() == 1) {
        return prodNodes.get(0);
      }

      Node timesNode = Node.performOp(prodNodes, "times", true);
      // System.out.println("RETURNING 1 " + timesNode);
      return timesNode;
    }
    // System.out.println("RETURNING 2 " + newNode);
    return newNode;
  }

  // public static Node simplifyRadical(Node newNode) {
  //   int power = Integer.parseInt(newNode.getOp().split("_")[1]);
  //   Node child = newNode.getChildren().get(0);
  //   if (child.getOp().equals("fraction")) {
  //     Fraction ncv0 = child.getValue();
  //     Fraction[] sr = ncv0.simplifyRadical(power);
  //     System.out.println(sr[0] + " , " + sr[1]);
  //     // if radical has been totally eliminated
  //     if (sr[1].getNum() == 1 && sr[1].getDenom() == 1) {
  //       //System.out.println("Inside radical 1");
  //       child.setValue(sr[0]);
  //       child.setParent(null);
  //       return child;
  //     }
  //     else {
  //       Node timesNode = new Node("times");
  //       Node radicalNode = new Node(newNode.getOp());
  //       //System.out.println("RADNODE: " + radicalNode);
  //       radicalNode.addChild(new Node(sr[1]));
  //       timesNode.addChild(radicalNode);
  //       Node fracNode0 = new Node(sr[0]);
  //       timesNode.addChild(fracNode0);
  //       if (ncv0.getNum() < 0) {
  //         // multiply by appropriate complex root of -1
  //         if (power == 2) {
  //           timesNode.addChild(new Node("i"));
  //         }
  //         if (power == 3) {
  //           timesNode.addChild(new Node(-1));
  //         }
  //       }
  //       System.out.println(sr[0].getNum() + "and" + sr[1].getDenom());
  //       if (sr[0].getNum() == 1 && sr[0].getDenom() == 1) {
  //         //System.out.println("OUTSIDE FRAC = 1");
  //         if (ncv0.getNum() < 0) {
  //           timesNode.remove(fracNode0);
  //           return timesNode;
  //         }
  //         else {
  //           radicalNode.setParent(null);
  //           return radicalNode;
  //         }
  //       }
  //       //System.out.println("TIMESNODE: " + timesNode);
  //       return timesNode;
  //     }
  //   }
  //   return newNode;
  // }

  public static Node simplifyRecip(Node newNode) {
    Node child = newNode.getChildren().get(0);
    if (child.getOp().equals("fraction")) {
      return new Node(child.getValue().reciprocal());
    }
    return newNode;
  }
}
