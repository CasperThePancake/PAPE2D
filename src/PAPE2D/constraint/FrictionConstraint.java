package PAPE2D.constraint;

import PAPE2D.DynamicConstraint;
import PAPE2D.helper.ContactManifold;
import PAPE2D.helper.Vector2;

import java.util.List;

public class FrictionConstraint extends DynamicConstraint {
    // =================================================================================
    // Attributes
    // =================================================================================
    private ContactConstraint myContactConstraint;
    private ContactManifold contactManifold;
    private double frictionCoefficient;

    // =================================================================================
    // Constructor
    // =================================================================================
    /**
     * Create a new friction constraint linked to given contact manifold and contact constraint, with given friction coefficient
     *
     * @param myContactConstraint Associated contact constraint object
     * @param contactManifold Given contact manifold
     * @param frictionCoefficient Given friction coefficient
     */
    public FrictionConstraint(ContactConstraint myContactConstraint, ContactManifold contactManifold, double frictionCoefficient) {
        this.myContactConstraint = myContactConstraint;
        this.contactManifold = contactManifold;
        this.frictionCoefficient = frictionCoefficient;
    }

    /**
     * Create a new friction constraint linked to given contact manifold and contact constraint, with default friction coefficient of 1.0
     *
     * @param myContactConstraint Associated contact constraint object
     * @param contactManifold Given contact manifold
     */
    public FrictionConstraint(ContactConstraint myContactConstraint, ContactManifold contactManifold) {
        this(myContactConstraint,contactManifold,1.0);
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
        double rel1CrossT = contactManifold.getContactPointRelativeBody1().cross(contactManifold.getTangentVector());
        double rel2CrossT = contactManifold.getContactPointRelativeBody2().cross(contactManifold.getTangentVector());
        double inertia1 = contactManifold.getBody1().getInertiaMoment();
        double inertia2 = contactManifold.getBody2().getInertiaMoment();

        double numerator = tangent.dot(body2speed.plus(body2rotSpeed).minus(body1speed).minus(body1rotSpeed));
        double denominator = 1/mass1 + 1/mass2 + rel1CrossT*rel1CrossT/inertia1 + rel2CrossT*rel2CrossT/inertia2;

        return -(numerator+getCurrentBias())/denominator;
    }

    @Override
    public double calculatePseudoDeltaJ() { // WIP
        return 0;
    }

    @Override
    public void capJ() {
        double limit = frictionCoefficient * Math.abs(myContactConstraint.getJ());
        if (J > limit) {
            J = limit;
        } else if (J < -limit) {
            J = -limit;
        }
    }

    @Override
    public void capPseudoJ() { // WIP

    }

    @Override
    public void updateVelocity(double realDeltaJ) {
        Vector2 tangent = contactManifold.getTangentVector();
        double mass1 = contactManifold.getBody1().getMass();
        double mass2 = contactManifold.getBody2().getMass();
        double rel1CrossT = contactManifold.getContactPointRelativeBody1().cross(contactManifold.getTangentVector());
        double rel2CrossT = contactManifold.getContactPointRelativeBody2().cross(contactManifold.getTangentVector());
        double inertia1 = contactManifold.getBody1().getInertiaMoment();
        double inertia2 = contactManifold.getBody2().getInertiaMoment();

        contactManifold.getBody1().addVelocity(tangent.times(-realDeltaJ/mass1));
        contactManifold.getBody2().addVelocity(tangent.times(realDeltaJ/mass2));
        contactManifold.getBody1().addAngularVelocity(-Math.abs(rel1CrossT)*realDeltaJ/inertia1);
        contactManifold.getBody2().addAngularVelocity(Math.abs(rel2CrossT)*realDeltaJ/inertia2);
    }

    @Override
    public void updatePseudoVelocity(double realPseudoDeltaJ) { // WIP

    }

    @Override
    public double calculateBias(double beta, double dt) {
        return 0; // Friction constraint has no meaningful bias!
    }
}
