package com.firstgame.packets;

import com.firstgame.game.Tile;
import com.firstgame.game.server.PlayerLite;

import java.io.Serializable;
import java.util.List;

public class GamePacketFromServer implements Serializable {

    private List<PlayerLite> players;
    private Tile[][] map;
    private double secondsToEnd;

    public GamePacketFromServer(GamePacketFromServer gps){
        this.players = gps.getPlayers();
        this.map = gps.getMap();
        this.secondsToEnd = gps.getSecondsToEnd();
    }

    public GamePacketFromServer(List<PlayerLite> players, Tile[][] map, double secondsToEnd){
        this.players = players;
        this.map = map;
        this.secondsToEnd  = secondsToEnd;
    }

    public List<PlayerLite> getPlayers(){
        return players;
    }

    public Tile[][] getMap(){
        return this.map;
    }

    public double getSecondsToEnd() {
        return secondsToEnd;
    }
}
