package com.isaiahnoelpulidosalazar.inpsandroid.edu;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class AreaCalculatorTest {

    private static final double EPS = 1e-6;

    @Test
    public void test2dAreas() {
        assertEquals(25.0, AreaCalculator.squareArea(5.0), EPS);
        assertEquals(24.0, AreaCalculator.rectangleArea(4.0, 6.0), EPS);
        assertEquals(15.0, AreaCalculator.triangleArea(5.0, 6.0), EPS);
        assertEquals(Math.sqrt(3), AreaCalculator.equilateralTriangleArea(2.0), EPS);
        assertEquals(20.0, AreaCalculator.trapezoidArea(3.0, 7.0, 4.0), EPS);
        assertEquals(12.0, AreaCalculator.rhombusArea(4.0, 6.0), EPS);
        assertEquals(Math.PI * 9.0, AreaCalculator.circleArea(3.0), EPS);
        assertEquals(Math.PI * 16.0, AreaCalculator.annulusArea(5.0, 3.0), EPS);
        assertEquals(Math.PI * 6.0, AreaCalculator.ellipseArea(2.0, 3.0), EPS);
    }

    @Test
    public void testSectorAndSegment() {
        assertEquals(Math.PI * 4.0 * 0.25, AreaCalculator.sectorArea(2.0, 90.0), EPS);
        double expectedSegment = Math.PI - 2.0;
        assertEquals(expectedSegment, AreaCalculator.segmentArea(2.0, 90.0), EPS);
    }

    @Test
    public void testPolygonShoelaceArea() {
        assertEquals(0.0, AreaCalculator.polygonShoelaceArea(null), EPS);
        assertEquals(0.0, AreaCalculator.polygonShoelaceArea(Collections.emptyList()), EPS);
        assertEquals(0.0, AreaCalculator.polygonShoelaceArea(Arrays.asList(
                new AreaCalculator.Point(0, 0),
                new AreaCalculator.Point(4, 0)
        )), EPS);

        assertEquals(12.0, AreaCalculator.polygonShoelaceArea(Arrays.asList(
                new AreaCalculator.Point(0, 0),
                new AreaCalculator.Point(4, 0),
                new AreaCalculator.Point(4, 3),
                new AreaCalculator.Point(0, 3)
        )), EPS);

        assertEquals(6.0, AreaCalculator.polygonShoelaceArea(Arrays.asList(
                new AreaCalculator.Point(0, 0),
                new AreaCalculator.Point(3, 0),
                new AreaCalculator.Point(0, 4)
        )), EPS);
    }

    @Test
    public void test3dSurfaceAreas() {
        assertEquals(54.0, AreaCalculator.cubeSurfaceArea(3.0), EPS);
        assertEquals(52.0, AreaCalculator.rectangularParallelepipedSurfaceArea(2.0, 3.0, 4.0), EPS);
        assertEquals(2.0 * Math.PI * 2.0 * 7.0, AreaCalculator.cylinderSurfaceArea(2.0, 5.0), EPS);
        assertEquals(Math.PI * 3.0 * 8.0, AreaCalculator.coneSurfaceArea(3.0, 5.0), EPS);
        assertEquals(4.0 * Math.PI * 9.0, AreaCalculator.sphereSurfaceArea(3.0), EPS);
        assertEquals(Math.PI * ((4.0 + 2.0) * 5.0 + 16.0 + 4.0),
                AreaCalculator.frustumSurfaceArea(4.0, 2.0, 5.0), EPS);
    }
}
