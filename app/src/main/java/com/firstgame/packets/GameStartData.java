package com.firstgame.packets;

import com.firstgame.game.Player;
import com.firstgame.game.World;

import java.io.Serializable;
import java.util.List;

public class GameStartData implements Serializable {

    private int UID;
    private List<Player> players;
    private World world;

    public GameStartData(int UID, List<Player> players, World world) {
        this.UID = UID;
        this.players = players;
        this.world = world;
    }

    public int getUID() {
        return UID;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public World getWorld() {
        return world;
    }
}
