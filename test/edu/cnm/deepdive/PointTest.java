package edu.cnm.deepdive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

class PointTest {

  /**
   * Whenever it is invoked on the same object more than once during an execution of a Java
   * application, hashCode() must consistently return the same value, provided no information used
   * in equals comparisons on the object is modified. This value needs not remain consistent from
   * one execution of an application to another execution of the same application
   *
   * If two objects are equal according to the equals(Object) method, then calling the hashCode()
   * method on each of the two objects must produce the same value
   *
   * Objects that are equal (according to their equals()) must return the same hash code. It's not
   * required for different objects to return different hash codes.
   */
  @RepeatedTest(3)
  void hashCodeEqual(RepetitionInfo repetitionInfo) {
    repetitionInfo.getTotalRepetitions();
    double x = 1;
    double y = .8;
    assertEquals(Point.fromXY(x, y).hashCode(), Point.fromXY(x, y).hashCode());

  }

  @RepeatedTest(3)
  void cartesianEquals() {
    double x = 1;
    double y = 1;
    System.out.println("hashcode" + Point.fromXY(x, y).hashCode());
    System.out.println("hashcode on same object as above " + Point.fromXY(x, y).hashCode());
    assertEquals(Point.fromXY(x, y), Point.fromXY(x, y));
  }

  /**
   * Objects that are equal (according to their equals()) must return the same hash code. It's not
   * required for different objects to return different hash codes.
   */
  @RepeatedTest(3)
  void hashCodeNotEqual() {
    double x1 = 4;
    double y1 = 3;
    double x2 = 4;
    double y2 = 3;
    Point p1 = Point.fromXY(x1, y1);
    Point p2 = Point.fromXY(x2, y2);
    if (p1.hashCode() != p2.hashCode()) {
      assertNotEquals(p1, p2);
    }
    System.out.println(p1.hashCode());
    System.out.println(p2.hashCode());
  }
}
