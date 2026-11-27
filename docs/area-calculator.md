# AreaCalculator

Package: `com.isaiahnoelpulidosalazar.inpsandroid.edu`

`AreaCalculator` provides closed-form 2D area and 3D surface-area formulas with a precomputed `sqrt(3)` constant.

## 2D Formulas

| Method | Formula / Notes |
|---|---|
| `squareArea(double a)` | $a^2$ |
| `rectangleArea(double a, double b)` | $a \times b$ |
| `triangleArea(double a, double h)` | $\frac{1}{2} a h$ |
| `equilateralTriangleArea(double a)` | $\frac{\sqrt{3}}{4} a^2$ |
| `trapezoidArea(double a, double b, double h)` | $\frac{a + b}{2} h$ |
| `rhombusArea(double d1, double d2)` | $\frac{d_1 d_2}{2}$ |
| `circleArea(double r)` | $\pi r^2$ |
| `annulusArea(double R, double r)` | $\pi (R^2 - r^2)$ |
| `ellipseArea(double a, double b)` | $\pi a b$ |
| `sectorArea(double r, double alphaDegrees)` | Circular sector with central angle in degrees |
| `segmentArea(double r, double alphaDegrees)` | Circular segment (sector minus triangle) |
| `polygonShoelaceArea(List<AreaCalculator.Point> vertices)` | Simple polygon area (`0.0` if `null` or fewer than 3 points) |

## 3D Surface-Area Formulas

| Method | Description |
|---|---|
| `cubeSurfaceArea(double a)` | $6a^2$ |
| `rectangularParallelepipedSurfaceArea(double a, double b, double c)` | $2(ab + ac + bc)$ |
| `cylinderSurfaceArea(double r, double h)` | $2\pi r(r + h)$ |
| `coneSurfaceArea(double r, double l)` | $\pi r(r + l)$ (`l` = slant height) |
| `sphereSurfaceArea(double r)` | $4\pi r^2$ |
| `frustumSurfaceArea(double R, double r, double l)` | $\pi ((R + r)l + R^2 + r^2)$ |

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.edu.AreaCalculator;

double circle = AreaCalculator.circleArea(5.0);
double tri = AreaCalculator.equilateralTriangleArea(4.0);
```
