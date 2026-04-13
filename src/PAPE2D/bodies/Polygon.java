package PAPE2D.bodies;

import PAPE2D.Body;
import PAPE2D.Optimize;
import PAPE2D.helper.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Polygon body class
 */
public class Polygon extends Body {
    private List<Vector2> internalVertices = new ArrayList<>();
    private List<Vector2> internalEdges = new ArrayList<>();
    private double area;

    /**
     * Create a polygon with given list of vertices, position, velocity, angle, angular velocity, and mass
     *
     * @param vertices List of polygon vertices
     * @param position Given position (corresponds with first vertex)
     * @param velocity Given velocity
     * @param angle Given rotation
     * @param angularVelocity Given rotational velocity
     * @param mass Given mass
     *
     * @throws IllegalArgumentException If given polygon is not convex
     *      | !isConvex(vertices)
     */
    public Polygon(List<Vector2> vertices, Vector2 position, Vector2 velocity, double angle, double angularVelocity, double mass) throws IllegalArgumentException {
        if (!isConvex(vertices)) {
            throw new IllegalArgumentException("The given polygon is not convex!");
        }

        super(position, velocity, angle, angularVelocity, mass, calculateInertiaMoment(mass,vertices), calculateOriginVector(mass,vertices));
        setArea(vertices);
        setInternalEdges(vertices);
        setInternalVertices(mass,vertices);
    }

    /**
     * Create a polygon with given list of vertices, position, velocity, mass, and no angle
     *
     * @param vertices List of polygon vertices
     * @param position Given position (corresponds with first vertex)
     * @param velocity Given velocity
     * @param mass Given mass
     */
    public Polygon(List<Vector2> vertices, Vector2 position, Vector2 velocity, double mass) {
        this(vertices,position, velocity, 0, 0, mass);
    }

    public double getArea() {
        return area;
    }

    private void setArea(List<Vector2> vertices) {
        this.area = calculateArea(vertices);
    }

    /**
     * Get the internal vertex vectors (from real origin/COM to vertices, non-rotated) for this polygon
     *
     * @return List of internal vertex vectors
     */
    public List<Vector2> getInternalVertices() {
        return internalVertices;
    }

    /**
     * Set the internal vertices for this polygon (non-rotated)
     *
     * @note Origin vector (super) must already have been set
     *
     * @param mass Given mass
     * @param vertices Given list of vertices
     */
    private void setInternalVertices(double mass, List<Vector2> vertices) {
        // Use only non-rotated things to store these!
        for (Vector2 v : vertices) {
            // Calculate vector from vertex 0 to vertex v
            Vector2 connect = v.minus(vertices.getFirst());

            internalVertices.add(getOriginVector().times(-1).plus(connect));
        }
    }

    /**
     * Store the internal edges for this polygon (non-rotated)
     *
     * @param vertices Given list of vertices
     */
    private void setInternalEdges(List<Vector2> vertices) {
        List<Vector2> output = new ArrayList<>();
        int N = vertices.size();
        for (int i = 0; i < N; i++) {
            internalEdges.add(vertices.get(i+1%N).minus(vertices.get(i)));
        }
    }

    /**
     * Get the internal edge vectors (vectors connecting vertices, non-rotated) for this polygon
     *
     * @return List of internal edge vectors
     */
    public List<Vector2> getInternalEdges() {
        return internalEdges;
    }

    /**
     * Check if a given list of vertices forms a convex polygon
     *
     * @param vertices Given list of vertices
     * @return Whether the formed polygon is convex
     */
    private static boolean isConvex(List<Vector2> vertices) {
        double previous = 0;
        double current = 0;
        int N = vertices.size();

        for (int i = 0; i < N; i++) {
            current = crossProduct(vertices.get(i), vertices.get(i+1 % N), vertices.get(i+2 % N));
            if (current * previous < 0) {
                return false;
            }
            previous = current;
        }

        return true;
    }

    private static double crossProduct(Vector2 vertex1, Vector2 vertex2, Vector2 vertex3) {
        Vector2 connect1 = vertex2.minus(vertex1);
        Vector2 connect2 = vertex3.minus(vertex2);

        return connect1.getX() * connect2.getY() - connect1.getY() * connect2.getX();
    }

    /**
     * Calculate the center of mass for this polygon
     *
     * @param mass Given mass
     * @param vertices Given list of vertices
     * @return Center of mass vector
     */
    public static Vector2 getCOM(double mass, List<Vector2> vertices) {
        int N = vertices.size();

        // Calculate centroid coordinates
        double xCOM = 0;
        double yCOM = 0;
        for (int i = 0; i < N; i++) {
            xCOM = xCOM + (vertices.get(i).getX() + vertices.get(i+1 % N).getX()) * (vertices.get(i).getX() * vertices.get(i+1 % N).getY() - vertices.get(i+1 % N).getX() * vertices.get(i).getY());
            yCOM = yCOM + (vertices.get(i).getY() + vertices.get(i+1 % N).getY()) * (vertices.get(i).getX() * vertices.get(i+1 % N).getY() - vertices.get(i+1 % N).getX() * vertices.get(i).getY());
        }
        xCOM /= 6*calculateArea(vertices);
        yCOM /= 6*calculateArea(vertices);

        return new Vector2(xCOM,yCOM);
    }

    public static double calculateArea(List<Vector2> vertices) {
        // Calculate shape area
        double area = 0;
        int N = vertices.size();
        for (int i = 0; i < N; i++) {
            area = area + vertices.get(i).getX() * vertices.get(i+1 % N).getY() - vertices.get(i+1 % N).getX() * vertices.get(i).getY();
        }

        return area;
    }

    /**
     * Calculate the moment of inertia for this polygon
     *
     * @param mass Given mass
     * @param vertices Given list of vertices
     * @return Moment of inertia for this polygon
     */
    public static double calculateInertiaMoment(double mass, List<Vector2> vertices) {
        // Useful things
        double area = calculateArea(vertices);
        Vector2 com = getCOM(mass,vertices);

        // Calculate polar moment of inertia around origin
        double j = 0;
        int N = vertices.size();
        for (int i = 0; i < N; i++) {
            j += (vertices.get(i).getX() * vertices.get(i + 1 % N).getY() - vertices.get(i + 1 % N).getX() * vertices.get(i).getY()) * (vertices.get(i).getX() * vertices.get(i).getX() + vertices.get(i).getX() * vertices.get(i + 1 % N).getX() + vertices.get(i + 1 % N).getX() * vertices.get(i + 1 % N).getX() + vertices.get(i).getY() * vertices.get(i).getY() + vertices.get(i).getY() * vertices.get(i + 1 % N).getY() + vertices.get(i + 1 % N).getY() * vertices.get(i + 1 % N).getY());
        }
        j /= 12;

        // Shift to COM using parallel axis theorem
        j -= area*(com.getX()*com.getX() + com.getY() * com.getY());

        // Return the normal moment of inertia (I=J*rho)
        return j * mass/area;
    }

    /**
     * Calculate the origin vector from user origin to real origin
     *
     * @param mass Given mass
     * @param vertices Given list of vertices
     * @return Origin vector
     */
    private static Vector2 calculateOriginVector(double mass, List<Vector2> vertices) {
        Vector2 com = getCOM(mass,vertices);

        return com.minus(vertices.getFirst());
    }

    @Override @Optimize
    public void updateAABB() {
        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;

        double cos = Math.cos(getAngle());
        double sin = Math.sin(getAngle());

        for (Vector2 v : getInternalVertices()) {
            Vector2 vReal = getPosition().plus(new Vector2(v.getX() * cos - v.getY() * sin, v.getX() * sin + v.getY() * cos));

            if (vReal.getX() < minX) {
                minX = vReal.getX();
            }

            if (vReal.getX() > maxX) {
                maxX = vReal.getX();
            }

            if (vReal.getY() < minY) {
                minY = vReal.getY();
            }

            if (vReal.getY() > maxY) {
                maxY = vReal.getY();
            }
        }

        setAABBminX(minX);
        setAABBmaxX(maxX);
        setAABBminY(minY);
        setAABBmaxY(maxY);
    }

    @Override
    public List<Vector2> getSATAxes(Body other) {
        // For a polygon, the SAT axes to check are the edge normals
        List<Vector2> output = new ArrayList<>();

        for (Vector2 v : getInternalEdges()) {
            output.add(v.rotate(getAngle()).normal());
        }

        return output;
    }

    @Override
    public Vector2 getClosestReferenceTo(Vector2 position) {
        double min = Double.POSITIVE_INFINITY;
        Vector2 minVec = null;

        double cos = Math.cos(getAngle());
        double sin = Math.sin(getAngle());
        for (Vector2 v : getInternalVertices()) {
            Vector2 vReal = getPosition().plus(v.rotate(getAngle()));

            if (vReal.distance(position) < min) {
                min = vReal.distance(position);
                minVec = vReal;
            }
        }

        return minVec;
    }

    @Override
    public Double[] getProjectionEdges(Vector2 projectionAxis) {
        Vector2 normalizedProjectionAxis = projectionAxis.normalized();

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        double centerDot = getPosition().dot(normalizedProjectionAxis);

        for (Vector2 v : getInternalVertices()) {
            // Vector2 vReal = getPosition().plus(v.rotate(getAngle()));
            // double p = vReal.dot(normalizedProjectionAxis); CAN BE FASTER!
            double p = centerDot + v.rotate(getAngle()).dot(normalizedProjectionAxis);

            min = Math.min(min, p);
            max = Math.max(max, p);
        }

        return new Double[]{min, max};
    }
}
