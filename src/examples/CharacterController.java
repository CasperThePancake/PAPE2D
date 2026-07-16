import PAPE2D.*;
import PAPE2D.bodies.Circle;
import PAPE2D.bodies.Rectangle;
import PAPE2D.bodies.Square;
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
    World world = new World(new SweepAndPrune(), new SAT(), 0.3, 5, 0);
    PhysicsLoop loop = new PhysicsLoop(world, 200);

    // Player
    Body player = new Square(new Vector2(0,15),15,1);
    player.addFlag(Flag.FROZEN_ROTATION);
    world.addBody(player);

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

    // Other obstacles
    Body slope = new Rectangle(new Vector2(400,25),150,25,new Vector2(),Math.PI/5,0,1);
    slope.addFlag(Flag.FROZEN);
    world.addBody(slope);

    // Forces and camera movement
    world.addUniversalForce(new Gravity(500));
    world.addPreUpdateTickListener(new CameraMovement(300));

    world.addPreUpdateTickListener(new TickListener() {
        private boolean down = false;

        private double movementSpeed = 100;

        private double jumpSpeed = 200;

        @Override
        public void onTick(World tickedWorld, PhysicsLoop tickedPhysicsLoop, double dt) {
            if (tickedPhysicsLoop.isKeyDown(KeyEvent.VK_D)) {
                if (player.getVelocity().getX() < movementSpeed) {
                    player.setVelocity(new Vector2(movementSpeed,player.getVelocity().getY()));
                }
            } else if (tickedPhysicsLoop.isKeyDown(KeyEvent.VK_Q)) {
                if (player.getVelocity().getX() > -movementSpeed) {
                    player.setVelocity(new Vector2(-movementSpeed,player.getVelocity().getY()));
                }
            }

            if (tickedPhysicsLoop.isKeyDown(KeyEvent.VK_Z) && !down) {
                if (player.getVelocity().getY() < jumpSpeed) {
                    player.setVelocity(new Vector2(player.getVelocity().getX(),jumpSpeed));
                }
                down = true;
            } else if (!tickedPhysicsLoop.isKeyDown(KeyEvent.VK_Z) && down) {
                down = false;
            }
        }
    });

    // Run the simulation
    loop.start();
}