package com.firstgame.packets;

import com.firstgame.game.ConnectedPlayer;

import java.io.Serializable;
import java.util.List;

public class EndPacket implements Serializable {

    private List<ConnectedPlayer> winners;

    public EndPacket(List<ConnectedPlayer> winnners){
        this.winners = winnners;
    }

    public List<ConnectedPlayer> getWinners(){
        return winners;
    }

}
