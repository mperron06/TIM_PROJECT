package com.morgane_perron.findmyduck;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by zz on 09/10/14.
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

    public MyPolygon(MyPolygon polygon) {
        this.points = new Point[TAILLEPOINTS];
        this.nb = 0;
        for (int i = 0; i < TAILLEPOINTS; i++)
            points[i] = polygon.getPoints()[i];

    }

    public void addPoint(float x, float y, int num) {
        points[nb++] = new Point(x,y);
    }
    public Point[] getPoints() {
        return this.points;
    }

    public void setPoints(Point p, int pos) {
        this.points[pos] = p;
    }

    public Point getOnePoint(int i) {
        return points[i];
    }

    public float[] extractArrete() {
        float[] cotes = new float[4];
        cotes[0] = points[0].x;
        cotes[1] = points[0].y;
        cotes[2] = points[2].x;
        cotes[3] = points[2].y;
        return cotes;
    }
}
