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

    // Construction
    Body square = new Square(new Vector2(0,0),25,1,new Vector2(100,0),0,20);
    world.addBody(square);

    // Forces and camera movement
    world.addUniversalForce(new AirResistance(0.6));

    // Run the simulation
    loop.start();
}