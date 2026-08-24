import PAPE2D.*;
import PAPE2D.bodies.Circle;
import PAPE2D.bodies.Rectangle;
import PAPE2D.broadphase.SweepAndPrune;
import PAPE2D.force.AirResistance;
import PAPE2D.force.Gravity;
import PAPE2D.graphics.Sprite;
import PAPE2D.helper.CollisionExclusion;
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

    /**
     * Sprites
     */
    Sprite pigSprite = null;
    try {
        pigSprite = new Sprite("src/examples/angrybirds_assets/pig.png");
    } catch (IOException e) {
        System.err.println("Failed to load sprite file: " + e.getMessage());
    }

    Sprite baseSprite = null;
    try {
        baseSprite = new Sprite("src/examples/angrybirds_assets/base.png");
    } catch (IOException e) {
        System.err.println("Failed to load sprite file: " + e.getMessage());
    }

    Sprite groundSprite = null;
    try {
        groundSprite = new Sprite("src/examples/angrybirds_assets/ground.png");
    } catch (IOException e) {
        System.err.println("Failed to load sprite file: " + e.getMessage());
    }

    Sprite iceSprite = null;
    try {
        iceSprite = new Sprite("src/examples/angrybirds_assets/ice.png");
    } catch (IOException e) {
        System.err.println("Failed to load sprite file: " + e.getMessage());
    }

    Sprite woodSprite = null;
    try {
        woodSprite = new Sprite("src/examples/angrybirds_assets/wood.png");
    } catch (IOException e) {
        System.err.println("Failed to load sprite file: " + e.getMessage());
    }

    Sprite redSprite = null;
    try {
        redSprite = new Sprite("src/examples/angrybirds_assets/red.png");
    } catch (IOException e) {
        System.err.println("Failed to load sprite file: " + e.getMessage());
    }

    Sprite slingshotSprite = null;
    try {
        slingshotSprite = new Sprite("src/examples/angrybirds_assets/slingshot.png");
    } catch (IOException e) {
        System.err.println("Failed to load sprite file: " + e.getMessage());
    }

    // Build level
    Body ground = new Rectangle(new Vector2(-1000,0),2000,50,1);
    ground.setSprite(groundSprite);
    ground.setSpriteScaleY(0.5);
    ground.addFlag(Flag.FROZEN);

    // Gemini helped me rebuild the level from a screenshot :)

    // =========================================================================
// 1. BASE LEVEL & PLATFORMS (Ground at Y = 0)
// Top-left Y = 20 means height of 20 sits on the ground [0..20]
// =========================================================================
    Body baseLeft  = new Rectangle(new Vector2(-115, 20), 110, 20, 5);
    Body baseRight = new Rectangle(new Vector2(-5, 20),   110, 20, 5);
    baseLeft.setSprite(baseSprite);
    baseRight.setSprite(baseSprite);

    World.SETTING_REFIT_BODY_SPRITE_ON_ROTATE = true;
    Body.DEFAULT_SPRITE_FIT = SpriteScalingMethod.STRETCH;

// Outer end stops resting on top of base platforms (Y: 20 -> 50)
    Body endStopLeft  = new Rectangle(new Vector2(-112, 50), 15, 30, 2);
    Body endStopRight = new Rectangle(new Vector2(97, 50),   15, 30, 2);
    endStopLeft.setSprite(woodSprite);
    endStopLeft.setSpriteRotate(Math.PI/2);

    endStopRight.setSprite(woodSprite);
    endStopRight.setSpriteRotate(Math.PI/2);

// =========================================================================
// 2. INNER GLASS MINI-STRUCTURE (Sitting on base)
// =========================================================================
// Lower wooden plate resting on base platforms (Y: 20 -> 30)
    Body glassBase = new Rectangle(new Vector2(-35, 30), 70, 10, 2);

    glassBase.setSprite(woodSprite);

// Glass pillars (Y: 30 -> 100)
    Body glassPillarLeft  = new Rectangle(new Vector2(-28, 100), 12, 70, 1);
    Body glassPillarRight = new Rectangle(new Vector2(16, 100),  12, 70, 1);

    glassPillarLeft.setSprite(iceSprite);
    glassPillarLeft.setSpriteRotate(Math.PI/2);

    glassPillarRight.setSprite(iceSprite);
    glassPillarRight.setSpriteRotate(Math.PI/2);

// Upper wooden cap resting on glass (Y: 100 -> 110)
    Body glassCap = new Rectangle(new Vector2(-35, 110), 70, 10, 2);

    glassCap.setSprite(woodSprite);

// =========================================================================
// 3. LOWER WOOD FRAME (Flanking Glass)
// =========================================================================
// Tall lower pillars resting on base platforms (Y: 20 -> 140)
    Body lowerPillarLeft  = new Rectangle(new Vector2(-52, 140), 15, 120, 4);
    Body lowerPillarRight = new Rectangle(new Vector2(37, 140),  15, 120, 4);

    lowerPillarLeft.setSprite(woodSprite);
    lowerPillarLeft.setSpriteRotate(Math.PI/2);

    lowerPillarRight.setSprite(woodSprite);
    lowerPillarRight.setSpriteRotate(Math.PI/2);

// Mid-level horizontal roof beam sitting on pillars (Y: 140 -> 155)
    Body midRoof = new Rectangle(new Vector2(-65, 155), 130, 15, 4);

    midRoof.setSprite(woodSprite);

// =========================================================================
// 4. UPPER WOOD FRAME & PIG TOWER
// =========================================================================
// Pig pedestal inside upper frame (Y: 155 -> 170)
    Body pigPedestal = new Rectangle(new Vector2(-15, 170), 30, 15, 2);

    pigPedestal.setSprite(woodSprite);

// Target Pig (Circle center at X: 0, Y: 187)
    Body pig = new Circle(new Vector2(0, 187), 17, 1);
    pig.setSprite(pigSprite);

// Upper vertical pillars resting on midRoof (Y: 155 -> 275)
    Body upperPillarLeft  = new Rectangle(new Vector2(-35, 275), 15, 120, 4);
    Body upperPillarRight = new Rectangle(new Vector2(20, 275),  15, 120, 4);

    upperPillarLeft.setSprite(woodSprite);
    upperPillarLeft.setSpriteRotate(-Math.PI/2);

    upperPillarRight.setSprite(woodSprite);
    upperPillarRight.setSpriteRotate(-Math.PI/2);

// Top roof beam (Y: 275 -> 290)
    Body topRoof = new Rectangle(new Vector2(-40, 290), 80, 15, 3);

    topRoof.setSprite(woodSprite);

// Top vertical spire block resting on topRoof (Y: 290 -> 320)
    Body topSpire = new Rectangle(new Vector2(-7.5, 320), 15, 30, 1);

    topSpire.setSprite(woodSprite);
    topSpire.setSpriteRotate(Math.PI/2);

    Body slingshot = new Rectangle(new Vector2(-300,100),50,100,1);
    slingshot.addFlag(Flag.FROZEN);
    slingshot.addFlag(Flag.NO_COLLISION);
    slingshot.setSprite(slingshotSprite);

    Body red = new Circle(new Vector2(-280,90),17,3);
    red.addFlag(Flag.FROZEN);
    red.setSprite(redSprite);

    //

    glassPillarLeft.setRestitution(0.1);
    glassPillarRight.setRestitution(0.1);

    world.addBody(red,slingshot,ground,baseLeft,baseRight,endStopRight,endStopLeft,glassBase,glassPillarLeft,glassPillarRight,glassCap,lowerPillarLeft,lowerPillarRight,midRoof,pigPedestal,pig,upperPillarLeft,upperPillarRight,topRoof,topSpire);
    Body.DEFAULT_FRICTION_COEFFICIENT = 5.0;
    Body.DEFAULT_RESTITUTION = 0;

    // Forces and camera movement
    world.addUniversalForce(new Gravity(200));
    loop.setCamX(-115.5);
    loop.setCamY(181.5);
    loop.setCamZoom(1.331);
    world.addPreUpdateTickListener(new TickListener() {
        private boolean down = false;

        @Override
        public void onTick(World tickedWorld, PhysicsLoop tickedPhysicsLoop, double dt) {
            if (tickedPhysicsLoop.isKeyDown(KeyEvent.VK_A) && !down) {
                Vector2 cursor = tickedPhysicsLoop.getMouseScreenPosition();
                cursor = tickedPhysicsLoop.screenToWorldCoords(cursor);
                Vector2 spawn = new Vector2(-280,90);
                double shootSpeed = 1;
                Vector2 velocity = cursor.minus(spawn).times(shootSpeed);
                red.changePosition(spawn);
                red.removeFlag(Flag.FROZEN);
                red.setVelocity(velocity);
                down = true;
            } else if (!tickedPhysicsLoop.isKeyDown(KeyEvent.VK_A) && down) {
                down = false;
            }
        }

        @Override
        public void init(World tickedWorld) {

        }
    });

    // Run the simulation
    loop.start();
}