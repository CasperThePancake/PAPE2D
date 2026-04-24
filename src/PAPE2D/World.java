package PAPE2D;

import PAPE2D.broadphase.SweepAndPrune;
import PAPE2D.constraint.ContactConstraint;
import PAPE2D.constraint.FrictionConstraint;
import PAPE2D.helper.ContactManifold;
import PAPE2D.helper.PotentialCollidingPair;
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
    private List<UniversalForce> universalForces = new ArrayList<>();
    private List<LocalForce> localForces = new ArrayList<>();

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
        List<PotentialCollidingPair> potentialPairs = broadPhase.getPotentialCollidingPairs();

        // Perform narrow phase collision detection
        List<ContactManifold> contactManifolds = narrowPhase.getContactManifolds(potentialPairs);

        // Initialize list of all constraints
        List<Constraint> constraints = new ArrayList<>(List.copyOf(staticConstraints)); // Begin with the static constraints
        constraints.addAll(ContactConstraint.createConstraints(contactManifolds));
        constraints.addAll(FrictionConstraint.createConstraints(contactManifolds)); // WIP

        // Calculate unconstrained velocity (= current velocity + unconstrained acceleration * dt)
        for (UniversalForce universalForce : universalForces) {
            universalForce.applyAcceleration(dt);
        }

        for (LocalForce localForce : localForces) {
            localForce.applyAcceleration(dt);
        }

        // Iterate over all constraints (thus J) multiple times

        // Perform simple step in time

        // Update associations with new positions/velocities (?)

        // Update each body internally for next step
        for (Body b : bodies) {
            b.updateInternally();
        }

        // Update the broad phase system for next step
        broadPhase.update();
    }

    // =================================================================================
    // Bodies
    // =================================================================================

    /**
     * Add a body to the world
     *
     * @param body Given body
     */
    public void addBody(Body body) {
        this.bodies.add(body);

        // Update associations with new object
        broadPhase.addBody(body);
        for (UniversalForce u : universalForces) {
            u.addBody(body);
        }
    }

    /**
     * Remove a body from the world
     *
     * @param body Given body
     */
    public void removeBody(Body body) {
        this.bodies.remove(body);

        // Update associations with new object
        broadPhase.removeBody(body);
        for (UniversalForce u : universalForces) {
            u.removeBody(body);
        }
    }

    // =================================================================================
    // BroadPhase
    // =================================================================================

    /**
     * Set the broad phase collision system for this world
     *
     * @param broadPhase Given broad phase collision system
     *
     * @note As the World is created with a system before any bodies are added, we need not initialize the system's bodies list while constructing.
     */
    private void setBroadPhase(BroadPhaseCollisionSystem broadPhase) {
        this.broadPhase = broadPhase;
    }

    // =================================================================================
    // NarrowPhase
    // =================================================================================

    /**
     * Set the narrow phase collision system for this world
     *
     * @param narrowPhase Given narrow phase collision system
     */
    private void setNarrowPhase(NarrowPhaseCollisionSystem narrowPhase) {
        this.narrowPhase = narrowPhase;
    }
}
