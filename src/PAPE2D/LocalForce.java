package PAPE2D;

/**
 * A local force generator that acts on select bodies
 */
public abstract class LocalForce {
    // =================================================================================
    // Abstract methods
    // =================================================================================
    /**
     * Apply this local force's acceleration over given time-step
     *
     * @param dt Given time-step (in seconds)
     */
    public abstract void applyAcceleration(double dt);
}
