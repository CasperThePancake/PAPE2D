package PAPE2D.bodies;

import PAPE2D.Body;
import PAPE2D.Fixture;
import PAPE2D.PhysicsLoop;
import PAPE2D.helper.Vector2;

import java.util.List;

/**
 * Circle body class
 */
public class Circle extends Body {
    // =================================================================================
    // Attributes
    // =================================================================================

    // None...

    // =================================================================================
    // Constructors
    // =================================================================================
    /**
     * Create a circle with given radius, position, velocity, angle, angular velocity, and mass
     *
     * @param radius Given radius
     * @param position Given position (corresponds with circle center)
     * @param velocity Given velocity
     * @param angle Given rotation
     * @param angularVelocity Given rotational velocity
     * @param mass Given mass
     */
    public Circle(Vector2 position, double radius, double mass, Vector2 velocity, double angle, double angularVelocity) {
        Fixture circleFixture = new PAPE2D.fixtures.Circle(mass,radius);

        super(position, List.of(circleFixture), List.of(new Vector2()), velocity, angle, angularVelocity);
    }

    /**
     * Create a circle with given radius, position, velocity, mass, and no starting rotation
     * @param radius Given radius
     * @param position Given position (corresponds with circle center)
     * @param velocity Given velocity
     * @param mass Given mass
     */
    public Circle(Vector2 position, double radius, double mass, Vector2 velocity) {
        this(position,radius,mass,velocity,0,0);
    }

    /**
     * Create a circle with given radius, position, mass, no starting rotation or velocity
     *
     * @param radius Given radius
     * @param position Given position (corresponds with circle center)
     * @param mass Given mass
     */
    public Circle(Vector2 position, double radius, double mass) {
        this(position,radius,mass,new Vector2(),0,0);
    }
}
