import PAPE2D.PhysicsLoop;
import PAPE2D.World;

/**
 * Example usage of PAPE2D
 */
public class Main {
    public static void main(String[] args) {
        // Initiate world and physics loop
        World world = new World();
        PhysicsLoop loop = new PhysicsLoop(world, 60);

        // (Add bodies, constraints, forces, ... here)

        // Add game or simulation logic using tick listeners
        world.addPreUpdateTickListener((tickedWorld, tickedLoop, dt) -> {
            // (Tick logic here)
        });

        // Run the simulation
        loop.start();
    }
}