package com.firstgame.game.server;

import com.firstgame.game.ConnectedPlayer;
import com.firstgame.game.Player;
import com.firstgame.game.math.Location;

import java.io.Serializable;

public class PlayerLite implements Serializable {

    private Location location;

    public PlayerLite(Location location){
        this.location = location;
    }

    public PlayerLite(ConnectedPlayer cp){
        location = cp.getLocation();
    }

    public PlayerLite(PlayerLite playerLite){
        this.location = playerLite.getLocation().clone();
    }

    public PlayerLite(Player player){
        this.location = player.getLocation();
    }

    public Location getLocation(){
        return location;
    }
}
