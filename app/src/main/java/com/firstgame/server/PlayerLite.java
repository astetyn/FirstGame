package com.firstgame.server;

import com.firstgame.game.math.Location;

import java.io.Serializable;

public class PlayerLite implements Serializable {

    private Location location;

    public PlayerLite(Location location){
        this.location = location;
    }

    public PlayerLite(PlayerLite playerLite){
        this.location = playerLite.getLocation().clone();
    }

    public Location getLocation(){
        return location;
    }
}
