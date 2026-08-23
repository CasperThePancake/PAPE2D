package PAPE2D.graphics;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Sprite class for preloading images to render
 */
public class Sprite {
    // =================================================================================
    // Attributes
    // =================================================================================
    private final BufferedImage image;
    private final int width;
    private final int height;

    // =================================================================================
    // Constructor
    // =================================================================================
    /**
     * Loads an image and converts it into a hardware-accelerated VRAM surface.
     *
     * @param filePath Path to the PNG file
     * @throws IOException If the file cannot be read
     */
    public Sprite(String filePath) throws IOException {
        BufferedImage loadedImage = ImageIO.read(new File(filePath));
        if (loadedImage == null) {
            throw new IOException("Could not decode image at: " + filePath);
        }

        // Convert to a VRAM-compatible image surface matching the default screen device
        this.image = createCompatibleImage(loadedImage);
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    // =================================================================================
    // Rendering methods
    // =================================================================================

    /**
     * Render the sprite onto the given Graphics2D context with position, scale, and rotation.
     *
     * @param g2d The frame's Graphics2D context
     * @param screenX Screen X position for the sprite's center
     * @param screenY Screen Y position for the sprite's center
     * @param scaleX Horizontal scaling multiplier (1.0 = original size)
     * @param scaleY Vertical scaling multiplier (1.0 = original size)
     * @param angleRadians Rotation angle in radians around the center
     */
    public void render(Graphics2D g2d, double screenX, double screenY, double scaleX, double scaleY, double angleRadians) {
        // Save the previous camera/global transform state
        AffineTransform oldTransform = g2d.getTransform();

        // Translate to the target screen position
        g2d.translate(screenX, screenY);

        // Apply rotation if needed
        if (angleRadians != 0.0) {
            g2d.rotate(angleRadians);
        }

        // Apply scaling if needed
        if (scaleX != 1.0 || scaleY != 1.0) {
            g2d.scale(scaleX, scaleY);
        }

        // Draw the image centered at (0, 0) relative to the applied transforms
        g2d.drawImage(image, -width / 2, -height / 2, null);

        // Restore the original camera/global transform state
        g2d.setTransform(oldTransform);
    }

    /**
     * Convert standard BufferedImage to VRAM-compatible surface
     */
    private static BufferedImage createCompatibleImage(BufferedImage image) {
        GraphicsConfiguration gc = GraphicsEnvironment // Query the user's OS for graphics details
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        BufferedImage compatibleImage = gc.createCompatibleImage( // Using the user's own screen config, create an optimal copy of the image
                image.getWidth(), image.getHeight(), image.getTransparency()
        );

        Graphics2D g2d = compatibleImage.createGraphics();
        g2d.drawImage(image, 0, 0, null); // Blit to GPU
        g2d.dispose();

        return compatibleImage;
    }

    /**
     * Get the width of the loaded image for this sprite
     *
     * @return Sprite's loaded image width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the loaded image for this sprite
     *
     * @return Sprite's loaded image height
     */
    public int getHeight() {
        return height;
    }
}