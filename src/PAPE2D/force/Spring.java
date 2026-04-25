package PAPE2D.force;

import PAPE2D.Body;
import PAPE2D.LocalForce;
import PAPE2D.UniversalForce;
import PAPE2D.helper.Vector2;

public class Spring extends LocalForce {
    // =================================================================================
    // Attributes
    // =================================================================================
    private Body body1;
    private Body body2;
    private double stiffness;
    private double defaultLength;

    // =================================================================================
    // Bodies
    // =================================================================================

    /**
     * Set the first body connected by this spring
     *
     * @param body1 Given first body
     */
    private void setBody1(Body body1) {
        this.body1 = body1;
    }

    /**
     * Set the second body connected by this spring
     *
     * @param body2 Given second body
     */
    private void setBody2(Body body2) {
        this.body2 = body2;
    }

    /**
     * Get the first body connected by this spring
     *
     * @return First body connected by this spring
     */
    public Body getBody1() {
        return body1;
    }

    /**
     * Get the second body connected by this spring
     *
     * @return Second body connected by this spring
     */
    public Body getBody2() {
        return body2;
    }

    // =================================================================================
    // Stiffness
    // =================================================================================

    /**
     * Get the stiffness of this spring
     *
     * @return Stiffness of this spring
     */
    public double getStiffness() {
        return stiffness;
    }

    /**
     * Set the stiffness of this spring
     *
     * @param stiffness Given stiffness value
     */
    private void setStiffness(double stiffness) {
        this.stiffness = stiffness;
    }

    // =================================================================================
    // Default length
    // =================================================================================

    /**
     * Get the default length of this spring
     *
     * @return Default length of this spring
     */
    public double getDefaultLength() {
        return defaultLength;
    }

    /**
     * Set the default length of this spring
     *
     * @param defaultLength Given default length
     */
    private void setDefaultLength(double defaultLength) {
        this.defaultLength = defaultLength;
    }

    // =================================================================================
    // Attributes
    // =================================================================================

    /**
     * Create a new spring force between two bodies with given stiffness and given default length
     *
     * @param body1 Given first body
     * @param body2 Given second body
     * @param stiffness Given stiffness value
     */
    public Spring(Body body1, Body body2, double stiffness, double defaultLength) {
        setBody1(body1);
        setBody2(body2);
        setStiffness(stiffness);
        setDefaultLength(defaultLength);
    }

    /**
     * Create a new spring force between two bodies with given stiffness and default length equal to their current distance
     *
     * @param body1 Given first body
     * @param body2 Given second body
     * @param stiffness Given stiffness value
     */
    public Spring(Body body1, Body body2, double stiffness) {
        this(body1,body2,stiffness,body1.getPosition().minus(body2.getPosition()).size());
    }

    @Override
    public void applyAcceleration(double dt) {
        Vector2 from1to2 = getBody2().getPosition().minus(getBody1().getPosition());
        double distance = from1to2.size();

        body1.addVelocity(from1to2.times(getStiffness() * (distance - getDefaultLength()) / body1.getMass() * dt));
        body2.addVelocity(from1to2.times(-getStiffness() * (distance - getDefaultLength()) / body2.getMass() * dt)); // I love Newton's third law!
    }
}
