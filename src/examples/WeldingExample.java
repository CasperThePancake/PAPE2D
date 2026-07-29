package examples;

import PAPE2D.*;
import PAPE2D.bodies.Rectangle;
import PAPE2D.bodies.Square;
import PAPE2D.constraint.AngularConstraint;
import PAPE2D.constraint.DistanceConstraint;
import PAPE2D.fixtures.Polygon;
import PAPE2D.force.Gravity;
import PAPE2D.helper.CollisionExclusion;
import PAPE2D.helper.Vector2;
import PAPE2D.ticklisteners.CameraMovement;

import java.util.List;

/**
 * Example of multiple fixtures welded together to form a body
 */
public class WeldingExample {
    public static void main(String[] args) {
        // Initiate world and physics loop
        World world = new World();
        PhysicsLoop loop = new PhysicsLoop(world, 200);

        // Form the shape (plus symbol)
        Fixture topSquare = new Polygon(1, List.of(new Vector2(0,0), new Vector2(50,0), new Vector2(50,-50), new Vector2(0,-50)));
        Fixture bottomSquare = new Polygon(1, List.of(new Vector2(0,0), new Vector2(50,0), new Vector2(50,-50), new Vector2(0,-50)));
        Fixture middleRectangle = new Polygon(1, List.of(new Vector2(0,0), new Vector2(150,0), new Vector2(150,-50), new Vector2(0,-50)));

        Body myBody = new Body(new Vector2(0,100),List.of(topSquare,bottomSquare,middleRectangle),List.of(new Vector2(0,50), new Vector2(0,-50), new Vector2(-50,0)),new Vector2(50,-50),0.1,9);

        // Floor
        Body myFloor = new Rectangle(new Vector2(-500,-200),1000,100,1);
        myFloor.addFlag(Flag.FROZEN);
        world.addBody(myFloor);

        world.addBody(myBody);

        world.addUniversalForce(new Gravity(400));

        world.addPreUpdateTickListener(new CameraMovement(300));

        // Run the simulation
        loop.start();
    }
}