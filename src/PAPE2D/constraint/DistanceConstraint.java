package PAPE2D.constraint;

import PAPE2D.Body;
import PAPE2D.StaticConstraint;
import PAPE2D.helper.Vector2;

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
        // Helpful constants
        Vector2 realBody1relative = getBody1relative().rotate(getBody1().getAngle());
        Vector2 realBody2relative = getBody2relative().rotate(getBody2().getAngle());
        Vector2 worldConnection1 = getBody1().getPosition().plus(realBody1relative);
        Vector2 worldConnection2 = getBody2().getPosition().plus(realBody2relative);
        Vector2 worldD = worldConnection2.minus(worldConnection1);
        double omega1 = getBody1().getAngularVelocity();
        double omega2 = getBody2().getAngularVelocity();

        // Calculate the update
        double numerator = worldD.dot(getBody2().getVelocity().minus(realBody2relative.cross(omega2)).minus(getBody1().getVelocity()).plus(realBody1relative.cross(omega1)));
        double denominator = Math.pow(worldD.size(),2)/getBody2().getMass() + Math.pow(worldD.size(),2)/getBody1().getMass() + Math.pow(worldD.cross(realBody2relative),2)/getBody2().getInertiaMoment() + Math.pow(worldD.cross(realBody1relative),2)/getBody1().getInertiaMoment();
        return -(numerator+getCurrentBias())/denominator;
    }

    @Override
    public void updateVelocity(double realDeltaJ) {
        // Helpful constants
        Vector2 realBody1relative = getBody1relative().rotate(getBody1().getAngle());
        Vector2 realBody2relative = getBody2relative().rotate(getBody2().getAngle());
        Vector2 worldConnection1 = getBody1().getPosition().plus(realBody1relative);
        Vector2 worldConnection2 = getBody2().getPosition().plus(realBody2relative);
        Vector2 worldD = worldConnection2.minus(worldConnection1);

        // Calculate the update
        getBody1().addVelocity(worldD.times(-realDeltaJ / getBody1().getMass()));
        getBody2().addVelocity(worldD.times(realDeltaJ / getBody2().getMass()));
        getBody1().addAngularVelocity(-realBody1relative.cross(worldD) / getBody1().getInertiaMoment() * realDeltaJ);
        getBody2().addAngularVelocity(realBody2relative.cross(worldD) / getBody2().getInertiaMoment() * realDeltaJ);
    }

    @Override
    public double calculateBias(double beta, double dt) {
        Vector2 worldConnection1 = getBody1().getPosition().plus(getBody1relative().rotate(getBody1().getAngle()));
        Vector2 worldConnection2 = getBody2().getPosition().plus(getBody2relative().rotate(getBody2().getAngle()));
        double distance = worldConnection1.distance(worldConnection2);
        return beta / dt * 1/2 * (distance*distance- getFixedDistance()*getFixedDistance());
    }
}
