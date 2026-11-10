package com.isaiahnoelpulidosalazar.inpsAndroid;

import java.util.List;

/**
 * Closed-form area and surface-area formulas. Inputs are not range-checked: callers are
 * responsible for passing non-negative dimensions.
 */
public final class AreaCalculator {

    private AreaCalculator() {
        throw new UnsupportedOperationException("AreaCalculator cannot be instantiated");
    }

    /** Immutable 2D point (v1 exposed mutable fields). */
    public static final class Point {
        public final double x;
        public final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static double sq(double v) { return v * v; } // cheaper and exact vs Math.pow(v, 2)

    // --- 2D shapes ---

    public static double squareArea(double a) { return sq(a); }
    public static double rectangleArea(double a, double b) { return a * b; }
    public static double parallelogramArea(double a, double h) { return a * h; }
    public static double triangleArea(double a, double h) { return 0.5 * a * h; }
    public static double rightTriangleArea(double a, double b) { return 0.5 * a * b; }
    public static double equilateralTriangleArea(double a) { return Math.sqrt(3) / 4.0 * sq(a); }
    public static double trapezoidArea(double a, double b, double h) { return (a + b) / 2.0 * h; }
    public static double rhombusArea(double d1, double d2) { return d1 * d2 / 2.0; }
    public static double squareAsRhombusArea(double d) { return sq(d) / 2.0; }
    public static double circleArea(double r) { return Math.PI * sq(r); }
    public static double annulusArea(double R, double r) { return Math.PI * (sq(R) - sq(r)); }
    public static double ellipseArea(double a, double b) { return Math.PI * a * b; }

    /** Circular sector with central angle in degrees. */
    public static double sectorArea(double r, double alphaDegrees) {
        return alphaDegrees / 360.0 * Math.PI * sq(r);
    }

    /** Circular segment (sector minus triangle) with central angle in degrees. */
    public static double segmentArea(double r, double alphaDegrees) {
        double sector = sectorArea(r, alphaDegrees);
        double triangle = 0.5 * sq(r) * Math.sin(Math.toRadians(alphaDegrees));
        return sector - triangle;
    }

    /** Simple polygon area by the shoelace formula; fewer than 3 vertices (or null) gives 0. */
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

    public static double cubeSurfaceArea(double a) { return 6.0 * sq(a); }
    public static double rectangularParallelepipedSurfaceArea(double a, double b, double c) { return 2.0 * (a * b + a * c + b * c); }
    public static double cylinderSurfaceArea(double r, double h) { return 2.0 * Math.PI * r * (r + h); }
    public static double coneSurfaceArea(double r, double l) { return Math.PI * r * (r + l); }
    public static double sphereSurfaceArea(double r) { return 4.0 * Math.PI * sq(r); }

    /** Frustum of a cone: lateral area plus both bases ({@code l} is the slant height). */
    public static double frustumSurfaceArea(double R, double r, double l) {
        return Math.PI * ((R + r) * l + sq(R) + sq(r));
    }
}
