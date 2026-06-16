package PAPE2D;

import PAPE2D.helper.Vector2;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

/**
 * Physics loop class linked to a World that runs, simulates, and renders its contents in a thread
 */
public class PhysicsLoop extends Canvas implements Runnable {
    // =================================================================================
    // Attributes
    // =================================================================================
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final World world;
    private final double targetDt;

    private Thread thread;
    private boolean running = false;

    // Direct pixel buffer
    private final BufferedImage image;
    private final int[] pixels;

    private long lastTime = 0;
    private double accumulator = 0.0;

    private double camX = 0;
    private double camY = 0;
    private double camZoom = 1;

    // =================================================================================
    // Constructors & initialization
    // =================================================================================
    public PhysicsLoop(World world, int targetFps) {
        this.world = world;
        this.targetDt = 1.0 / targetFps;

        Dimension size = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(size);

        // Set up the raw blitting buffer
        this.image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        // Boot up the window frame
        initWindow();
    }

    private void initWindow() {
        JFrame frame = new JFrame("PAPE2D Simulation");
        frame.setResizable(false);
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // =================================================================================
    // Camera methods
    // =================================================================================

    /**
     * Convert world coordinates to their corresponding screen pixel coordinates
     *
     * @param worldCoords Given tuple of world coordinates (x,y)
     *
     * @return Tuple of pixel coordinates (x,y) corresponding to the given world coordinates
     *
     * @note Method does not check whether given world coordinates appear on the screen, so extra boundary checks are recommended!
     */
    public double[] worldToScreenCoords(double[] worldCoords) {
        double worldX = worldCoords[0];
        double worldY = worldCoords[1];
        return new double[]{(worldX - camX)*camZoom + (double) WIDTH/2, (worldY - camY)*camZoom + (double) HEIGHT/2};
    }

    /**
     * Convert screen pixel coordinates to their corresponding world coordinates
     *
     * @param screenCoords Given tuple of screen pixel coordinates (x,y)
     *
     * @return Tuple of world coordinates (x,y) corresponding to the given screen pixel coordinates
     *
     * @throws IllegalArgumentException If given camera coordinates fall outside the screen boundaries
     */
    public double[] screenToWorldCoords(double[] screenCoords) throws IllegalArgumentException {
        double screenX = screenCoords[0];
        double screenY = screenCoords[1];

        // Valid coordinates check
        if (screenX < 0 || screenX > WIDTH || screenY < 0 || screenY > HEIGHT) {
            throw new IllegalArgumentException("Given camera coordinates fall outside the screen boundaries!");
        }

        return new double[]{(screenX-(double) WIDTH/2) / camZoom + camX, (screenY-(double) HEIGHT/2) / camZoom + camY};
    }

    /**
     * Get the current world x coordinate corresponding to the middle of the screen
     *
     * @return Current world x coordinate corresponding to the middle of the screen
     */
    public double getCamX() {
        return camX;
    }

    /**
     * Set the camera's x coordinate
     *
     * @param camX World x coordinate to appear at the center of the screen
     */
    public void setCamX(double camX) {
        this.camX = camX;
    }

    /**
     * Get the current world y coordinate corresponding to the center of the screen
     *
     * @return Current world y coordinate corresponding to the center of the screen
     */
    public double getCamY() {
        return camY;
    }

    /**
     * Set the camera's y coordinate
     *
     * @param camY World y coordinate to appear at the center of the screen
     */
    public void setCamY(double camY) {
        this.camY = camY;
    }

    /**
     * Get the current camera zoom level, corresponding to #pixels / #units
     *
     * @return Camera zoom level
     */
    public double getCamZoom() {
        return camZoom;
    }

    /**
     * Set the camera zoom level, corresponding to #pixels / #units
     *
     * @param camZoom Given camera zoom level
     */
    public void setCamZoom(double camZoom) {
        this.camZoom = camZoom;
    }

    // =================================================================================
    // Playback control
    // =================================================================================
    /**
     * Start the physics loop
     */
    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "PhysicsLoopThread");
        thread.start();
    }

    /**
     * Pause the physics loop
     */
    public synchronized void pause() {
        running = false;
    }

    /**
     * Stop the physics loop, closing open simulation windows
     */
    public synchronized void stop() {
        running = false;
        System.exit(0);
    }

    // =================================================================================
    // Running
    // =================================================================================
    @Override
    public void run() {
        lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            double elapsed = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;

            if (elapsed > 0.25) elapsed = 0.25; // Lag spike protection
            accumulator += elapsed;

            // Step the deterministic physics
            while (accumulator >= targetDt) {
                world.step(targetDt, this);
                accumulator -= targetDt;
            }

            // Blit everything to screen
            render();

            // Tiny sleep to prevent running hot at 100% CPU thread starvation
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // =================================================================================
    // Rendering
    // =================================================================================
    private void render() {
        // Create buffer strategy
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3); // Triple buffering
            return;
        }

        // Clear Screen with dark gray
        Arrays.fill(pixels, 0x222222);

        // Draw bodies
        for (Body b : world.getBodies()) {
            b.render(this);
        }

        // Push the raw pixel data to the native monitor hardware
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    // RENDERING UTILITIES

    /**
     * Base single pixel drawing method
     *
     * @param x Given screen x coordinate to draw
     * @param y Given screen y coordinate to draw
     * @param color Given color to draw
     */
    public void setPixel(int x, int y, int color) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return;
        pixels[y * WIDTH + x] = color;
    }

    /**
     * Draw a (rotated) rectangle on the screen, with (x,y) corresponding to the top-left corner if unrotated
     *
     * @param x Given unrotated top-left corner x coordinate
     * @param y Given unrotated top-left corner y coordinate
     * @param width Given rectangle width
     * @param height Given rectangle height
     * @param angle Given rotation angle
     */
    public void drawRectangle(double x, double y, double width, double height, double angle) {
        // WIP (could just use drawPolygon() unless separate implementation significantly saves computation)
    }

    /**
     * Draw a polygon on the screen, with given list of vertices (screen pixel coordinates)
     *
     * @param vertices Given list of vertices
     */
    public void drawPolygon(Vector2[] vertices) {
        // WIP
    }

    /**
     * Draw a circle on the screen, with (x,y) corresponding to its center
     *
     * @param x Given center x coordinate
     * @param y Given center y coordinate
     * @param radius Given circle radius
     */
    public void drawCircle(double x, double y, double radius) {
        // WIP
    }
}