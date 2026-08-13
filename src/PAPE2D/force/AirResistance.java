package PAPE2D.force;

import PAPE2D.Body;
import PAPE2D.Flag;
import PAPE2D.UniversalForce;

/**
 * Simple velocity-based air resistanceTranslation force class
 */
public class AirResistance extends UniversalForce {
    // =================================================================================
    // Attributes
    // =================================================================================
    private double resistanceTranslation;
    private double resistanceRotation;

    // =================================================================================
    // Resistance
    // =================================================================================
    /**
     * Get the translational air resistance value of this force
     *
     * @return Translational air resistance value of this force
     */
    public double getResistanceTranslation() {
        return resistanceTranslation;
    }

    /**
     * Set the translational air resistance value of this force
     *
     * @param resistanceTranslation Given translational air resistance value
     */
    public void setResistanceTranslation(double resistanceTranslation) {
        this.resistanceTranslation = resistanceTranslation;
    }

    /**
     * Get the rotational air resistance value of this force
     *
     * @return Rotational air resistance value of this force
     */
    public double getResistanceRotation() {
        return resistanceRotation;
    }

    /**
     * Set the rotational air resistance value of this force
     *
     * @param resistanceRotation Given rotational air resistance value
     */
    public void setResistanceRotation(double resistanceRotation) {
        this.resistanceRotation = resistanceRotation;
    }

    // =================================================================================
    // Constructor
    // =================================================================================
    /**
     * Create a new air resistance force with given resistance factor for both translation and rotation
     *
     * @param resistance Given resistance factor
     */
    public AirResistance(double resistance) {
        this.setResistanceTranslation(resistance);
        this.setResistanceRotation(resistance);
    }

    /**
     * Create a new air resistance force with given rotational and translational resistance factors
     *
     * @param resistanceTranslation Given translational air resistance factor
     * @param resistanceRotation Given rotational air resistance factor
     */
    public AirResistance(double resistanceTranslation, double resistanceRotation) {
        this.resistanceTranslation = resistanceTranslation;
        this.resistanceRotation = resistanceRotation;
    }

    // =================================================================================
    // Apply acceleration
    // =================================================================================
    @Override
    public void applyAcceleration(double dt) {
        for (Body b : getBodies()) {
            if (b.hasFlag(Flag.IGNORE_UNIVERSAL_FORCES)) {
                continue;
            }
            b.setVelocity(b.getVelocity().times(1/(1+resistanceTranslation/b.getMass() * dt)));
            b.setAngularVelocity(b.getAngularVelocity()*(1/(1+resistanceRotation/b.getMass() * dt)));
        }
    }
}
