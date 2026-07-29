package PAPE2D;

import PAPE2D.helper.Vector2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract rigid body class
 *
 * @note All bodies are considered fully homogeneous in terms of mass
 *
 * @note A body itself carries no shape or mass information, this is defined by its composition of Fixtures and their relative positions (determines total mass, inertia, COM, etc.)
 *
 * @note When creating a Body, the user specifies some position (x,y) which could i.e. represent the top-left rectangle corner; internally we use that to calculate where the COM is and use that as the anchor, so its position is stored as the Body's Position.
 *
 * @note For clarity: USER ORIGIN = place in body the user gives position coordinates for (like top-left of rectangle), REAL ORIGIN = actual place in body we store position of, so the COM
 *
 * @note When the user passes a user origin, we use a pre-calculated originVector and the rotation to properly determine where the real origin should be, such that their expectation is satisfied
 */
public class Body {
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
    private Vector2 pseudoVelocity;
    private double pseudoAngularVelocity = 0;
    private Set<Flag> flags = new HashSet<>();
    public static double DEFAULT_RESTITUTION = 0.7;
    private double restitution = DEFAULT_RESTITUTION;
    public static double DEFAULT_FRICTION_COEFFICIENT = 1;
    private double frictionCoefficient = DEFAULT_FRICTION_COEFFICIENT;
    private List<Fixture> fixtures = new ArrayList<>();

    // =================================================================================
    // Constructors
    // =================================================================================

    /**
     * Create a new body with given position, velocity, angle, and angular velocity
     *
     * @param position Given position
     * @param fixtures List of fixtures composing this body
     * @param fixtureOrigins List of vectors pointing from given body position to each equal-index fixture's origin point (origin meaning depends on type of fixture)
     * @param velocity Given velocity
     * @param angle Given angle
     * @param angularVelocity Given angular velocity
     *
     * @throws IllegalArgumentException If given list of fixtures is empty
     */
    public Body(Vector2 position, List<Fixture> fixtures, List<Vector2> fixtureOrigins, Vector2 velocity, double angle, double angularVelocity) throws IllegalArgumentException {
        // General things
        this.setAngle(angle);
        this.setAngularVelocity(angularVelocity);
        this.setVelocity(velocity);

        // Fixture composition (everything is calculated in an unrotated state!)
        if (fixtures.isEmpty()) {
            throw new IllegalArgumentException("Body must be composed of at least one fixture.");
        }

        this.fixtures = fixtures;

        // Determine total mass
        double totalMass = 0;

        for (Fixture f : fixtures) {
            totalMass += f.getMass();
        }

        this.setMass(totalMass);

        // Determine total COM
        Vector2 totalCOM = new Vector2();

        for (int i = 0; i < fixtures.size(); i++) {
            Fixture f = fixtures.get(i);
            Vector2 fOrigin = fixtureOrigins.get(i);
            totalCOM = totalCOM.plus(f.getCOM(position.plus(fOrigin)).times(f.getMass()));
        }
        this.setPosition(totalCOM.times(1/totalMass));

        // Update fixtures' references
        for (int i = 0; i < fixtures.size(); i++) {
            Fixture f = fixtures.get(i);
            Vector2 fOrigin = fixtureOrigins.get(i);
            f.setParentCOMToThisCOM(f.getCOM(position.plus(fOrigin)).times(f.getMass()).minus(getPosition()));
            f.setParentBody(this);
        }

        // Determine total inertia moment
        double totalInertia = 0;

        for (Fixture f : fixtures) {
            totalInertia += f.getInertiaMoment() + f.getMass() * (f.getParentCOMToThisCOM().size()) * (f.getParentCOMToThisCOM().size()); // Parallel axis theorem
        }

        setInertiaMoment(totalInertia);

        // Determine and set origin vector (FROM user origin defined via this constructor/shapes TO actual position, so center of mass)
        Vector2 originVector = getPosition().minus(position);

        // Make sure everything is instantly updated (mainly important for polygons)
        updateInternally();
    }

    /**
     * Create a new body with given position, velocity, and no angle
     *
     * @param position Given position
     * @param fixtures List of fixtures composing this body
     * @param fixtureOrigins List of vectors pointing from given body position to each equal-index fixture's origin point (origin meaning depends on type of fixture)
     * @param velocity Given velocity
     */
    public Body(Vector2 position, List<Fixture> fixtures, List<Vector2> fixtureOrigins, Vector2 velocity) {
        this(position,fixtures,fixtureOrigins,velocity,0,0);
    }

    /**
     * Create a new body with given position, no velocity, no angle
     *
     * @param position Given position
     * @param fixtures List of fixtures composing this body
     * @param fixtureOrigins List of vectors pointing from given body position to each equal-index fixture's origin point (origin meaning depends on type of fixture)
     */
    public Body(Vector2 position, List<Fixture> fixtures, List<Vector2> fixtureOrigins) {
        this(position,fixtures,fixtureOrigins,new Vector2(), 0, 0);
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

    /**
     * Get the inverse mass of this body
     *
     * @return 1/mass or 0 if frozen
     */
    public double getInverseMass() {
        if (hasFlag(Flag.FROZEN) || hasFlag(Flag.FROZEN_TRANSLATION)) return 0.0;
        return 1.0 / this.mass;
    }

    /**
     * Get the inverse inertia of this body
     *
     * @return 1/inertia or 0 if frozen
     */
    public double getInverseInertia() {
        if (hasFlag(Flag.FROZEN) || hasFlag(Flag.FROZEN_ROTATION)) return 0.0;
        return 1.0 / this.inertiaMoment;
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
     * Add a deltaV to this body's angular velocity
     *
     * @param deltaAngularVelocity Given change in angular velocity
     */
    public void addAngularVelocity(double deltaAngularVelocity) {
        setAngularVelocity(getAngularVelocity()+deltaAngularVelocity);
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
    public void updateInternally() {
        for (Fixture f : fixtures) {
            f.updateInternally();
        }
    }

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

    /**
     * Get this body's pseudo-velocity vector
     *
     * @return This body's pseudo-velocity vector
     */
    public Vector2 getPseudoVelocity() {
        return pseudoVelocity;
    }

    /**
     * Set this body's pseudo-velocity vector
     *
     * @param pseudoVelocity Given pseudo-velocity vector
     */
    public void setPseudoVelocity(Vector2 pseudoVelocity) {
        this.pseudoVelocity = pseudoVelocity;
    }

    /**
     * Add to this body's pseudo-velocity vector
     *
     * @param deltaPseudoVelocity Given pseudo-velocity delta
     */
    public void addPseudoVelocity(Vector2 deltaPseudoVelocity) {
        setPseudoVelocity(getPseudoVelocity().plus(deltaPseudoVelocity));
    }

    /**
     * Get this body's pseudo-angular velocity
     *
     * @return This body's pseudo-angular velocity
     */
    public double getPseudoAngularVelocity() {
        return pseudoAngularVelocity;
    }

    /**
     * Set this body's pseudo-angular velocity
     *
     * @param pseudoAngularVelocity Given pseudo-angular velocity
     */
    public void setPseudoAngularVelocity(double pseudoAngularVelocity) {
        this.pseudoAngularVelocity = pseudoAngularVelocity;
    }

    /**
     * Add to this body's pseudo-angular velocity
     *
     * @param deltaPseudoAngularVelocity Given pseudo-angular velocity delta
     */
    public void addPseudoAngularVelocity(double deltaPseudoAngularVelocity) {
        setPseudoAngularVelocity(getPseudoAngularVelocity()+deltaPseudoAngularVelocity);
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

    public void updateAABB() {
        // Let the fixtures update theirs first
        for (Fixture f : fixtures) {
            f.updateAABB();
        }

        // Body AABB is most stretched out combination of underlying fixture AABBs
        // AABBminX
        double AABBminX = Double.POSITIVE_INFINITY;

        for (Fixture f : fixtures) {
            if (f.getAABBminX() < AABBminX) {
                AABBminX = f.getAABBminX();
            }
        }

        setAABBminX(AABBminX);

        // AABBmaxX
        double AABBmaxX = Double.NEGATIVE_INFINITY;

        for (Fixture f : fixtures) {
            if (f.getAABBmaxX() > AABBmaxX) {
                AABBmaxX = f.getAABBmaxX();
            }
        }

        setAABBmaxX(AABBmaxX);

        // AABBminY
        double AABBminY = Double.POSITIVE_INFINITY;

        for (Fixture f : fixtures) {
            if (f.getAABBminY() < AABBminY) {
                AABBminY = f.getAABBminY();
            }
        }

        setAABBminY(AABBminY);

        // AABBmaxY
        double AABBmaxY = Double.NEGATIVE_INFINITY;

        for (Fixture f : fixtures) {
            if (f.getAABBmaxY() > AABBmaxY) {
                AABBmaxY = f.getAABBmaxY();
            }
        }

        setAABBmaxY(AABBmaxY);
    }

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
    // Restitution
    // =================================================================================

    /**
     * Get this body's restitution value
     *
     * @return This body's restitution value
     */
    public double getRestitution() {
        return restitution;
    }

    /**
     * Set this body's restitution value
     *
     * @param restitution Given restitution value
     *
     * @note For logical physics, this should be a value between 0 (= full energy loss) and 1 (= full energy conservation)
     */
    public void setRestitution(double restitution) {
        this.restitution = restitution;
    }

    // =================================================================================
    // Friction coefficient
    // =================================================================================

    /**
     * Get this body's friction coefficient
     *
     * @return This body's friction coefficient
     */
    public double getFrictionCoefficient() {
        return frictionCoefficient;
    }

    /**
     * Set this body's friction coefficient
     *
     * @param frictionCoefficient Given friction coefficient
     */
    public void setFrictionCoefficient(double frictionCoefficient) {
        this.frictionCoefficient = frictionCoefficient;
    }

    // =================================================================================
    // Rendering
    // =================================================================================
    public void render(PhysicsLoop physicsLoop) {
        for (Fixture f : fixtures) {
            f.render(physicsLoop);
        }
    }

    // =================================================================================
    // Flags
    // =================================================================================

    /**
     * Add a flag to this body
     *
     * @param flag Given flag
     */
    public void addFlag(Flag flag) {
        flags.add(flag);
    }

    /**
     * Check if this body has a given flag
     *
     * @param flag Given flag
     *
     * @return True if body has flag; false otherwise
     */
    public boolean hasFlag(Flag flag) {
        return flags.contains(flag);
    }

    /**
     * Remove a flag from this body
     *
     * @param flag Given flag
     */
    public void removeFlag(Flag flag) {
        flags.remove(flag);
    }

    // =================================================================================
    // Fixtures
    // =================================================================================

    /**
     * Get the list of all fixtures composing this body
     *
     * @return List of all fixtures composing this body
     */
    public List<Fixture> getFixtures() {
        return fixtures;
    }
}
