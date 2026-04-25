package PAPE2D.bodies;

import PAPE2D.Body;
import PAPE2D.helper.Vector2;

import java.util.List;

/**
 * Circle body class
 */
public class Circle extends Body {
    // =================================================================================
    // Attributes
    // =================================================================================
    private double radius;

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
    public Circle(double radius, Vector2 position, Vector2 velocity, double angle, double angularVelocity, double mass) {
        super(position, velocity, angle, angularVelocity, mass, calculateInertiaMoment(mass,radius),calculateOriginVector());
        this.setRadius(radius);
    }

    /**
     * Create a circle with given radius, position, velocity, mass, and no starting rotation
     * @param radius Given radius
     * @param position Given position (corresponds with circle center)
     * @param velocity Given velocity
     * @param mass Given mass
     */
    public Circle(double radius, Vector2 position, Vector2 velocity, double mass) {
        super(position, velocity, 0, 0, mass, calculateInertiaMoment(mass,radius),calculateOriginVector());
        this.setRadius(radius);
    }

    // =================================================================================
    // Radius
    // =================================================================================
    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    // =================================================================================
    // Body necessities
    // =================================================================================
    public static double calculateInertiaMoment(double mass, double radius) {
        return 0.5 * mass * radius * radius;
    }

    public static Vector2 calculateOriginVector() {
        return new Vector2(0,0);
    }

    @Override
    public void updateInternally() {
        // Nothing...
    }

    @Override
    public void updateAABB() {
        setAABBminX(getPosition().getX() - getRadius());
        setAABBmaxX(getPosition().getX() + getRadius());
        setAABBminY(getPosition().getY() - getRadius());
        setAABBmaxY(getPosition().getY() + getRadius());
    }

    @Override
    public List<Vector2> getSATAxes(Body other) {
        // For a circle, the SAT axis to check is the one connecting its center to the other body's closest reference (like closest polygon vertex)
        return List.of(getPosition().minus(other.getClosestReferenceTo(getPosition())));
    }

    @Override
    public Vector2 getClosestReferenceTo(Vector2 position) {
        return getPosition();
    }

    @Override
    public Double[] getProjectionEdges(Vector2 projectionAxis) {
        Vector2 normalizedProjectionAxis = projectionAxis.normalized();
        double p = getPosition().dot(normalizedProjectionAxis);

        return new Double[]{p-getRadius(), p+getRadius()};
    }
}
