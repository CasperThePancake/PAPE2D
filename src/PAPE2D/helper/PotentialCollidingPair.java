package PAPE2D.helper;

import PAPE2D.Body;

public class PotentialCollidingPair {
    private Body body1;
    private Body body2;

    public Body getBody1() {
        return body1;
    }

    public Body getBody2() {
        return body2;
    }

    private void setBody1(Body body1) {
        this.body1 = body1;
    }

    private void setBody2(Body body2) {
        this.body2 = body2;
    }

    public PotentialCollidingPair(Body body1, Body body2) {
        this.setBody1(body1);
        this.setBody2(body2);
    }
}
