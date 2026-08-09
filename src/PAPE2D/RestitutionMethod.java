package PAPE2D;

/**
 * Simple enum for options in how a world handles restitution calculation
 */
public enum RestitutionMethod {
    /**
     * Restitution between two bodies is the product of their restitutions
     */
    PRODUCT,
    /**
     * Restitution between two bodies is the minimum of their restitutions
     */
    MIN,
    /**
     * Restitution between two bodies is the maximum of their restitutions
     */
    MAX;

    RestitutionMethod() {
    }

    /**
     * Calculate the combined restitution from two given restitutions, using the selected method
     *
     * @param r1 Given first restitution
     * @param r2 Given second restitution
     *
     * @return Combined restitution
     */
    public double calculateRestitution(double r1, double r2) {
        if (this.equals(PRODUCT)) {
            return r1 * r2;
        } else if (this.equals(MIN)) {
            return Math.min(r1,r2);
        } else if (this.equals(MAX)) {
            return Math.max(r1,r2);
        }

        return Body.DEFAULT_RESTITUTION; // Default case fall-back
    }
}
