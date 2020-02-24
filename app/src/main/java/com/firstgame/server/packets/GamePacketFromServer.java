package com.firstgame.server.packets;

import com.firstgame.game.PlayerLite;
import com.firstgame.game.Tile;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;

public class GamePacketFromServer implements Serializable {

    private List<PlayerLite> players;
    private LinkedHashSet<Tile> updatedTiles;
    private double secondsToEnd;

    public GamePacketFromServer(List<PlayerLite> players, LinkedHashSet<Tile> updatedTiles, double secondsToEnd){
        this.players = players;
        this.updatedTiles = updatedTiles;
        this.secondsToEnd  = secondsToEnd;
    }

    public List<PlayerLite> getPlayers(){
        return players;
    }

    public LinkedHashSet<Tile> getUpdatedTiles() {
        return updatedTiles;
    }

    public double getSecondsToEnd() {
        return secondsToEnd;
    }
}
