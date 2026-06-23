package PAPE2D;

import PAPE2D.broadphase.SweepAndPrune;
import PAPE2D.constraint.ContactConstraint;
import PAPE2D.constraint.FrictionConstraint;
import PAPE2D.helper.ContactManifold;
import PAPE2D.helper.PotentialCollidingPair;
import PAPE2D.helper.Vector2;
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
    private List<TickListener> preUpdateListeners = new ArrayList<>();
    private List<TickListener> postUpdateListeners = new ArrayList<>();
    private double beta;
    private double detail;

    // =================================================================================
    // Constructors
    // =================================================================================
    /**
     * Create a world with recommended settings
     */
    public World() {
        this(new SweepAndPrune(), new SAT(), 1, 20);
    }

    /**
     * Create a world with given deltaTime, broad phase collision system, and narrow phase collision system
     *
     * @param broadPhase Given broad phase collision system
     * @param narrowPhase Given narrow phase collision system
     * @param beta Given beta value for Baumgarte stabilization (0 for no stabilization)
     * @param detail Given amount of all-constraint iterations for dynamics solver per step
     */
    public World(BroadPhaseCollisionSystem broadPhase, NarrowPhaseCollisionSystem narrowPhase, double beta, double detail) {
        this.setBroadPhase(broadPhase);
        this.setNarrowPhase(narrowPhase);
        this.beta = beta;
        this.detail = detail;
    }

    // =================================================================================
    // Simulation
    // =================================================================================

    /**
     * Perform a single time-step dt for this world
     *
     * @param dt Length of time-step in seconds
     */
    public void step(double dt, PhysicsLoop linkedLoop) {
        // Pre-update ticking
        for (TickListener tickListener : preUpdateListeners) {
            tickListener.onTick(this,linkedLoop,dt);
        }

        // Perform broad phase collision detection
        List<PotentialCollidingPair> potentialPairs = broadPhase.getPotentialCollidingPairs();

        // Perform narrow phase collision detection
        List<ContactManifold> contactManifolds = narrowPhase.getContactManifolds(potentialPairs);

        // Initialize list of all constraints
        List<Constraint> constraints = new ArrayList<>(List.copyOf(staticConstraints)); // Begin with the static constraints
        List<ContactConstraint> contactConstraints = ContactConstraint.createConstraints(contactManifolds); // Then the contact constraints, based on collision results
        for (ContactConstraint c : contactConstraints) {
            constraints.add(c);
            FrictionConstraint associatedFrictionConstraint = c.createFrictionConstraint(); // Friction constraints created here, to be associated properly with their contacts
            c.setMyFrictionConstraint(associatedFrictionConstraint);
            constraints.add(associatedFrictionConstraint);
        }

        // Calculate unconstrained velocity (= current velocity + unconstrained acceleration * dt)
        for (UniversalForce universalForce : universalForces) {
            universalForce.applyAcceleration(dt);
        }

        for (LocalForce localForce : localForces) {
            localForce.applyAcceleration(dt);
        }

        // Initialize every constraint's solving tech (mainly initializes Baumgarte term)
        for (Constraint c : constraints) {
            c.initConstraint(beta,dt);
        }

        // Iterate over all constraints (thus J) multiple times (amount determined by 'detail' argument) (PGS)
        for (int i = 0; i < detail; i++) {
            for (Constraint c : constraints) {
                c.updateConstraint();
            }
        }

        // Iterate for positional constraints, yielding pseudo-velocity (amount determined by 'detail' argument) (NGS)
        for (Body b : bodies) { // Reset pseudo-velocities
            b.setPseudoVelocity(new Vector2());
            b.setPseudoAngularVelocity(0);
        }

        for (int i = 0; i < detail; i++) { // Iteration for pseudo-velocities
            for (Constraint c : constraints) {
                c.updatePseudoConstraint();
            }
            // temporarily move positions so next iteration sees updated error
            for (Body b : bodies) {
                b.setPosition(b.getPosition().plus(b.getPseudoVelocity().times(dt/detail)));
                b.setAngle(b.getAngle() + dt/detail * b.getPseudoAngularVelocity());
                b.setPseudoVelocity(new Vector2());
                b.setPseudoAngularVelocity(0);
            }
        }

        // Perform simple step in time
        for (Body b : bodies) {
            b.setPosition(b.getPosition().plus(b.getVelocity().times(dt)));
            b.setAngle(b.getAngle()+dt*(b.getAngularVelocity()));
        }

        // Update each body internally for next step
        for (Body b : bodies) {
            b.updateInternally();
        }

        // Update the broad phase system for next step
        broadPhase.update();

        // Post-update ticking
        for (TickListener tickListener : postUpdateListeners) {
            tickListener.onTick(this,linkedLoop,dt);
        }
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

    /**
     * Get the list of all bodies present in this world
     *
     * @return List of all bodies present in this world
     */
    public List<Body> getBodies() {
        return bodies;
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

    // =================================================================================
    // Static constraints
    // =================================================================================

    /**
     * Add a given static constraint to the world
     *
     * @param staticConstraint Given static constraint
     */
    public void addStaticConstraint(StaticConstraint staticConstraint) {
        this.staticConstraints.add(staticConstraint);
    }

    /**
     * Remove all static constraints from this world
     */
    public void clearStaticConstraints() {
        this.staticConstraints.clear();
    }

    /**
     * Remove the given static constraint from the world, if it exists
     *
     * @param staticConstraint Given static constraint
     */
    public void removeStaticConstraint(StaticConstraint staticConstraint) {
        this.staticConstraints.remove(staticConstraint);
    }

    // =================================================================================
    // Forces
    // =================================================================================

    /**
     * Add the given local force generator to the world
     *
     * @param localForce Given local force generator
     */
    public void addLocalForce(LocalForce localForce) {
        this.localForces.add(localForce);
    }

    /**
     * Remove all local forces from this world
     */
    public void clearLocalForces() {
        this.localForces.clear();
    }

    /**
     * Remove the given local force generator from the world, if it exists
     *
     * @param localForce Given local force generator
     */
    public void removeLocalForce(LocalForce localForce) {
        this.localForces.remove(localForce);
    }

    /**
     * Add the given universal force generator to the world
     *
     * @param universalForce Given universal force generator
     */
    public void addUniversalForce(UniversalForce universalForce) {
        this.universalForces.add(universalForce);

        // Give it all the bodies that are already present
        for (Body b : bodies) {
            universalForce.addBody(b);
        }
    }

    /**
     * Remove all universal forces from this world
     */
    public void clearUniversalForces() {
        this.universalForces.clear();
    }

    /**
     * Remove the given universal force generator from the world, if it exists
     *
     * @param universalForce Given universal force generator
     */
    public void removeUniversalForce(UniversalForce universalForce) {
        this.universalForces.remove(universalForce);
    }

    // =================================================================================
    // TickListeners
    // =================================================================================

    /**
     * Add the given tick listener to the pre-update listener list
     *
     * @param tickListener Given tick listener
     */
    public void addPreUpdateTickListener(TickListener tickListener) {
        this.preUpdateListeners.add(tickListener);
    }

    /**
     * Remove all pre-update tick listeners from this world
     */
    public void clearPreUpdateTickListeners() {
        this.preUpdateListeners.clear();
    }

    /**
     * Remove the given tick listener from the pre-update listener list
     *
     * @param tickListener Given tick listener
     */
    public void removePreUpdateTickListener(TickListener tickListener) {
        this.preUpdateListeners.remove(tickListener);
    }

    /**
     * Add the given tick listener to the post-update listener list
     *
     * @param tickListener Given tick listener
     */
    public void addPostUpdateTickListener(TickListener tickListener) {
        this.postUpdateListeners.add(tickListener);
    }

    /**
     * Remove all post-update tick listeners from this world
     */
    public void clearPostUpdateTickListeners() {
        this.postUpdateListeners.clear();
    }

    /**
     * Remove the given tick listener from the post-update listener list
     *
     * @param tickListener Given tick listener
     */
    public void removePostUpdateTickListener(TickListener tickListener) {
        this.postUpdateListeners.remove(tickListener);
    }
}
