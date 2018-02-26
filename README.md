# Java Model for Solving Polynomials
 
## Introduction
Quadratics, cubics, and quartics are always solvable by radicals. Computing their solutions numerically is actually quite straightforward using Cardano's, Ferrari's, or Lagrange's methods. The task can be accomplished in just a few lines of code. However, I sought to accomplish with this project something more meaningful: a solution in exact mathematical expressions. Rather than merely performing a series of numerical operations on complex numbers at each step of an algorithm, ignorant of the mathematical structure of the expressions at hand, this approach actually seeks to use and express that underlying structure. That means that this model, which was implemented in the object-oriented programming language Java, has to be able to represent, simplify, and display mathematical expressions: a much more complicated and deeper task than just dealing with decimal complex numbers.

## Representation of Mathematical Expressions
How do you represent a mathematical expression? If you are dealing exclusively with rational numbers, it is quite easy. Any number can just be represented as the quotient of two integers. If you save those two integers, and know that they represent the numerator and denominator of a fraction, then you know the underlying structure of the number. However, what if you move up from just $\mathbb{Q}$ to include radicals? Say, for example, that you choose to only represent numbers that can be expressed in the form $x = a + b\sqrt{2}$, where $a$ and $b$ are rational numbers. Then, if you keep track of $a$ and $b$, you know the value of the number as well. Or, what if you choose to include complex numbers, which can be represented in a similar fashion? Similarly, by keeping track of a few numbers and knowing their roles in the expression, we have a representation that a computer program can save and use. But what if we have no guarantee of the form that our expression takes? What if we have an expression like the following? 

<img src="http://latex.codecogs.com/svg.latex?x=-\frac{2}{3}+{\left(-\frac{1}{2}+i\sqrt{3}\frac{1}{2}\right)}^{2}\sqrt[3]{\frac{10}{27}+\sqrt{3}i\frac{1}{3}}+\left(-\frac{1}{2}+i\sqrt{3}\frac{1}{2}\right)\sqrt[3]{\frac{10}{27}-\frac{1}{3}\sqrt{3}i}" border="0"/>

This expression consists of operations upon operations, radicals inside radicals, and complex and irrational numbers in a complicated arrangement. Quickly we see that when we deal with expressions such as these, it is foolish to rely on a constrained format that constitutes an ever-more-convoluted and nonsensical representation of the data. We need instead an adaptive model that knows the structure of the data, and holds it in a way that is conducive to evaluation of and manipulation of its structure. This means we have to think more deeply about what a mathematical expression really is.

A mathematical expression is basically a computational graph or tree of operations. It represents numbers, symbols, and operations performed on them. The fundamental unit of my Java model is the Node, an object that represents either a constant or an operation. If it represents an operation, it has pointers to a list of other Node objects, which may in turn be operations or constants.

The Node objects in my Java model use pointers to other nodes to form a computational tree. This just means that a Node can store references to other Nodes, which are its "parent" and "children". These references allow us to navigate the tree and evaluate its contents. Each node represents an expression, and an equivalent exact value. This style of representation is inherently recursive. A Node is just the base of a tree, the nodes of which are also Nodes. To evaluate the exact value of a node, for example, we just perform the operation that the node entails on the recursively calculated value of its children nodes.

So what does this more intelligent data structure look like? Let us examine the messy expression outlined earlier in \ref{eq:1}. For this expression, the representation looks like this:

\begin{figure}[H]
\caption{The computational tree equivalent to equation \ref{eq:1}}.
\includegraphics[width=\textwidth]{pic1}
\end{figure}

We can see here that each node connotes either a number or an operation. Importantly, all operational nodes are commutative; that is, it doesn't matter the order of their children nodes in performing the operation. Nodes that are rational numbers or the imaginary number $i = \sqrt{-1}$ are terminal, i.e. they have no children.

## Overview of the Node Class
Nodes represent operations or numbers. Even the nodes that represent operations, of course, have a number that they evaluate to, as a function of their children nodes. This overview will give a sense of what the Nodes exactly represent and what they can do. 

Each Node object has four fields. They are:

* **op** (of class String). This is the operation that the node represents. It can take any of the following values, all referring to commutative operations. This is important. They are:
    * **Fraction**: This is a rational number, stored as an instance of the Fraction class. That is the basic unit of exact scalar used in my model.
    * **Times**: This is a multiplication node. It multiplies the values of all its children nodes.
    * **Plus**: This is an addition node. It adds the values of all its children nodes.
    * **Power**: This node can only have one child. It performs the exponent function, to an integer power saved in the String. For instance, if it represented squaring its children, it would take the value "power_2".
    * **Radical**: This node can also only have one child. It performs the radical function. It extracts a specified root of the child node, which it saves in its value, i.e. a cube root node has op = "radical_3".
    * **Reciprocal**: This node takes the reciprocal of its single child node. If the child node is a fraction, that is quite simple. The purpose of this node is to allow division to take place in the computational tree while only using commutative operations.
    * **i**: This node represents the imaginary square root of -1. 
* **value**: This is the value of a Node. It is specified only if the node has op = "fraction". It is of class Fraction.
* **parent**: This is a pointer to the node's parent node. While I didn't use this much, if at all, for the tasks I applied this data-representation model to, it could be useful for other purposes.
* **children**: This is an ArrayList of children nodes upon which to perform the node's operation. An ArrayList is a Java data structure that is good for adding and removing elements, as it doesn't have a fixed size like an array. This field is not set until after the Node is instantiated. Connecting one Node to another is done by setting pointers between them with the children and parent fields, after the involved nodes have been created.

## The Challenges of Manipulating the Data Tree
So now we understand what it means to represent a tree of operations with Node objects. But how are these operations performed? And how does the tree simplify itself so as to actually render itself useful?

I will begin this section by admitting that my model lacks both the computational power and advanced features of WolframAlpha's incredible computation algorithm. However, it can perform mathematical operations on Nodes with some basic simplification functionality.

To perform an operation on a Node or an ArrayList of Nodes, the model sets those nodes as children of a newly created operation node, and then follows a series of basic simplification algorithms. Performing operations is basically a rearrangement of pointers between Nodes. For example, to add two fraction Nodes (with their op field set to "fraction"), we proceed in three steps:

1. Begin with two nonconnected fraction Nodes.
2. Create a new Node with the op field set to "plus".
3. Pass the plus Node to a simplifying algorithm that recognizes that it can be reduced to just one fraction total. This returns a new fraction Node.

While there are many ways to simplify the computational graph (such as distributing constants in multiplication, summing common terms, etc.), this model only performs a few simple but essential ones. They include:

* **Combining constant terms under addition and multiplication**. When you perform a multiplication operation on an array of Node objects, you pass those objects to a newly created "times" Node. That node then looks through all of its children and combines all the "fraction" Nodes into one. If there are no child nodes that are not simple "fraction" Nodes, then the simplifier actually returns a "fraction" Node rather than a "times" Node or "plus" Node. This can be visualized as follows:

* **Recognizing zeros in multiplication**. This one is kind of a corollary of the previous simplification algorithm. However, it is an important special case, and is slightly different. When the model performs a multiplication operation an an array of Node objects, it checks if any of them are "fraction" Nodes with their value field set to the zero Fraction object. If so, then the whole product, no matter what the other factors are, can be reduced to one "fraction" Node with value 0.
* **Collapsing multi-layered times and plus operations**. Basically what this does is prevent very deep trees from occurring. In other words, if you perform multiplication or addition on some Node objects, then the simplification algorithm checks if any of those child Nodes have the same op field as the newly created operation Node. If they do, then (in the case of addition), the children of the child "plus" Node will be added to the parent "plus" Node, and the child to which they were previously attached gets deleted.
* **Simplifying radicals**. When the model performs a root extraction on a Node (this operation is unary), it checks to see if it can simplify the expression into a more readable and reasonable format. That is, if you are taking a square root, it seeks to factor out perfect squares from the radicand, and get radicals out of the denominator. This simplification only applies when the direct child of the newly created "radical_p" Node has op = "fraction". 

\begin{center}
\begin{tabular}{|c|c|}
\hline
Unsimplified Node structure & Structure after simplification \\
\hline\hline
\includegraphics[width=0.5\textwidth,height=1.8cm]{addcombineBad1} & \includegraphics[width=0.5\textwidth,height=1.8cm]{addcombineGood1} \\
\hline
\includegraphics[width=0.5\textwidth,height=1.8cm]{addcombineBad2} & \includegraphics[width=0.5\textwidth,height=1.8cm]{addcombineGood2} \\
\hline
\includegraphics[width=0.5\textwidth,height=1.8cm]{zeroMultBad} & \includegraphics[width=0.5\textwidth,height=1.8cm]{zeroMultGood} \\
\hline
\includegraphics[width=0.5\textwidth,height=1.8cm]{collapseBad} & \includegraphics[width=0.5\textwidth,height=1.8cm]{collapseGood} \\
\hline
\includegraphics[width=0.5\textwidth,height=1.8cm]{radicalBad} & \includegraphics[width=0.5\textwidth,height=1.8cm]{radicalGood} \\
\hline
\end{tabular}
\end{center}

## Overview of the all the classes

* **Node.java**: This represents an operation or constant, and a mathematical expression in itself. As explained earlier, the Node object has pointers to other Nodes, upon which it performs its operation. This class provides the functionality for manipulating pointers between Nodes and attaching them to each other. It also has a method that writes a Node into \LaTeX\ code using recursion, and a similarly recursive method that evaluates the Node's numerical value as a complex number (in the form of an instance of the Complex class).
* **Fraction.java**: This is a representation of a fraction, and deals with the math associated with fractions. For example, a Fraction instance is always in the most reduced form, and throws an exception when the denominator is set to 0. Methods in this class allow the user to multiply, add, divide, subtract, and take powers and roots of fractions, in the most reduced form.
* **Polynomial.java**: This is a representation of a polynomial. It has an array, of fixed size, of Node coefficients. This class has methods that perform necessary mathematical operations on polynomials. That includes a method that multiplies Polynomial instances and returns a new Polynomial, according to the formula $\sum_{k=0}{n}{a_kx^k} \times \sum_{k=0}^{m}{b_kx^k} = \sum_{k=0}{n+m}{c_k x^k}$ where $c_k = \sum_{i+j=k}{a_i b_j}$. It also handles polynomial addition, and can divide out a known root of the polynomial, returning a Polynomial of degree one lower.
* **Complex.java**: This is a representation of complex numbers. It is used to evaluate the exact value of Nodes, among other things. It stores the real and imaginary parts in doubles, or decimals. This class can handle multiplication of complex numbers, taking conjugates, i.e. $a +bi \to a-bi$, and taking roots and powers of them. Importantly, when taking odd roots, i.e. cube roots, if the Complex object has a negative real part and zero imaginary part (within a tiny threshold because of floating-point math errors), then it takes the real root rather than a primitive root.
* **TreeGraphics.java**: This allows you to visualize a Node object as a tree. It was immensely useful for debugging, but is also useful for understanding the approach and conceptualizing the representation of numbers.
* **Simplify.java**: This class holds all the aforementioned simplification algorithms that are called whenever operations are performed on Nodes. Refer to the previous section to see what that entails.
* **Solver.java**: This is the superclass for all of the classes that use specific solving algorithms. That means that those classes inherit methods from Solver.java, and are forced to implement its abstract methods. The main use of this class is to handle the input-output (IO), i.e. writing to files, and to take in and process user command-line input.
* **Quadratic.java**: This class implements the quadratic formula to solve a quadratic Polynomial instance. It takes user input in the command-line to get the coefficients, and then writes the solution in \LaTeX\ code to a text file. It simplifies as much as possible.
* **Cardano.java**: This class implements Cardano's method to solve cubics. It takes user input in the command-line to get the coefficients, and then writes the solution in \LaTeX\ code to a text file. It gives an intelligent walkthrough of the method of solution, and can respond differently to different cases. For example, if the coefficient on the linear term is zero in the depressed cubic, then it knows that the problem is simpler, and just returns $y = \sqrt[3]{-q}$, to use the terminology used previously in this paper in the section on Cardano. This solution also checks the value of the discriminant and then says what that means for the realness of the roots. Furthermore, once it finds the expressions for the solutions, even if they are cumbersome it can attempt to simplify. Knowing that $x_1 = u+v$ is always real, it calculates its numeric value and then checks if $n x_1 \in \mathbb{Z}$ for $n\in [1,\dots,200]$. In other words, it checks if the first solution is rational for a small range of denominators. If it is, then the algorithm divides that rational root out and then solves for the other roots as a quadratic; this gives much more simple yet still exact solutions if the cubic has a real rational root. If the algorithm can not simplify its solutions, then it provides numerical solutions.
* **Ferrari.java**: This class implements Ferrari's method to solve quartics. It takes user input in the command-line to get the coefficients, and then writes the solution in \LaTeX\ code to a text file. It gives an intelligent walkthrough of the method of solution, and can respond differently to different cases. For example, if the linear term is zero in the depressed quartic, then it recognizes that the problem can be solved as a biquadratic, with the substitution $z=y^2$. When it can't do that simplified case, the Ferrari method calls the Cardano class to solve the cubic equation for the auxiliary parameter $\alpha$ (see the previous section on Ferrari's method). That solution may or may not be simplified (depending on if the cubic has a nonzero, real, rational root). If it is not, then the solution to the quartic is incredibly messy and the solver knows to format it accordingly in tiny print. Like with Cardano, if this algorithm doesn't sufficiently simplify the solution, it evaluates it numerically.
* **Depress.java**: This class handles the depressing of a Polynomial object. It applies the substitution $x=y-\frac{a_{n-1}}{n}$ and then returns the depressed polynomial in a new Polynomial instance. A Depress instance also handles the shifting back after the depressed polynomial has been solved.
* **LagrangeCubic.java**: This class solves implements Lagrange's method to solve cubics. It takes user input in the command-line to get the coefficients, and then writes the solution in \LaTeX\ code to a text file. It gives an intelligent walkthrough of the method of solution.
