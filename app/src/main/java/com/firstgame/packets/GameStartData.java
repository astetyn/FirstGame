package com.firstgame.packets;

import com.firstgame.game.Player;
import com.firstgame.game.World;

import java.io.Serializable;
import java.util.List;

public class GameStartData implements Serializable {

    private Player player;
    private List<Player> enemies;
    private World world;

    public GameStartData(Player player, List<Player> enemies, World world) {
        this.player = player;
        this.enemies = enemies;
        this.world = world;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Player> getEnemies() {
        return enemies;
    }

    public World getWorld() {
        return world;
    }

}
