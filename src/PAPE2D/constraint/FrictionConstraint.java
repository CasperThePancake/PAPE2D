package PAPE2D.constraint;

import PAPE2D.DynamicConstraint;
import PAPE2D.helper.ContactManifold;

import java.util.List;

public class FrictionConstraint extends DynamicConstraint {
    // =================================================================================
    // Attributes
    // =================================================================================
    private ContactConstraint myContactConstraint;
    private ContactManifold contactManifold;

    // =================================================================================
    // Constructor
    // =================================================================================
    /**
     * Create a new friction constraint linked to given contact manifold and contact constraint
     *
     * @param myContactConstraint Associated contact constraint object
     * @param contactManifold Given contact manifold
     */
    public FrictionConstraint(ContactConstraint myContactConstraint, ContactManifold contactManifold) {
        this.myContactConstraint = myContactConstraint;
        this.contactManifold = contactManifold;
    }

    @Override
    public double calculateDeltaJ() {
        return 0;
    }

    @Override
    public void capJ() {

    }

    @Override
    public void updateVelocity(double realDeltaJ) {

    }

    @Override
    public double calculateBias(double beta, double dt) {
        return 0;
    }
}
