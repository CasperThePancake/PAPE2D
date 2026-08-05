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
    public abstract void initConstraint(double beta, double dt);

    public abstract void updateConstraint();

    public abstract void updatePseudoConstraint();

    public abstract void resetConstraint(World linkedWorld);
}
