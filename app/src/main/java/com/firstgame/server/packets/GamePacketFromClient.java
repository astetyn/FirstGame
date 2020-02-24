package com.firstgame.server.packets;

import com.firstgame.server.PlayerLite;

import java.io.Serializable;

public class GamePacketFromClient implements Serializable {

    private PlayerLite player;
    private int clientID;

    public GamePacketFromClient(PlayerLite player, int clientID){
        this.player = player;
        this.clientID = clientID;
    }

    public GamePacketFromClient(GamePacketFromClient gpc){
        this.player = new PlayerLite(gpc.getPlayer());
        this.clientID = gpc.getClientID();
    }

    public PlayerLite getPlayer(){
        return player;
    }

    public int getClientID() {
        return clientID;
    }

}
