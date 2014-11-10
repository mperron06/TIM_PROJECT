package com.morgane_perron.findmyduck;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Class SoundView
 */
public class SoundView extends View {
    private ArrayList<MyPolygon> elements;
    private int []originalSize = new int[2];
    private ArrayList<Integer> listeCarreVisite;
    private int nb = 10;
    private Paint paint;
    private float w;
    private float h;

    private int currentRectangle;

    public SoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paint = new Paint();
        this.listeCarreVisite = new ArrayList<Integer>();
        originalSize[0] = 1000;
        originalSize[1] = 2000;

        currentRectangle = 0;
        this.listeCarreVisite.add(currentRectangle);
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
        paintAllRectangleVisited(g);
        paint.setColor(Color.BLACK);
        g.drawRect(elements.get(currentRectangle).getOnePoint(0).x, elements.get(currentRectangle).getOnePoint(0).y, elements.get(currentRectangle).getOnePoint(2).x, elements.get(currentRectangle).getOnePoint(2).y, paint);
        this.listeCarreVisite.add(currentRectangle);

    }

    /**
     * Colorie la liste des carrés visités en noir
     * @param g
     */
    private void paintAllRectangleVisited(Canvas g) {
        paint.setColor(Color.BLACK);
        for (int i=0; i<listeCarreVisite.size(); i++) {
            int toDraw = this.listeCarreVisite.get(i);
            g.drawRect(elements.get(toDraw).getOnePoint(0).x, elements.get(toDraw).getOnePoint(0).y, elements.get(toDraw).getOnePoint(2).x, elements.get(toDraw).getOnePoint(2).y, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        originalSize[0] = getMeasuredWidth();
        originalSize[1] = getMeasuredHeight();

        generatePolygons();

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
        invalidate();
    }

    /**
     * Retourne un point au hasard du tableau
     * sauf la case (0,0)
     * @return Point
     */
    public Point getRandomPolygon() {
        int x = (int) (Math.random() * elements.size());
        if(elements.get(x).getPoints()[0].x == 0 && elements.get(x).getPoints()[0].y == 0){
            // the duck can't be in the initial case (0,0)
            return getRandomPolygon();
        }
        return elements.get(x).getPoints()[0];
    }

    /**
     * Retourne le point selon la direction choisie
     * @param direction
     * @return Point
     */
    public Point moveSelection(String direction) {
        boolean ok = false;
        if(direction.equals("gauche") && currentRectangle>=nb) {
            currentRectangle -= nb;
            ok = true;
        } else if (direction.equals("droite") && currentRectangle<=elements.size()-nb-1) {
            ok = true;
            currentRectangle += nb;
        }else if (direction.equals("bas") && currentRectangle<=elements.size()-2) {
            ok = true;
            currentRectangle += 1;
        }else if ((direction.equals("haut") || direction.equals("Oh") || direction.equals("o") || direction.equals("eau") || direction.equals("lol")) && currentRectangle>=1) {
            ok = true;
            currentRectangle -= 1;
        }
        if(ok) {
            invalidate();
            return elements.get(currentRectangle).getPoints()[0];
        }
        return null;
    }

    /**
     * Retourne la largeur d'un case du tableau
     * @return float
     */
    public float getWRectangle() {
        return w;
    }

    /**
     * Retourne la hauteur d'un case du tableau
     * @return float
     */
    public float getHRectangle() {
        return h;
    }

    /**
     * définit le nombre de cases dans le tableau nb x nb
     * @param nb
     */
    public void setNb(int nb) {
        this.nb = nb;
    }

    /**
     * Vide la liste des carrés visités
     */
    public void clear(){
        this.listeCarreVisite.clear();
        this.currentRectangle = 0;
    }

    /**
     * Retourne le point en haut à gauche de la case qui contient le point en paramètre
     * @param p
     * @return
     */
    public Point caseContainsPoint(Point p){
        for(int i=0; i<elements.size(); i++) {
            if(p.x >= elements.get(i).getPoints()[0].x && p.x <= elements.get(i).getPoints()[2].x){
                if(p.y >= elements.get(i).getPoints()[0].y && p.y <= elements.get(i).getPoints()[2].y){
                    this.listeCarreVisite.add(i);
                    currentRectangle = i;
                    invalidate();
                    return elements.get(i).getPoints()[0];
                }
            }
        }
        return null;
    }
}
