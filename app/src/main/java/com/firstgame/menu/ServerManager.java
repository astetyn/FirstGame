package com.firstgame.menu;

import com.firstgame.server.ServerGateway;

public class ServerManager {

    private MainActivity activity;
    private int requiredPlayers;
    private ServerGateway serverGateway;

    public ServerManager(int players, MainActivity activity){

        this.activity = activity;
        requiredPlayers = players;
        this.serverGateway = new ServerGateway();

    }

}
