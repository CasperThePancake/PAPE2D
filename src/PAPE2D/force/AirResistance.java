package PAPE2D.force;

import PAPE2D.Body;
import PAPE2D.UniversalForce;

/**
 * Simple velocity-based air resistance force class
 */
public class AirResistance extends UniversalForce {
    // =================================================================================
    // Attributes
    // =================================================================================
    private double resistance;

    // =================================================================================
    // Resistance
    // =================================================================================
    /**
     * Get the resistance value of this force
     *
     * @return Air resistance value of this force
     */
    public double getResistance() {
        return resistance;
    }

    /**
     * Set the resistance value of this force
     *
     * @param resistance Given resistance value
     */
    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    // =================================================================================
    // Constructor
    // =================================================================================
    /**
     * Create a new air resistance force with given resistance factor
     *
     * @param resistance Given resistance factor
     */
    public AirResistance(double resistance) {
        this.setResistance(resistance);
    }

    // =================================================================================
    // Apply acceleration
    // =================================================================================
    @Override
    public void applyAcceleration(double dt) {
        for (Body b : getBodies()) {
            b.addVelocity(b.getVelocity().times(-resistance*dt/b.getMass()));
        }
    }
}
