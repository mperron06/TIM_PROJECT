package com.morgane_perron.findmyduck;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by zz on 17/10/14.
 */
public class SoundView extends View {
    private ArrayList<MyPolygon> elements;
    private ArrayList<MyPolygon> newElements;
    private int []originalSize = new int[2];
    private int nb = 10;
    private Paint paint;
    private float w;
    private float h;

    private float x;
    private float y;

    private int currentRectangle;

    public SoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(new MyTouchListener());
        this.paint = new Paint();
        originalSize[0] = 1000;
        originalSize[1] = 2000;
        generatePolygons();

        currentRectangle = 0;
    }



    @Override
    protected void onDraw(Canvas g) {
        super.onDraw(g);
        for (MyPolygon p : elements) {
            paint.setColor(p.color);
// liste des points sous la forme d'un tableau (x1, y1, x2, y2, ...)
// les (xi, yi) sont les sommets
            Point[] pts = p.getPoints();
            for (int i = 0; i < pts.length - 1; i++) {
                g.drawLine(pts[i].x, pts[i].y, pts[i + 1].x, pts[i + 1].y, paint);
            }
// ligne entre le dernier sommet et le premier somment
            g.drawLine(pts[pts.length - 1].x, pts[pts.length - 1].y, pts[0].x, pts[0].y, paint);

        }
        paint.setColor(Color.BLACK);
        //g.drawRect(50,50,100,100,paint);
        //
        //g.drawRect(elements.get(0).getOnePoint(0).x, elements.get(0).getOnePoint(0).y, elements.get(0).getOnePoint(2).x, elements.get(0).getOnePoint(2).y, paint );
        g.drawRect(elements.get(currentRectangle).getOnePoint(0).x, elements.get(currentRectangle).getOnePoint(0).y, elements.get(currentRectangle).getOnePoint(2).x, elements.get(currentRectangle).getOnePoint(2).y, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        originalSize[0] = getMeasuredWidth();
        originalSize[1] = getMeasuredHeight();

        generatePolygons();
        //deform(100,100);

    }

    private void deform(float x, float y) {
        this.x = x;
        this.y = y;
        //Pour chaque polygone
        generatePolygons();
        for(int i=0; i<elements.size(); i++) {
            Point []points = elements.get(i).getPoints();
            //Pour chaque points de polygone
            for (int j=0; j<points.length; j++) {
                //Calcul de la distance
                double dist = Math.sqrt(Math.pow(x-points[j].x,2) + Math.pow(y-points[j].y,2));
                double scale = scale(dist);
                double xp = x + (points[j].x - x) * scale;
                double yp = y + (points[j].y - y) * scale;
                Point p = new Point((float)xp,(float)yp);
                elements.get(i).setPoints(p,j);
            }
        }
        invalidate();
    }

    private double fDeform(double x) {
        float r = 100;
        float z = 10;
        float o = 50;


        return ( (r*z/(-z-x)+r ));
        /*

        double r2 = r*r;
        double z2 = z*z;
        double x2 = x*x;
        return x2*x2 + x2*(2*z*o - r2 + (z-o)*(z-o)) + z2*(o*o-r2);
        */
    }

    private double scale(double dist) {
        if (dist < 90 ) // r - z
            return fDeform(dist)/dist;
        else return 1;
    }

    protected void generatePolygons()
    {
        elements = new ArrayList<MyPolygon>();
        // dimension dans laquelle s'inscrit un polygone
        w = (originalSize[0]) / nb;
        h = (originalSize[1]) / nb;
        // création de tous les polygones
        for(int i = 0; i < nb; i++)
        {
            for(int j = 0; j < nb; j++) {
                MyPolygon p = new MyPolygon();
                float dx = w * i;
                float dy = h * j;

                p.addPoint(dx, dy, i+j);
                p.addPoint(dx, dy + h, i+j);
                p.addPoint(dx + w, dy + h, i+j);
                p.addPoint(dx + w, dy, i+j);
                p.color = Color.BLACK;
                // ou une autre couleur qui dépend de i et de j
                elements.add(p);
            }
        }
    }

    public int getCentreX() {
        return (int) x;
    }
    public int getCentreY() {
        return (int) y;
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                deform(x, y);
                return true;
            } if(motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                deform(x, y);
                return true;
            } else {
                generatePolygons();
                invalidate();
                return false;
            }
        }
    }

    public void setXY(float x, float y) {
        if (x>0 && x<originalSize[0] && y>0 && y<originalSize[1]) {
            generatePolygons();
            deform(x, y);
        }
    }

    public Point getRandomPolygon() {
        int x = (int) (Math.random() * elements.size());
        return elements.get(x).getPoints()[0];
    }
    public int getRandomPolygonInt() {
        int x = (int) (Math.random() * elements.size());
        return x;
    }
    // public Point moveSelection(String direction) {
    public Point moveSelection(String direction) {
        if(direction.equals("gauche") && currentRectangle>=nb) {
            currentRectangle -= nb;
        } else if (direction.equals("droite") && currentRectangle<=elements.size()-nb-1) {
            currentRectangle += nb;
        }else if (direction.equals("bas") && currentRectangle<=elements.size()-2) {
            currentRectangle += 1;
        }else if (direction.equals("haut") && currentRectangle>=1) {
            currentRectangle -= 1;
        }
        Log.e("currentRectangle", currentRectangle + " "+ elements.get(currentRectangle).getPoints()[0].x + " "+ elements.get(currentRectangle).getPoints()[0].y);
        invalidate();
        return elements.get(currentRectangle).getPoints()[0];
    }

    public float getWRectangle() {
        return w;
    }

    public float getHRectangle() {
        return h;
    }
}
