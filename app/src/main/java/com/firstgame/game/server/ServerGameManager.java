package com.firstgame.game.server;

import android.graphics.Color;
import android.widget.FrameLayout;

import com.firstgame.R;
import com.firstgame.game.ConnectedPlayer;
import com.firstgame.game.EnginePhysics;
import com.firstgame.game.Game;
import com.firstgame.game.GameManager;
import com.firstgame.game.Player;
import com.firstgame.game.World;
import com.firstgame.game.math.Location;
import com.firstgame.game.math.RGBColor;
import com.firstgame.menu.MainActivity;
import com.firstgame.packets.GamePacketFromClient;
import com.firstgame.packets.GamePacketFromServer;
import com.firstgame.packets.GameStartData;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import processing.android.PFragment;

public class ServerGameManager extends GameManager {

    private final int GAME_DURATION = 60;

    private Game game;
    private int requiredPlayers;
    private ServerGateway serverGateway;
    private World readyWorld;
    private List<ConnectedPlayer> connectedPlayers;
    private Player player;
    private float remainingTime;
    private EnginePhysics enginePhysics;

    public ServerGameManager(int players, MainActivity activity){
        super(activity);
        requiredPlayers = players;
        readyWorld = new World(20,20);
        connectedPlayers = new ArrayList<>();
        this.serverGateway = new ServerGateway(this);
        remainingTime = GAME_DURATION;
    }

    @Override
    public void onLogicLoop(float timePassed) {
        enginePhysics.loop(timePassed,getAccelValues());
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>(connectedPlayers);
        players.add(player);
        return players;
    }

    @Override
    public void onStop() {
        if(game!=null){
            game.onStop();
        }
    }

    @Override
    public void onDestroy() {
        if(game!=null){
            game.onDestroy();
        }
    }

    public void playerWantsToConnect(InetAddress address){

        for(ConnectedPlayer cc : connectedPlayers){
            if(cc.getAddress().toString().equals(address.toString())){
                return;
            }
        }
        int UID = connectedPlayers.size()+1;
        RGBColor color = new RGBColor(50,50,200);
        connectedPlayers.add(new ConnectedPlayer(new Location(50,50),color,UID,address));
        getActivity().getWaitRoomLog().setText("Waiting for players: "+(connectedPlayers.size()+1)+"/"+requiredPlayers);

        if(connectedPlayers.size()+1==requiredPlayers){

            //TODO nastavit farby a lokacie hracom na zaciatku
            player = new Player(new Location(50,50),new RGBColor(0,0,0),0);

            List<Player> players = new ArrayList<>();
            List<PlayerLite> playersLite = new ArrayList<>();

            players.add(player);
            playersLite.add(new PlayerLite(player));

            for(ConnectedPlayer cp : connectedPlayers){
                players.add(cp);
                playersLite.add(new PlayerLite(cp));
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(int i = 0;i<connectedPlayers.size();i++){
                GameStartData gsd = new GameStartData(i+1,players,readyWorld);
                serverGateway.sendObject(connectedPlayers.get(i).getAddress(),gsd);
            }

            enginePhysics = new EnginePhysics(readyWorld, player, new ArrayList<>(connectedPlayers));
            game = new Game(this, readyWorld, player);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getActivity().setContentView(R.layout.game);

                    FrameLayout frame = getActivity().findViewById(R.id.gameFrame);
                    PFragment fragment = new PFragment(game);
                    fragment.setView(frame, getActivity());
                }
            });

            GamePacketFromServer gps = new GamePacketFromServer(playersLite,readyWorld.getTileMap(),GAME_DURATION);
            serverGateway.setGamePacketToBeSent(gps);

            serverGateway.setReadyToStart(true);
        }
    }

    public void onGamePacketReceived(GamePacketFromClient gpc){  //unsynchronized

        //process packet from client

    }

    public int getRequiredPlayers() {
        return requiredPlayers;
    }

    public World getReadyWorld(){
        return readyWorld;
    }

    public List<ConnectedPlayer> getConnectedPlayers() {
        return connectedPlayers;
    }
}
