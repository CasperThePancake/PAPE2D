package PAPE2D.force;

import PAPE2D.Body;
import PAPE2D.LocalForce;
import PAPE2D.helper.Vector2;

public class GravitationalAttraction extends LocalForce {
    // =================================================================================
    // Attributes
    // =================================================================================
    private final double G;
    private final Body body1;
    private final Body body2;

    // =================================================================================
    // Constructor
    // =================================================================================

    /**
     * Create a new gravitational attraction force
     *
     * @param body1 The first body in the relation
     * @param body2 The second body in the relation
     * @param G The value for G, the gravitational constant
     */
    public GravitationalAttraction(Body body1, Body body2, double G) {
        this.body1 = body1;
        this.body2 = body2;
        this.G = G;
    }

    // =================================================================================
    // Apply acceleration
    // =================================================================================
    @Override
    public void applyAcceleration(double dt) {
        Vector2 connection = body2.getPosition().minus(body1.getPosition()).normalized(); // Vector from body 1 to body 2
        double distance = body2.getPosition().distance(body1.getPosition());
        double m1 = body1.getMass();
        double m2 = body2.getMass();

        body1.addVelocity(  connection.times(G * m1 * m2 / (distance * distance) * dt).times(body1.getInverseMass())  );
        body2.addVelocity(  connection.times(-G * m1 * m2 / (distance * distance) * dt).times(body2.getInverseMass())  );
    }
}
