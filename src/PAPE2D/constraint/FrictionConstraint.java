package PAPE2D.constraint;

import PAPE2D.DynamicConstraint;
import PAPE2D.Internal;
import PAPE2D.ScalarDynamicConstraint;
import PAPE2D.World;
import PAPE2D.helper.ContactManifold;
import PAPE2D.helper.Vector2;

/**
 * Constraint that reduces tangential motion between two bodies up to a maximum
 */
public class FrictionConstraint extends ScalarDynamicConstraint {
    // =================================================================================
    // Attributes
    // =================================================================================
    private ContactConstraint myContactConstraint;
    private ContactManifold contactManifold;
    private double frictionCoefficient;

    // =================================================================================
    // Constructors
    // =================================================================================
    public FrictionConstraint(ContactConstraint myContactConstraint, ContactManifold contactManifold, double frictionCoefficient) {
        this.myContactConstraint = myContactConstraint;
        this.contactManifold = contactManifold;
        this.frictionCoefficient = frictionCoefficient;
    }

    public FrictionConstraint(ContactConstraint myContactConstraint, ContactManifold contactManifold) {
        this(myContactConstraint, contactManifold, 1.0);
    }

    // =================================================================================
    // Solver methods
    // =================================================================================
    @Override
    public double calculateDeltaJ() {
        Vector2 tangent = contactManifold.getTangentVector();
        Vector2 body1speed = contactManifold.getBody1().getVelocity();
        Vector2 body1rotSpeed = contactManifold.getContactPointRelativeBody1().cross(contactManifold.getBody1().getAngularVelocity()).times(-1);
        Vector2 body2speed = contactManifold.getBody2().getVelocity();
        Vector2 body2rotSpeed = contactManifold.getContactPointRelativeBody2().cross(contactManifold.getBody2().getAngularVelocity()).times(-1);

        double mass1 = contactManifold.getBody1().getMass();
        double mass2 = contactManifold.getBody2().getMass();

        double rel1CrossT = contactManifold.getContactPointRelativeBody1().cross(tangent);
        double rel2CrossT = contactManifold.getContactPointRelativeBody2().cross(tangent);

        double inertia1 = contactManifold.getBody1().getInertiaMoment();
        double inertia2 = contactManifold.getBody2().getInertiaMoment();

        Vector2 relativeVelocityVector = body2speed.plus(body2rotSpeed).minus(body1speed).minus(body1rotSpeed);
        double relativeVelocity = relativeVelocityVector.dot(tangent);

        // Friction has no position bias!
        double numerator = relativeVelocity;
        double denominator = 1.0 * contactManifold.getBody1().getInverseMass() + 1.0 * contactManifold.getBody2().getInverseMass() + (rel1CrossT * rel1CrossT) * contactManifold.getBody1().getInverseInertia() + (rel2CrossT * rel2CrossT) * contactManifold.getBody2().getInverseInertia();

        if (denominator == 0.0) return 0.0;

        return -numerator / denominator;
    }

    @Override @Internal
    public void capJ() {
        // Friction clamping
        double limit = frictionCoefficient * Math.abs(myContactConstraint.getJ());
        if (J > limit) {
            J = limit;
        } else if (J < -limit) {
            J = -limit;
        }
    }

    @Override
    public void updateVelocity(double realDeltaJ) {
        Vector2 tangent = contactManifold.getTangentVector();
        double mass1 = contactManifold.getBody1().getMass();
        double mass2 = contactManifold.getBody2().getMass();

        double rel1CrossT = contactManifold.getContactPointRelativeBody1().cross(tangent);
        double rel2CrossT = contactManifold.getContactPointRelativeBody2().cross(tangent);

        double inertia1 = contactManifold.getBody1().getInertiaMoment();
        double inertia2 = contactManifold.getBody2().getInertiaMoment();

        contactManifold.getBody1().addVelocity(tangent.times(-realDeltaJ * contactManifold.getBody1().getInverseMass()));
        contactManifold.getBody2().addVelocity(tangent.times(realDeltaJ * contactManifold.getBody2().getInverseMass()));

        contactManifold.getBody1().addAngularVelocity(-rel1CrossT * realDeltaJ * contactManifold.getBody1().getInverseInertia());
        contactManifold.getBody2().addAngularVelocity(rel2CrossT * realDeltaJ * contactManifold.getBody2().getInverseInertia());
    }

    @Override
    public double calculateBias(double beta, double dt) {
        return 0;
    }

    // Friction constraints have no concept of physical separation, leave these blank
    @Override public double calculatePseudoDeltaJ() { return 0; }
    @Override public void capPseudoJ() {}

    @Override
    public void resetConstraint(World world) {
        // Nothing...
    }

    @Override public void updatePseudoVelocity(double realPseudoDeltaJ) {}
}