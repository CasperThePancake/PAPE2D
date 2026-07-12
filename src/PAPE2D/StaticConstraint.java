package PAPE2D;

/**
 * Specific type of constraint that is always active, independent of the current frame
 */
public abstract class StaticConstraint extends Constraint {
    @Override
    public void capJ() {
        // Static constraints generally do not perform J capping, this is only used for contact/friction constraints, which are dynamic constraints.
    }

    @Override
    public void capPseudoJ() {
        // Same here!
    }

    public abstract void resetConstraint(World world);
}
