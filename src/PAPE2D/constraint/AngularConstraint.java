package PAPE2D.constraint;

import PAPE2D.Body;
import PAPE2D.ScalarStaticConstraint;
import PAPE2D.StaticConstraint;
import PAPE2D.World;

/**
 * Constraint that enforces two bodies have an equal rotational velocity and remain at the same angular difference
 */
public class AngularConstraint extends ScalarStaticConstraint {
    // =================================================================================
    // Attributes
    // =================================================================================
    private Body body1;
    private Body body2;
    private double angleDifference;

    // =================================================================================
    // Constructors
    // =================================================================================
    /**
     * Create a new angular constraint between the two given bodies, with enforced angular difference based on current value
     *
     * @param body1 Given first body
     * @param body2 Given second body
     */
    public AngularConstraint(Body body1, Body body2) {
        this.body1 = body1;
        this.body2 = body2;
        this.angleDifference = body2.getAngle() - body1.getAngle();
    }

    /**
     * Create a new angular constraint between the two given bodies, with given enforced angular difference
     *
     * @param body1 Given first body
     * @param body2 Given second body
     * @param angleDifference Given angular difference
     */
    public AngularConstraint(Body body1, Body body2, double angleDifference) {
        this.body1 = body1;
        this.body2 = body2;
        this.angleDifference = angleDifference;
    }

    // =================================================================================
    // Solver methods
    // =================================================================================
    @Override
    public double calculateDeltaJ() {
        double rotSpeed1 = body1.getAngularVelocity();
        double rotSpeed2 = body2.getAngularVelocity();

        double numerator = rotSpeed2 - rotSpeed1;
        double denominator = 1.0 * body1.getInverseInertia() + 1.0 * body2.getInverseInertia();

        if (denominator == 0.0) return 0.0;

        return -numerator / denominator;
    }

    @Override
    public void updateVelocity(double realDeltaJ) {
        body1.addAngularVelocity(-realDeltaJ * body1.getInverseInertia());
        body2.addAngularVelocity( realDeltaJ * body2.getInverseInertia());
    }

    @Override
    public double calculateBias(double beta, double dt) {
        double currentDifference = body2.getAngle() - body1.getAngle();
        double error = currentDifference - angleDifference;

        // Wrap error to [-pi,pi] to prevent it thinking 2pi and 0 is a 2pi error
        error = Math.atan2(Math.sin(error), Math.cos(error));

        return beta/1.0 * error;
    }

    @Override
    public double calculatePseudoDeltaJ() {
        double rotSpeed1 = body1.getPseudoAngularVelocity();
        double rotSpeed2 = body2.getPseudoAngularVelocity();

        double numerator = rotSpeed2 - rotSpeed1 + getCurrentBias();
        double denominator = 1.0 * body1.getInverseInertia() + 1.0 * body2.getInverseInertia();

        if (denominator == 0.0) return 0.0;

        return -numerator / denominator;
    }

    @Override
    public void updatePseudoVelocity(double realPseudoDeltaJ) {
        body1.addPseudoAngularVelocity(-realPseudoDeltaJ * body1.getInverseInertia());
        body2.addPseudoAngularVelocity( realPseudoDeltaJ * body2.getInverseInertia());
    }

    @Override
    public void resetConstraint(World world) {
        // Nothing to reset...
    }
}
