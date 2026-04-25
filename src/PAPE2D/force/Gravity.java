package PAPE2D.force;

import PAPE2D.Body;
import PAPE2D.UniversalForce;
import PAPE2D.helper.Vector2;

/**
 * A basic gravity force pulling all bodies in a world down
 */
public class Gravity extends UniversalForce {
    // =================================================================================
    // Attributes
    // =================================================================================
    double g = 9.81;

    // =================================================================================
    // Constructors
    // =================================================================================
    /**
     * Create a new gravity force with given g value
     *
     * @param g Given g value
     */
    public Gravity(double g) {
        setG(g);
    }

    /**
     * Create a new gravity force with default Earth gravity (9.81 m/s²)
     */
    public Gravity() {
    }

    // =================================================================================
    // G
    // =================================================================================
    /**
     * Get the value of g for this gravity force
     *
     * @return Value of g for this gravity force
     */
    public double getG() {
        return g;
    }

    /**
     * Set the value of g for this gravity force
     *
     * @param g Given value of g
     */
    public void setG(double g) {
        this.g = g;
    }

    // =================================================================================
    // Apply acceleration
    // =================================================================================
    @Override
    public void applyAcceleration(double dt) {
        Vector2 gravity = new Vector2(0,-getG());
        for (Body b : getBodies()) {
            b.addVelocity(gravity.times(dt));
        }
    }
}
