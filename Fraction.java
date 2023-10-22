/**
*Isaac Newell
* Represents a fraction with an int numerator and int denominator
* and provides methods for adding and multiplying fractions.
*/

public class Fraction
{
  private int n;
  private int d;

  public Fraction()
  {
    n = 0;
    d = 1;
  }

  public Fraction(int n)
  {
    this.n = n;
    this.d = 1;
  }

  public Fraction(int n, int d)
  {

    if (d != 0)
    {
      this.n = n;
      this.d = d;
      reduce();
    }
    else
    {
      throw new IllegalArgumentException(
           "Fraction construction error: denominator is 0");
    }
  }

  public Fraction(String s) {
    int ind = s.indexOf("/");
    if (ind == -1) {
      this.n = Integer.parseInt(s);
      this.d = 1;
    } else {
      int n1 = Integer.parseInt(s.substring(0,ind));
      int d1 = Integer.parseInt(s.substring(ind+1));
      if (d1 != 0) {
        this.n = n1;
        this.d = d1;
        reduce();
      } else {
        throw new IllegalArgumentException(
             "Fraction construction error: denominator is 0");
      }
    }
  }

  public Fraction(Fraction other)  // copy constructor
  {
    this.n = other.n;
    this.d = other.d;
  }

  // ********************  Public methods  ********************

  public int getNum()
  {
    return n;
  }

  public int getDenom()
  {
    return d;
  }

  // Returns the sum of this fraction and other
  public Fraction add(Fraction other)
  {
    //System.out.println("N: " + other.n + " , D: " + other.d);
    int newNum = n * other.d + d * other.n;
    int newDenom = d * other.d;
    return new Fraction(newNum, newDenom);
  }

  // Returns the sum of this fraction and m
  public Fraction add(int m)
  {
    return new Fraction(n + m * d, d);
  }

  // Returns the difference of this fraction and other
  public Fraction subtract(Fraction other)
  {
    int newNum = n * other.d - d * other.n;
    int newDenom = d * other.d;
    return new Fraction(newNum, newDenom);
  }

  // Returns the difference of this fraction and m
  public Fraction subtract(int m)
  {
    return new Fraction(n - m * d, d);
  }

  // Returns the product of this fraction and other
  public Fraction multiply(Fraction other)
  {
    int newNum = n * other.n;
    int newDenom = d * other.d;
    return new Fraction(newNum, newDenom);
  }

  // Returns the product of this fraction and m
  public Fraction multiply(int m)
  {
    return new Fraction(n * m, d);
  }

  private static int power(int n, int p) {
    int total = 1;
    for (int i = 0; i < p; i++) {
      total *= n;
    }
    return total;
  }

  public Fraction power(int p) {
    return new Fraction(power(n,p), power(d,p));
  }

  // returns an int array; outside rad, inside rad.
  public static int[] simplifyRadical(int r, int p) {
    int sign = r > 0 ? 1 : -1;
    int insideRoot = r * sign;
    int outsideRoot = 1;
    int d = 2;
    while (power(d, p) <= insideRoot) {
      if (insideRoot % power(d, p) == 0) {
        insideRoot /= power(d,p);
        outsideRoot *= d;
      } else {
        d ++;
      }
    }
    return new int[] {outsideRoot, insideRoot};
  }

  // Takes a fraction under the radical and returns an array of fractions,
  // of length 2. The first element is the fraction out of the radical.
  // The second element is the fraction inside the radical.
  public Fraction[] simplifyRadical(int p) {
    int[] nsf = simplifyRadical(n, p);
    int[] dsf = simplifyRadical(d, p);

    Fraction[] output = new Fraction[2];

    output[0] = new Fraction(nsf[0], dsf[0]);
    output[1] = new Fraction(nsf[1], dsf[1]);

    return output;
  }

  // Returns the quotient of this fraction and other
  public Fraction divide(Fraction other)
  {
    if (other.n == 0)
      throw new IllegalArgumentException("Divide by zero");
    int newNum = n * other.d;
    int newDenom = d * other.n;
    return new Fraction(newNum, newDenom);
  }

  // Returns the quotient of this fraction and m
  public Fraction divide(int m)
  {
    if (m == 0)
      throw new IllegalArgumentException("Divide by zero");
    return new Fraction(n, d * m);
  }

  public Fraction reciprocal() {
    if (d == 0) {
      throw new IllegalArgumentException("Divide by zero");
    }
    return new Fraction(d,n);
  }

  //What if n==0?
  public Fraction reciprocal_n() {
    if (n == 0) {
      throw new IllegalArgumentException("Divide by zero");
    }
    return new Fraction(d,n);
  }

  // Returns the value of this fraction as a double
  public double getValue()
  {
    return (double)n / (double)d;
  }

  // Returns a string representation of this fraction
  public String toString()
  {
    if (d == 1)
      return n + "";
    return n + "/" + d;
  }

  public boolean sameAs(int m) {
    return n == m && d == 1;
  }

  public boolean sameAs(Fraction other) {
    return this.n == other.n && this.d == other.d;
  }

  // *******************  Private methods  ********************

  // Reduces this fraction by the gcf and makes d > 0
  private void reduce()
  {
    if (n == 0)
    {
      d = 1;
      return;
    }

    if (d < 0)
    {
      n = -n;
      d = -d;
    }

    int q = gcf(Math.abs(n), d);
    n /= q;
    d /= q;
  }

  // Returns the greatest common factor of two positive integers
  private int gcf(int x, int y)
  {
    if (x <= 0 || y <= 0)
    {
      throw new IllegalArgumentException(
                  "gcf precondition failed: " + x + ", " + y);
    }

    while (y != 0)
    {
      int temp = y;
      y = x % y;
      x = temp;
    }
    return x;
  }
}
