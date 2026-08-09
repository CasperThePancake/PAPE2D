package PAPE2D;

/**
 * Abstract constraint class
 */
public abstract class Constraint {
    // =================================================================================
    // Attributes
    // =================================================================================

    // None...

    // =================================================================================
    // Constraint solving
    // =================================================================================
    protected abstract void initConstraint(double beta, double dt);

    protected abstract void updateConstraint();

    protected abstract void updatePseudoConstraint();

    protected abstract void resetConstraint(World linkedWorld);
}
