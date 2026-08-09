package PAPE2D;

import PAPE2D.helper.Vector2;

import java.util.List;

/**
 * Abstract fixture class for shapes that make up rigid bodies
 *
 * @note Like bodies, all fixtures are considered fully homogeneous in terms of mass distribution
*/
public abstract class Fixture {
    // =================================================================================
    // Attributes
    // =================================================================================

    private double mass;
    private double inertiaMoment;
    private double AABBminX, AABBmaxX, AABBminY, AABBmaxY;
    private Body parentBody;
    private Vector2 parentCOMToThisCOM;

    // =================================================================================
    // Constructors
    // =================================================================================

    /**
     * Create a new fixture with given mass
     *
     * @note Information about actual shape and origin point is defined via overriding of abstract methods (for AABB, COM, rendering, inertia, SAT, etc.)
     *
     * @param mass Given mass
     * @param inertiaMoment Given inertia moment (expected to be consistent with mass and shape)
     */
    public Fixture(double mass, double inertiaMoment) {
        this.setMass(mass);
        this.setInertiaMoment(inertiaMoment);
    }

    // =================================================================================
    // Inertia & mass
    // =================================================================================

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

    /**
     * Get the position of this fixture's center of mass
     *
     * @param originPoint Vector for the origin point of this fixture, used to orient it in space (assumed unrotated)
     *
     * @return Vector position of this fixture's center of mass
     */
    protected abstract Vector2 getCOM(Vector2 originPoint);

    /**
     * Set the vector pointing from this fixture's parent body's COM to its own COM
     *
     * @note To be used when a body is constructed using this fixture
     *
     * @param parentCOMToThisCOM Given vector
     */
    void setParentCOMToThisCOM(Vector2 parentCOMToThisCOM) {
        this.parentCOMToThisCOM = parentCOMToThisCOM;
    }

    /**
     * Get the vector pointing from this fixture's parent body's COM to its own COM
     *
     * @return Specified vector
     */
    public Vector2 getParentCOMToThisCOM() {
        return parentCOMToThisCOM;
    }

    /**
     * Set the parent body for this fixture
     *
     * @param parentBody Given parent body
     */
    void setParentBody(Body parentBody) {
        this.parentBody = parentBody;
    }

    /**
     * Get the parent body for this fixture
     *
     * @return Parent body for this fixture
     */
    public Body getParentBody() {
        return parentBody;
    }

    /**
     * Get this fixture's current position (of COM) in the world
     *
     * @throws IllegalStateException If fixture is not yet part of an effective body
     *
     * @return Current fixture (COM) position
     */
    public Vector2 getPosition() throws IllegalStateException {
       if (getParentBody() == null) {
            throw new IllegalStateException("Method can only be run when fixture is part of effective body.");
       }

       return getParentBody().getPosition().plus(getParentCOMToThisCOM().rotate(getParentBody().getAngle()));
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

    protected abstract void updateAABB();

    /**
     * Get a specified AABB edge for this body
     *
     * @param axis The axis which the requested edge falls on (i.e. X for left edge)
     * @param bound The boundary type of the requested edge
     *
     * @return The requested edge value
     */
    @Internal
    public double getEdgeValue(Axis axis, Bound bound) {
        if (axis == Axis.X) {
            return bound == Bound.MIN ? AABBminX : AABBmaxX;
        } else {
            return bound == Bound.MIN ? AABBminY : AABBmaxY;
        }
    }

    /**
     * Checks if this body's AABB overlaps with a given other body's AABB
     *
     * @param other Given other body
     *
     * @return True if AABBs have overlap; false otherwise
     */
    @Internal
    public boolean AABBOverlaps(Fixture other) {
        return (this.getEdgeValue(Axis.X,Bound.MIN) <= other.getEdgeValue(Axis.X,Bound.MAX))
                && (this.getEdgeValue(Axis.X,Bound.MAX) >= other.getEdgeValue(Axis.X,Bound.MIN))
                && (this.getEdgeValue(Axis.Y,Bound.MIN) <= other.getEdgeValue(Axis.Y,Bound.MAX))
                && (this.getEdgeValue(Axis.Y,Bound.MAX) >= other.getEdgeValue(Axis.Y,Bound.MIN));
    }

    protected abstract void updateInternally();

    // =================================================================================
    // SAT stuff
    // =================================================================================

    /**
     * Get the list of SAT axes to check for this fixture, given the other candidate colliding fixture
     *
     * @param other Other fixture
     *
     * @return List of SAT axes to check for this fixture
     */
    @Internal
    public abstract List<Vector2> getSATAxes(Fixture other);

    /**
     * Get the closest SAT reference point for this body to a given position
     *
     * @param position Given position
     *
     * @return Closest SAT reference point
     */
    @Internal
    public abstract Vector2 getClosestReferenceTo(Vector2 position);

    /**
     * Find the edge point values of this fixture's projection on a given axis
     *
     * @param projectionAxis Given vector defining the projection axis
     *
     * @return Edge point values of this fixture's projection
     */
    @Internal
    public abstract Double[] getProjectionEdges(Vector2 projectionAxis);

    // =================================================================================
    // Rendering
    // =================================================================================
    protected abstract void render(PhysicsLoop physicsLoop);
}
