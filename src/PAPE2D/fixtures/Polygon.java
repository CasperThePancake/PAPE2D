package PAPE2D.fixtures;

import PAPE2D.Fixture;
import PAPE2D.PhysicsLoop;
import PAPE2D.helper.Edge;
import PAPE2D.helper.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fixture implementation for a convex polygon shape
 *
 * @note Origin point corresponds to first specified vertex
 */
public class Polygon extends Fixture {
    private List<Vector2> internalVertices = new ArrayList<>();
    private List<Vector2> internalEdges = new ArrayList<>();
    private List<Vector2> worldVertices = new ArrayList<>();
    private double area;

    /**
     * Create a new (convex) polygon fixture with given mass and list of vertex coordinates
     *
     * @param mass Given mass
     * @param vertices Given list of vertex coordinates
     *
     * @note The list is ordered; all that matters in the list are the relative positions of the vertices to define the shape, not the absolute coordinates used
     */
    public Polygon(double mass, List<Vector2> vertices) {
        // 1. Create a working copy so we don't mutate the user's input list directly
        List<Vector2> workingVertices = new ArrayList<>(vertices);
        Vector2 specialPoint = vertices.getFirst(); // Important this stays first, for user origin referencing

        // 2. Check convexity
        if (!isConvex(workingVertices)) {
            throw new IllegalArgumentException("The given polygon is not convex!");
        }

        // 3. Winding check & correction
        if (calculateArea(workingVertices) < 0) { // If vertices are given CW, area becomes negative (shoelace)
            // Reverse to ensure CCW
            Collections.reverse(workingVertices);

            // Cycle the special first point back to the list start
            while (!workingVertices.getFirst().equals(specialPoint)) {
                Collections.rotate(workingVertices, 1);
            }
        }

        super(mass,calculateInertiaMoment(mass,vertices));

        setArea(workingVertices);
        setInternalVertices(workingVertices);
        setInternalEdges(workingVertices);
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
     * @param vertices Given list of vertices
     */
    private void setInternalVertices(List<Vector2> vertices) {
        Vector2 tempCOM = calculateCOM(vertices);

        // Use only non-rotated things to store these!
        for (Vector2 v : vertices) {
            internalVertices.add(v.minus(tempCOM));
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
            internalEdges.add(vertices.get((i+1)%N).minus(vertices.get(i)));
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

    public double getArea() {
        return area;
    }

    private void setArea(List<Vector2> vertices) {
        this.area = calculateArea(vertices);
    }

    public static double calculateArea(List<Vector2> vertices) {
        // Calculate shape area
        double area = 0;
        int N = vertices.size();
        for (int i = 0; i < N; i++) {
            area = area + vertices.get(i).getX() * vertices.get((i+1) % N).getY() - vertices.get((i+1) % N).getX() * vertices.get(i).getY();
        }

        return area / 2;
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
            current = crossProduct(vertices.get(i), vertices.get((i+1) % N), vertices.get((i+2) % N));
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

    protected static double calculateInertiaMoment(double mass, List<Vector2> vertices) {
        // Useful things
        double area = calculateArea(vertices);
        Vector2 com = calculateCOM(vertices);

        // Calculate polar moment of inertia around origin
        double j = 0;
        int N = vertices.size();
        for (int i = 0; i < N; i++) {
            j += (vertices.get(i).getX() * vertices.get((i + 1) % N).getY() - vertices.get((i + 1) % N).getX() * vertices.get(i).getY()) * (vertices.get(i).getX() * vertices.get(i).getX() + vertices.get(i).getX() * vertices.get((i + 1) % N).getX() + vertices.get((i + 1) % N).getX() * vertices.get((i + 1) % N).getX() + vertices.get(i).getY() * vertices.get(i).getY() + vertices.get(i).getY() * vertices.get((i + 1) % N).getY() + vertices.get((i + 1) % N).getY() * vertices.get((i + 1) % N).getY());
        }
        j /= 12;

        // Shift to COM using parallel axis theorem
        j -= area*(com.getX()*com.getX() + com.getY() * com.getY());

        // Return the normal moment of inertia (I=J*rho)
        return j * mass/area;
    }

    public static Vector2 calculateCOM(List<Vector2> vertices) {
        int N = vertices.size();

        // Calculate centroid coordinates
        double xCOM = 0;
        double yCOM = 0;
        for (int i = 0; i < N; i++) { // Shoelace formula
            xCOM = xCOM + (vertices.get(i).getX() + vertices.get((i+1) % N).getX()) * (vertices.get(i).getX() * vertices.get((i+1) % N).getY() - vertices.get((i+1) % N).getX() * vertices.get(i).getY());
            yCOM = yCOM + (vertices.get(i).getY() + vertices.get((i+1) % N).getY()) * (vertices.get(i).getX() * vertices.get((i+1) % N).getY() - vertices.get((i+1) % N).getX() * vertices.get(i).getY());
        }
        xCOM /= 6*calculateArea(vertices);
        yCOM /= 6*calculateArea(vertices);

        return new Vector2(xCOM,yCOM);
    }

    @Override
    protected Vector2 getCOM(Vector2 originPoint) {
        return originPoint.plus(getInternalVertices().getFirst().times(-1));
    }

    @Override
    public void updateAABB() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Vector2 v : getInternalVertices()) {
            Vector2 vReal = getPosition().plus(v.rotate(getParentBody().getAngle()));

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
    public void updateInternally() {
        if (!worldVertices.isEmpty()) {
            for (int i = 0; i < getInternalVertices().size(); i++) {
                worldVertices.set(i, getPosition().plus(getInternalVertices().get(i).rotate(getParentBody().getAngle())));
            }
        } else { // First initiation of worldVertices
            for (int i = 0; i < getInternalVertices().size(); i++) {
                worldVertices.add(getPosition().plus(getInternalVertices().get(i).rotate(getParentBody().getAngle())));
            }
        }
    }

    @Override
    public List<Vector2> getSATAxes(Fixture other) {
        // For a polygon, the SAT axes to check are the edge normals
        List<Vector2> output = new ArrayList<>();

        for (Vector2 v : getInternalEdges()) {
            output.add(v.rotate(getParentBody().getAngle()).normal());
        }

        return output;
    }

    @Override
    public Vector2 getClosestReferenceTo(Vector2 position) {
        double min = Double.POSITIVE_INFINITY;
        Vector2 minVec = null;

        for (Vector2 v : worldVertices) {
            if (v.distance(position) < min) {
                min = v.distance(position);
                minVec = v;
            }
        }

        return minVec;
    }

    @Override
    public Double[] getProjectionEdges(Vector2 projectionAxis) {
        Vector2 normalizedProjectionAxis = projectionAxis.normalized();

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (Vector2 v : worldVertices) {
            double p = v.dot(normalizedProjectionAxis);

            min = Math.min(min, p);
            max = Math.max(max, p);
        }

        return new Double[]{min, max};
    }

    @Override
    public void render(PhysicsLoop physicsLoop) {
        List<Vector2> screenVertices = new ArrayList<>();

        for (Vector2 worldVertex : worldVertices) {
            screenVertices.add(physicsLoop.worldToScreenCoords(worldVertex));
        }

        physicsLoop.drawPolygon(screenVertices.toArray(new Vector2[0]));
    }

    public Edge findBestEdge(Vector2 outwardNormal) {
        int N = getInternalVertices().size();

        Edge bestEdge = null;
        double bestAlign = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < N; i++) {
            Vector2 edgeVector = worldVertices.get((i+1)%N).minus(worldVertices.get(i));
            Vector2 outwardVector = edgeVector.rotate(-Math.PI/2).normalized();
            if (outwardVector.dot(outwardNormal) > bestAlign) {
                bestEdge = new Edge(worldVertices.get(i), worldVertices.get((i+1)%N), outwardVector);
                bestAlign = outwardVector.dot(outwardNormal);
            }
        }

        return bestEdge;
    }
}
