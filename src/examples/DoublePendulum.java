import PAPE2D.*;
import PAPE2D.bodies.Circle;
import PAPE2D.bodies.Rectangle;
import PAPE2D.bodies.Square;
import PAPE2D.constraint.DistanceConstraint;
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

    // Pendulum parts
    double m1 = 1;
    double m2 = 1;
    double l1 = 50;
    double l2 = 50;

    Body topHinge = new Square(new Vector2(0,0),10,1);
    topHinge.addFlag(Flag.FROZEN);
    Body mass1 = new Circle(new Vector2(5,-5-l1),5,m1,new Vector2(50,0));
    Body mass2 = new Circle(new Vector2(5,-5-l1-l2),10,m2,new Vector2(350,0));

    world.addBody(topHinge);
    world.addBody(mass1);
    world.addBody(mass2);

    world.addStaticConstraint(new DistanceConstraint(mass1,topHinge));
    world.addStaticConstraint(new DistanceConstraint(mass1,mass2));

    // Forces and camera movement
    world.addUniversalForce(new Gravity(200));
    loop.setCamX(5);
    loop.setCamY(-20);
    loop.setCamZoom(2);

    // Run the simulation
    loop.start();
}