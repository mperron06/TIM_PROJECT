package com.morgane_perron.findmyduck;

/**
 * Created by zz on 03/11/14.
 */
public class DataGamer {
    private String interactionType;
    private long time;
    private int nbCarre;

    public DataGamer(String interactionType, long time, int nbCarre) {
        this.interactionType = interactionType;
        this.time = time;
        this.nbCarre = nbCarre;
    }

    public String toString() {
        return interactionType + ":" + time + ":" + nbCarre + ";\n";
    }

}
