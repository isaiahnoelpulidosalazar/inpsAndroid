package com.isaiahnoelpulidosalazar.inpsAndroid;

import java.util.List;

public class AreaCalculator {

    /**
     * Inner class representing a 2D coordinate point for polygon calculation.
     */
    public static class Point {
        public double x;
        public double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // --- 2D Shapes ---

    /** Calculates the area of a square (S = a^2). */
    public static double squareArea(double a) {
        return Math.pow(a, 2);
    }

    /** Calculates the area of a rectangle (S = a * b). */
    public static double rectangleArea(double a, double b) {
        return a * b;
    }

    /** Calculates the area of a parallelogram (S = a * h). */
    public static double parallelogramArea(double a, double h) {
        return a * h;
    }

    /** Calculates the area of a triangle (S = 1/2 * a * h). */
    public static double triangleArea(double a, double h) {
        return 0.5 * a * h;
    }

    /** Calculates the area of a right triangle (S = 1/2 * a * b). */
    public static double rightTriangleArea(double a, double b) {
        return 0.5 * a * b;
    }

    /** Calculates the area of an equilateral triangle (S = (sqrt(3)/4) * a^2). */
    public static double equilateralTriangleArea(double a) {
        return (Math.sqrt(3) / 4.0) * Math.pow(a, 2);
    }

    /** Calculates the area of a trapezoid (S = ((a + b) / 2) * h). */
    public static double trapezoidArea(double a, double b, double h) {
        return ((a + b) / 2.0) * h;
    }

    /** Calculates the area of a rhombus given diagonals (S = (d1 * d2) / 2). */
    public static double rhombusArea(double d1, double d2) {
        return (d1 * d2) / 2.0;
    }

    /** Calculates square area using its diagonal (S = d^2 / 2). */
    public static double squareAsRhombusArea(double d) {
        return Math.pow(d, 2) / 2.0;
    }

    /** Calculates the area of a circle (S = pi * r^2). */
    public static double circleArea(double r) {
        return Math.PI * Math.pow(r, 2);
    }

    /** Calculates the area of an annulus / ring (S = pi * (R^2 - r^2)). */
    public static double annulusArea(double R, double r) {
        return Math.PI * (Math.pow(R, 2) - Math.pow(r, 2));
    }

    /** Calculates the area of a circular sector. */
    public static double sectorArea(double r, double alphaDegrees) {
        return (alphaDegrees / 360.0) * Math.PI * Math.pow(r, 2);
    }

    /** Calculates the area of a circular segment. */
    public static double segmentArea(double r, double alphaDegrees) {
        double alphaRadians = Math.toRadians(alphaDegrees);
        double sectorPart = (alphaDegrees / 360.0) * Math.PI * Math.pow(r, 2);
        double trianglePart = 0.5 * Math.pow(r, 2) * Math.sin(alphaRadians);
        return sectorPart - trianglePart;
    }

    /** Calculates the area of an ellipse (S = pi * a * b). */
    public static double ellipseArea(double a, double b) {
        return Math.PI * a * b;
    }

    /** Calculates the area of an arbitrary polygon using the Shoelace formula. */
    public static double polygonShoelaceArea(List<Point> vertices) {
        int n = vertices.size();
        if (n < 3) return 0.0;

        double area = 0.0;
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            area += vertices.get(i).x * vertices.get(j).y;
            area -= vertices.get(j).x * vertices.get(i).y;
        }
        return Math.abs(area) / 2.0;
    }

    // --- 3D Surface Areas ---

    /** Calculates total surface area of a cube (S = 6 * a^2). */
    public static double cubeSurfaceArea(double a) {
        return 6.0 * Math.pow(a, 2);
    }

    /** Calculates surface area of a rectangular parallelepiped. */
    public static double rectangularParallelepipedSurfaceArea(double a, double b, double c) {
        return 2.0 * (a * b + a * c + b * c);
    }

    /** Calculates surface area of a cylinder (S = 2 * pi * r * (r + h)). */
    public static double cylinderSurfaceArea(double r, double h) {
        return 2.0 * Math.PI * r * (r + h);
    }

    /** Calculates surface area of a cone (S = pi * r * (r + l)). */
    public static double coneSurfaceArea(double r, double l) {
        return Math.PI * r * (r + l);
    }

    /** Calculates surface area of a frustum of a cone. */
    public static double frustumSurfaceArea(double R, double r, double l) {
        double lateralArea = Math.PI * (R + r) * l;
        double baseAreas = Math.PI * (Math.pow(R, 2) + Math.pow(r, 2));
        return lateralArea + baseAreas;
    }

    /** Calculates total surface area of a sphere (S = 4 * pi * r^2). */
    public static double sphereSurfaceArea(double r) {
        return 4.0 * Math.PI * Math.pow(r, 2);
    }
}