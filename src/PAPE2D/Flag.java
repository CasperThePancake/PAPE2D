package PAPE2D;

/**
 * Enum for single-body flags, impacting their behavior when added
 */
public enum Flag {
    /**
     * Disable a body's translational and rotational movement
     */
    FROZEN,
    /**
     * Disable a body's translational movement
     */
    FROZEN_TRANSLATION,
    /**
     * Disable a body's rotational movement
     */
    FROZEN_ROTATION,
    /**
     * Disable all collisions with a body
     */
    NO_COLLISION,
    /**
     * Display debug information for a body
     */
    DEBUG,
    /**
     * Disable the effect of universal forces on a body
     */
    IGNORE_UNIVERSAL_FORCES,
    /**
     * Disable rendering for a body
     */
    HIDDEN,
    /**
     * Stop a body's sprite from rotating along with it, with the sprite's rotation only controlled by 'spriteRotate' attribute
     */
    SPRITE_FIXED_ANGLE;

    Flag() {
    }
}
