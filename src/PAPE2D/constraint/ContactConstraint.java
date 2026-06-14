package PAPE2D.constraint;

import PAPE2D.DynamicConstraint;
import PAPE2D.helper.ContactManifold;
import PAPE2D.helper.Vector2;

import java.util.ArrayList;
import java.util.List;

public class ContactConstraint extends DynamicConstraint {
    // =================================================================================
    // Attributes
    // =================================================================================
    private ContactManifold contactManifold; // Holds bodies, normal/tangential, penetration depth, relative vectors for velocity, ...
    private FrictionConstraint myFrictionConstraint;

    // =================================================================================
    // Constructors
    // =================================================================================

    /**
     * Create a new contact constraint based on the given contact manifold
     *
     * @param contactManifold Given contact manifold
     */
    public ContactConstraint(ContactManifold contactManifold) {
        setContactManifold(contactManifold);
    }

    // =================================================================================
    // Getters & setters
    // =================================================================================

    /**
     * Get the contact manifold associated with this contact constraint
     *
     * @return Contact manifold associated with this contact constraint
     */
    public ContactManifold getContactManifold() {
        return contactManifold;
    }

    /**
     * Set the contact manifold for this contact constraint
     *
     * @param contactManifold Given contact manifold
     */
    private void setContactManifold(ContactManifold contactManifold) {
        this.contactManifold = contactManifold;
    }

    /**
     * Get the friction constraint associated with this contact constraint
     *
     * @return Associated friction constraint
     */
    public FrictionConstraint getMyFrictionConstraint() {
        return myFrictionConstraint;
    }

    /**
     * Set the friction constraint for this contact constraint
     *
     * @param myFrictionConstraint Given friction constraint
     */
    public void setMyFrictionConstraint(FrictionConstraint myFrictionConstraint) {
        this.myFrictionConstraint = myFrictionConstraint;
    }

    // =================================================================================
    // Solver methods
    // =================================================================================

    /**
     * Create the list of all necessary contact constraints, given list of contact manifolds
     *
     * @param contactManifolds Given list of contact manifolds
     *
     * @return Resulting list of contact constraints
     */
    public static List<ContactConstraint> createConstraints(List<ContactManifold> contactManifolds) {
        List<ContactConstraint> outputList = new ArrayList<>();

        for (ContactManifold manifold : contactManifolds) {
            outputList.add(new ContactConstraint(manifold));
        }

        return outputList;
    }

    /**
     * Create the friction constraint associated with this contact constraint
     *
     * @return Associated friction constraint
     */
    public FrictionConstraint createFrictionConstraint() {
        return new FrictionConstraint(this,getContactManifold());
    }

    // =================================================================================
    // Implementations
    // =================================================================================
    @Override
    public double calculateDeltaJ() {
        Vector2 normal = contactManifold.getNormalVector();
        Vector2 body1speed = contactManifold.getBody1().getVelocity();
        Vector2 body1rotSpeed = contactManifold.getContactPointRelativeBody1().cross(contactManifold.getBody1().getAngularVelocity()).times(-1);
        Vector2 body2speed = contactManifold.getBody2().getVelocity();
        Vector2 body2rotSpeed = contactManifold.getContactPointRelativeBody2().cross(contactManifold.getBody2().getAngularVelocity()).times(-1);
        double mass1 = contactManifold.getBody1().getMass();
        double mass2 = contactManifold.getBody2().getMass();
        double rel1CrossN = contactManifold.getContactPointRelativeBody1().cross(contactManifold.getNormalVector());
        double rel2CrossN = contactManifold.getContactPointRelativeBody2().cross(contactManifold.getNormalVector());
        double inertia1 = contactManifold.getBody1().getInertiaMoment();
        double inertia2 = contactManifold.getBody2().getInertiaMoment();

        double numerator = normal.dot(body2speed.plus(body2rotSpeed).minus(body1speed).minus(body1rotSpeed));
        double denominator = 1/mass1 + 1/mass2 + Math.abs(rel1CrossN)*Math.abs(rel1CrossN)/inertia1 + Math.abs(rel2CrossN)*Math.abs(rel2CrossN)/inertia2;

        return -(numerator+getCurrentBias())/denominator;
    }

    @Override
    public void capJ() {
        if (J < 0) {
            J = 0;
        }
    }

    @Override
    public void updateVelocity(double realDeltaJ) {
        Vector2 normal = contactManifold.getNormalVector();
        double mass1 = contactManifold.getBody1().getMass();
        double mass2 = contactManifold.getBody2().getMass();
        double rel1CrossN = contactManifold.getContactPointRelativeBody1().cross(contactManifold.getNormalVector());
        double rel2CrossN = contactManifold.getContactPointRelativeBody2().cross(contactManifold.getNormalVector());
        double inertia1 = contactManifold.getBody1().getInertiaMoment();
        double inertia2 = contactManifold.getBody2().getInertiaMoment();

        contactManifold.getBody1().addVelocity(normal.times(-realDeltaJ/mass1));
        contactManifold.getBody2().addVelocity(normal.times(realDeltaJ/mass2));
        contactManifold.getBody1().addAngularVelocity(-Math.abs(rel1CrossN)*realDeltaJ/inertia1);
        contactManifold.getBody2().addAngularVelocity(Math.abs(rel2CrossN)*realDeltaJ/inertia2);
    }

    @Override
    public double calculateBias(double beta, double dt) {
        return beta/dt * Math.min(0,contactManifold.getPenetrationDepth());
    }
}
