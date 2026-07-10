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
    private double beta = 0;

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

        double numerator = relativeVelocity - bounceVelocity;
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
        double depth = contactManifold.getPenetrationDepth();

        // Allowed penetration allowance (linear slop)
        double allowedPenetration = 0.05;
        double error = Math.max(0.0, depth - allowedPenetration);

        // Calculate the raw Baumgarte push
        double rawBias = (beta / dt) * error;

        // Cap the maximum push speed to prevent explosions
        double maxResolutionVelocity = 30.0;
        return Math.min(rawBias, maxResolutionVelocity);
    }

    @Override
    public void initConstraint(double beta, double dt) {
        super.initConstraint(beta, dt);
        this.beta = beta;

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

    @Override
    public double calculatePseudoDeltaJ() {
        Vector2 normal = contactManifold.getNormalVector();

        // Pseudo velocities do not include linear/angular structural body velocities
        Vector2 body1PseudoSpeed = contactManifold.getBody1().getPseudoVelocity();
        Vector2 body1PseudoRotSpeed = contactManifold.getContactPointRelativeBody1().cross(contactManifold.getBody1().getPseudoAngularVelocity()).times(-1);

        Vector2 body2PseudoSpeed = contactManifold.getBody2().getPseudoVelocity();
        Vector2 body2PseudoRotSpeed = contactManifold.getContactPointRelativeBody2().cross(contactManifold.getBody2().getPseudoAngularVelocity()).times(-1);

        double mass1 = contactManifold.getBody1().getMass();
        double mass2 = contactManifold.getBody2().getMass();

        double rel1CrossN = contactManifold.getContactPointRelativeBody1().cross(normal);
        double rel2CrossN = contactManifold.getContactPointRelativeBody2().cross(normal);

        double inertia1 = contactManifold.getBody1().getInertiaMoment();
        double inertia2 = contactManifold.getBody2().getInertiaMoment();

        // Calculate penetration error velocity profile
        Vector2 relativePseudoVelocityVector = body2PseudoSpeed.plus(body2PseudoRotSpeed).minus(body1PseudoSpeed).minus(body1PseudoRotSpeed);
        double relativePseudoVelocity = relativePseudoVelocityVector.dot(normal);

        double numerator = relativePseudoVelocity - calculateBias(beta,1.0);
        double denominator = 1.0/mass1 + 1.0/mass2 + (rel1CrossN * rel1CrossN) / inertia1 + (rel2CrossN * rel2CrossN) / inertia2;

        if (denominator == 0.0) return 0.0;

        return -numerator / denominator;
    }

    @Override
    public void capPseudoJ() {
        if (pseudoJ < 0) {
            pseudoJ = 0;
        }
    }

    @Override
    public void updatePseudoVelocity(double realPseudoDeltaJ) {
        Vector2 normal = contactManifold.getNormalVector();
        double mass1 = contactManifold.getBody1().getMass();
        double mass2 = contactManifold.getBody2().getMass();

        double rel1CrossN = contactManifold.getContactPointRelativeBody1().cross(normal);
        double rel2CrossN = contactManifold.getContactPointRelativeBody2().cross(normal);

        double inertia1 = contactManifold.getBody1().getInertiaMoment();
        double inertia2 = contactManifold.getBody2().getInertiaMoment();

        contactManifold.getBody1().addPseudoVelocity(normal.times(-realPseudoDeltaJ / mass1));
        contactManifold.getBody2().addPseudoVelocity(normal.times(realPseudoDeltaJ / mass2));

        contactManifold.getBody1().addPseudoAngularVelocity(-rel1CrossN * realPseudoDeltaJ / inertia1);
        contactManifold.getBody2().addPseudoAngularVelocity(rel2CrossN * realPseudoDeltaJ / inertia2);
    }
}