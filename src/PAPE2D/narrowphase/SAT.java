package PAPE2D.narrowphase;

import PAPE2D.NarrowPhaseCollisionSystem;
import PAPE2D.helper.ContactManifold;
import PAPE2D.helper.PotentialCollidingPair;
import PAPE2D.helper.Vector2;

import java.util.ArrayList;
import java.util.List;

public class SAT extends NarrowPhaseCollisionSystem {
    @Override
    public ContactManifold getContactManifold(PotentialCollidingPair potentialCollidingPair) {
        // 1. Get list of axes to check from Bodies
        List<Vector2> SATAxes = potentialCollidingPair.getSATAxes();
        List<Double> overlapAmounts = new ArrayList<>();

        // 2. For each axis, perform the proper projection, check if seperation (=> not colliding, return null), if done they're colliding and continue
        for (Vector2 axis : SATAxes) {
            // Let the Body calculate the outer points of its projection, represented as directed length from (0,0) (dot product between (cos,sin) and outer points)
            Double[] body1ProjectionEdges = potentialCollidingPair.getBody1().getProjectionEdges(axis); // Assumed (min,max) sorted!
            Double[] body2ProjectionEdges = potentialCollidingPair.getBody2().getProjectionEdges(axis);
            if (body1ProjectionEdges[1] < body2ProjectionEdges[0] || body2ProjectionEdges[1] < body1ProjectionEdges[0]) {
                // No overlap!
                return null;
            }
            // Overlap: store amount
            overlapAmounts.add(Math.min(body1ProjectionEdges[1],body2ProjectionEdges[1]) - Math.max(body1ProjectionEdges[0],body2ProjectionEdges[0]));
        }

        // 3. Determine normal and tangential
        // Find the axis that had the least overlap, this defines the normal vector and perpendicular tangential vector
        double minOverlap = Double.POSITIVE_INFINITY;
        Vector2 normal = null;
        for (int i = 0; i < SATAxes.size(); i++) {
            if (overlapAmounts.get(i) < minOverlap) {
                minOverlap = overlapAmounts.get(i);
                normal = SATAxes.get(i).normalized();
            }
        }

        Vector2 tangent = normal.normal();

        // 4. Determine reference edge

        // 5. Determine incident edge

        // 6. Determine and filter contact point(s)

        // 7. Determine penetration depth

        // 8. Determine relative position vectors for both bodies

        // 9. Put it all into a contact manifold and return
        return null;
    }
}
