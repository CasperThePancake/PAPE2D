package PAPE2D.constraint;

import PAPE2D.*;
import PAPE2D.bodies.Circle;
import PAPE2D.helper.ContactBuffer;
import PAPE2D.helper.ContactManifold;
import PAPE2D.helper.ContactRecord;
import PAPE2D.helper.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * Constraint that forces two possibly colliding bodies to stop penetrating (and bounce back)
 */
public class ContactConstraint extends ScalarDynamicConstraint {
    // =================================================================================
    // Attributes
    // =================================================================================
    private ContactManifold contactManifold;
    private FrictionConstraint myFrictionConstraint;
    private double bounceVelocity = 0.0;
    private double beta = 0;
    private RestitutionMethod restitutionMethod;

    // =================================================================================
    // Constructor
    // =================================================================================
    public ContactConstraint(ContactManifold contactManifold, RestitutionMethod restitutionMethod) {
        setContactManifold(contactManifold);
        this.restitutionMethod = restitutionMethod;
    }

    // =================================================================================
    // Initialization and friction relation
    // =================================================================================
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

    public static List<ContactConstraint> createConstraints(List<ContactManifold> contactManifolds, RestitutionMethod restitutionMethod) {
        List<ContactConstraint> outputList = new ArrayList<>();
        for (ContactManifold manifold : contactManifolds) {
            outputList.add(new ContactConstraint(manifold, restitutionMethod));
        }
        return outputList;
    }

    public FrictionConstraint createFrictionConstraint(FrictionCoefficientMethod frictionCoefficientMethod) {
        return new FrictionConstraint(this, getContactManifold(), frictionCoefficientMethod.calculateCoefficient(contactManifold.getBody1().getFrictionCoefficient(),contactManifold.getBody2().getFrictionCoefficient()));
    }

    // =================================================================================
    // Solver methods
    // =================================================================================
    @Override
    public double calculateDeltaJ() {
        Vector2 normal = contactManifold.getNormalVector();

        Vector2 body1speed = contactManifold.getBody1().getVelocity();
        Vector2 body1rotSpeed = contactManifold.getContactPointRelativeBody1().cross(contactManifold.getBody1().getAngularVelocity()).times(-1);

        Vector2 body2speed = contactManifold.getBody2().getVelocity();
        Vector2 body2rotSpeed = contactManifold.getContactPointRelativeBody2().cross(contactManifold.getBody2().getAngularVelocity()).times(-1);

        // USE INVERSE MASS/INERTIA DIRECTLY
        double invMass1 = contactManifold.getBody1().getInverseMass();
        double invMass2 = contactManifold.getBody2().getInverseMass();
        double invInertia1 = contactManifold.getBody1().getInverseInertia();
        double invInertia2 = contactManifold.getBody2().getInverseInertia();

        double rel1CrossN = contactManifold.getContactPointRelativeBody1().cross(normal);
        double rel2CrossN = contactManifold.getContactPointRelativeBody2().cross(normal);

        Vector2 relativeVelocityVector = body2speed.plus(body2rotSpeed).minus(body1speed).minus(body1rotSpeed);
        double relativeVelocity = relativeVelocityVector.dot(normal);

        double numerator = relativeVelocity - bounceVelocity;

        // Clean denominator using inverse values
        double denominator = invMass1 + invMass2 + (rel1CrossN * rel1CrossN) * invInertia1 + (rel2CrossN * rel2CrossN) * invInertia2;

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

        double invMass1 = contactManifold.getBody1().getInverseMass();
        double invMass2 = contactManifold.getBody2().getInverseMass();
        double invInertia1 = contactManifold.getBody1().getInverseInertia();
        double invInertia2 = contactManifold.getBody2().getInverseInertia();

        double rel1CrossN = contactManifold.getContactPointRelativeBody1().cross(normal);
        double rel2CrossN = contactManifold.getContactPointRelativeBody2().cross(normal);

        // Multiply by inverse mass. If frozen, invMass is 0.0, so velocity change is 0.0!
        contactManifold.getBody1().addVelocity(normal.times(-realDeltaJ * invMass1));
        contactManifold.getBody2().addVelocity(normal.times(realDeltaJ * invMass2));

        contactManifold.getBody1().addAngularVelocity(-rel1CrossN * realDeltaJ * invInertia1);
        contactManifold.getBody2().addAngularVelocity(rel2CrossN * realDeltaJ * invInertia2);
    }

    @Override
    public double calculateBias(double beta, double dt) {
        double depth = contactManifold.getPenetrationDepth();

        // Allowed penetration allowance (linear slop)
        double allowedPenetration = 0.002;
        double error = Math.max(0.0, depth - allowedPenetration);

        // Calculate the raw Baumgarte push
        double rawBias = (beta / dt) * error;

        // Cap the maximum push speed to prevent explosions
        double maxResolutionVelocity = 30.0;
        return Math.min(rawBias, maxResolutionVelocity);
    }

    @Override
    public void initConstraint(double beta, double dt) {
        // Wake up bodies if necessary
        // (two sleeping bodies colliding would not create a manifold (see beginning of SAT.java), so this would mean a non-sleeper hit a sleeper)
        if (contactManifold.getBody1().getIsland() != contactManifold.getBody2().getIsland() && !contactManifold.getBody1().hasFlag(Flag.FROZEN) && !contactManifold.getBody2().hasFlag(Flag.FROZEN) ) {
            contactManifold.getBody1().wake();
            contactManifold.getBody2().wake();
        }

        // General initialization
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

        double restitution = restitutionMethod.calculateRestitution(contactManifold.getBody1().getRestitution(),contactManifold.getBody2().getRestitution());

        if (relativeVelocity < -0.5) {
            bounceVelocity = -restitution * relativeVelocity;
        } else {
            bounceVelocity = 0.0;
        }
    }

    @Override
    public void resetConstraint(World world) {
        // Nothing...
    }

    @Override
    public double calculatePseudoDeltaJ() {
        Vector2 normal = contactManifold.getNormalVector();

        // Pseudo velocities do not include linear/angular structural body velocities
        Vector2 body1PseudoSpeed = contactManifold.getBody1().getPseudoVelocity();
        Vector2 body1PseudoRotSpeed = contactManifold.getContactPointRelativeBody1().cross(contactManifold.getBody1().getPseudoAngularVelocity()).times(-1);

        Vector2 body2PseudoSpeed = contactManifold.getBody2().getPseudoVelocity();
        Vector2 body2PseudoRotSpeed = contactManifold.getContactPointRelativeBody2().cross(contactManifold.getBody2().getPseudoAngularVelocity()).times(-1);

        // Pull inverse mass and inertia directly
        double invMass1 = contactManifold.getBody1().getInverseMass();
        double invMass2 = contactManifold.getBody2().getInverseMass();
        double invInertia1 = contactManifold.getBody1().getInverseInertia();
        double invInertia2 = contactManifold.getBody2().getInverseInertia();

        double rel1CrossN = contactManifold.getContactPointRelativeBody1().cross(normal);
        double rel2CrossN = contactManifold.getContactPointRelativeBody2().cross(normal);

        // Calculate penetration error velocity profile
        Vector2 relativePseudoVelocityVector = body2PseudoSpeed.plus(body2PseudoRotSpeed).minus(body1PseudoSpeed).minus(body1PseudoRotSpeed);
        double relativePseudoVelocity = relativePseudoVelocityVector.dot(normal);

        double numerator = relativePseudoVelocity - calculateBias(beta, 1.0);

        // Clean, non-destructive denominator using multiplication by inverse values
        double denominator = invMass1 + invMass2 + (rel1CrossN * rel1CrossN) * invInertia1 + (rel2CrossN * rel2CrossN) * invInertia2;

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

        // Pull inverse mass and inertia directly
        double invMass1 = contactManifold.getBody1().getInverseMass();
        double invMass2 = contactManifold.getBody2().getInverseMass();
        double invInertia1 = contactManifold.getBody1().getInverseInertia();
        double invInertia2 = contactManifold.getBody2().getInverseInertia();

        double rel1CrossN = contactManifold.getContactPointRelativeBody1().cross(normal);
        double rel2CrossN = contactManifold.getContactPointRelativeBody2().cross(normal);

        contactManifold.getBody1().addPseudoVelocity(normal.times(-realPseudoDeltaJ * invMass1));
        contactManifold.getBody2().addPseudoVelocity(normal.times(realPseudoDeltaJ * invMass2));

        contactManifold.getBody1().addPseudoAngularVelocity(-rel1CrossN * realPseudoDeltaJ * invInertia1);
        contactManifold.getBody2().addPseudoAngularVelocity(rel2CrossN * realPseudoDeltaJ * invInertia2);
    }

    // =================================================================================
    // Contact records
    // =================================================================================

    /**
     * Add the contact record for this (solved) contact constraint to the given contact buffer
     *
     * @param contactBuffer Given contact buffer
     */
    @Internal
    public void addRecord(ContactBuffer contactBuffer) {
        contactBuffer.addContact(contactManifold.getBody1(),contactManifold.getBody2(),contactManifold.getContactPoint(),contactManifold.getNormalVector(),contactManifold.getTangentVector(),contactManifold.getPenetrationDepth(),getJ());
    }
}