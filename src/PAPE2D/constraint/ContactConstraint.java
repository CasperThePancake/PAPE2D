package PAPE2D.constraint;

import PAPE2D.DynamicConstraint;
import PAPE2D.helper.ContactManifold;
import PAPE2D.helper.Vector2;
import java.util.ArrayList;
import java.util.List;

public class ContactConstraint extends DynamicConstraint {
    private ContactManifold contactManifold;
    private FrictionConstraint myFrictionConstraint;
    private double bounceVelocity = 0.0;

    public ContactConstraint(ContactManifold contactManifold) {
        setContactManifold(contactManifold);
    }

    public ContactManifold getContactManifold() {
        return contactManifold;
    }

    private void setContactManifold(ContactManifold contactManifold) {
        this.contactManifold = contactManifold;
    }

    public FrictionConstraint getMyFrictionConstraint() {
        return myFrictionConstraint;
    }

    public void setMyFrictionConstraint(FrictionConstraint myFrictionConstraint) {
        this.myFrictionConstraint = myFrictionConstraint;
    }

    public static List<ContactConstraint> createConstraints(List<ContactManifold> contactManifolds) {
        List<ContactConstraint> outputList = new ArrayList<>();
        for (ContactManifold manifold : contactManifolds) {
            outputList.add(new ContactConstraint(manifold));
        }
        return outputList;
    }

    public FrictionConstraint createFrictionConstraint() {
        return new FrictionConstraint(this, getContactManifold());
    }

    @Override
    public double calculateDeltaJ() {
        Vector2 normal = contactManifold.getNormalVector();

        Vector2 body1speed = contactManifold.getBody1().getVelocity();
        Vector2 body1rotSpeed = contactManifold.getContactPointRelativeBody1().cross(contactManifold.getBody1().getAngularVelocity()).times(-1);

        Vector2 body2speed = contactManifold.getBody2().getVelocity();
        Vector2 body2rotSpeed = contactManifold.getContactPointRelativeBody2().cross(contactManifold.getBody2().getAngularVelocity()).times(-1);

        double mass1 = contactManifold.getBody1().getMass();
        double mass2 = contactManifold.getBody2().getMass();

        double rel1CrossN = contactManifold.getContactPointRelativeBody1().cross(normal);
        double rel2CrossN = contactManifold.getContactPointRelativeBody2().cross(normal);

        double inertia1 = contactManifold.getBody1().getInertiaMoment();
        double inertia2 = contactManifold.getBody2().getInertiaMoment();

        Vector2 relativeVelocityVector = body2speed.plus(body2rotSpeed).minus(body1speed).minus(body1rotSpeed);
        double relativeVelocity = relativeVelocityVector.dot(normal);

        // REMOVED early guard condition to allow proper PGS convergence across iterations! (WIP RESEARCH)

        double numerator = relativeVelocity - bounceVelocity - getCurrentBias();
        double denominator = 1.0/mass1 + 1.0/mass2 + (rel1CrossN * rel1CrossN) / inertia1 + (rel2CrossN * rel2CrossN) / inertia2;

        if (denominator == 0.0) return 0.0;

        return -numerator / denominator;
    }

    @Override
    public void capJ() {
        if (J < 0) J = 0;
    }

    @Override
    public void updateVelocity(double realDeltaJ) {
        Vector2 normal = contactManifold.getNormalVector();
        double mass1 = contactManifold.getBody1().getMass();
        double mass2 = contactManifold.getBody2().getMass();

        // FIXED: Enforce clear, explicit cross matching to normal directionality rules
        double rel1CrossN = contactManifold.getContactPointRelativeBody1().cross(normal);
        double rel2CrossN = contactManifold.getContactPointRelativeBody2().cross(normal);

        double inertia1 = contactManifold.getBody1().getInertiaMoment();
        double inertia2 = contactManifold.getBody2().getInertiaMoment();

        contactManifold.getBody1().addVelocity(normal.times(-realDeltaJ / mass1));
        contactManifold.getBody2().addVelocity(normal.times(realDeltaJ / mass2));

        contactManifold.getBody1().addAngularVelocity(-rel1CrossN * realDeltaJ / inertia1);
        contactManifold.getBody2().addAngularVelocity(rel2CrossN * realDeltaJ / inertia2);
    }

    @Override
    public double calculateBias(double beta, double dt) {
        return beta / dt * Math.max(0, contactManifold.getPenetrationDepth());
    }

    @Override
    public void initConstraint(double beta, double dt) {
        super.initConstraint(beta, dt);

        Vector2 normal = contactManifold.getNormalVector();
        Vector2 v1 = contactManifold.getBody1().getVelocity().plus(
                contactManifold.getContactPointRelativeBody1().cross(contactManifold.getBody1().getAngularVelocity()).times(-1)
        );
        Vector2 v2 = contactManifold.getBody2().getVelocity().plus(
                contactManifold.getContactPointRelativeBody2().cross(contactManifold.getBody2().getAngularVelocity()).times(-1)
        );
        double relativeVelocity = v2.minus(v1).dot(normal);

        double restitution = 0.3;
        if (relativeVelocity < -0.5) {
            bounceVelocity = -restitution * relativeVelocity;
        } else {
            bounceVelocity = 0.0;
        }
    }

    @Override public double calculatePseudoDeltaJ() { // WIP (and below)
        return 0;
    }

    @Override public void capPseudoJ() {}

    @Override public void updatePseudoVelocity(double realPseudoDeltaJ) {

    }
}