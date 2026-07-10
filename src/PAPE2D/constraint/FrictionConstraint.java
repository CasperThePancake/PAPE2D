package PAPE2D.constraint;

import PAPE2D.DynamicConstraint;
import PAPE2D.helper.ContactManifold;
import PAPE2D.helper.Vector2;

public class FrictionConstraint extends DynamicConstraint {
    private ContactConstraint myContactConstraint;
    private ContactManifold contactManifold;
    private double frictionCoefficient;

    public FrictionConstraint(ContactConstraint myContactConstraint, ContactManifold contactManifold, double frictionCoefficient) {
        this.myContactConstraint = myContactConstraint;
        this.contactManifold = contactManifold;
        this.frictionCoefficient = frictionCoefficient;
    }

    public FrictionConstraint(ContactConstraint myContactConstraint, ContactManifold contactManifold) {
        this(myContactConstraint, contactManifold, 1.0);
    }

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
        double denominator = 1.0/mass1 + 1.0/mass2 + (rel1CrossT * rel1CrossT) / inertia1 + (rel2CrossT * rel2CrossT) / inertia2;

        if (denominator == 0.0) return 0.0;

        return -numerator / denominator;
    }

    @Override
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

        contactManifold.getBody1().addVelocity(tangent.times(-realDeltaJ / mass1));
        contactManifold.getBody2().addVelocity(tangent.times(realDeltaJ / mass2));

        contactManifold.getBody1().addAngularVelocity(-rel1CrossT * realDeltaJ / inertia1);
        contactManifold.getBody2().addAngularVelocity(rel2CrossT * realDeltaJ / inertia2);
    }

    @Override
    public double calculateBias(double beta, double dt) {
        return 0;
    }

    // Friction constraints have no concept of physical separation, leave these blank
    @Override public double calculatePseudoDeltaJ() { return 0; }
    @Override public void capPseudoJ() {}
    @Override public void updatePseudoVelocity(double realPseudoDeltaJ) {}
}