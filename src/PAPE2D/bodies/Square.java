package PAPE2D.bodies;

import PAPE2D.helper.Vector2;

/**
 * Square body class
 */
public class Square extends Rectangle {
    // =================================================================================
    // Constructors
    // =================================================================================
    /**
     * Create a square with given width, position, velocity, angle, angular velocity, and mass
     *
     * @param position Given position (corresponds with top-left corner)
     * @param width Given width
     * @param velocity Given velocity
     * @param angle Given angle
     * @param angularVelocity Given angular velocity
     * @param mass Given mass
     */
    public Square(Vector2 position, double width, double mass, Vector2 velocity, double angle, double angularVelocity) {
        super(position, width, width, mass, velocity, angle, angularVelocity);
    }

    /**
     * Create a square with given width, position, velocity, mass and no angle
     *
     * @param position Given position (corresponds with top-left corner)
     * @param width Given width
     * @param velocity Given velocity
     * @param mass Given mass
     */
    public Square(Vector2 position, double width, double mass, Vector2 velocity) {
        this(position, width, mass, velocity, 0, 0);
    }

    /**
     * Create a square with given width, position, mass, no angle or velocity
     *
     * @param position Given position (corresponds with top-left corner)
     * @param width Given width
     * @param mass Given mass
     */
    public Square(Vector2 position, double width, double mass) {
        this(position, width, mass, new Vector2(), 0, 0);
    }
}
