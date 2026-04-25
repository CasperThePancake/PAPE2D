package PAPE2D.helper;

import PAPE2D.Body;

public class ContactManifold {
    // =================================================================================
    // Attributes
    // =================================================================================
    private final Vector2 contactPoint;
    private final double penetrationDepth;
    private final Body body1;
    private final Body body2;
    private final Vector2 contactPointRelativeBody1;
    private final Vector2 contactPointRelativeBody2;

    // =================================================================================
    // Constructor
    // =================================================================================

    /**
     * Create a new contact manifold with given attributes
     *
     * @param contactPoint Given contact point vector
     * @param penetrationDepth Given penetration depth
     * @param body1 Given first body
     * @param body2 Given second body
     */
    public ContactManifold(Vector2 contactPoint, double penetrationDepth, Body body1, Body body2) {
        this.contactPoint = contactPoint;
        this.penetrationDepth = penetrationDepth;
        this.body1 = body1;
        this.body2 = body2;
        this.contactPointRelativeBody1 = body1.getRelativePosition(contactPoint);
        this.contactPointRelativeBody2 = body2.getRelativePosition(contactPoint);
    }

    // =================================================================================
    // Contact point
    // =================================================================================

    /**
     * Get the contact point of this contact manifold
     *
     * @return Contact point of this contact manifold
     */
    public Vector2 getContactPoint() {
        return contactPoint;
    }

    // =================================================================================
    // Penetration depth
    // =================================================================================

    /**
     * Get the penetration depth of this contact manifold
     *
     * @return Penetration depth of this contact manifold
     */
    public double getPenetrationDepth() {
        return penetrationDepth;
    }

    // =================================================================================
    // Body 1
    // =================================================================================

    /**
     * Get the first body in this contact manifold
     *
     * @return First body in this contact manifold
     */
    public Body getBody1() {
        return body1;
    }

    // =================================================================================
    // Body 2
    // =================================================================================

    /**
     * Get the second body in this contact manifold
     *
     * @return Second body in this contact manifold
     */
    public Body getBody2() {
        return body2;
    }

    // =================================================================================
    // Relative contact point for body 1
    // =================================================================================

    /**
     * Get the relative contact point vector for body 1
     *
     * @return Relative contact point vector for body 1
     */
    public Vector2 getContactPointRelativeBody1() {
        return contactPointRelativeBody1;
    }

    // =================================================================================
    // Relative contact point for body 2
    // =================================================================================

    /**
     * Get the relative contact point vector for body 2
     *
     * @return Relative contact point vector for body 2
     */
    public Vector2 getContactPointRelativeBody2() {
        return contactPointRelativeBody2;
    }
}
