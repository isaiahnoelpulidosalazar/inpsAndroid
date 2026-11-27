# AreaCalculator

The `AreaCalculator` utility provides closed-form area and surface-area formulas for common 2D and 3D shapes.

## Purpose & Scope

The purpose of this utility is to provide:
- Ready-to-use geometry formulas with `double` arithmetic
- An immutable `Point` type for polygon area calculation

Inputs are not range-checked: callers are responsible for passing non-negative dimensions. The class is `final` with a private constructor.

### AreaCalculator.Point

An immutable 2D point with `final double x` and `final double y`, created with `new AreaCalculator.Point(x, y)`.

## API Reference

### 2D Shapes

#### static double squareArea(double a)
Area of a square.

```java
double v = AreaCalculator.squareArea(3);
```

#### static double rectangleArea(double a, double b)
Area of a rectangle.

```java
double v = AreaCalculator.rectangleArea(3, 4);
```

#### static double parallelogramArea(double a, double h)
Area of a parallelogram from base and height.

```java
double v = AreaCalculator.parallelogramArea(3, 4);
```

#### static double triangleArea(double a, double h)
Area of a triangle from base and height.

```java
double v = AreaCalculator.triangleArea(3, 4);
```

#### static double rightTriangleArea(double a, double b)
Area of a right triangle from its legs.

```java
double v = AreaCalculator.rightTriangleArea(3, 4);
```

#### static double equilateralTriangleArea(double a)
Area of an equilateral triangle from its side.

```java
double v = AreaCalculator.equilateralTriangleArea(3);
```

#### static double trapezoidArea(double a, double b, double h)
Area of a trapezoid from parallel sides and height.

```java
double v = AreaCalculator.trapezoidArea(3, 4, 5);
```

#### static double rhombusArea(double d1, double d2)
Area of a rhombus from its diagonals.

```java
double v = AreaCalculator.rhombusArea(3, 4);
```

#### static double squareAsRhombusArea(double d)
Area of a square from its diagonal.

```java
double v = AreaCalculator.squareAsRhombusArea(3);
```

#### static double circleArea(double r)
Area of a circle.

```java
double v = AreaCalculator.circleArea(3);
```

#### static double annulusArea(double R, double r)
Area of a ring between outer radius `R` and inner radius `r`.

```java
double v = AreaCalculator.annulusArea(3, 4);
```

#### static double ellipseArea(double a, double b)
Area of an ellipse from its semi-axes.

```java
double v = AreaCalculator.ellipseArea(3, 4);
```

#### static double sectorArea(double r, double alphaDegrees)
Area of a circular sector with central angle in degrees.

```java
double v = AreaCalculator.sectorArea(3, 4);
```

#### static double segmentArea(double r, double alphaDegrees)
Area of a circular segment (sector minus triangle) with central angle in degrees.

```java
double v = AreaCalculator.segmentArea(3, 4);
```

#### static double polygonShoelaceArea(List<Point> vertices)
Simple-polygon area by the shoelace formula. Returns `0` for `null` or fewer than 3 vertices.

```java
double a = AreaCalculator.polygonShoelaceArea(Arrays.asList(
    new AreaCalculator.Point(0, 0), new AreaCalculator.Point(1, 0),
    new AreaCalculator.Point(1, 1), new AreaCalculator.Point(0, 1))); // 1.0
```

---

### 3D Surface Areas

#### static double cubeSurfaceArea(double a)
Surface area of a cube.

```java
double v = AreaCalculator.cubeSurfaceArea(3);
```

#### static double rectangularParallelepipedSurfaceArea(double a, double b, double c)
Surface area of a rectangular box.

```java
double v = AreaCalculator.rectangularParallelepipedSurfaceArea(3, 4, 5);
```

#### static double cylinderSurfaceArea(double r, double h)
Total surface area of a cylinder.

```java
double v = AreaCalculator.cylinderSurfaceArea(3, 4);
```

#### static double coneSurfaceArea(double r, double l)
Total surface area of a cone, where `l` is the slant height.

```java
double v = AreaCalculator.coneSurfaceArea(3, 4);
```

#### static double sphereSurfaceArea(double r)
Surface area of a sphere.

```java
double v = AreaCalculator.sphereSurfaceArea(3);
```

#### static double frustumSurfaceArea(double R, double r, double l)
Lateral area plus both bases of a cone frustum, where `l` is the slant height.

```java
double v = AreaCalculator.frustumSurfaceArea(3, 4, 5);
```
