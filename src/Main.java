import PAPE2D.Body;
import PAPE2D.Flag;
import PAPE2D.PhysicsLoop;
import PAPE2D.World;
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
        PhysicsLoop loop = new PhysicsLoop(world, 200);

        // Not a point-mass double pendulum, but full rods with mass!
        Body anchor = new Square(new Vector2(0,0),10,1);
        Body rod1 = new Rectangle(new Vector2(0,0),10,100,1, new Vector2(200,0),3.1,0);
        Body rod2 = new Rectangle(new Vector2(0,-90),10,100,1, new Vector2(-60,0),1.5,0);

        // Disable collisions between the rods
        world.addCollisionExclusion(new CollisionExclusion(anchor,rod1));
        world.addCollisionExclusion(new CollisionExclusion(rod1,rod2));
        world.addCollisionExclusion(new CollisionExclusion(anchor,rod2));
        anchor.addFlag(Flag.FROZEN);

        // Rods are connected by revolute joints
        world.addStaticConstraint(new RevoluteJointConstraint(anchor,rod1,new Vector2(0,0),new Vector2(0,45)));
        world.addStaticConstraint(new RevoluteJointConstraint(rod1,rod2,new Vector2(0,-45), new Vector2(0,45)));

        world.addBody(anchor);
        world.addBody(rod1);
        world.addBody(rod2);

        world.addUniversalForce(new Gravity(400));

        world.addPreUpdateTickListener(new CameraMovement(300));

        // Run the simulation
        loop.start();
    }
}