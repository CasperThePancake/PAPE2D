package PAPE2D;

import PAPE2D.helper.PotentialCollidingPair;

import java.util.List;

/**
 * Abstract class for a broad-phase collision system
 *
 * @note The broad phase system keeps track of its objects (exact implementation differs, hence no attributes), hence abstract methods
 */
public abstract class BroadPhaseCollisionSystem {
    // =================================================================================
    // Attributes
    // =================================================================================

    protected World linkedWorld;

    // =================================================================================
    // Linked world
    // =================================================================================

    void setLinkedWorld(World linkedWorld) {
        this.linkedWorld = linkedWorld;
    }

    // =================================================================================
    // Abstract methods
    // =================================================================================

    protected abstract void addBody(Body b);

    protected abstract void removeBody(Body b);

    protected abstract void update();

    protected abstract List<PotentialCollidingPair> getPotentialCollidingPairs();
}