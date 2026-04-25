package PAPE2D.helper;

import PAPE2D.Body;

import java.util.ArrayList;
import java.util.List;

public class PotentialCollidingPair {
    // =================================================================================
    // Attributes
    // =================================================================================
    private Body body1;
    private Body body2;

    // =================================================================================
    // Bodies
    // =================================================================================

    /**
     * Get the first body of this potential colliding pair
     *
     * @return First body
     */
    public Body getBody1() {
        return body1;
    }

    /**
     * Get the second body of this potential colliding pair
     *
     * @return Second body
     */
    public Body getBody2() {
        return body2;
    }

    /**
     * Set the first body of this potential colliding pair
     *
     * @param body1 Given first body
     */
    private void setBody1(Body body1) {
        this.body1 = body1;
    }

    /**
     * Set the second body of this potential colliding pair
     *
     * @param body2 Given second body
     */
    private void setBody2(Body body2) {
        this.body2 = body2;
    }

    // =================================================================================
    // Constructor
    // =================================================================================

    /**
     * Create a new potential colliding pair with given bodies
     *
     * @param body1 Given first body
     * @param body2 Given second body
     */
    public PotentialCollidingPair(Body body1, Body body2) {
        this.setBody1(body1);
        this.setBody2(body2);
    }

    // =================================================================================
    // Getting list of combined SAT axes
    // =================================================================================

    /**
     * Get the SAT axes associated with this potential colliding pair
     *
     * @return List containing every SAT axis for both bodies
     */
    public List<Vector2> getSATAxes() {
        List<Vector2> output = new ArrayList<>(getBody1().getSATAxes(getBody2()));
        output.addAll(getBody2().getSATAxes(getBody1()));
        return output;
    }
}
