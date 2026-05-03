package PAPE2D;

/**
 * Abstract constraint class
 */
public abstract class Constraint {
    // =================================================================================
    // Attributes
    // =================================================================================

    // Constraint holds references to the bodies it operates on (defined in subclasses)
    private double J = 0;
    private double currentBias;

    // =================================================================================
    // Getters & setters
    // =================================================================================
    public double getCurrentBias() {
        return currentBias;
    }

    // =================================================================================
    // Constraint solving
    // =================================================================================
    public void initConstraint(double beta, double dt) {
        currentBias = calculateBias(beta,dt);
    }

    public void updateConstraint() {
        double J_old = J;
        double deltaJ = calculateDeltaJ();
        J += deltaJ;
        capJ();
        double realDeltaJ = J - J_old;
        updateVelocity(realDeltaJ);
    }

    // Abstract methods
    public abstract double calculateDeltaJ();

    public abstract void capJ();

    public abstract void updateVelocity(double realDeltaJ);

    public abstract double calculateBias(double beta, double dt);
}
