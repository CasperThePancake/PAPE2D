import PAPE2D.Body;
import PAPE2D.Flag;
import PAPE2D.PhysicsLoop;
import PAPE2D.World;
import PAPE2D.bodies.Circle;
import PAPE2D.bodies.Rectangle;
import PAPE2D.force.Gravity;
import PAPE2D.helper.Vector2;
import PAPE2D.TickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        World world = new World();
        // High target FPS so engine runs at maximum uncapped speed
        PhysicsLoop loop = new PhysicsLoop(world, 1000);

        // Walls
        Body floor = new Rectangle(new Vector2(0, -200), 410, 50, 1);
        Body wallLeft = new Rectangle(new Vector2(-200, 1000), 10, 2400, 1);
        Body wallRight = new Rectangle(new Vector2(200, 1000), 10, 2400, 1);

        floor.addFlag(Flag.FROZEN);
        wallLeft.addFlag(Flag.FROZEN);
        wallRight.addFlag(Flag.FROZEN);

        world.addBody(floor, wallLeft, wallRight);
        world.addUniversalForce(new Gravity(400));

        // Hide off-screen to avoid rendering overhead
        loop.setCamX(2500);

        // Thread-safe queue for adding balls from the main thread
        Queue<Body> spawnQueue = new ConcurrentLinkedQueue<>();

        world.addPreUpdateTickListener(new TickListener() {
            @Override
            public void init(World tickedWorld) {}

            @Override
            public void onTick(World tickedWorld, PhysicsLoop tickedPhysicsLoop, double dt) {
                while (!spawnQueue.isEmpty()) {
                    tickedWorld.addBody(spawnQueue.poll());
                }
            }
        });

        loop.start();

        // BENCHMARK CONFIGURATION
        int startX = 50;             // Initial batch of balls
        int targetN = 2000;           // Total maximum balls
        int initialSettleMs = 10000;  // 1 MINUTE delay for the stack to fully rest
        int stepSettleMs = 200;       // Settling time per individual added ball
        int sampleCycles = 5;         // Number of FPS readings per step
        int sampleIntervalMs = 100;

        Random rand = new Random(42);
        List<Double> fpsStats = new ArrayList<>();
        List<Integer> countStats = new ArrayList<>();

        System.out.println("Pre-populating world with " + startX + " balls...");

        // 1. Queue initial X balls
        for (int i = 0; i < startX; i++) {
            double spawnX = (rand.nextDouble() - 0.5) * 350;
            double spawnY = 800 + (i * 2); // Stagger vertically to minimize overlap explosions
            spawnQueue.add(new Circle(new Vector2(spawnX, spawnY), 5, 1));
        }

        // 2. Wait 1 full minute for initial stack to settle
        System.out.println("Waiting 60 seconds for initial " + startX + " balls to settle into resting stack...");
        Thread.sleep(initialSettleMs);

        System.out.println("Starting Benchmark from N = " + startX + " to " + targetN + "...");

        // 3. Incremental benchmark loop from X to N
        for (int currentN = startX; currentN <= targetN; currentN++) {
            // Add 1 extra ball
            double spawnX = (rand.nextDouble() - 0.5) * 350;
            spawnQueue.add(new Circle(new Vector2(spawnX, 1200), 5, 1));

            // Settle time for individual ball
            Thread.sleep(stepSettleMs);

            // Sample real FPS from PhysicsLoop
            double sumFps = 0;
            for (int s = 0; s < sampleCycles; s++) {
                sumFps += loop.getCurrentFps();
                Thread.sleep(sampleIntervalMs);
            }

            double avgFps = sumFps / sampleCycles;
            fpsStats.add(avgFps);
            countStats.add(currentN);

            System.out.printf("N: %-6d | FPS: %.1f%n", currentN, avgFps);
        }

        // Output summary
        System.out.println("\n=========================");
        System.out.printf("%-8s %s%n", "N Balls", "Average FPS");
        System.out.println("=========================");
        for (int i = 0; i < fpsStats.size(); i++) {
            System.out.printf("%-8d %d%n", countStats.get(i), Math.round(fpsStats.get(i)));
        }

        loop.stop();
    }
}