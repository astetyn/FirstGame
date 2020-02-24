package com.firstgame.game;

import com.firstgame.game.math.Location;
import com.firstgame.game.math.RGBColor;

import java.io.Serializable;

public class Enemy extends PlayerLite implements Serializable {

    public Enemy(Location location, int sizeX, int sizeY, RGBColor color, int uniqueID) {
        super(location, sizeX, sizeY, color, uniqueID);
    }

}
