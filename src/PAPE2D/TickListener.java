package PAPE2D;

/**
 * Interface for a listener that, when added to a World, will run before/after each tick update of that world when running. Useful for parsing user input, writing simulation logic or creating games.
 */
public interface TickListener {
    /**
     * Called every single tick
     *
     * @param tickedWorld World associated with this tick
     * @param dt Time elapsed since last tick
     * @param tickedPhysicsLoop Physics loop associated with this tick
     */
    void onTick(World tickedWorld, PhysicsLoop tickedPhysicsLoop, double dt);
}
