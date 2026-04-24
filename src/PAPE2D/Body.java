package PAPE2D;

import PAPE2D.helper.Vector2;

import java.util.List;

/**
 * Abstract rigid body class
 *
 * @note All bodies are considered fully homogeneous in terms of mass
 *
 * @note When creating a Body, the user specifies some position (x,y) which could i.e. represent the top-left rectangle corner; internally we use that to calculate where the COM is and use that as the anchor, so its position is stored as the Body's Position.
 *
 * @note For clarity: USER ORIGIN = place in body the user gives position coordinates for (like top-left of rectangle), REAL ORIGIN = actual place in body we store position of, so the COM
 *
 * @note When the user passes a user origin, we use a pre-calculated originVector and the rotation to properly determine where the real origin should be, such that their expectation is satisfied
 */
public abstract class Body {
    // =================================================================================
    // Attributes
    // =================================================================================
    private Vector2 position;
    private Vector2 velocity;
    private double angle = 0;
    private double angularVelocity = 0;
    private double mass;
    private double inertiaMoment;
    private Vector2 originVector;
    private double AABBminX, AABBmaxX, AABBminY, AABBmaxY;

    // =================================================================================
    // Constructors
    // =================================================================================

    /**
     * Create a new body with given position, velocity, angle, angular velocity, mass, inertia moment, and origin vector
     * @param position Given position
     * @param velocity Given velocity
     * @param angle Given angle
     * @param angularVelocity Given angular velocity
     * @param mass Given mass
     * @param inertiaMoment Given inertia moment
     * @param originVector Given origin vector
     */
    public Body(Vector2 position, Vector2 velocity, double angle, double angularVelocity, double mass, double inertiaMoment, Vector2 originVector) {
        this.setOriginVector(originVector);
        this.setAngle(angle);
        this.setAngularVelocity(angularVelocity);
        this.setPosition(position);
        this.setVelocity(velocity);
        this.setMass(mass);
        this.setInertiaMoment(inertiaMoment);
    }

    /**
     * Create a new body with given position, velocity, inertia moment, origin vector, and no angle
     * @param position Given position
     * @param velocity Given velocity
     * @param mass Given mass
     * @param inertiaMoment Given inertia moment
     * @param originVector Given origin vector
     */
    public Body(Vector2 position, Vector2 velocity, double mass, double inertiaMoment, Vector2 originVector) {
        this(position,velocity,0,0,mass,inertiaMoment,originVector);
    }

    // =================================================================================
    // Inertia & mass
    // =================================================================================

    /**
     * Get the origin vector for this body
     *
     * @note The origin vector is the vector pointing from the user origin (eg. rectangle corner) to the real origin (what stored position represents, mass center)
     *
     * @return Origin vector for this body
     */
    public Vector2 getOriginVector() {
        return originVector;
    }

    /**
     * Set the origin vector for this body
     *
     * @note The origin vector is the vector pointing from the user origin (eg. rectangle corner) to the real origin (what stored position represents, mass center)
     *
     * @param originVector Given origin vector
     */
    private void setOriginVector(Vector2 originVector) {
        this.originVector = originVector;
    }

    /**
     * Get the mass of this body
     *
     * @return Mass of this body
     */
    public double getMass() {
        return mass;
    }

    /**
     * Set the mass of this body
     *
     * @param mass Given mass
     */
    private void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * Get the inertia moment of this body
     *
     * @note As this is a 2D simulation, the inertia moment refers to the one around the flat body's 3D normal (its only rotational axis)
     *
     * @return Inertia moment of this body
     */
    public double getInertiaMoment() {
        return inertiaMoment;
    }

    /**
     * Set the inertia moment of this body
     *
     * @param inertiaMoment Given inertia moment
     */
    private void setInertiaMoment(double inertiaMoment) {
        this.inertiaMoment = inertiaMoment;
    }

    // =================================================================================
    // Position & velocity
    // =================================================================================

    /**
     * Change the position of this body
     *
     * @param newPosition Given new position (corresponds to physical point on body referenced while constructing this body)
     */
    public void changePosition(Vector2 newPosition) {
        // User supplies newPosition (user origin), use (rotated) originVector on it to find where the real position (COM) should be to satisfy their desire
        setPosition(newPosition.plus(getOriginVector().rotate(getAngle())));
    }

    /**
     * Set the position of this body
     *
     * @param newPosition Given position
     *
     * @note This method is package-private, for use in solver. Users should use changePosition(), as it anchors the input to a user-attributed origin like the corner of a rectangle.
     */
    void setPosition(Vector2 newPosition) {
        this.position = newPosition;
    }

    /**
     * Set the velocity of this body
     *
     * @param newVelocity Given velocity
     */
    public void setVelocity(Vector2 newVelocity) {
        this.velocity = newVelocity;
    }

    /**
     * Add a deltaV to this body's velocity
     *
     * @param deltaVelocity Given change in velocity
     *
     * @note This method is package-private, for use in solver.
     */
    public void addVelocity(Vector2 deltaVelocity) {
        setVelocity(getVelocity().plus(deltaVelocity));
    }

    /**
     * Get the position of this body
     *
     * @return Position of this body
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Get the velocity of this body
     *
     * @return Velocity of this body
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    /**
     * Set the angle of this body to given value
     *
     * @param angle Given angle
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     * Set the angular velocity of this body to given value
     *
     * @param angularVelocity Given angular velocity
     */
    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    /**
     * Get the angle of this body
     *
     * @return Angle of this body
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Get the angular velocity of this body
     *
     * @return Angular velocity of this body
     */
    public double getAngularVelocity() {
        return angularVelocity;
    }

    /**
     * Update the internals of the body
     */
    public abstract void updateInternally();

    /**
     * Given a point on the body, returns the relative vector pointing from object COM/anchor to given point
     *
     * @note Used for torque calculations
     *
     * @param bodyPoint Point on the body to point towards
     * @return Relative vector pointing from COM/anchor to given point
     */
    public Vector2 getRelativePosition(Vector2 bodyPoint) {
        return bodyPoint.minus(this.getPosition());
    }

    // =================================================================================
    // AABB & edges
    // =================================================================================
    protected double getAABBminX() {
        return AABBminX;
    }

    protected double getAABBmaxX() {
        return AABBmaxX;
    }

    protected double getAABBminY() {
        return AABBminY;
    }

    protected double getAABBmaxY() {
        return AABBmaxY;
    }

    protected void setAABBminX(double AABBminX) {
        this.AABBminX = AABBminX;
    }

    protected void setAABBmaxX(double AABBmaxX) {
        this.AABBmaxX = AABBmaxX;
    }

    protected void setAABBminY(double AABBminY) {
        this.AABBminY = AABBminY;
    }

    protected void setAABBmaxY(double AABBmaxY) {
        this.AABBmaxY = AABBmaxY;
    }

    public abstract void updateAABB();

    public double getEdgeValue(Axis axis, Bound bound) {
        if (axis == Axis.X) {
            return bound == Bound.MIN ? AABBminX : AABBmaxX;
        } else {
            return bound == Bound.MIN ? AABBminY : AABBmaxY;
        }
    }

    public boolean AABBOverlaps(Body other) {
        return (this.getEdgeValue(Axis.X,Bound.MIN) <= other.getEdgeValue(Axis.X,Bound.MAX))
                && (this.getEdgeValue(Axis.X,Bound.MAX) >= other.getEdgeValue(Axis.X,Bound.MIN))
                && (this.getEdgeValue(Axis.Y,Bound.MIN) <= other.getEdgeValue(Axis.Y,Bound.MAX))
                && (this.getEdgeValue(Axis.Y,Bound.MAX) >= other.getEdgeValue(Axis.Y,Bound.MIN));
    }

    // =================================================================================
    // SAT stuff
    // =================================================================================
    public abstract List<Vector2> getSATAxes(Body other);

    public abstract Vector2 getClosestReferenceTo(Vector2 position);

    public abstract Double[] getProjectionEdges(Vector2 projectionAxis);
}
