package com.firstgame.game.math;

import java.io.Serializable;

public class RGBColor implements Serializable {

    private short r,g,b;

    public RGBColor(short r, short g, short b){
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public short getR() {
        return r;
    }

    public short getG() {
        return g;
    }

    public short getB() {
        return b;
    }
}
