package PAPE2D.narrowphase;

import PAPE2D.Body;
import PAPE2D.Flag;
import PAPE2D.bodies.Circle;
import PAPE2D.bodies.Polygon;
import PAPE2D.NarrowPhaseCollisionSystem;
import PAPE2D.helper.ContactManifold;
import PAPE2D.helper.Edge;
import PAPE2D.helper.PotentialCollidingPair;
import PAPE2D.helper.Vector2;

import java.util.ArrayList;
import java.util.List;

public class SAT extends NarrowPhaseCollisionSystem {
    // =================================================================================
    // getContactManifolds
    // =================================================================================
    @Override
    public List<ContactManifold> getContactManifolds(PotentialCollidingPair potentialCollidingPair) {

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
                return new ArrayList<>();
            }
            // Overlap: store amount
            overlapAmounts.add(Math.min(body1ProjectionEdges[1],body2ProjectionEdges[1]) - Math.max(body1ProjectionEdges[0],body2ProjectionEdges[0]));
        }

        // 3. Determine normal and tangential
        // Find the axis that had the least overlap, this defines the normal vector and perpendicular tangential vector
        int bestAxisIndex = -1;
        double minOverlap = Double.POSITIVE_INFINITY;
        Vector2 normal = null;
        for (int i = 0; i < SATAxes.size(); i++) {
            if (overlapAmounts.get(i) < minOverlap) {
                minOverlap = overlapAmounts.get(i);
                normal = SATAxes.get(i).normalized();
                bestAxisIndex = i;
            }
        }

        Vector2 tangent = normal.normal();

        // 4. Determine reference body and incident body (reference body owns the axis that had least overlap)
        Body referenceBody;
        Body incidentBody;

        // If the index is less than the number of axes Body 1 provided, Body 1 is the reference
        if (bestAxisIndex < potentialCollidingPair.getBody1().getSATAxes(potentialCollidingPair.getBody2()).size()) {
            referenceBody = potentialCollidingPair.getBody1();
            incidentBody = potentialCollidingPair.getBody2();
        } else {
            referenceBody = potentialCollidingPair.getBody2();
            incidentBody = potentialCollidingPair.getBody1();
        }

        // Make sure the normal vector points from reference to incident (for later usage)
        Double[] refProjection = referenceBody.getProjectionEdges(normal);
        Double[] incProjection = incidentBody.getProjectionEdges(normal);

        // Calculate the midpoints of these projections
        double refCenterAlongAxis = (refProjection[0] + refProjection[1]) / 2.0;
        double incCenterAlongAxis = (incProjection[0] + incProjection[1]) / 2.0;

        // If the incident body's center is behind the reference body's center on this axis,
        // it means our normal is pointing backwards. Flip it!
        if (incCenterAlongAxis < refCenterAlongAxis) {
            normal.multiply(-1);
        }

        // 5. Determine contact points and penetration depths, this is type-based!
        List<Vector2> contactPoints = new ArrayList<>();
        List<Double> penetrationDepths = new ArrayList<>();

        if (referenceBody instanceof Circle && incidentBody instanceof Polygon) { // Reference is circle, incident is polygon: contact point is the closest vertex of polygon to circle center
            contactPoints.add(incidentBody.getClosestReferenceTo(referenceBody.getPosition()));
            penetrationDepths.add( ((Circle) referenceBody).getRadius() - referenceBody.getPosition().minus(contactPoints.getLast()).size());
        } else if (incidentBody instanceof Circle) { // Incident is circle (reference can be both): contact point is point on circle perimeter in normal direction, inside other body
            // Type casting my beloved?
            contactPoints.add(incidentBody.getPosition().minus(normal.times(((Circle) incidentBody).getRadius())));
            penetrationDepths.add(minOverlap); // Penetration depth is obvious here
        } else if (referenceBody instanceof Polygon && incidentBody instanceof Polygon) {
            // 1. Find the edges facing each other
            Edge referenceEdge = ((Polygon) referenceBody).findBestEdge(normal);
            // Incident edge faces the opposite direction of the reference outward normal
            Edge incidentEdge = ((Polygon) incidentBody).findBestEdge(normal.times(-1));

            Vector2 reference1 = referenceEdge.getV1();
            Vector2 reference2 = referenceEdge.getV2();

            Vector2 candidate1 = incidentEdge.getV1();
            Vector2 candidate2 = incidentEdge.getV2();

            // Pass 1: Clip against Reference 2 side plane
            boolean c1Out = reference1.minus(reference2).dot(candidate1.minus(reference2)) < 0;
            boolean c2Out = reference1.minus(reference2).dot(candidate2.minus(reference2)) < 0;

            if (c1Out && c2Out) return new ArrayList<>();
            if (c1Out) candidate1 = intersectSnap(candidate2, candidate1, reference2, reference1.minus(reference2));
            if (c2Out) candidate2 = intersectSnap(candidate1, candidate2, reference2, reference1.minus(reference2));

            // Pass 2: Clip against Reference 1 side plane
            boolean c1Out2 = reference2.minus(reference1).dot(candidate1.minus(reference1)) < 0;
            boolean c2Out2 = reference2.minus(reference1).dot(candidate2.minus(reference1)) < 0;

            if (c1Out2 && c2Out2) return new ArrayList<>();
            if (c1Out2) candidate1 = intersectSnap(candidate2, candidate1, reference1, reference2.minus(reference1));
            if (c2Out2) candidate2 = intersectSnap(candidate1, candidate2, reference1, reference2.minus(reference1));

            // Pass 3: Only keep points that are behind the reference edge plane (penetrating)
            // Project the point relative to the reference edge onto the outward normal
            // Use 'normal' because it is mathematically guaranteed to point towards the incident body
            double separation1 = candidate1.minus(reference1).dot(normal);
            double separation2 = candidate2.minus(reference1).dot(normal);

            // Negative separation means the point is underneath/inside the edge surface, so we count it!
            double contactPaddingSkin = 0.1; // Allow some numerical leeway

            if (separation1 <= contactPaddingSkin) {
                contactPoints.add(candidate1);
                penetrationDepths.add(-separation1); // Turn negative separation into positive depth
            }
            if (separation2 <= contactPaddingSkin) {
                contactPoints.add(candidate2);
                penetrationDepths.add(-separation2);
            }

            if (incidentBody.hasFlag(Flag.DEBUG) || referenceBody.hasFlag(Flag.DEBUG)) {
                IO.println("Amount of contact points: "+contactPoints.size());
            }
        }

        // 7. Put it all into contact manifold and return
        List<ContactManifold> contactManifolds = new ArrayList<>();

        for (int i = 0; i < contactPoints.size(); i++) {
            contactManifolds.add(new ContactManifold(contactPoints.get(i), penetrationDepths.get(i), referenceBody, incidentBody, normal, tangent));
        } // Normal points reference > incident, so as contacts enforce n * (v2-v1) this should be from body 1 to body 2, meaning reference=body1, incident=body2

        return contactManifolds;
    }

    // =================================================================================
    // Intersect snapping
    // =================================================================================
    public Vector2 intersectSnap(Vector2 pointA, Vector2 pointB, Vector2 pointQ, Vector2 vecSide) {
        double c = (pointQ.minus(pointA).dot(vecSide)) / (pointB.minus(pointA).dot(vecSide));
        return pointB.times(c).plus(pointA.times(1-c));
    }
}
