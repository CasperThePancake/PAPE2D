import PAPE2D.Body;
import PAPE2D.PhysicsLoop;
import PAPE2D.World;
import PAPE2D.bodies.Circle;
import PAPE2D.bodies.Polygon;
import PAPE2D.bodies.Rectangle;
import PAPE2D.bodies.Square;
import PAPE2D.constraint.DistanceConstraint;
import PAPE2D.force.AirResistance;
import PAPE2D.force.Gravity;
import PAPE2D.force.Spring;
import PAPE2D.helper.Vector2;
import PAPE2D.ticklisteners.CameraMovement;

import java.util.List;

/**
 * Example usage of PAPE2D
 *
 * @note Currently being used to perform loads of tests!
 */
public class Main {
    public static void main(String[] args) {
        // Initiate world and physics loop
        World world = new World();
        PhysicsLoop loop = new PhysicsLoop(world, 200);

        Body myBody1 = new Circle(25,new Vector2(0,25), new Vector2(350,0),2);

        // Floor
        Body myFloor1 = new Rectangle(new Vector2(-500,0),1000,50,1);
        Body myFloor2 = new Rectangle(new Vector2(-500,500),1000,50,1);
        Body myWall1 = new Rectangle(new Vector2(500,500),50,500,1);
        Body myWall2 = new Rectangle(new Vector2(-550,500),50,500,1);
        myFloor1.setFrozen(true);
        myFloor2.setFrozen(true);
        myWall1.setFrozen(true);
        myWall2.setFrozen(true);

        world.addBody(myBody1);
        world.addBody(myFloor1);
        world.addBody(myFloor2);
        world.addBody(myWall1);
        world.addBody(myWall2);
        world.addUniversalForce(new Gravity(50));
//        world.addStaticConstraint(new DistanceConstraint(myBody1,myBody2,new Vector2(20,20),new Vector2(20,20),200));
        world.addPreUpdateTickListener(new CameraMovement(300));

//        // Adding a listener directly via a lambda expression
//        world.addPreUpdateTickListener((tickedWorld, tickedPhysicsLoop, dt) -> {
//            // Rotate
//            myPolygon.setAngle(myPolygon.getAngle()+0.01);
//        });

        // Run the simulation
        loop.start();
    }
}