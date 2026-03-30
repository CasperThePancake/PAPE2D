package PAPE2D.bodies;

import PAPE2D.Body;
import PAPE2D.helper.Vector2;

public class Circle extends Body {
    private double radius;

    /**
     * Create a circle with given radius, position (= circle center), velocity, and mass
     *
     * @param position Given position (= circle center)
     * @param velocity Given velocity
     * @param mass Given mass
     */
    public Circle(double radius, Vector2 position, Vector2 velocity, double angle, double angularVelocity, double mass) {
        super(position, velocity, angle, angularVelocity, mass, calculateInertiaMoment(mass,radius),calculateOriginVector());
        this.setRadius(radius);
    }

    public Circle(double radius, Vector2 position, Vector2 velocity, double mass) {
        super(position, velocity, 0, 0, mass, calculateInertiaMoment(mass,radius),calculateOriginVector());
        this.setRadius(radius);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public static double calculateInertiaMoment(double mass, double radius) {
        return 0.5 * mass * radius * radius;
    }

    public static Vector2 calculateOriginVector() {
        return new Vector2(0,0);
    }

    @Override
    public void updateAABB() {
        setAABBminX(getPosition().getX() - getRadius());
        setAABBmaxX(getPosition().getX() + getRadius());
        setAABBminY(getPosition().getY() - getRadius());
        setAABBmaxY(getPosition().getY() + getRadius());
    }
}
