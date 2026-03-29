package PAPE2D;

import PAPE2D.helper.Vector2;

/**
 * Abstract rigid body class
 */
public abstract class Body {
    // =================================================================================
    // Attributes
    // =================================================================================
    private Vector2 position;
    private Vector2 velocity;
    private double AABBminX, AABBmaxX, AABBminY, AABBmaxY;

    // =================================================================================
    // Constructors
    // =================================================================================
    public Body(Vector2 position, Vector2 velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    // =================================================================================
    // Position & velocity
    // =================================================================================
    public void setPosition(Vector2 newPosition) {
        this.position = newPosition;
    }

    public void setVelocity(Vector2 newVelocity) {
        this.velocity = newVelocity;
    }

    // =================================================================================
    // AABB & edges
    // =================================================================================

    public abstract void updateAABB();

    public double getEdgeValue(Axis axis, Bound bound) {
        if (axis == Axis.X) {
            return bound == Bound.MIN ? AABBminX : AABBmaxX;
        } else {
            return bound == Bound.MIN ? AABBminY : AABBmaxY;
        }
    }

    public boolean AABBOverlaps(Body other) {
        return (this.getEdgeValue(Axis.X,Bound.MIN) <= other.getEdgeValue(Axis.X,Bound.MAX))
                && (this.getEdgeValue(Axis.X,Bound.MAX) >= other.getEdgeValue(Axis.X,Bound.MIN))
                && (this.getEdgeValue(Axis.Y,Bound.MIN) <= other.getEdgeValue(Axis.Y,Bound.MAX))
                && (this.getEdgeValue(Axis.Y,Bound.MAX) >= other.getEdgeValue(Axis.Y,Bound.MIN));
    }
}
