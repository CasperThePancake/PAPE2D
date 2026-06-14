package PAPE2D.rendering;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class SoftwareRenderer extends Canvas implements Runnable {
    // Screen dimensions
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // Thread
    private Thread thread;
    private boolean running = false;

    // Rendering components
    private BufferedImage image;
    private int[] pixels;

    // Mock Physics variables
    private float boxX = 100;
    private float boxY = 100;
    private float velX = 20.0f;
    private float velY = 15f;

    public SoftwareRenderer() {
        Dimension size = new Dimension(WIDTH, HEIGHT);
        setPreferredSize(size);

        // 1. Create the image buffer
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        // 2. Extract the raw pixel array (This is the "magic" part)
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    public synchronized void start() {
        running = true;
        thread = new Thread(this, "Renderer");
        thread.start();
    }

    @Override
    public void run() {
        while (running) {
            update(); // Physics logic
            render(); // Graphics logic

            try {
                Thread.sleep(16); // Aim for ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        // Simple bounce physics
        boxX += velX;
        boxY += velY;

        if (boxX < 0 || boxX + 50 > WIDTH) velX *= -1;
        if (boxY < 0 || boxY + 50 > HEIGHT) velY *= -1;
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3); // Triple buffering
            return;
        }

        // 3. Clear the screen (Fill with dark gray)
        Arrays.fill(pixels, 0x222222);

        // 4. Draw our "Physics Body" manually
        drawRect((int)boxX, (int)boxY, 50, 50, 0xFF0000); // Red Square

        // 5. Push the buffer to the screen
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    // --- CUSTOM DRAW METHODS ---
    public void setPixel(int x, int y, int color) {
        // The most important check in software rendering: Clipping
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) return;

        // 2D to 1D mapping: Index = y * width + x
        pixels[y * WIDTH + x] = color;
    }

    public void drawRect(int xp, int yp, int w, int h, int color) {
        for (int y = yp; y < yp + h; y++) {
            for (int x = xp; x < xp + w; x++) {
                setPixel(x, y, color);
            }
        }
    }

    // Example usage
    public static void main(String[] args) {
        SoftwareRenderer renderer = new SoftwareRenderer();
        JFrame frame = new JFrame("Custom Software Renderer");
        frame.setResizable(false);
        frame.add(renderer);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        renderer.start();
    }
}