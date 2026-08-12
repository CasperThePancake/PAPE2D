package PAPE2D;

import PAPE2D.helper.Vector2;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.List;

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
    private double currentFps = 0;
    private final List<Double> cacheFPS = new ArrayList<>();
    private final static int cacheFPSDistance = 60;

    private double consoleRefreshTimer = 0;
    private boolean consoleFirstDraw = true;

    private static final String ESC = "\u001B";

    private final boolean[] keys = new boolean[256];
    private int mouseWheelDelta = 0;

    private final World world;
    private final double targetDt;

    private boolean running = false;

    // Graphics2D context used during the current frame's render pass
    private Graphics2D currentGraphics;

    private double accumulator = 0.0;

    private double camX = 0;
    private double camY = 0;
    private double camZoom = 1;

    // =================================================================================
    // Constructors & initialization
    // =================================================================================

    /**
     * Create a new physics loop, simulating the given world at a given target FPS
     *
     * @param world Given world to simulate
     * @param targetFps Given target FPS
     */
    public PhysicsLoop(World world, int targetFps) {
        this.world = world;
        this.targetDt = 1.0 / targetFps;

        Dimension size = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(size);

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
     * Get the current position of the user's cursor relative to this canvas
     *
     * @return (x,y) position of cursor relative to the canvas's top-left corner
     */
    public Vector2 getMouseScreenPosition() {
        Point cursor = MouseInfo.getPointerInfo().getLocation();

        // Guard against the canvas not being on-screen yet (e.g. called before the window is shown)
        if (!isShowing()) {
            return new Vector2(0, 0);
        }

        Point origin = getLocationOnScreen();
        return new Vector2(cursor.getX() - origin.getX(), cursor.getY() - origin.getY());
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
    public Vector2 worldToScreenCoords(Vector2 worldCoords) {
        double worldX = worldCoords.getX();
        double worldY = worldCoords.getY();

        return new Vector2((worldX - camX)*camZoom + (double) WIDTH/2, (double) HEIGHT/2 - (worldY - camY)*camZoom);
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
    public Vector2 screenToWorldCoords(Vector2 screenCoords) throws IllegalArgumentException {
        double screenX = screenCoords.getX();
        double screenY = screenCoords.getY();

        // Valid coordinates check
        if (screenX < 0 || screenX > WIDTH || screenY < 0 || screenY > HEIGHT) {
            throw new IllegalArgumentException("Given camera coordinates fall outside the screen boundaries!");
        }

        return new Vector2(
                (screenX - (double) WIDTH / 2) / camZoom + camX,
                camY - (screenY - (double) HEIGHT / 2) / camZoom
        );
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
        Thread thread = new Thread(this, "PhysicsLoopThread");
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
        long lastTime = System.nanoTime();

        int frameCount = 0; // actual renders in this window

        while (running) {
            long now = System.nanoTime();
            double elapsed = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;

            // FPS calculation window
            fpsPrintTimer += elapsed;

            // Console screen
            consoleRefreshTimer += elapsed;
            if (consoleRefreshTimer >= 0.2) {
                printConsoleBox();
                consoleRefreshTimer = 0;
            }

            if (elapsed > 0.25) elapsed = 0.25; // Lag spike protection
            accumulator += elapsed;

            boolean didStep = false;

            // Step the deterministic physics
            while (accumulator >= targetDt) {
                didStep = true;
                world.step(targetDt, this);
                accumulator -= targetDt;
            }

            if (didStep) {
                render();
                frameCount++; // count every actual render
            }

            if (fpsPrintTimer >= 1.0) {
                currentFps = frameCount / fpsPrintTimer; // real renders per real second
                cacheFPS.add(currentFps);
                if (cacheFPS.size() > cacheFPSDistance) {
                    cacheFPS.removeFirst();
                }
                frameCount = 0;
                fpsPrintTimer = 0;
            }

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

        Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Clear Screen with black
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        currentGraphics = g2d;

        // Draw bodies
        for (Body b : world.getBodies()) {
            if (!b.hasFlag(Flag.HIDDEN)) {
                b.render(this);
            }
        }

        g2d.dispose();
        bs.show();
    }

    // RENDERING UTILITIES

    /**
     * Draw a square at the given position, with given width and color
     *
     * @param x Given x screen coordinate
     * @param y Given y screen coordinate
     * @param width Given width
     * @param color Given color
     */
    public void drawSquare(double x, double y, double width, Color color) {
        currentGraphics.setColor(color);
        currentGraphics.fill(new Rectangle2D.Double(x, y, width, width));
    }

    /**
     * Draw a line between the given points, with given color
     *
     * @param x1 Given x1 screen coordinate
     * @param y1 Given y1 screen coordinate
     * @param x2 Given x2 screen coordinate
     * @param y2 Given y2 screen coordinate
     * @param color Given color
     */
    @Internal
    public void drawLine(double x1, double y1, double x2, double y2, Color color) {
        currentGraphics.setColor(color);
        currentGraphics.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    /**
     * Draw a polygon on the screen, with given list of vertices (screen pixel coordinates)
     *
     * @param vertices Given list of vertices
     */
    @Internal
    public void drawPolygon(Vector2[] vertices) {
        int count = vertices.length;
        if (count < 3) return;

        Path2D.Double path = new Path2D.Double();
        path.moveTo(vertices[0].getX(), vertices[0].getY());
        for (int i = 1; i < count; i++) {
            path.lineTo(vertices[i].getX(), vertices[i].getY());
        }
        path.closePath();

        currentGraphics.setColor(Color.WHITE);
        currentGraphics.fill(path);
    }

    /**
     * Draw a circle on the screen, with (x,y) corresponding to its center
     *
     * @param cX Given center x coordinate
     * @param cY Given center y coordinate
     * @param radius Given circle radius
     */
    @Internal
    public void drawCircle(double cX, double cY, double radius) {
        Ellipse2D.Double circle = new Ellipse2D.Double(cX - radius, cY - radius, radius * 2, radius * 2);

        currentGraphics.setColor(Color.WHITE);
        currentGraphics.fill(circle);
    }

    // =================================================================================
    // Console screen
    // =================================================================================

    private String[] buildConsoleBox() {
        int width = 100;
        String border = "+" + "-".repeat(width - 2) + "+";

        List<String> lines = new ArrayList<>();
        lines.add(border);
        lines.add(padLine("| FPS:        " + (int) currentFps, width));
        lines.add(padLine("| FPS (rolling avg):        " + (int) average(cacheFPS), width));

        // dynamically add one line per body
        for (Body b : world.getBodies()) {
            if (b.hasFlag(Flag.DEBUG)) {
                lines.add(padLine("| ", width));
                lines.add(padLine("| "+b.getName()+": POS ("+(int) b.getPosition().getX()+","+(int) b.getPosition().getY()+") VEL ("+(int) b.getVelocity().getX()+","+(int) b.getVelocity().getY()+") ROT "+(int) b.getAngle()+" ROTVEL "+(int) b.getAngularVelocity(), width));
                // WIP debug features: visually: COM dot, bounding box;
            }
        }

        lines.add(border);

        return lines.toArray(new String[0]);
    }

    private String padLine(String s, int width) {
        int spaces = Math.max(0, width - 1 - s.length());
        return s + " ".repeat(spaces) + "|";
    }

    private void printConsoleBox() {
        String[] box = buildConsoleBox();

        if (!consoleFirstDraw) {
            System.out.print(ESC + "[" + box.length + "F");
        }
        consoleFirstDraw = false;

        for (String line : box) {
            System.out.print(ESC + "[2K" + line + "\n");
        }
        System.out.flush();
    }

    private double average(List<Double> numList) {
        if (numList.isEmpty()) {
            return 0;
        }

        double sum = 0;
        for (double num : numList) {
            sum += num;
        }
        return sum / numList.size();
    }
}