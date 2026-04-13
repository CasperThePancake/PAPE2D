package PAPE2D;

import PAPE2D.helper.ContactManifold;
import PAPE2D.helper.PotentialCollidingPair;

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
    // Abstract methods
    // =================================================================================

    public abstract ContactManifold getContactManifold(PotentialCollidingPair potentialCollidingPair);

    public boolean isColliding(PotentialCollidingPair potentialCollidingPair) {
        return getContactManifold(potentialCollidingPair) != null;
    }
}
