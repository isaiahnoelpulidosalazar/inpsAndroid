package com.isaiahnoelpulidosalazar.inpsandroid.edu;

import java.util.List;

/**
 * Closed-form 2D area and 3D surface-area formulas.
 *
 * <p>Callers are responsible for passing non-negative dimensions.
 */
public final class AreaCalculator {

  private static final double SQRT_3 = Math.sqrt(3.0);

  private AreaCalculator() {
    throw new UnsupportedOperationException("No instances");
  }

  /** Immutable 2D Cartesian point used by {@link #polygonShoelaceArea(List)}. */
  public static final class Point {
    /** X coordinate. */
    public final double x;

    /** Y coordinate. */
    public final double y;

    /**
     * Creates a 2D point.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     */
    public Point(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }

  private static double sq(double v) {
    return v * v;
  }

  // --- 2D shapes ---

  /**
   * Area of a square ({@code a^2}).
   *
   * @param a side length
   * @return area of the square
   */
  public static double squareArea(double a) {
    return sq(a);
  }

  /**
   * Area of a rectangle or parallelogram ({@code a * b}).
   *
   * @param a base length
   * @param b height or adjacent side length
   * @return area of the rectangle
   */
  public static double rectangleArea(double a, double b) {
    return a * b;
  }

  /**
   * Area of a triangle ({@code 0.5 * a * h}).
   *
   * @param a base length
   * @param h perpendicular height
   * @return area of the triangle
   */
  public static double triangleArea(double a, double h) {
    return 0.5 * a * h;
  }

  /**
   * Area of an equilateral triangle ({@code (sqrt(3) / 4) * a^2}).
   *
   * @param a side length
   * @return area of the equilateral triangle
   */
  public static double equilateralTriangleArea(double a) {
    return (SQRT_3 / 4.0) * sq(a);
  }

  /**
   * Area of a trapezoid ({@code ((a + b) / 2) * h}).
   *
   * @param a first parallel base length
   * @param b second parallel base length
   * @param h perpendicular height
   * @return area of the trapezoid
   */
  public static double trapezoidArea(double a, double b, double h) {
    return (a + b) / 2.0 * h;
  }

  /**
   * Area of a rhombus from its diagonals ({@code d1 * d2 / 2}).
   *
   * @param d1 length of the first diagonal
   * @param d2 length of the second diagonal
   * @return area of the rhombus
   */
  public static double rhombusArea(double d1, double d2) {
    return d1 * d2 / 2.0;
  }

  /**
   * Area of a circle ({@code pi * r^2}).
   *
   * @param r circle radius
   * @return area of the circle
   */
  public static double circleArea(double r) {
    return Math.PI * sq(r);
  }

  /**
   * Area of an annulus/ring ({@code pi * (outerRadius^2 - r^2)}).
   *
   * @param outerRadius outer radius
   * @param r inner radius
   * @return area of the ring
   */
  public static double annulusArea(double outerRadius, double r) {
    return Math.PI * (sq(outerRadius) - sq(r));
  }

  /**
   * Area of an ellipse ({@code pi * a * b}).
   *
   * @param a semi-major axis length
   * @param b semi-minor axis length
   * @return area of the ellipse
   */
  public static double ellipseArea(double a, double b) {
    return Math.PI * a * b;
  }

  /**
   * Area of a circular sector with central angle in degrees.
   *
   * @param r circle radius
   * @param alphaDegrees central angle in degrees
   * @return area of the sector
   */
  public static double sectorArea(double r, double alphaDegrees) {
    return alphaDegrees / 360.0 * Math.PI * sq(r);
  }

  /**
   * Area of a circular segment (sector minus chord triangle) with central angle in degrees.
   *
   * @param r circle radius
   * @param alphaDegrees central angle in degrees
   * @return area of the circular segment
   */
  public static double segmentArea(double r, double alphaDegrees) {
    double sector = sectorArea(r, alphaDegrees);
    double triangle = 0.5 * sq(r) * Math.sin(Math.toRadians(alphaDegrees));
    return sector - triangle;
  }

  /**
   * Simple polygon area via the shoelace formula.
   *
   * @param vertices ordered polygon vertices, may be {@code null}
   * @return polygon area, or {@code 0.0} when {@code vertices} is {@code null} or has fewer than 3
   *     points
   */
  public static double polygonShoelaceArea(List<Point> vertices) {
    if (vertices == null || vertices.size() < 3) return 0.0;
    int n = vertices.size();
    double sum = 0.0;
    Point prev = vertices.get(n - 1);
    for (int i = 0; i < n; i++) {
      Point cur = vertices.get(i);
      sum += prev.x * cur.y - cur.x * prev.y;
      prev = cur;
    }
    return Math.abs(sum) / 2.0;
  }

  // --- 3D surface areas ---

  /**
   * Total surface area of a cube ({@code 6 * a^2}).
   *
   * @param a edge length
   * @return surface area of the cube
   */
  public static double cubeSurfaceArea(double a) {
    return 6.0 * sq(a);
  }

  /**
   * Total surface area of a rectangular prism ({@code 2 * (ab + ac + bc)}).
   *
   * @param a width
   * @param b depth
   * @param c height
   * @return surface area of the prism
   */
  public static double rectangularParallelepipedSurfaceArea(double a, double b, double c) {
    return 2.0 * (a * b + a * c + b * c);
  }

  /**
   * Total surface area of a closed circular cylinder ({@code 2 * pi * r * (r + h)}).
   *
   * @param r base radius
   * @param h height
   * @return total surface area of the cylinder
   */
  public static double cylinderSurfaceArea(double r, double h) {
    return 2.0 * Math.PI * r * (r + h);
  }

  /**
   * Total surface area of a closed right circular cone ({@code pi * r * (r + l)}).
   *
   * @param r base radius
   * @param l slant height
   * @return total surface area of the cone
   */
  public static double coneSurfaceArea(double r, double l) {
    return Math.PI * r * (r + l);
  }

  /**
   * Surface area of a sphere ({@code 4 * pi * r^2}).
   *
   * @param r sphere radius
   * @return surface area of the sphere
   */
  public static double sphereSurfaceArea(double r) {
    return 4.0 * Math.PI * sq(r);
  }

  /**
   * Total surface area of a conical frustum (lateral area plus both circular bases).
   *
   * @param outerRadius bottom base radius
   * @param r top base radius
   * @param l slant height
   * @return total surface area of the frustum
   */
  public static double frustumSurfaceArea(double outerRadius, double r, double l) {
    return Math.PI * ((outerRadius + r) * l + sq(outerRadius) + sq(r));
  }
}
