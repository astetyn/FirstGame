package com.firstgame.menu;

import com.firstgame.game.Game;
import com.firstgame.game.World;
import com.firstgame.server.ClientGateway;
import com.firstgame.server.PlayerLite;
import com.firstgame.server.packets.GamePacketFromClient;
import com.firstgame.server.packets.GamePacketFromServer;

public class ClientManager {

    private MainActivity activity;
    private ClientGateway clientGateway;
    private Game game;
    private World readyWorld;
    private int playersToStart;

    public ClientManager(MainActivity activity){

        this.activity =  activity;
        clientGateway = new ClientGateway(this);
        activity.getWaitRoomLog().setText("Connecting to server.");

    }

    public void onServerConnection(World world, final int playersConnected, final int playersToStart){

        this.readyWorld = world;
        this.playersToStart = playersToStart;
        updatePlayersConnected(playersConnected);

    }

    public void onGamePacketReceived(GamePacketFromServer gamePacketFromServer){

        if(game==null){
            game = new Game(activity, readyWorld);
        }

        //process packet

        GamePacketFromClient gpc = new GamePacketFromClient(new PlayerLite(game.getPlayer().getLocation()),clientGateway.getClientID());
        clientGateway.setGamePacketToBeSent(gpc);
    }

    public void updatePlayersConnected(final int playersConnected){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getWaitRoomLog().setText("Connected. Waiting for players: "+playersConnected+1+"/"+playersToStart);
            }
        });
    }

}
