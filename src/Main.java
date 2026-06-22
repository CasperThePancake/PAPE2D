import PAPE2D.Body;
import PAPE2D.PhysicsLoop;
import PAPE2D.World;
import PAPE2D.bodies.Circle;
import PAPE2D.bodies.Polygon;
import PAPE2D.bodies.Rectangle;
import PAPE2D.bodies.Square;
import PAPE2D.force.AirResistance;
import PAPE2D.force.Gravity;
import PAPE2D.force.Spring;
import PAPE2D.helper.Vector2;

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
        PhysicsLoop loop = new PhysicsLoop(world, 60);

//        List<Vector2> vertices = List.of(
//                new Vector2(0, 60),    // 1. Sharp top point
//                new Vector2(45, 20),   // 2. Upper right shoulder
//                new Vector2(35, -40),  // 3. Lower right taper
//                new Vector2(0, -40),   // 4. Flat bottom edge (Midpoint)
//                new Vector2(-35, -40), // 5. Lower left taper
//                new Vector2(-45, 20)   // 6. Upper left shoulder
//        );
//        Polygon myPolygon = new Polygon(vertices,1);
//
//        world.addBody(myPolygon);

        Body myBody1 = new Square(new Vector2(50,0),50, new Vector2(100,100), 1);
        Body myBody2 = new Square(new Vector2(-50,0),50, new Vector2(-100,-100), 1);

        world.addBody(myBody1);
        world.addBody(myBody2);
        world.addLocalForce(new Spring(myBody1,myBody2,0.05,400));

//        // Adding a listener directly via a lambda expression
//        world.addPreUpdateTickListener((tickedWorld, tickedPhysicsLoop, dt) -> {
//            // Rotate
//            myPolygon.setAngle(myPolygon.getAngle()+0.01);
//        });

        // Run the simulation
        loop.start();
    }
}