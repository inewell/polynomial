/**
* Author: Isaac Newell
* Description: Visualizes Node instances
*/

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import java.lang.Math;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.List;
import java.util.ArrayList;

public class TreeGraphics extends JFrame
{
  private Node tree;
  private int width = 1200;
  private int height= 750;
  private int hmargin = 20;
  private int numLayers = 12;
  private int drop = height/numLayers;
  private int nodeRad = 12;
  private Font font = new Font("Serif", Font.PLAIN, 12);

  public TreeGraphics(Node n)
  {
    tree = n;

    setSize(width, height);
    setVisible(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }



  public void drawTree(Graphics g, Node root, int xPos, int yPos, int layer)
  {
    drawNode(g, root, xPos, yPos);
    int numCh = root.getChildren().size();
    if (numCh == 0) return;

    int space = (int)((width-2*hmargin)/(Math.pow(2, layer+1)));

    for (int i = 0; i < numCh; i++) {
      int newX = numCh > 1 ? (int)(xPos-space + ((float)(i))/(numCh-1) *2*space) : xPos;
      drawTree(g,
              root.getChildren().get(i),
              newX,
              yPos+drop,
              layer+1);
      g.drawLine(xPos, yPos + nodeRad, newX, yPos+drop-nodeRad);
    }
    // if (root.getLeft() != null)
    // {
    //   drawTree(g, root.getLeft(), xPos-space, yPos+drop, layer+1);
    //   g.drawLine(xPos, yPos+nodeRad, xPos-space, yPos+drop-nodeRad);
    // }
    // if (root.getRight() != null)
    // {
    //   drawTree(g, root.getRight(), xPos+space, yPos+drop, layer+1);
    //   g.drawLine(xPos, yPos+nodeRad, xPos+space, yPos+drop-nodeRad);
    // }
  }

  public void drawNode(Graphics g, Node n, int xPos, int yPos)
  {
    g.setColor(Color.BLACK);
    g.drawOval(xPos-nodeRad, yPos-nodeRad, 2*nodeRad, 2*nodeRad);
    //g.drawString(n.getContents().toString(), xPos - 8, yPos + 3);
    String disp = n.getOp().equals("fraction") ? n.getValue().toString() : n.getOp();

    drawCenteredString(g, disp, xPos-nodeRad, yPos-nodeRad, nodeRad*2, nodeRad*2, font);
  }

  // Draws a string centered around (xCoord, yCoord)
  // Adapted from http://stackoverflow.com/questions/27706197/how-can-i-center-graphics-drawstring-in-java
  public void drawCenteredString(Graphics g, String text, int xCoord, int yCoord, int w, int h, Font font)
  {
    // Get the FontMetrics
    FontMetrics metrics = g.getFontMetrics(font);
    // Determine the X coordinate for the text
    int x = xCoord + ((w - metrics.stringWidth(text)) / 2);
    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
    int y = yCoord + ((h - metrics.getHeight()) / 2) + metrics.getAscent();
    // Set the font
    g.setFont(font);
    // Draw the String
    g.drawString(text, x, y);
  }

  public void paint(Graphics g)
  {
    drawTree(g, tree, width/2, 50, 1);
  }

  public static void main(String[] args)
  {
    // Node n1 = new Node(-1);
    // Node n2 = new Node(new Fraction(2,3));
    // Node n3 = Node.performOp(new Node(3), "radical_2", true);
    //
    // Node n4 = Node.performOp(new Node[] {n1, n3}, "times", true);
    //
    // Node n5 = Node.performOp(new Node[] {n2,n4}, "plus", true);
    //
    // System.out.println(n5);
    // System.out.println(n5.toLatex());


    Node n1 = new Node(new Fraction(27,4));
    Node n2 = new Node("radical_2");
    Node n3 = Node.performOp(n1,"radical_2",true);

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


    TreeGraphics tg1 = new TreeGraphics(n3);
  }
}
