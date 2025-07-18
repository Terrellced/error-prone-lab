/*
 * A sample file for the checker to process.
 */

package edu.appstate.cs.examples;

public class BadNames {
  public void testinglengthname(String s) {
  	System.out.println(s);
  }

  public static void main(String[] args) {
    String m = "This is a message";

    BadNames b = new BadNames();
    b.testinglengthname(m);
  }
}
