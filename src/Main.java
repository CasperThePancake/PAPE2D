import PAPE2D.Body;
import PAPE2D.Flag;
import PAPE2D.PhysicsLoop;
import PAPE2D.World;
import PAPE2D.bodies.Rectangle;
import PAPE2D.bodies.Square;
import PAPE2D.constraint.AngularConstraint;
import PAPE2D.constraint.DistanceConstraint;
import PAPE2D.force.Gravity;
import PAPE2D.helper.Vector2;
import PAPE2D.ticklisteners.CameraMovement;

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

        Body myBody1 = new Square(new Vector2(50,100),50,new Vector2(0,0),1,0,1);
        Body myBody2 = new Square(new Vector2(100,150),50,new Vector2(-50,-50),0,0,1);

        world.addBody(myBody1);
        world.addBody(myBody2);

        myBody1.addFlag(Flag.FROZEN_TRANSLATION);

        // Floor
        Body myFloor1 = new Rectangle(new Vector2(-500,0),1000,50,1);
        Body myFloor2 = new Rectangle(new Vector2(-500,500),1000,50,1);
        Body myWall1 = new Rectangle(new Vector2(500,500),50,500,1);
        Body myWall2 = new Rectangle(new Vector2(-550,500),50,500,1);
        myFloor1.addFlag(Flag.FROZEN);
        myFloor2.addFlag(Flag.FROZEN);
        myWall1.addFlag(Flag.FROZEN);
        myWall2.addFlag(Flag.FROZEN);

        world.addBody(myFloor1);
        world.addBody(myFloor2);
        world.addBody(myWall1);
        world.addBody(myWall2);
        world.addUniversalForce(new Gravity(200));
        world.addPreUpdateTickListener(new CameraMovement(300));

        // Run the simulation
        loop.start();
    }
}