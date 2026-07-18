package PAPE2D;

/**
 * Simple enum for options in how a world handles friction coefficient calculation
 */
public enum FrictionCoefficientMethod {
    /**
     * Friction coefficient between two bodies is the product of their friction coefficients
     */
    PRODUCT,
    /**
     * Friction coefficient between two bodies is the minimum of their friction coefficients
     */
    MIN,
    /**
     * Friction coefficient between two bodies is the maximum of their friction coefficients
     */
    MAX,
    /**
     * Friction coefficient between two bodies is the average of their friction coefficients (arithmetic mean)
     */
    AVERAGE,
    /**
     * Friction coefficient between two bodies is the geometric mean of their friction coefficients
     */
    MEAN_GEOMETRIC;

    FrictionCoefficientMethod() {
    }

    public double calculateCoefficient(double f1, double f2) {
        if (this.equals(PRODUCT)) {
            return f1 * f2;
        } else if (this.equals(MIN)) {
            return Math.min(f1,f2);
        } else if (this.equals(MAX)) {
            return Math.max(f1,f2);
        } else if (this.equals(AVERAGE)) {
            return (f1+f2)/2;
        } else if (this.equals(MEAN_GEOMETRIC)) {
            return Math.sqrt(f1*f2);
        }

        return Body.DEFAULT_RESTITUTION; // Default case fall-back
    }
}
