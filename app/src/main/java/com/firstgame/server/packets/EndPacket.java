package com.firstgame.server.packets;

import com.firstgame.server.ConnectedClient;

import java.io.Serializable;
import java.util.List;

public class EndPacket implements Serializable {

    private List<ConnectedClient> winners;

    public EndPacket(List<ConnectedClient> winnners){
        this.winners = winnners;
    }

    public List<ConnectedClient> getWinners(){
        return winners;
    }

}
