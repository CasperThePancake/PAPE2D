package PAPE2D.broadphase;

import PAPE2D.*;
import PAPE2D.helper.PotentialCollidingPair;

import java.util.ArrayList;
import java.util.List;

public class SweepAndPrune extends BroadPhaseCollisionSystem {
    // =================================================================================
    // Nested classes
    // =================================================================================
    /**
     * Nested helper class that keeps track of a body's AABB edge
     */
    private static class Edge {
        private final Body owner;
        private double value;
        private final Bound bound;
        private final Axis axis;

        Edge(Body owner, double value, Bound bound, Axis axis) {
            this.owner = owner;
            this.setValue(value);
            this.bound = bound;
            this.axis = axis;
        }

        void setValue(double value) {
            this.value = value;
        }

        Body getOwner() {
            return owner;
        }

        double getValue() {
            return value;
        }

        Bound getBound() {
            return bound;
        }

        Axis getAxis() {
            return axis;
        }

        void updateValue() {

        }
    }

    // =================================================================================
    // Attributes
    // =================================================================================
    private List<Edge> xEdges = new ArrayList<>();
    private List<PotentialCollidingPair> pairBuffer = new ArrayList<>();
    private List<Body> scanBuffer = new ArrayList<>();

    // =================================================================================
    // Constructor
    // =================================================================================

    public SweepAndPrune() {
    }

    // =================================================================================
    // Body management
    // =================================================================================
    private void insertEdgeX(Edge newEdge) {
        int index = 0;
        while (index < getNbEdgesX() && xEdges.get(index).getValue() < newEdge.getValue()) {
            index++;
        }
        xEdges.add(index, newEdge);
    }

    private int getNbEdgesX() {
        return xEdges.size();
    }

    @Override
    public void addBody(Body b) {
        Edge xEdgeMin = new Edge(b, b.getEdgeValue(Axis.X,Bound.MIN), Bound.MIN, Axis.X);
        Edge xEdgeMax = new Edge(b, b.getEdgeValue(Axis.X,Bound.MAX), Bound.MAX, Axis.X);

        insertEdgeX(xEdgeMin);
        insertEdgeX(xEdgeMax);
    }

    @Override @Optimize
    public void removeBody(Body b) {
        xEdges.removeIf(e -> e.getOwner() == b);
    }

    @Override
    public void update() {
        // Update all edges
        for (Edge e : xEdges) {
            e.setValue(e.getOwner().getEdgeValue(e.getAxis(),e.getBound()));
        }

        // Insertion sort
        for (int i = 1; i < getNbEdgesX(); i++) {
            Edge tempEdge = xEdges.get(i);
            double tempVal = tempEdge.getValue();
            int j = i-1;
            while (j >= 0 && xEdges.get(j).getValue() > tempVal) {
                xEdges.set(j+1,xEdges.get(j));
                j--;
            }
            xEdges.set(j,tempEdge);
        }
    }

    // =================================================================================
    // Sweep and prune algorithm
    // =================================================================================
    @Override
    public List<PotentialCollidingPair> getPotentialCollidingPairs() {
        pairBuffer.clear();
        scanBuffer.clear();

        for (Edge e : xEdges) {
            if (e.getBound() == Bound.MIN) {
                for (Body o : scanBuffer) { // Everything else in scanBuffer is an overlap candidate
                    if (o.AABBOverlaps(e.getOwner())) { // Check full AABB overlap
                        pairBuffer.add(new PotentialCollidingPair(o,e.getOwner()));
                    }
                }
                scanBuffer.add(e.getOwner());
            } else {
                scanBuffer.remove(e.getOwner());
            }
        }

        return pairBuffer;
    }
}
