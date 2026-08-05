package PAPE2D;

/**
 * Specific type of constraint that is always active, independent of the current frame
 */
public abstract class ScalarDynamicConstraint extends DynamicConstraint {
    // =================================================================================
    // Attributes
    // =================================================================================

    // Constraint holds references to the bodies it operates on (defined in subclasses)
    protected double J = 0;
    protected double pseudoJ = 0;
    private double currentBias;

    // =================================================================================
    // Getters & setters
    // =================================================================================
    public double getCurrentBias() {
        return currentBias;
    }

    public double getJ() {
        return J;
    }

    // =================================================================================
    // Scalar implementation
    // =================================================================================

    public abstract void capJ();

    public abstract void capPseudoJ();

    public void initConstraint(double beta, double dt) {
        currentBias = calculateBias(beta,dt);
        J = 0; // No warm-starting (if you do add this, make sure to add that contribution of starting non-zero J here)
        pseudoJ = 0; // Never warm-start for pseudo-velocities!
    }

    public void updateConstraint() {
        double J_old = J;
        double deltaJ = calculateDeltaJ();
        J += deltaJ;
        capJ();
        double realDeltaJ = J - J_old;
        updateVelocity(realDeltaJ);
    }

    public void updatePseudoConstraint() {
        double pseudoJ_old = pseudoJ;
        double pseudoDeltaJ = calculatePseudoDeltaJ();
        pseudoJ += pseudoDeltaJ;
        capPseudoJ();
        double realPseudoDeltaJ = pseudoJ - pseudoJ_old;
        updatePseudoVelocity(realPseudoDeltaJ);
    }

    public abstract void resetConstraint(World world);

    // Abstract methods
    public abstract double calculateDeltaJ();

    public abstract double calculatePseudoDeltaJ();

    public abstract void updateVelocity(double realDeltaJ);

    public abstract void updatePseudoVelocity(double realPseudoDeltaJ);

    public abstract double calculateBias(double beta, double dt);
}
