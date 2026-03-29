package PAPE2D;

import PAPE2D.broadphase.SweepAndPrune;
import PAPE2D.narrowphase.SAT;

import java.util.ArrayList;
import java.util.List;

/**
 * The main frame holding all simulation information
 */
public class World {
    // =================================================================================
    // Attributes
    // =================================================================================
    private List<Body> bodies = new ArrayList<>();
    private List<StaticConstraint> staticConstraints = new ArrayList<>();
    private BroadPhaseCollisionSystem broadPhase;
    private NarrowPhaseCollisionSystem narrowPhase;

    // =================================================================================
    // Constructors
    // =================================================================================
    /**
     * Create a world with the latest recommended settings
     */
    public World() {
        this(new SweepAndPrune(), new SAT());
    }

    /**
     * Create a world with given deltaTime, broad phase collision system, and narrow phase collision system
     *
     * @param broadPhase Given broad phase collision system
     * @param narrowPhase Given narrow phase collision system
     */
    public World(BroadPhaseCollisionSystem broadPhase, NarrowPhaseCollisionSystem narrowPhase) {
        this.setBroadPhase(broadPhase);
        this.setNarrowPhase(narrowPhase);
    }

    // =================================================================================
    // Simulation
    // =================================================================================

    /**
     * Perform a single time-step dt for this world
     *
     * @param dt Length of time-step in seconds
     */
    public void step(double dt) {
        // Perform broad phase collision detection

        // Perform narrow phase collision detection

        // Initialize list of all constraints

        // Calculate unconstrained velocity

        // Iterate over all constraints (thus J) multiple times

        // Perform simple step in time

        // Update associations with new positions/velocities

    }

    // =================================================================================
    // Bodies
    // =================================================================================
    public void addBody(Body body) {
        this.bodies.add(body);

        // Update associations with new object

    }

    public void removeBody(Body body) {
        this.bodies.remove(body);

        // Update associations with new object
    }

    // =================================================================================
    // BroadPhase
    // =================================================================================
    private void setBroadPhase(BroadPhaseCollisionSystem broadPhase) {
        this.broadPhase = broadPhase;
    }

    // =================================================================================
    // NarrowPhase
    // =================================================================================
    private void setNarrowPhase(NarrowPhaseCollisionSystem narrowPhase) {
        this.narrowPhase = narrowPhase;
    }
}
