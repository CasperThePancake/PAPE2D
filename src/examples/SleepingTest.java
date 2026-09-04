import PAPE2D.*;
import PAPE2D.bodies.Circle;
import PAPE2D.bodies.Rectangle;
import PAPE2D.bodies.Square;
import PAPE2D.constraint.DistanceConstraint;
import PAPE2D.force.AirResistance;
import PAPE2D.force.Gravity;
import PAPE2D.helper.Vector2;
import PAPE2D.ticklisteners.CameraMovement;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Example usage of PAPE2D
 */
void main() {
    // Initiate world and physics loop
    World world = new World();
    PhysicsLoop loop = new PhysicsLoop(world, 200);

    // Ground
    Body floor = new Rectangle(new Vector2(-100, 0), 200, 20, 1);
    floor.addFlag(Flag.FROZEN);

    // Leave tiny gaps during spawning to prevent immediate contact causing numerical errors (optional)

    // Block 1 (Bottom) - resting just above floor (Y = 5.05)
    Body box1 = new Rectangle(new Vector2(0, 20), 20, 10, 2);

    // Block 2 (Middle) - resting just above box1 (Y = 15.10)
    Body box2 = new Rectangle(new Vector2(0, 40), 20, 10, 2);

    // Block 3 (Top) - resting just above box2 (Y = 25.15)
    Body box3 = new Rectangle(new Vector2(0, 60), 20, 10, 2);

    box1.addFlag(Flag.DEBUG);
    box2.addFlag(Flag.DEBUG);
    box3.addFlag(Flag.DEBUG);

    world.addBody(floor,box1,box2,box3);
    world.addUniversalForce(new Gravity(300));
    world.addPreUpdateTickListener(new CameraMovement(100));

    // Run the simulation
    loop.start();
}