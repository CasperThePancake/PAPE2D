package PAPE2D.helper;

public class Edge {
    private final Vector2 v1;
    private final Vector2 v2;
    private final Vector2 outwardNormal;

    public Vector2 getV1() {
        return v1;
    }

    public Vector2 getV2() {
        return v2;
    }

    public Vector2 getOutwardNormal() {
        return outwardNormal;
    }

    public Edge(Vector2 v1, Vector2 v2, Vector2 outwardNormal) {
        this.v1 = v1;
        this.v2 = v2;
        this.outwardNormal = outwardNormal;
    }

    public static double distance(Edge edge, Vector2 point) {
        Vector2 lineNormal = edge.outwardNormal;
        return Math.abs(edge.v1.minus(point).dot(lineNormal));
    }
}
