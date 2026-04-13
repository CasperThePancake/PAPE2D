package PAPE2D.helper;

public class Vector2 {
    private double x = 0;
    private double y = 0;

    /**
     * Create a new vector with given x and y components
     *
     * @param x X component
     * @param y Y component
     */
    public Vector2(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    /**
     * Set the x component for this vector
     *
     * @param x New x component
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Set the y component for this vector
     *
     * @param y New y component
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Get the y component for this vector
     *
     * @return Y component for this vector
     */
    public double getY() {
        return y;
    }

    /**
     * Get the x component for this vector
     *
     * @return X component for this vector
     */
    public double getX() {
        return x;
    }

    /**
     * Add another vector to this vector
     *
     * @param other Other vector to add
     */
    public void add(Vector2 other) {
        this.setX(this.getX() + other.getX());
        this.setY(this.getY() + other.getY());
    }

    /**
     * Return this vector plus other vector
     *
     * @param other Other vector to add
     *
     * @return New vector equal to this vector plus other vector
     */
    public Vector2 plus(Vector2 other) {
        return new Vector2(this.getX() + other.getX(),this.getY() + other.getY());
    }

    /**
     * Subtract another vector from this vector
     *
     * @param other Other vector to subtract
     */
    public void subtract(Vector2 other) {
        this.setX(this.getX() - other.getX());
        this.setY(this.getY() - other.getY());
    }

    /**
     * Return this vector minus other vector
     *
     * @param other Other vector to subtract
     *
     * @return New vector equal to this vector minus other vector
     */
    public Vector2 minus(Vector2 other) {
        return new Vector2(this.getX() - other.getX(),this.getY() - other.getY());
    }

    /**
     * Multiply this vector with another component-wise
     *
     * @param other Other vector to multiply with
     */
    public void multiply(Vector2 other) {
        this.setX(this.getX() * other.getX());
        this.setY(this.getY() * other.getY());
    }

    /**
     * Return this vector times other vector component-wise
     *
     * @param other Other vector to multiply with
     *
     * @return New vector equal to this vector multiplied component-wise with other vector
     */
    public Vector2 times(Vector2 other) {
        return new Vector2(this.getX() * other.getX(),this.getY() * other.getY());
    }

    /**
     * Multiply this vector with a given factor
     *
     * @param factor Given factor to multiply with
     */
    public void multiply(double factor) {
        this.setX(factor * this.getX());
        this.setY(factor * this.getY());
    }

    /**
     * Return this vector times a given factor
     *
     * @param factor Given factor to multiply with
     *
     * @return New vector equal to this vector multiplied by given factor
     */
    public Vector2 times(double factor) {
        return new Vector2(this.getX() * factor,this.getY() * factor);
    }

    /**
     * Return dot product between this vector and given other vector
     *
     * @param other Given other vector
     *
     * @return Dot product between this vector and given other vector
     */
    public double dot(Vector2 other) {
        return this.getX() * other.getX() + this.getY() * other.getY();
    }

    /**
     * Return the distance between this vector and given other vector
     *
     * @param other Given other vector
     *
     * @return Distance between this vector and given other vector
     */
    public double distance(Vector2 other) {
        return Math.sqrt(this.minus(other).dot(this.minus(other)));
    }

    /**
     * Return the vector normal to this vector
     *
     * @note Equivalent to a 90 degrees rotation
     *
     * @return New vector normal to this vector
     */
    public Vector2 normal() {
        return new Vector2(getY(),-getX());
    }

    /**
     * Return this vector rotated by a given angle
     *
     * @param angle Given angle to rotate by
     *
     * @return New vector equal to this vector rotated by given angle
     */
    public Vector2 rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        return new Vector2(getX() * cos - getY() * sin, getX() * sin + getY() * cos);
    }

    /**
     * Return the size of this vector
     *
     * @return Size of this vector
     */
    public double size() {
        return Math.sqrt(this.dot(this));
    }

    /**
     * Return the normalized version of this vector
     *
     * @return New vector equal in direction to this vector but normalized to a size of 1
     */
    public Vector2 normalized() {
        return this.times(1/size());
    }
}
