package com.firstgame.game;

import com.firstgame.game.math.Location;
import com.firstgame.game.math.TilePosition;

import java.io.Serializable;

public class Tile implements Serializable {

    private boolean solid;
    private Location location;
    private TilePosition position;
    private ColorCode coloredCode;

    public Tile(boolean solid, Location location, TilePosition position){
        this.solid = solid;
        this.location = location;
        this.position = position;
        if(solid){
            coloredCode = ColorCode.SOLID;
        }else{
            coloredCode = ColorCode.DEFAULT;
        }
    }

    public boolean isSolid() {
        return solid;
    }

    public Location getLocation() {
        return location;
    }

    public TilePosition getPosition() {
        return position;
    }

    public ColorCode getColoredCode() {
        return coloredCode;
    }

    public void setColoredCode(ColorCode coloredCode) {
        this.coloredCode = coloredCode;
    }
}
