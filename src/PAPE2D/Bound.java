package PAPE2D;

/**
 * Helper enum to differentiate min and max edges (used between Body and SweepAndPrune)
 */
public enum Bound {
    /**
     * Minimum boundary (lowest on axis)
     */
    MIN,
    /**
     * Maximum boundary (highest on axis)
     */
    MAX
}
