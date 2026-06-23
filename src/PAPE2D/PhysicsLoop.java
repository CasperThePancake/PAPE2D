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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Physics loop class linked to a World that runs, simulates, and renders its contents in a thread
 */
public class PhysicsLoop extends Canvas implements Runnable {
    // =================================================================================
    // Attributes
    // =================================================================================
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private double fpsPrintTimer = 0;

    private final boolean[] keys = new boolean[256];
    private int mouseWheelDelta = 0;

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

        // Attach a key listener directly to this Canvas component
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode >= 0 && keyCode < keys.length) {
                    keys[keyCode] = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode >= 0 && keyCode < keys.length) {
                    keys[keyCode] = false;
                }
            }
        });

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // Safely accumulate scroll movements dynamically across threads
                synchronized(PhysicsLoop.this) {
                    mouseWheelDelta += e.getWheelRotation();
                }
            }
        });

        // Ensure the Canvas can immediately capture input focus
        this.setFocusable(true);
        this.requestFocusInWindow();

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

    /**
     * Check if keyboard key with given code is pressed
     *
     * @param keyCode Given key code
     *
     * @return Whether or not the key is pressed
     */
    public boolean isKeyDown(int keyCode) {
        if (keyCode >= 0 && keyCode < keys.length) {
            return keys[keyCode];
        }
        return false;
    }

    /**
     * Returns and resets the current mouse wheel scroll delta
     *
     * @return Mouse wheel scroll delta
     */
    public synchronized int flushMouseWheelDelta() {
        int delta = mouseWheelDelta;
        mouseWheelDelta = 0;
        return delta;
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

        return new double[]{(worldX - camX)*camZoom + (double) WIDTH/2, (double) HEIGHT/2 - (worldY - camY)*camZoom};
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
     * Convert given alpha, red, green, blue values to 32-bit color integer
     *
     * @param a Alpha value (0-255)
     *
     * @param r Red value (0-255)
     *
     * @param g Green value (0-255)
     *
     * @param b Blue value (0-255)
     *
     * @return According 32-bit color integer
     */
    public static int argb(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
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
            // "FPS" display
            fpsPrintTimer += elapsed;
            if (fpsPrintTimer >= 1.0) {
                IO.println("Thread (run) FPS: " + (int)(1/elapsed));
                fpsPrintTimer = 0;
            }
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

        // Clear Screen with black
        Arrays.fill(pixels, 0x000000);

        // Draw bodies
        for (Body b : world.getBodies()) {
            b.render(this);
        }

        // WIP REMOVE: for testing, mark origin red
        setPixel((int) Math.round(WIDTH / 2), (int) Math.round(HEIGHT / 2), argb(255,255,0,0));

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
     * Draw a polygon on the screen, with given list of vertices (screen pixel coordinates)
     *
     * @param vertices Given list of vertices
     */
    public void drawPolygon(Vector2[] vertices) {
        int count = vertices.length;

        if (count < 3) return;

        // "Sort" vertices vertically
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int highestVertexIndex = 0;

        for (int i = 0; i < count; i++) {
            Vector2 vec = vertices[i];
            if (vec.getY() < minY) {
                minY = (int) Math.floor(vec.getY());
                highestVertexIndex = i;
            }

            if (vec.getY() > maxY) {
                maxY = (int) Math.ceil(vec.getY());
            }
        }

        // Clip to screen boundaries to prevent out-of-bounds array crashes
        if (minY < 0) minY = 0;
        if (maxY >= HEIGHT) maxY = HEIGHT - 1;

        // Identify starting edges (ones connected to highest vertex)
        int leftIndex = highestVertexIndex;
        int rightIndex = highestVertexIndex;

        int leftNextIndex = (highestVertexIndex - 1 + count) % count; // The "+ count" part has no effect on modulo but is required since modulo doesn't work on negative ints
        int rightNextIndex = (highestVertexIndex + 1) % count;

        // Entry and exit tracking (easier since convex)
        double entryX = vertices[highestVertexIndex].getX() * 4; // Using 4-subpixel rendering for antialiasing
        double exitX = vertices[highestVertexIndex].getX() * 4;

        double leftInverseSlope = calculateInverseSlope(vertices[leftIndex],vertices[leftNextIndex]) * 4; // Once again, reasoning in subpixels
        double rightInverseSlope = calculateInverseSlope(vertices[rightIndex],vertices[rightNextIndex]) * 4;

        // Interpolate entryX/exitX from the top vertex down to the actual starting scanline
        double topVertexY = vertices[highestVertexIndex].getY();
        if (topVertexY < minY) {
            double dy = minY - topVertexY;
            entryX += leftInverseSlope * dy;
            exitX += rightInverseSlope * dy;
        }

        // Start scanning down, line by line
        for (int y = minY; y <= maxY; y++) {
            // Check if left edge has expired
            while (y > vertices[leftNextIndex].getY() && y < maxY) {
                leftIndex = leftNextIndex;
                leftNextIndex = (leftIndex - 1 + count) % count;
                leftInverseSlope = calculateInverseSlope(vertices[leftIndex],vertices[leftNextIndex]) * 4;
                // Re-calculate the entry and slope
                double vertexY = vertices[leftIndex].getY();
                entryX = (vertices[leftIndex].getX() + (y - vertexY) * (leftInverseSlope / 4.0)) * 4;
            }

            // Check if right edge has expired
            while (y > vertices[rightNextIndex].getY() && y < maxY) {
                rightIndex = rightNextIndex;
                rightNextIndex = (rightIndex + 1) % count;
                rightInverseSlope = calculateInverseSlope(vertices[rightIndex],vertices[rightNextIndex]) * 4;
                // Re-calculate the exit and slope
                double vertexY = vertices[rightIndex].getY();
                exitX = (vertices[rightIndex].getX() + (y - vertexY) * (rightInverseSlope / 4.0)) * 4;
            }

            // Determine subpixel and pixel entry/exit properly
            int subXStart = (int) Math.round(Math.min(entryX,exitX));
            int subXEnd = (int) Math.round(Math.max(entryX,exitX));

            int pixelXStart = subXStart / 4;
            int pixelXEnd = subXEnd / 4;

            // Fill the row, using antialiasing
            for (int x = pixelXStart; x <= pixelXEnd; x++) {
                int subXLeft = x * 4; // Sub-pixels for this x
                int subXRight = x * 4 + 3;

                // Determine amount of covered subpixels
                int activeSubPixels;
                if (subXLeft >= subXStart && subXRight <= subXEnd) {
                    activeSubPixels = 4;
                } else {
                    int overlapStart = Math.max(subXLeft,subXStart);
                    int overlapEnd = Math.min(subXRight,subXEnd);
                    activeSubPixels = Math.max(0, overlapEnd - overlapStart + 1);
                }

                if (activeSubPixels == 0) continue; // No coverage somehow, go to next pixel

                double coverage = activeSubPixels / 4.0;

                // Set pixel (black 'n white)
                setPixel(x,y,argb((int) Math.round(255*coverage), 255, 255, 255));
            }

            // Update edges using slopes
            entryX += leftInverseSlope;
            exitX += rightInverseSlope;

            // Clamp to valid X range of current edges (prevents blow-up on near-horizontal edges)
            double leftMinX = Math.min(vertices[leftIndex].getX(), vertices[leftNextIndex].getX()) * 4;
            double leftMaxX = Math.max(vertices[leftIndex].getX(), vertices[leftNextIndex].getX()) * 4;
            double rightMinX = Math.min(vertices[rightIndex].getX(), vertices[rightNextIndex].getX()) * 4;
            double rightMaxX = Math.max(vertices[rightIndex].getX(), vertices[rightNextIndex].getX()) * 4;

            entryX = Math.max(leftMinX, Math.min(leftMaxX, entryX));
            exitX = Math.max(rightMinX, Math.min(rightMaxX, exitX));
        }
    }

    /**
     * Calculate the inverse slope from one point to another
     *
     * @param fromPoint Starting point
     * @param toPoint End point
     *
     * @return Inverse slope from starting point to end point (dx/dy)
     */
    private double calculateInverseSlope(Vector2 fromPoint, Vector2 toPoint) {
        double x1 = fromPoint.getX(), y1 = fromPoint.getY(), x2 = toPoint.getX(), y2 = toPoint.getY();
        if (y1 == y2) return 0;
        return (x2 - x1) / (y2 - y1);
    }

    /**
     * Draw a circle on the screen, with (x,y) corresponding to its center
     *
     * @param cX Given center x coordinate
     * @param cY Given center y coordinate
     * @param radius Given circle radius
     */
    public void drawCircle(double cX, double cY, double radius) {
        // Determine square that bounds the circle (for pixel looping)
        int minX = (int) Math.max(0, Math.floor(cX - radius));
        int maxX = (int) Math.min(WIDTH,Math.ceil(cX + radius));
        int minY = (int) Math.max(0, Math.floor(cY - radius));
        int maxY = (int) Math.min(HEIGHT, Math.ceil(cY + radius));

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                // Determine pixel center coordinates
                double pX = x + 0.5;
                double pY = y + 0.5;

                // Determine distance from center
                double d = Math.sqrt((cX - pX)*(cX - pX) + (cY - pY)*(cY - pY));

                // Filling logic
                if (d < radius - 0.5) { // Pixel fully covered
                    setPixel(x,y,argb(255,255,255,255));
                } else if (d > radius + 0.5) { // Pixel fully uncovered
                    continue;
                } else { // Partially covered: anti-aliasing
                    double coverage = (radius + 0.5) - d;
                    setPixel(x,y,argb((int) Math.round(255*coverage), 255, 255, 255));
                }
            }
        }
    }
}