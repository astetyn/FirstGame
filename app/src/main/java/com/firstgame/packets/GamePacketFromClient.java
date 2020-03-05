package com.firstgame.packets;

import com.firstgame.game.server.PlayerLite;

import java.io.Serializable;

public class GamePacketFromClient implements Serializable {

    private PlayerLite player;
    private int clientID;

    public GamePacketFromClient(PlayerLite player, int clientID){
        this.player = player;
        this.clientID = clientID;
    }

    public PlayerLite getPlayer(){
        return player;
    }

    public int getClientID() {
        return clientID;
    }

}
