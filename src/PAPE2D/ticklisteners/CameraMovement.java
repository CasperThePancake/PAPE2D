package PAPE2D.ticklisteners;

import PAPE2D.PhysicsLoop;
import PAPE2D.TickListener;
import PAPE2D.World;

import java.awt.event.KeyEvent;

/**
 * Example tick listener for basic camera movement
 */
public class CameraMovement implements TickListener {
    private double panSpeed = 300.0; // Pixels per second

    public CameraMovement(double panSpeed) {
        this.panSpeed = panSpeed;
    }

    @Override
    public void onTick(World tickedWorld, PhysicsLoop tickedPhysicsLoop, double dt) {
        double dx = 0;
        double dy = 0;

        // Panning using arrow keys
        if (tickedPhysicsLoop.isKeyDown(KeyEvent.VK_UP)) dy += panSpeed * dt;
        if (tickedPhysicsLoop.isKeyDown(KeyEvent.VK_DOWN)) dy -= panSpeed * dt;
        if (tickedPhysicsLoop.isKeyDown(KeyEvent.VK_LEFT)) dx -= panSpeed * dt;
        if (tickedPhysicsLoop.isKeyDown(KeyEvent.VK_RIGHT)) dx += panSpeed * dt;

        tickedPhysicsLoop.setCamX(tickedPhysicsLoop.getCamX() + dx);
        tickedPhysicsLoop.setCamY(tickedPhysicsLoop.getCamY() + dy);

        // Display current levels
        if (tickedPhysicsLoop.isKeyDown(KeyEvent.VK_ENTER)) {
            IO.println("Cam position: ("+tickedPhysicsLoop.getCamX()+","+tickedPhysicsLoop.getCamY()+")");
            IO.println("Cam zoom: "+tickedPhysicsLoop.getCamZoom());
        }

        // Zooming using scroll wheel
        int scrollAmount = tickedPhysicsLoop.flushMouseWheelDelta();
        if (scrollAmount != 0) {
            double currentZoom = tickedPhysicsLoop.getCamZoom();

            if (scrollAmount < 0) {
                currentZoom *= 1.1;
            } else {
                currentZoom /= 1.1;
            }

            // Zoom level bounds
            if (currentZoom < 0.1) currentZoom = 0.1;
            if (currentZoom > 10.0) currentZoom = 10.0;

            tickedPhysicsLoop.setCamZoom(currentZoom);
        }
    }
}
