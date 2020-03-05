package com.firstgame.game.server;

import com.firstgame.game.ConnectedPlayer;
import com.firstgame.game.Player;
import com.firstgame.game.math.Location;
import com.firstgame.game.math.Vector;

import java.io.Serializable;

public class PlayerLite implements Serializable {

    private Location location;
    private Vector velocity;

    public PlayerLite(ConnectedPlayer cp){
        location = cp.getLocation();
        velocity = cp.getVelocity();
    }

    public PlayerLite(Player player){
        this.location = player.getLocation();
        this.velocity = player.getVelocity();
    }

    public Location getLocation(){
        return location;
    }

    public Vector getVelocity(){
        return velocity;
    }

}
