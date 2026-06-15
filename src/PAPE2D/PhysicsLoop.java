package PAPE2D;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class PhysicsLoop extends Canvas implements Runnable {
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
        // WIP

        // Push the raw pixel data to the native monitor hardware
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    // RENDERING UTILITIES

    public void setPixel(int x, int y, int color) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return;
        pixels[y * WIDTH + x] = color;
    }
}