package PAPE2D.constraint;

import PAPE2D.Body;
import PAPE2D.Constraint;
import PAPE2D.StaticConstraint;
import PAPE2D.World;
import PAPE2D.helper.Vector2;

public class RevoluteJointConstraint extends StaticConstraint {
    // =================================================================================
    // Attributes
    // =================================================================================
    protected Vector2 J = new Vector2();
    protected Vector2 pseudoJ = new Vector2();
    private Vector2 currentBias;

    private Body body1;
    private Body body2;
    private Vector2 body1Connection;
    private Vector2 body2Connection;
    private Vector2 body1ConnectionUnrotated;
    private Vector2 body2ConnectionUnrotated;

    // =================================================================================
    // Constructor
    // =================================================================================

    /**
     * Create a new revolute joint constraint, keeping a shared point on two bodies locked in place with free revolution
     *
     * @param body1 Given first body
     * @param body2 Given second body
     * @param body1Connection Given vector from body 1's COM to the shared connection point (assumed unrotated)
     * @param body2Connection Given vector from body 2's COM to the shared connection point (assumed unrotated)
     */
    public RevoluteJointConstraint(Body body1, Body body2, Vector2 body1Connection, Vector2 body2Connection) {
        this.body1 = body1;
        this.body2 = body2;
        this.body1ConnectionUnrotated = body1Connection;
        this.body2ConnectionUnrotated = body2Connection;
        this.body1Connection = body1ConnectionUnrotated.rotate(body1.getAngle());
        this.body2Connection = body2ConnectionUnrotated.rotate(body2.getAngle());
    }

    // =================================================================================
    // Getters & setters
    // =================================================================================
    public Vector2 getCurrentBias() {
        return currentBias;
    }

    public Vector2 getJ() {
        return J;
    }

    private void rotateConnections() {
        this.body1Connection = body1ConnectionUnrotated.rotate(body1.getAngle());
        this.body2Connection = body2ConnectionUnrotated.rotate(body2.getAngle());
    }

    // =================================================================================
    // Solver methods
    // =================================================================================

    public void capJ() {
        // No J capping for this static constraint!
    }

    public void capPseudoJ() {
        // Same here!
    }

    public void initConstraint(double beta, double dt) {
        rotateConnections();
        currentBias = calculateBias(beta,dt);
        J = new Vector2(); // No warm-starting (if you do add this, make sure to add that contribution of starting non-zero J here)
        pseudoJ = new Vector2(); // Never warm-start for pseudo-velocities!
    }

    public void updateConstraint() {
        Vector2 J_old = J;
        Vector2 deltaJ = calculateDeltaJ();
        J = J.plus(deltaJ);
        capJ();
        Vector2 realDeltaJ = J.minus(J_old);
        updateVelocity(realDeltaJ);
    }

    public void updatePseudoConstraint() {
        Vector2 pseudoJ_old = pseudoJ;
        Vector2 pseudoDeltaJ = calculatePseudoDeltaJ();
        pseudoJ = pseudoJ.plus(pseudoDeltaJ);
        capPseudoJ();
        Vector2 realPseudoDeltaJ = pseudoJ.minus(pseudoJ_old);
        updatePseudoVelocity(realPseudoDeltaJ);
    }

    public void resetConstraint(World world) {
        // Nothing...
    }

    public Vector2 calculateDeltaJ() {
        Vector2 relativeVelocity = body2.getVelocity().plus(body2Connection.cross(body2.getAngularVelocity()).times(-1)).minus(body1.getVelocity()).minus(body1Connection.cross(body1.getAngularVelocity()).times(-1));
        double r1x = body1Connection.getX();
        double r1y = body1Connection.getY();
        double r2x = body2Connection.getX();
        double r2y = body2Connection.getY();

        double a = body1.getInverseMass() + r1y*r1y*body1.getInverseInertia() + body2.getInverseMass() + r2y*r2y*body2.getInverseInertia();
        double b = -r1x*r1y*body1.getInverseInertia() - r2x*r2y*body2.getInverseInertia();
        double c = body1.getInverseMass() + r1x*r1x*body1.getInverseInertia() + body2.getInverseMass() + r2x*r2x*body2.getInverseInertia();

        return new Vector2(
            c*relativeVelocity.getX() - b*relativeVelocity.getY(),
            a*relativeVelocity.getY() - b*relativeVelocity.getX()
        ).times(-1/(a*c-b*b));
    }

    public Vector2 calculatePseudoDeltaJ() {
        Vector2 relativeVelocity = body2.getPseudoVelocity().plus(body2Connection.cross(body2.getPseudoAngularVelocity()).times(-1)).minus(body1.getPseudoVelocity()).minus(body1Connection.cross(body1.getPseudoAngularVelocity()).times(-1));
        double r1x = body1Connection.getX();
        double r1y = body1Connection.getY();
        double r2x = body2Connection.getX();
        double r2y = body2Connection.getY();

        double a = body1.getInverseMass() + r1y*r1y*body1.getInverseInertia() + body2.getInverseMass() + r2y*r2y*body2.getInverseInertia();
        double b = -r1x*r1y*body1.getInverseInertia() - r2x*r2y*body2.getInverseInertia();
        double c = body1.getInverseMass() + r1x*r1x*body1.getInverseInertia() + body2.getInverseMass() + r2x*r2x*body2.getInverseInertia();

        Vector2 numerator = relativeVelocity.plus(getCurrentBias());

        return new Vector2(
                c*numerator.getX() - b*numerator.getY(),
                a*numerator.getY() - b*numerator.getX()
        ).times(-1/(a*c-b*b));
    }

    public void updateVelocity(Vector2 realDeltaJ) {
        body1.addVelocity(realDeltaJ.times(-body1.getInverseMass()));
        body1.addAngularVelocity(body1Connection.getY()*realDeltaJ.getX()*body1.getInverseInertia() - body1Connection.getX()* realDeltaJ.getY()*body1.getInverseInertia());
        body2.addVelocity(realDeltaJ.times(body2.getInverseMass()));
        body2.addAngularVelocity(body2Connection.getX()*realDeltaJ.getY()*body2.getInverseInertia() - body2Connection.getY()* realDeltaJ.getX()* body2.getInverseInertia());
    }

    public void updatePseudoVelocity(Vector2 realPseudoDeltaJ) {
        body1.addPseudoVelocity(realPseudoDeltaJ.times(-body1.getInverseMass()));
        body1.addPseudoAngularVelocity(body1Connection.getY()*realPseudoDeltaJ.getX()*body1.getInverseInertia() - body1Connection.getX()* realPseudoDeltaJ.getY()*body1.getInverseInertia());
        body2.addPseudoVelocity(realPseudoDeltaJ.times(body2.getInverseMass()));
        body2.addPseudoAngularVelocity(body2Connection.getX()*realPseudoDeltaJ.getY()*body2.getInverseInertia() - body2Connection.getY()* realPseudoDeltaJ.getX()* body2.getInverseInertia());
    }

    public Vector2 calculateBias(double beta, double dt) {
        Vector2 posError = body2.getPosition().plus(body2Connection).minus(body1.getPosition().plus(body1Connection));
        return posError.times(beta / 1.0);
    }
}
