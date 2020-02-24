package com.firstgame.server.packets;

import com.firstgame.game.World;

import java.io.Serializable;

public class HandshakePacketFromServer implements Serializable {

    private int clientID;
    private World world;
    private int playersConnected;
    private int playersToStart;

    public HandshakePacketFromServer(int clientID, World world, int playersConnected, int playersToStart){
        this.clientID = clientID;
        this.world = world;
        this.playersConnected = playersConnected;
        this.playersToStart = playersToStart;
    }

    public int getClientID() {
        return clientID;
    }

    public World getWorld() {
        return world;
    }

    public int getPlayersConnected() {
        return playersConnected;
    }

    public int getPlayersToStart() {
        return playersToStart;
    }
}
