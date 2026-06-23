package PAPE2D.ticklisteners;

import PAPE2D.PhysicsLoop;
import PAPE2D.TickListener;
import PAPE2D.World;

import java.awt.event.KeyEvent;

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

        // Zooming using scroll wheel
        int scrollAmount = tickedPhysicsLoop.flushMouseWheelDelta();
        if (scrollAmount != 0) {
            double currentZoom = tickedPhysicsLoop.getCamZoom();

            if (scrollAmount < 0) {
                currentZoom *= 1.1;
            } else {
                currentZoom /= 1.1;
            }

            // Clamp bounds
            if (currentZoom < 0.1) currentZoom = 0.1;
            if (currentZoom > 10.0) currentZoom = 10.0;

            tickedPhysicsLoop.setCamZoom(currentZoom);
        }
    }
}
