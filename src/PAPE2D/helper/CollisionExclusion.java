package PAPE2D.helper;

import PAPE2D.Body;

import java.util.Objects;

/**
 * Helper class for exclusion rules in collisions, disabling collisions between two specified bodies
 */
public class CollisionExclusion {
    // =================================================================================
    // Attributes
    // =================================================================================
    private Body body1;
    private Body body2;

    // =================================================================================
    // Constructor
    // =================================================================================

    /**
     * Create a new collision exclusion rule
     *
     * @note Add this rule to a World object to disable collisions between the specified bodies
     *
     * @param body1 Given first body
     * @param body2 Given second body
     */
    public CollisionExclusion(Body body1, Body body2) {
        // Order by hashcode or system ID to ensure (b1, b2) equals (b2, b1)
        if (System.identityHashCode(body1) < System.identityHashCode(body2)) {
            this.body1 = body1;
            this.body2 = body2;
        } else {
            this.body1 = body2;
            this.body2 = body1;
        }
    }

    // =================================================================================
    // Getters and inspectors
    // =================================================================================

    /**
     * Get the first body in this collision exclusion rule
     *
     * @return First body in this collision exclusion rule
     */
    public Body getBody1() {
        return body1;
    }

    /**
     * Get the second body in this collision exclusion rule
     *
     * @return Second body in this collision exclusion rule
     */
    public Body getBody2() {
        return body2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollisionExclusion that = (CollisionExclusion) o;

        // Pure reference identity comparison (==), ignoring any overridden equals() inside Body
        return this.body1 == that.body1 && this.body2 == that.body2;
    }

    @Override
    public int hashCode() {
        // Compute the hash strictly from their memory address identities.
        // This remains completely constant even if the bodies move, rotate, or mutate!
        int hash1 = System.identityHashCode(body1);
        int hash2 = System.identityHashCode(body2);

        // Commutative combination of hashes
        return 31 * hash1 + hash2;
    }
}
