import PAPE2D.Body;
import PAPE2D.Flag;
import PAPE2D.PhysicsLoop;
import PAPE2D.World;
import PAPE2D.bodies.Circle;
import PAPE2D.bodies.Rectangle;
import PAPE2D.bodies.Square;
import PAPE2D.constraint.AngularConstraint;
import PAPE2D.constraint.DistanceConstraint;
import PAPE2D.constraint.RevoluteJointConstraint;
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
        PhysicsLoop loop = new PhysicsLoop(world, 240);

        // N balls simulation (stress test)
        int N = 100;

        for (int i = 0; i < N; i++) {
            Body myBall = new Circle(new Vector2((double) i /N * 40, (double) i /N * 45),10,1);
            world.addBody(myBall);
        }

        // Walls
        Body floor = new Rectangle(new Vector2(-200,-200),400,50,1);
        Body wallLeft = new Rectangle(new Vector2(-200,2000),50,2200,1);
        Body wallRight = new Rectangle(new Vector2(150,2000),50,2200,1);

        floor.addFlag(Flag.FROZEN);
        wallLeft.addFlag(Flag.FROZEN);
        wallRight.addFlag(Flag.FROZEN);

        world.addBody(floor,wallLeft,wallRight);

        world.addUniversalForce(new Gravity(400));

        world.addPreUpdateTickListener(new CameraMovement(300));

        // Run the simulation
        loop.start();
    }
}