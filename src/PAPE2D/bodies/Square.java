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
    public Square(Vector2 position, double width, Vector2 velocity, double angle, double angularVelocity, double mass) {
        super(position, width, width, velocity, angle, angularVelocity, mass);
    }

    /**
     * Create a square with given width, position, velocity, mass and no angle
     *
     * @param position Given position (corresponds with top-left corner)
     * @param width Given width
     * @param velocity Given velocity
     * @param mass Given mass
     */
    public Square(Vector2 position, double width, Vector2 velocity, double mass) {
        this(position, width, velocity, 0, 0, mass);
    }
}
