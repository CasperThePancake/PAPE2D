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
    public Body(Vector2 position, Vector2 velocity, double angle, double angularVelocity, double mass, double inertiaMoment, Vector2 originVector) {
        this.setOriginVector(originVector);
        this.setAngle(angle);
        this.setAngularVelocity(angularVelocity);
        this.setPosition(position);
        this.setVelocity(velocity);
        this.setMass(mass);
        this.setInertiaMoment(inertiaMoment);
    }

    public Body(Vector2 position, Vector2 velocity, double mass, double inertiaMoment, Vector2 originVector) {
        this(position,velocity,0,0,mass,inertiaMoment,originVector);
    }

    // =================================================================================
    // Inertia & mass
    // =================================================================================
    public Vector2 getOriginVector() {
        return originVector;
    }

    public void setOriginVector(Vector2 originVector) {
        this.originVector = originVector;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getInertiaMoment() {
        return inertiaMoment;
    }

    public void setInertiaMoment(double inertiaMoment) {
        this.inertiaMoment = inertiaMoment;
    }

    // =================================================================================
    // Position & velocity
    // =================================================================================
    public void setPosition(Vector2 newPosition) {
        // User supplies newPosition (user origin), use (rotated) originVector on it to find where the real position (COM) should be to satisfy their desire
        this.position = newPosition.plus(getOriginVector().rotate(getAngle()));

        // Optional updating (WIP: run this after a time step, not just position change!)
        updateInternally();
    }

    public void setVelocity(Vector2 newVelocity) {
        this.velocity = newVelocity;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public double getAngle() {
        return angle;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

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
