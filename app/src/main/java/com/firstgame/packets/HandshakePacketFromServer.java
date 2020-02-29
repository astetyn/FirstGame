package com.firstgame.packets;

import com.firstgame.game.World;
import com.firstgame.game.math.RGBColor;

import java.io.Serializable;

public class HandshakePacketFromServer implements Serializable {

    private int playersConnected;
    private int playersToStart;

    public HandshakePacketFromServer(int playersConnected, int playersToStart){
        this.playersConnected = playersConnected;
        this.playersToStart = playersToStart;
    }

    public int getPlayersConnected() {
        return playersConnected;
    }

    public int getPlayersToStart() {
        return playersToStart;
    }
}
