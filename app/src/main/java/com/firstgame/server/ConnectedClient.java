package com.firstgame.server;

import com.firstgame.game.PlayerLite;

import java.net.InetAddress;

public class ConnectedClient {

    private PlayerLite playerLite;
    private InetAddress address;

    public ConnectedClient(PlayerLite playerLite, InetAddress address){
        this.playerLite = playerLite;
        this.address = address;
    }

    public PlayerLite getPlayerLite() {
        return playerLite;
    }

    public InetAddress getAddress() {
        return address;
    }
}
