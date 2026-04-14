package PAPE2D.helper;

import PAPE2D.Body;

public class ContactManifold {
    private final Vector2 contactPoint;
    private final double penetrationDepth;
    private final Body body1;
    private final Body body2;
    private final Vector2 contactPointRelativeBody1;
    private final Vector2 contactPointRelativeBody2;

    public ContactManifold(Vector2 contactPoint, double penetrationDepth, Body body1, Body body2) {
        this.contactPoint = contactPoint;
        this.penetrationDepth = penetrationDepth;
        this.body1 = body1;
        this.body2 = body2;
        this.contactPointRelativeBody1 = body1.getRelativePosition(contactPoint);
        this.contactPointRelativeBody2 = body2.getRelativePosition(contactPoint);
    }

    public Vector2 getContactPoint() {
        return contactPoint;
    }

    public double getPenetrationDepth() {
        return penetrationDepth;
    }

    public Body getBody1() {
        return body1;
    }

    public Body getBody2() {
        return body2;
    }

    public Vector2 getContactPointRelativeBody1() {
        return contactPointRelativeBody1;
    }

    public Vector2 getContactPointRelativeBody2() {
        return contactPointRelativeBody2;
    }
}
