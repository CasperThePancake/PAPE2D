package PAPE2D;

/**
 * Specific type of constraint that is always active, independent of the current frame
 */
public abstract class ScalarStaticConstraint extends StaticConstraint {
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
    protected double getCurrentBias() {
        return currentBias;
    }

    protected double getJ() {
        return J;
    }

    // =================================================================================
    // Scalar implementation
    // =================================================================================

    protected void capJ() {
        // Static constraints generally do not perform J capping, this is only used for contact/friction constraints, which are dynamic constraints.
    }

    protected void capPseudoJ() {
        // Same here!
    }

    protected void initConstraint(double beta, double dt) {
        currentBias = calculateBias(beta,dt);
        J = 0; // No warm-starting (if you do add this, make sure to add that contribution of starting non-zero J here)
        pseudoJ = 0; // Never warm-start for pseudo-velocities!
    }

    protected void updateConstraint() {
        double J_old = J;
        double deltaJ = calculateDeltaJ();
        J += deltaJ;
        capJ();
        double realDeltaJ = J - J_old;
        updateVelocity(realDeltaJ);
    }

    protected void updatePseudoConstraint() {
        double pseudoJ_old = pseudoJ;
        double pseudoDeltaJ = calculatePseudoDeltaJ();
        pseudoJ += pseudoDeltaJ;
        capPseudoJ();
        double realPseudoDeltaJ = pseudoJ - pseudoJ_old;
        updatePseudoVelocity(realPseudoDeltaJ);
    }

    protected abstract void resetConstraint(World world);

    // Abstract methods
    protected abstract double calculateDeltaJ();

    protected abstract double calculatePseudoDeltaJ();

    protected abstract void updateVelocity(double realDeltaJ);

    protected abstract void updatePseudoVelocity(double realPseudoDeltaJ);

    protected abstract double calculateBias(double beta, double dt);
}
