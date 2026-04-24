package PAPE2D;

public abstract class LocalForce {
    /**
     * Apply this local force's acceleration over given time-step
     *
     * @param dt Given time-step (in seconds)
     */
    public abstract void applyAcceleration(double dt);
}
