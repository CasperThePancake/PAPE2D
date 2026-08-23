package PAPE2D.helper;

import PAPE2D.Body;

/**
 * Helper class that records useful information about a collision, used for custom detection implementation via TickListeners
 *
 * @note When two flat surfaces collide, two contact points may be registered, resulting in two separate contact records for the same colliding object pair
 */
public class ContactRecord {
    // =================================================================================
    // Attributes
    // =================================================================================
    private Body bodyA;
    private Body bodyB;
    private final Vector2 contactPoint = new Vector2();
    private final Vector2 contactNormal = new Vector2();
    private final Vector2 contactTangent = new Vector2();
    private double penetrationDepth;
    private double contactImpulse;


    // =================================================================================
    // Setter
    // =================================================================================

    /**
     * Package-private initializer for the contact record
     *
     * @param bodyA Given first body
     * @param bodyB Given second body
     * @param contactPoint Given contact point vector
     * @param contactNormal Given normal vector
     * @param contactTangent Given tangent vector
     * @param penetrationDepth Given penetration depth
     * @param contactImpulse Given contact impulse (after solving)
     */
    void set(Body bodyA, Body bodyB, Vector2 contactPoint, Vector2 contactNormal, Vector2 contactTangent, double penetrationDepth, double contactImpulse) {
        this.bodyA = bodyA;
        this.bodyB = bodyB;
        this.contactPoint.copyFrom(contactPoint);
        this.contactNormal.copyFrom(contactNormal);
        this.contactTangent.copyFrom(contactTangent);
        this.penetrationDepth = penetrationDepth;
        this.contactImpulse = contactImpulse;
    }

    // =================================================================================
    // Getters
    // =================================================================================

    /**
     * Get the first body involved in this collision
     *
     * @return First body involved in this collision
     */
    public Body getBodyA() {
        return bodyA;
    }

    /**
     * Get the second body involved in this collision
     *
     * @return Second body involved in this collision
     */
    public Body getBodyB() {
        return bodyB;
    }

    /**
     * Get the contact point vector for this collision
     *
     * @return Contact point vector for this collision
     */
    public Vector2 getContactPoint() {
        return contactPoint;
    }

    /**
     * Get the contact normal vector for this collision
     *
     * @return Contact normal vector for this collision
     */
    public Vector2 getContactNormal() {
        return contactNormal;
    }

    /**
     * Get the contact tangent vector for this collision
     *
     * @return Contact tangent vector for this collision
     */
    public Vector2 getContactTangent() {
        return contactTangent;
    }

    /**
     * Get the penetration depth for this collision
     *
     * @return Penetration depth for this collision
     */
    public double getPenetrationDepth() {
        return penetrationDepth;
    }

    /**
     * Get the contact impulse for this collision
     *
     * @return Contact impulse for this collision
     */
    public double getContactImpulse() {
        return contactImpulse;
    }

    /**
     * Check whether this contact record involves the given body
     *
     * @param body Given body
     *
     * @return Whether the given body was involved in this contact record's collision
     */
    public boolean involves(Body body) {
        return bodyA == body || bodyB == body;
    }
}
