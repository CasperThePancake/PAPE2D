import PAPE2D.Body;
import PAPE2D.Flag;
import PAPE2D.PhysicsLoop;
import PAPE2D.World;
import PAPE2D.bodies.Rectangle;
import PAPE2D.bodies.Square;
import PAPE2D.constraint.AngularConstraint;
import PAPE2D.constraint.DistanceConstraint;
import PAPE2D.force.Gravity;
import PAPE2D.helper.CollisionExclusion;
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

        Body myBody1 = new Square(new Vector2(-200,0),50,1,new Vector2(50,0),0,0);
        Body myBody2 = new Square(new Vector2(200,0),50,1,new Vector2(-50,0),0,0);

        world.addBody(myBody1);
        world.addBody(myBody2);

        world.addPreUpdateTickListener(new CameraMovement(300));

        // Run the simulation
        loop.start();
    }
}