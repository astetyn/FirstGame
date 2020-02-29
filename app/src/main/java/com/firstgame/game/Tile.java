package com.firstgame.game;

import com.firstgame.game.math.Location;
import com.firstgame.game.math.RGBColor;
import com.firstgame.game.math.TilePosition;

import java.io.Serializable;

public class Tile implements Serializable {

    private boolean solid;
    private Location location;
    private TilePosition position;
    private RGBColor color;

    public Tile(boolean solid, Location location, TilePosition position, RGBColor color){
        this.solid = solid;
        this.location = location;
        this.position = position;
        this.color = color;
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

    public RGBColor getColor() {
        return color;
    }

    public void setColor(RGBColor color) {
        this.color = color;
    }
}
