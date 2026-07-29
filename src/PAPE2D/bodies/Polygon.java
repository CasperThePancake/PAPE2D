package PAPE2D.bodies;

import PAPE2D.Body;
import PAPE2D.Fixture;
import PAPE2D.Optimize;
import PAPE2D.PhysicsLoop;
import PAPE2D.helper.Edge;
import PAPE2D.helper.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Polygon body class
 */
public class Polygon extends Body {
    // =================================================================================
    // Attributes
    // =================================================================================

    // None...

    // =================================================================================
    // Constructors
    // =================================================================================
    /**
     * Create a polygon with given list of vertices, velocity, angle, angular velocity, and mass
     *
     * @param vertices List of polygon vertices
     * @param velocity Given velocity
     * @param angle Given rotation
     * @param angularVelocity Given rotational velocity
     * @param mass Given mass
     *
     * @throws IllegalArgumentException If given polygon is not convex
     *      | !isConvex(vertices)
     *
     * @note With fixtures, support for concave polygons is possible (WIP)
     */
    public Polygon(List<Vector2> vertices, double mass, Vector2 velocity, double angle, double angularVelocity) throws IllegalArgumentException {
        Fixture polygonFixture = new PAPE2D.fixtures.Polygon(mass,vertices);

        super(vertices.getFirst(),List.of(polygonFixture),List.of(new Vector2()),velocity,angle,angularVelocity);
    }

    /**
     * Create a polygon with given list of vertices, velocity, mass, and no angle
     *
     * @param vertices List of polygon vertices
     * @param velocity Given velocity
     * @param mass Given mass
     */
    public Polygon(List<Vector2> vertices, double mass, Vector2 velocity) {
        this(vertices, mass, velocity, 0, 0);
    }

    /**
     * Create a polygon with given list of vertices, mass, and no angle or velocity
     *
     * @param vertices List of polygon vertices
     * @param mass Given mass
     */
    public Polygon(List<Vector2> vertices, double mass) {
        this(vertices, mass, new Vector2(), 0, 0);
    }
}
