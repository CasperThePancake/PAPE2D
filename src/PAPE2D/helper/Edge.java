package PAPE2D.helper;

public class Edge {
    // =================================================================================
    // Attributes
    // =================================================================================
    private final Vector2 v1;
    private final Vector2 v2;
    private final Vector2 outwardNormal;

    // =================================================================================
    // Getters
    // =================================================================================

    /**
     * Get the first point of this edge
     *
     * @return First point of this edge
     */
    public Vector2 getV1() {
        return v1;
    }

    /**
     * Get the second point of this edge
     *
     * @return Second point of this edge
     */
    public Vector2 getV2() {
        return v2;
    }

    /**
     * Get the outward normal of this edge
     *
     * @return Outward normal of this edge
     */
    public Vector2 getOutwardNormal() {
        return outwardNormal;
    }

    // =================================================================================
    // Constructor
    // =================================================================================

    /**
     * Create a new edge with given endpoints and outward normal
     *
     * @param v1 Given first endpoint
     * @param v2 Given second endpoint
     * @param outwardNormal Given outward normal
     */
    public Edge(Vector2 v1, Vector2 v2, Vector2 outwardNormal) {
        this.v1 = v1;
        this.v2 = v2;
        this.outwardNormal = outwardNormal;
    }

    // =================================================================================
    // Other methods
    // =================================================================================

    /**
     * Calculate the distance between the given edge and a given point in space
     *
     * @param edge Given edge
     * @param point Given point
     * @return Shortest (perpendicular) distance between the given edge and the given point
     */
    public static double distance(Edge edge, Vector2 point) {
        Vector2 lineNormal = edge.outwardNormal;
        return Math.abs(edge.v1.minus(point).dot(lineNormal));
    }
}
