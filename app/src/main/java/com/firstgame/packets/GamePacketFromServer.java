package com.firstgame.packets;

import com.firstgame.game.Tile;
import com.firstgame.game.server.PlayerLite;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class GamePacketFromServer implements Serializable {

    private List<PlayerLite> players;
    private Set<Tile> activeTiles;
    private double secondsToEnd;

    public GamePacketFromServer(List<PlayerLite> players, Set<Tile> activeTiles, double secondsToEnd){
        this.players = players;
        this.activeTiles = activeTiles;
        this.secondsToEnd  = secondsToEnd;
    }

    public List<PlayerLite> getPlayers(){
        return players;
    }

    public Set<Tile> getActiveTiles(){
        return this.activeTiles;
    }

    public double getSecondsToEnd() {
        return secondsToEnd;
    }
}
