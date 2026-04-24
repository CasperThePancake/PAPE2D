package PAPE2D;

import java.util.ArrayList;
import java.util.List;

/**
 * A universal force generator that acts on all bodies at once
 */
public abstract class UniversalForce {
    List<Body> bodies = new ArrayList<>();

    /**
     * Get the list of bodies
     *
     * @return List of bodies
     */
    protected List<Body> getBodies() {
        return bodies;
    }

    /**
     * Add a body to the universal force's stored list
     *
     * @param body Given body
     */
    void addBody(Body body) {
        bodies.add(body);
    }

    /**
     * Remove a body from the universal force's stored list
     *
     * @param body Given body
     */
    void removeBody(Body body) {
        bodies.remove(body);
    }

    /**
     * Apply this universal force's acceleration over given time-step
     *
     * @param dt Given time-step (in seconds)
     */
    public abstract void applyAcceleration(double dt);
}
