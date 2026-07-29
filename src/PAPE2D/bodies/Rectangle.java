package PAPE2D.bodies;

import PAPE2D.helper.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Rectangle body class
 */
public class Rectangle extends Polygon {
    // =================================================================================
    // Constructors
    // =================================================================================
    /**
     * Create a rectangle with given width, height, position, velocity, angle, angular velocity, and mass
     *
     * @param position Given position (corresponds with top-left corner)
     * @param width Given width (+x direction)
     * @param height Given height (-y direction)
     * @param velocity Given velocity
     * @param angle Given angle
     * @param angularVelocity Given angular velocity
     * @param mass Given mass
     */
    public Rectangle(Vector2 position, double width, double height, double mass, Vector2 velocity, double angle, double angularVelocity) {
        // Construct rectangle vertices
        List<Vector2> vertices = new ArrayList<>();
        vertices.add(position);
        vertices.add(position.plus(new Vector2(width,0)));
        vertices.add(position.plus(new Vector2(width,-height)));
        vertices.add(position.plus(new Vector2(0,-height)));

        super(vertices, mass, velocity, angle, angularVelocity);
    }

    /**
     * Create a rectangle with given width, height, position, velocity, mass, and no angle
     *
     * @param position Given position (corresponds with top-left corner)
     * @param width Given width (+x direction)
     * @param height Given height (-y direction)
     * @param velocity Given velocity
     * @param mass Given mass
     */
    public Rectangle(Vector2 position, double width, double height, double mass, Vector2 velocity) {
        this(position,width,height,mass,velocity,0,0);
    }

    /**
     * Create a rectangle with given width, height, position, mass, no angle or velocity
     *
     * @param position Given position (corresponds with top-left corner)
     * @param width Given width (+x direction)
     * @param height Given height (-y direction)
     * @param mass Given mass
     */
    public Rectangle(Vector2 position, double width, double height, double mass) {
        this(position,width,height,mass,new Vector2(),0,0);
    }
}
