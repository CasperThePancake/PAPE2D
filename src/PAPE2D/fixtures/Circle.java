package PAPE2D.fixtures;

import PAPE2D.Fixture;
import PAPE2D.PhysicsLoop;
import PAPE2D.helper.Vector2;

import java.util.List;

/**
 * Fixture implementation for a basic circle shape
 *
 * @note Origin point corresponds to circle center
 */
public class Circle extends Fixture {
    // =================================================================================
    // Attributes
    // =================================================================================
    private double radius;

    // =================================================================================
    // Constructor
    // =================================================================================
    /**
     * Create a new circle fixture with given mass and radius
     *
     * @param mass Given mass
     * @param radius Given radius
     */
    public Circle(double mass, double radius) {
        this.radius = radius;
        super(mass,calculateInertiaMoment(mass,radius));
    }

    // =================================================================================
    // Getters
    // =================================================================================
    /**
     * Get the radius of this circle fixture
     *
     * @return Radius of this circle fixture
     */
    public double getRadius() {
        return radius;
    }

    // =================================================================================
    // Internal methods
    // =================================================================================
    protected static double calculateInertiaMoment(double mass, double radius) {
        return 0.5 * mass * radius * radius;
    }

    @Override
    protected Vector2 getCOM(Vector2 originPoint) {
        return originPoint; // COM of circle is just the center, which is the origin here
    }

    @Override
    public void updateAABB() {
        setAABBminX(getPosition().getX() - radius);
        setAABBmaxX(getPosition().getX() + radius);
        setAABBminY(getPosition().getY() - radius);
        setAABBmaxY(getPosition().getY() + radius);
    }

    @Override
    public void updateInternally() {
        // Nothing...
    }

    @Override
    public List<Vector2> getSATAxes(Fixture other) {
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

        return new Double[]{p-radius, p+radius};
    }

    @Override
    public void render(PhysicsLoop physicsLoop) {
        // Calculate pixel coordinates
        double cX = physicsLoop.worldToScreenCoords(getPosition()).getX();
        double cY = physicsLoop.worldToScreenCoords(getPosition()).getY();

        // Calculate radius properly
        double xFurther = physicsLoop.worldToScreenCoords(new Vector2(getPosition().getX()+radius,getPosition().getY())).getX();
        double screenRadius = xFurther - cX;

        physicsLoop.drawCircle(cX,cY,screenRadius);
    }
}
