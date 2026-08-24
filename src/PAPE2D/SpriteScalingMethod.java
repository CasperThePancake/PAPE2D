package PAPE2D;

/**
 * Simple enum for options in how a world handles friction coefficient calculation
 */
public enum SpriteScalingMethod {
    /**
     * Stretch the sprite (with current rotation) separately in both directions until it fills the body's AABB with no gaps
     */
    STRETCH,
    /**
     * Scale the sprite (with current rotation) while retaining aspect ratio such that the body's AABB is fully covered; sprite may leak out of AABB
     */
    COVER,
    /**
     * Scale the sprite (with current rotation) while retaining aspect ratio such that the full sprite is contained within the body's AABB, with minimal gaps
     */
    FIT,
    /**
     * Stretch the sprite separately in both directions until it fills the body's AABB with no gaps; ignore any set rotation for the sprite
     */
    STRETCH_IGNORE_ROTATION,
    /**
     * Scale the sprite while retaining aspect ratio such that the body's AABB is fully covered; sprite may leak out of AABB; ignore any set rotation for the sprite
     */
    COVER_IGNORE_ROTATION,
    /**
     * Scale the sprite while retaining aspect ratio such that the full sprite is contained within the body's AABB, with minimal gaps; ignore any set rotation for the sprite
     */
    FIT_IGNORE_ROTATION;

    SpriteScalingMethod() {
    }

    /**
     * Calculate the scaling coefficients for both directions in order to scale a sprite to a body's AABB in the requested way
     *
     * @param widthAABB Given width of the body's AABB
     * @param heightAABB Given height of the body's AABB
     * @param widthSprite Given width of the sprite (unrotated)
     * @param heightSprite Given height of the sprite (unrotated)
     *
     * @return Necessary scaling coefficients for both directions
     */
    public double[] calculateScaling(double widthAABB, double heightAABB, double widthSprite, double heightSprite, double rotationSprite) {
        if (this.equals(STRETCH) || this.equals(COVER) || this.equals(FIT)) {
            // Rotate widths/heights
            double theta = rotationSprite;
            double c = Math.abs(Math.cos(theta));
            double s = Math.abs(Math.sin(theta));

            double widthSpriteRotated  = widthSprite * c + heightSprite * s;
            double heightSpriteRotated = widthSprite * s + heightSprite * c;

            // Cover and fit have same scaling in both directions, so nothing special
            if (this.equals(COVER)) {
                double factor = Math.max(widthAABB / widthSpriteRotated, heightAABB / heightSpriteRotated);
                return new double[]{factor, factor};
            } else if (this.equals(FIT)) {
                double factor = Math.min(widthAABB / widthSpriteRotated, heightAABB / heightSpriteRotated);
                return new double[]{factor, factor};
            } else if (this.equals(STRETCH)) {
                // System: w*c*sx + h*s*sy = widthAABB
                //         w*s*sx + h*c*sy = heightAABB
                double det = widthSprite * heightSprite * (c * c - s * s);

                if (Math.abs(det) < 1e-9) {
                    // Det = 0 cases (i.e. 45° where rotated width = rotated height so both equations mean the same = linearly dependent)
                    return new double[]{ // But these cases are simple to solve
                            widthAABB / widthSpriteRotated,
                            heightAABB / heightSpriteRotated
                    };
                }

                double sx = (heightSprite * c * widthAABB  - heightSprite * s * heightAABB) / det;
                double sy = (widthSprite  * c * heightAABB - widthSprite  * s * widthAABB)  / det;
                return new double[]{sx, sy};
            }
        } else if (this.equals(STRETCH_IGNORE_ROTATION) || this.equals(COVER_IGNORE_ROTATION) || this.equals(FIT_IGNORE_ROTATION)) {
            // Cover and fit have same scaling in both directions, so nothing special
            if (this.equals(COVER_IGNORE_ROTATION)) {
                double factor = Math.max(widthAABB / widthSprite, heightAABB / heightSprite);
                return new double[]{factor, factor};
            } else if (this.equals(FIT_IGNORE_ROTATION)) {
                double factor = Math.min(widthAABB / widthSprite, heightAABB / heightSprite);
                return new double[]{factor, factor};
            } else if (this.equals(STRETCH_IGNORE_ROTATION)) {
                double sx = widthAABB / widthSprite;
                double sy = heightAABB / heightSprite;
                return new double[]{sx, sy};
            }
        }

        return new double[]{1,1}; // Default case fall-back
    }
}
