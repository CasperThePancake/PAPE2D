package PAPE2D;

import PAPE2D.helper.PotentialCollidingPair;

import java.util.List;

/**
 * Abstract class for a broad-phase collision system
 *
 * @note The broad phase system keeps track of its objects (exact implementation differs, hence no attributes), hence abstract methods
 */
public abstract class BroadPhaseCollisionSystem extends CollisionSystem {
    // =================================================================================
    // Attributes
    // =================================================================================

    // Empty...

    // =================================================================================
    // Abstract methods
    // =================================================================================

    protected abstract void addBody(Body b);

    protected abstract void removeBody(Body b);

    public abstract void update();

    public abstract List<PotentialCollidingPair> getPotentialCollidingPairs();
}