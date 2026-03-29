package PAPE2D.helper;

public class Vector2 {
    private double x = 0;
    private double y = 0;

    public Vector2(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public void add(Vector2 other) {
        this.setX(this.getX() + other.getX());
        this.setY(this.getY() + other.getY());
    }

    public void subtract(Vector2 other) {
        this.setX(this.getX() - other.getX());
        this.setY(this.getY() - other.getY());
    }

    public void multiply(Vector2 other) {
        this.setX(this.getX() * other.getX());
        this.setY(this.getY() * other.getY());
    }

    public void multiply(double factor) {
        this.setX(factor * this.getX());
        this.setY(factor * this.getY());
    }

    public double dot(Vector2 other) {
        return this.getX() * other.getX() + this.getY() * other.getY();
    }
}
