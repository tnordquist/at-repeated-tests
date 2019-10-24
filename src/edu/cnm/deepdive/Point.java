package edu.cnm.deepdive;

import java.util.Comparator;
import java.util.Objects;

/**
 * Point in 2-dimensional Euclidean space. An instance of this class can be created by specifying
 * its location in Cartesian or polar coordinates, or as a copy of an existing instance.
 *
 * @author Nicholas Bennett &amp; Deep Dive Coding
 */
public final class Point implements Comparable<Point> {

  /**
   * Origin of Cartesian coordinate system.
   */
  public static final Point ORIGIN = fromXY(0, 0);
  /**
   * {@link Comparator Comparator&lt;Point&gt;} ordering on X, then Y.
   */
  public static final Comparator<Point> XY_COMPARATOR =
      Comparator.comparingDouble(Point::getX).thenComparingDouble(Point::getY);
  /**
   * {@link Comparator Comparator&lt;Point&gt;} ordering on Y, then X.
   */
  public static final Comparator<Point> YX_COMPARATOR =
      Comparator.comparingDouble(Point::getY).thenComparingDouble(Point::getX);
  /**
   * {@link Comparator Comparator&lt;Point&gt;} ordering on {@code Math.abs(X) + Math.abs(Y)}.
   */
  public static final Comparator<Point> MANHATTAN_COMPARATOR =
      Comparator.comparingDouble((p) -> Math.abs(p.x) + Math.abs(p.y));

  private static final double TAU = Math.PI * 2;
  private static final String STRING_PATTERN = "Point(%.15f, %.15f)";

  private final double x;
  private final double y;
  private final double r;
  private final double theta;
  private final int hashCode;
  private final boolean cartesianCentric;

  /**
   * Creates and returns a new {@code Point} instance, identical in state to {@code other}. In fact,
   * this operation is of limited value, since instances of this class are immutable: copying a
   * reference would be just as useful as copying an entire instance.
   *
   * @param source {@code Point} to be copied.
   * @return copy of {@code other}.
   */
  public static Point fromPoint(Point source) {
    return new Point(source);
  }

  /**
   * Creates and returns a new {@code Point} instance, located at the specified {@code (x, y)}
   * coordinate location.
   *
   * @param x abscissa (first coordinate).
   * @param y ordinate (second coordinate).
   * @return new {@code Point} instance located at {@code (x, y)}.
   */
  public static Point fromXY(double x, double y) {
    return new Point(x, y, null, null);
  }

  /**
   * Creates and returns a new {@code Point} instance, located at the specified location, stated in
   * polar coordinates {@code (r, theta)}.
   *
   * @param r distance from origin.
   * @param theta counter-clockwise angle, measured from positive _X_-axis.
   * @return new {@code Point} instance located at polar coordinates {@code (r, theta)}.
   */
  public static Point fromPolar(double r, double theta) {
    return new Point(null, null, r, theta);
  }


   Point(Point source) {
    cartesianCentric = source.cartesianCentric;
    x = source.x;
    y = source.y;
    r = source.r;
    theta = source.theta;
    hashCode = source.hashCode;
  }

   Point(Double x, Double y, Double r, Double theta) {
    cartesianCentric = x != null && y != null;
    if (cartesianCentric) {
      this.x = x;
      this.y = y;
      this.r = Math.hypot(x, y);
      theta = Math.atan2(y, x);
      this.theta = (theta < 0) ? theta + TAU : theta;
    } else {
      if (r > 0) {
        this.r = r;
        this.theta = normalize(theta);
      } else if (r < 0) {
        this.r = -r;
        this.theta = reflect(theta);
      } else {
        this.r = 0;
        this.theta = 0;
      }
      this.x = this.r * Math.cos(this.theta);
      this.y = this.r * Math.sin(this.theta);
    }
    hashCode = Objects.hash(x, y);
  }

  /**
   * Returns the abscissa (first, or _X_ coordinate) of this instance.
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the ordinate (second, or _Y_ coordinate) of this instance.
   */
  public double getY() {
    return y;
  }

  /**
   * Returns Cartesian coordinates of this instance as a {@code double[]} with {@code length} 2.
   */
  public double[] getCoordinates() {
    return new double[]{x, y};
  }

  /**
   * Returns this instance's distance from origin.
   */
  public double getR() {
    return r;
  }

  /**
   * Returns the angle formed between the positive _X_-axis and the line segment connecting the
   * origin and this instance, measured in counter-clockwise radians. This method differs from
   * {@link Math#atan2(double, double) Math.atan2(Math.getY(), Math.getX())} in just one key
   * respect: the return value of {@link Math#atan2(double, double)} is in the range [-&#960;,
   * &#960;], while the return value of this method is in the range [0, 2&#960;).
   *
   * @return polar angle, in the range [0, 2&#960;).
   */
  public double getTheta() {
    return theta;
  }

  /**
   * Creates and new returns a {@code Point} instance that is the vector sum of this instance and
   * {@code other}.
   *
   * @param other {@code Point} to be added to this instance.
   * @return {@code this + other}, as vector sum.
   */
  public Point add(Point other) {
    return (theta == other.theta) ? fromPolar(r + other.r, theta)
        : fromXY(x + other.x, y + other.y);
  }


  /**
   * Creates and new returns a {@code Point} instance that is the vector difference of this instance
   * and {@code other}. Invoking this method is equivalent to invoking {@code
   * add(other.multiply(-1))}.
   *
   * @param other {@code Point} to be subtracted from this instance.
   * @return {@code this - other}, as vector difference.
   */
  public Point subtract(Point other) {
    return (theta == other.theta) ? fromPolar(r - other.r, theta)
        : fromXY(x - other.x, y - other.y);
  }

  /**
   * Creates and new returns a {@code Point} instance that is the product of the {@code scale} (a
   * scalar) and this instance (a vector).
   *
   * @param scale scalar multiplicative factor.
   * @return {@code scale * this}.
   */
  public Point multiply(double scale) {
    Point result;
    if (cartesianCentric) {
      result = fromXY(x * scale, y * scale);
    } else if (scale >= 0) {
      result = fromPolar(r * scale, theta);
    } else {
      result = fromPolar(-r * scale, reflect(theta));
    }
    return result;
  }

  /**
   * Creates and new returns a {@code Point} instance that is the product of the {@code 1/scale} (a
   * scalar) and this instance (a vector). Invoking this method is equivalent to invoking {@code
   * multiply(1 / scale)}.
   *
   * @param scale scalar multiplicative factor.
   * @return {@code (1 / scale) * this}.
   */
  public Point divide(double scale) {
    return multiply(1 / scale);
  }

  /**
   * Computes and returns the dot product of this instance and {@code other}. (The dot product is
   * the scalar sum of pairwise coordinate products: {@code this.getX() * other.getX() + this.getY()
   * * other.getY()}.
   *
   * @return {@code this} &#183; {@code other}.
   */
  public double dot(Point other) {
    return x * other.x + y * other.y;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    boolean equals = false;
    if (obj == this) {
      equals = true;
    } else if (obj instanceof Point && ((Point) obj).hashCode == hashCode) {
      Point other = (Point) obj;
      if (!other.cartesianCentric && !cartesianCentric) {
        equals = other.r == r && other.theta == theta;
      } else {
        equals = other.x == x && other.y == y;
      }
    }
    return equals;
  }

  @Override
  public String toString() {
    return String.format(STRING_PATTERN, x, y);
  }

  @Override
  public int compareTo(Point other) {
    return Double.compare(r, other.r);
  }

  private double normalize(double theta) {
    theta %= TAU;
    if (theta < 0) {
      theta += TAU;
    }
    return theta;
  }

  private double reflect(double theta) {
    return normalize(theta + Math.PI);
  }

  /**
   * Implementation of {@link Comparator Comparator&lt;Point&gt;} which orders {@link Point}
   * instances by _X_ coordinate, then (if the _X_ coordinates are equal) by _Y_ coordinate.
   */
  public static class XYComparator implements Comparator<Point> {

    @Override
    public int compare(Point p1, Point p2) {
      int comparison = Double.compare(p1.x, p2.x);
      if (comparison == 0) {
        comparison = Double.compare(p1.y, p2.y);
      }
      return comparison;
    }

  }

  /**
   * Implementation of {@link Comparator Comparator&lt;Point&gt;} which orders {@link Point}
   * instances by _Y_ coordinate, then (if the _Y_ coordinates are equal) by _X_ coordinate.
   */
  public static class YXComparator implements Comparator<Point> {

    @Override
    public int compare(Point p1, Point p2) {
      int comparison = Double.compare(p1.y, p2.y);
      if (comparison == 0) {
        comparison = Double.compare(p1.x, p2.x);
      }
      return comparison;
    }

  }

  /**
   * Implementation of {@link Comparator Comparator&lt;Point&gt;} which orders {@link Point}
   * instances by their sum of the absolute coordinate values.
   */
  public static class ManhattanComparator implements Comparator<Point> {

    @Override
    public int compare(Point p1, Point p2) {
      return Double.compare(Math.abs(p1.x) + Math.abs(p1.y), Math.abs(p2.x) + Math.abs(p2.y));
    }

  }

}
