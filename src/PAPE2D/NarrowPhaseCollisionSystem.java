package PAPE2D;

import PAPE2D.helper.ContactManifold;
import PAPE2D.helper.PotentialCollidingPair;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for a narrow-phase collision system
 *
 * @note Unlike the broad-phase collision system, this one does not keep track of bodies, instead working on a case-to-case basis, checking each potentialPair
 */
public abstract class NarrowPhaseCollisionSystem extends CollisionSystem {
    // =================================================================================
    // Attributes
    // =================================================================================

    // Empty...

    // =================================================================================
    // Main methods
    // =================================================================================

    /**
     * Return a list of all contact manifolds based on given possible colliding pair
     *
     * @param potentialCollidingPair Given potential colliding pair
     *
     * @return List of all contact manifolds
     */
    public abstract List<ContactManifold> getContactManifolds(PotentialCollidingPair potentialCollidingPair);

    /**
     * Return a list of all contact manifolds based on given list of possible colliding pairs
     *
     * @param potentialCollidingPairs List of potential colliding pairs
     *
     * @return List of all corresponding contact manifolds
     */
    public List<ContactManifold> getContactManifolds(List<PotentialCollidingPair> potentialCollidingPairs) {
        List<ContactManifold> outputManifolds = new ArrayList<>();
        for (PotentialCollidingPair potentialPair : potentialCollidingPairs) {
            List<ContactManifold> contactManifolds = getContactManifolds(potentialPair);
            if (!contactManifolds.isEmpty()) {
                outputManifolds.addAll(contactManifolds);
            }
        }
        return outputManifolds;
    }

    /**
     * Check whether this potential colliding pair is a proper collision
     *
     * @param potentialCollidingPair Given potential colliding pair
     *
     * @return Whether the potential colliding pair is colliding
     */
    public boolean isColliding(PotentialCollidingPair potentialCollidingPair) {
        return getContactManifolds(potentialCollidingPair) != null;
    }
}
