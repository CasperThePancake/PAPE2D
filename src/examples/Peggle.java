import PAPE2D.*;
import PAPE2D.bodies.Circle;
import PAPE2D.bodies.Rectangle;
import PAPE2D.broadphase.SweepAndPrune;
import PAPE2D.force.Gravity;
import PAPE2D.helper.Vector2;
import PAPE2D.narrowphase.SAT;
import PAPE2D.ticklisteners.CameraMovement;
import java.awt.event.KeyEvent;

/**
 * Example usage of PAPE2D
 */
void main() {
    // Initiate world and physics loop
    World world = new World(new SweepAndPrune(), new SAT(), 0.3, 10, RestitutionMethod.MAX,FrictionCoefficientMethod.MEAN_GEOMETRIC);
    PhysicsLoop loop = new PhysicsLoop(world, 200);

    // Pegs
    double pegDistanceX = 50;
    double pegDistanceY = 50;
    for (double x = -500; x <= 500; x += pegDistanceX) {
        for (double y = 300; y >= 0; y -= pegDistanceY) {
            Body newPeg = new Circle(new Vector2(x, y),5, 1);
            newPeg.addFlag(Flag.FROZEN);
            world.addBody(newPeg);
        }
    }

    // Floors and walls
    Body myFloor1 = new Rectangle(new Vector2(-500, 0), 1000, 50, 1);
    Body myFloor2 = new Rectangle(new Vector2(-500, 500), 1000, 50, 1);
    Body myWall1 = new Rectangle(new Vector2(500, 500), 50, 500, 1);
    Body myWall2 = new Rectangle(new Vector2(-550, 500), 50, 500, 1);
    myFloor1.addFlag(Flag.FROZEN);
    myFloor2.addFlag(Flag.FROZEN);
    myWall1.addFlag(Flag.FROZEN);
    myWall2.addFlag(Flag.FROZEN);
    world.addBody(myFloor1);
    world.addBody(myFloor2);
    world.addBody(myWall1);
    world.addBody(myWall2);

    // Forces and camera movement
    world.addUniversalForce(new Gravity(200));
    world.addPreUpdateTickListener(new CameraMovement(300));

    world.addPreUpdateTickListener(new TickListener() {
        private boolean down = false;

        @Override
        public void onTick(World tickedWorld, PhysicsLoop tickedPhysicsLoop, double dt) {
            if (tickedPhysicsLoop.isKeyDown(KeyEvent.VK_A) && !down) {
                Vector2 cursor = tickedPhysicsLoop.getMouseScreenPosition();
                cursor = tickedPhysicsLoop.screenToWorldCoords(cursor);
                Vector2 spawn = new Vector2(0,440);
                double shootSpeed = 200;
                Vector2 velocity = cursor.minus(spawn).normalized().times(shootSpeed);
                Body ball = new Circle(spawn,10,1,velocity);
                ball.setRestitution(1.0);
                tickedWorld.addBody(ball);
                down = true;
            } else if (!tickedPhysicsLoop.isKeyDown(KeyEvent.VK_A) && down) {
                down = false;
            }
        }
    });

    // Run the simulation
    loop.start();
}