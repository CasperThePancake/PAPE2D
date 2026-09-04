package PAPE2D;

import java.util.HashSet;
import java.util.Set;

/**
 * Island class to hold structurally connected groups of bodies with the possibility of sleeping
 */
public class Island {
    // =================================================================================
    // Attributes
    // =================================================================================

    private Set<Body> bodies = new HashSet<>();
    private boolean sleeping = false;
    private boolean destroyed = false;
    private double sleepTimer = 0;

    private static final double thresholdSleepTimer = World.SETTING_SLEEP_TIMER;
    private static final double thresholdMotion = World.SETTING_SLEEP_MOTION_THRESHOLD;

    // =================================================================================
    // Bodies
    // =================================================================================

    void addBody(Body b) {
        bodies.add(b);
    }

    // =================================================================================
    // Sleeping and waking
    // =================================================================================
    void update(double dt) {
        if (sleeping) {
            return; // Already sleeping: no need for checks, can only be woken up by external body triggers
        }

        boolean allLowMotion = true;

        for (Body b : bodies) {
            double bodyMotion = b.getPosition().distance(b.getTempPosition());

            if (bodyMotion > thresholdMotion) {
                allLowMotion = false;
            }
        }

        if (allLowMotion) { // Every body is barely moving
            sleepTimer += dt;
            if (sleepTimer >= thresholdSleepTimer) { // Bodies have been at rest for long enough, time for sleep
                for (Body b : bodies) {
                    b.setSleeping(true);
                }

                sleeping = true;
            }
        } else { // There is still movement
            wake();
        }
    }

    void wake() {
        for (Body b : bodies) {
            destroyed = true;
            b.setSleeping(false);
            b.setIsland(null);
        }

        // The garbage collector will do the rest...
    }

    boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Get the size of this island
     *
     * @return Number of bodies in this island
     */
    public int getSize() {
        return bodies.size();
    }
}
