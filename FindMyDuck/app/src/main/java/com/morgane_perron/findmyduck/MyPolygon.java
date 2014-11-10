package com.morgane_perron.findmyduck;

/**
 * Class MyPolygon
 */
public class MyPolygon {
    private static final int TAILLEPOINTS = 4;
    private Point[] points;
    private int nb;
    public int color;

    public MyPolygon() {
        this.points = new Point[TAILLEPOINTS];
        this.nb = 0;
    }

    public void addPoint(float x, float y, int num) {
        points[nb++] = new Point(x,y);
    }
    public Point[] getPoints() {
        return this.points;
    }

    public Point getOnePoint(int i) {
        return points[i];
    }

}
