package com.firstgame.server.packets;

import java.io.Serializable;

public class UpdateWaitingPacketFromServer implements Serializable {

    private int playersConnected;

    public UpdateWaitingPacketFromServer(int playersConnected){
        this.playersConnected = playersConnected;
    }

    public int getPlayersConnected() {
        return playersConnected;
    }
}
