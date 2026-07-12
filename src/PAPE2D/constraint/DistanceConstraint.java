package PAPE2D.constraint;

import PAPE2D.Body;
import PAPE2D.StaticConstraint;
import PAPE2D.World;
import PAPE2D.helper.Vector2;

/**
 * Constraint that enforces points on two bodies to always be at some fixed distance
 */
public class DistanceConstraint extends StaticConstraint {
    // =================================================================================
    // Attributes
    // =================================================================================
    private Body body1;
    private Body body2;
    private Vector2 body1relative; // Relative vector from center of body 1 to its connection point (assumed unrotated!)
    private Vector2 body2relative; // Relative vector from center of body 2 to its connection point (assumed unrotated!)
    private double fixedDistance; // Distance to force using this constraint

    // =================================================================================
    // Constructor
    // =================================================================================

    /**
     * Create a new fixedDistance constraint which forces points on two bodies to be at a specific fixedDistance
     *
     * @param body1 First body
     * @param body2 Second body
     * @param body1relative Relative vector from center of body 1 to its connection point, assumed unrotated
     * @param body2relative Relative vector from center of body 2 to its connection point, assumed unrotated
     * @param fixedDistance Given fixed fixedDistance
     */
    public DistanceConstraint(Body body1, Body body2, Vector2 body1relative, Vector2 body2relative, double fixedDistance) {
        this.setBody1(body1);
        this.setBody2(body2);
        this.setBody1relative(body1relative);
        this.setBody2relative(body2relative);
        this.setFixedDistance(fixedDistance);
    }

    /**
     * Create a new fixedDistance constraint which forces points on two bodies to be at their current distance forever
     *
     * @param body1 First body
     * @param body2 Second body
     * @param body1relative Relative vector from center of body 1 to its connection point, assumed unrotated
     * @param body2relative Relative vector from center of body 2 to its connection point, assumed unrotated
     */
    public DistanceConstraint(Body body1, Body body2, Vector2 body1relative, Vector2 body2relative) {
        this(body1,body2,body1relative,body2relative,body1.getPosition().plus(body1relative.rotate(body1.getAngle())).distance(body2.getPosition().plus(body2relative.rotate(body2.getAngle()))));
    }

    /**
     * Create a new distance constraint which forces the centers of two bodies to be at their current distance forever
     *
     * @param body1 First body
     * @param body2 Second body
     */
    public DistanceConstraint(Body body1, Body body2) {
        this(body1,body2,new Vector2(0,0),new Vector2(0, 0),body1.getPosition().distance(body2.getPosition()));
    }

    // =================================================================================
    // Getters & setters
    // =================================================================================

    /**
     * Get the first body connected by this constraint
     *
     * @return First body connected by this constraint
     */
    public Body getBody1() {
        return body1;
    }

    /**
     * Set the first body connected by this constraint
     *
     * @param body1 Given first body
     */
    private void setBody1(Body body1) {
        this.body1 = body1;
    }

    /**
     * Get the second body connected by this constraint
     *
     * @return Second body connected by this constraint
     */
    public Body getBody2() {
        return body2;
    }

    /**
     * Set the second body connected by this constraint
     *
     * @param body2 Given second body
     */
    private void setBody2(Body body2) {
        this.body2 = body2;
    }

    /**
     * Get the vector from the center of the first connected body's center to its connection point, unrotated
     *
     * @return Vector from the center of the first connected body's center to its connection point, unrotated
     */
    public Vector2 getBody1relative() {
        return body1relative;
    }

    /**
     * Set the vector from the center of the first connected body's center to its connection point, unrotated
     *
     * @param body1relative Given vector from the center of the first connected body's center to its connection point, unrotated
     */
    private void setBody1relative(Vector2 body1relative) {
        this.body1relative = body1relative;
    }

    /**
     * Get the vector from the center of the second connected body's center to its connection point, unrotated
     *
     * @return Vector from the center of the second connected body's center to its connection point, unrotated
     */
    public Vector2 getBody2relative() {
        return body2relative;
    }

    /**
     * Set the vector from the center of the second connected body's center to its connection point, unrotated
     *
     * @param body2relative Given vector from the center of the second connected body's center to its connection point, unrotated
     */
    private void setBody2relative(Vector2 body2relative) {
        this.body2relative = body2relative;
    }

    /**
     * Get the fixed distance associated with this constraint
     *
     * @return Fixed distance associated with this constraint
     */
    public double getFixedDistance() {
        return fixedDistance;
    }

    /**
     * Set the fixed distance for this constraint
     *
     * @param fixedDistance Fixed distance for this constraint
     */
    private void setFixedDistance(double fixedDistance) {
        this.fixedDistance = fixedDistance;
    }

    // =================================================================================
    // Implementations
    // =================================================================================
    @Override
    public double calculateDeltaJ() {
        Vector2 realBody1relative = getBody1relative().rotate(getBody1().getAngle());
        Vector2 realBody2relative = getBody2relative().rotate(getBody2().getAngle());
        Vector2 worldConnection1 = getBody1().getPosition().plus(realBody1relative);
        Vector2 worldConnection2 = getBody2().getPosition().plus(realBody2relative);
        Vector2 worldD = worldConnection2.minus(worldConnection1);
        Vector2 dHat = worldD.normalized();
        double omega1 = getBody1().getAngularVelocity();
        double omega2 = getBody2().getAngularVelocity();

        // Ċ = d̂ · (v2 + ω2×r2 - v1 - ω1×r1)
        double numerator = dHat.dot(
                getBody2().getVelocity().plus(realBody2relative.cross(omega2))
                        .minus(getBody1().getVelocity()).minus(realBody1relative.cross(omega1))
        );

        // J·M⁻¹·Jᵀ
        double denominator =
                getBody1().getInverseMass()
                        + getBody2().getInverseMass()
                        + Math.pow(realBody1relative.cross(dHat), 2) * getBody1().getInverseInertia()
                        + Math.pow(realBody2relative.cross(dHat), 2) * getBody2().getInverseInertia();

        return -(numerator) / denominator;
    }

    @Override
    public double calculatePseudoDeltaJ() {
        Vector2 realBody1relative = getBody1relative().rotate(getBody1().getAngle());
        Vector2 realBody2relative = getBody2relative().rotate(getBody2().getAngle());
        Vector2 worldConnection1 = getBody1().getPosition().plus(realBody1relative);
        Vector2 worldConnection2 = getBody2().getPosition().plus(realBody2relative);
        Vector2 worldD = worldConnection2.minus(worldConnection1);
        Vector2 dHat = worldD.normalized();
        double omega1 = getBody1().getPseudoAngularVelocity();
        double omega2 = getBody2().getPseudoAngularVelocity();

        // Ċ = d̂ · (v2 + ω2×r2 - v1 - ω1×r1)
        double numerator = dHat.dot(
                getBody2().getPseudoVelocity().plus(realBody2relative.cross(omega2))
                        .minus(getBody1().getPseudoVelocity()).minus(realBody1relative.cross(omega1))
        );

        // J·M⁻¹·Jᵀ
        double denominator =
                getBody1().getInverseMass()
                        + getBody2().getInverseMass()
                        + Math.pow(realBody1relative.cross(dHat), 2) * getBody1().getInverseInertia()
                        + Math.pow(realBody2relative.cross(dHat), 2) * getBody2().getInverseInertia();

        return -(numerator + getCurrentBias()) / denominator;
    }

    @Override
    public void updateVelocity(double realDeltaJ) {
        Vector2 realBody1relative = getBody1relative().rotate(getBody1().getAngle());
        Vector2 realBody2relative = getBody2relative().rotate(getBody2().getAngle());
        Vector2 worldConnection1 = getBody1().getPosition().plus(realBody1relative);
        Vector2 worldConnection2 = getBody2().getPosition().plus(realBody2relative);
        Vector2 dHat = worldConnection2.minus(worldConnection1).normalized();

        // v += J^T * ΔJ / m,  ω += r × J^T * ΔJ / I
        getBody1().addVelocity(dHat.times(-realDeltaJ * getBody1().getInverseMass()));
        getBody2().addVelocity(dHat.times( realDeltaJ * getBody2().getInverseMass()));
        getBody1().addAngularVelocity(-realBody1relative.cross(dHat) * realDeltaJ * getBody1().getInverseInertia());
        getBody2().addAngularVelocity( realBody2relative.cross(dHat) * realDeltaJ * getBody2().getInverseInertia());
    }

    @Override
    public void updatePseudoVelocity(double realPseudoDeltaJ) {
        Vector2 realBody1relative = getBody1relative().rotate(getBody1().getAngle());
        Vector2 realBody2relative = getBody2relative().rotate(getBody2().getAngle());
        Vector2 worldConnection1 = getBody1().getPosition().plus(realBody1relative);
        Vector2 worldConnection2 = getBody2().getPosition().plus(realBody2relative);
        Vector2 dHat = worldConnection2.minus(worldConnection1).normalized();

        // v += J^T * ΔJ / m,  ω += r × J^T * ΔJ / I
        getBody1().addPseudoVelocity(dHat.times(-realPseudoDeltaJ * getBody1().getInverseMass()));
        getBody2().addPseudoVelocity(dHat.times( realPseudoDeltaJ * getBody2().getInverseMass()));
        getBody1().addPseudoAngularVelocity(-realBody1relative.cross(dHat) * realPseudoDeltaJ * getBody1().getInverseInertia());
        getBody2().addPseudoAngularVelocity( realBody2relative.cross(dHat) * realPseudoDeltaJ * getBody2().getInverseInertia());
    }

    @Override
    public double calculateBias(double beta, double dt) {
        Vector2 worldConnection1 = getBody1().getPosition().plus(getBody1relative().rotate(getBody1().getAngle()));
        Vector2 worldConnection2 = getBody2().getPosition().plus(getBody2relative().rotate(getBody2().getAngle()));
        double distance = worldConnection1.distance(worldConnection2);
        return (beta / 1.0) * (distance - getFixedDistance());
    }

    @Override
    public void resetConstraint(World world) {
        // Nothing to reset...
    }
}