package com.firstgame.game.server;

import android.os.Handler;
import android.widget.FrameLayout;

import com.firstgame.R;
import com.firstgame.game.ConnectedPlayer;
import com.firstgame.game.EnginePhysics;
import com.firstgame.game.GameRenderer;
import com.firstgame.game.GameManager;
import com.firstgame.game.Player;
import com.firstgame.game.Tile;
import com.firstgame.game.World;
import com.firstgame.game.math.Location;
import com.firstgame.game.math.RGBColor;
import com.firstgame.menu.MainActivity;
import com.firstgame.packets.GamePacketFromClient;
import com.firstgame.packets.GamePacketFromServer;
import com.firstgame.packets.GameStartData;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import processing.android.PFragment;

public class ServerGameManager extends GameManager {

    private final int GAME_DURATION = 60;

    private GameRenderer gameRenderer;
    private int requiredPlayers;
    private ServerGateway serverGateway;
    private World world;
    private List<ConnectedPlayer> connectedPlayers;
    private Player player;
    private float remainingTime;
    private EnginePhysics enginePhysics;
    Set<Tile> coloredTiles;

    public ServerGameManager(int players, MainActivity activity){
        super(activity);
        requiredPlayers = players;
        world = new World(20,20);
        connectedPlayers = new ArrayList<>();
        this.serverGateway = new ServerGateway(this);
        Thread t = new Thread(serverGateway);
        t.start();
        remainingTime = GAME_DURATION;
    }

    @Override
    public void onLogicLoop(float timePassed) {
        coloredTiles = enginePhysics.loop(timePassed,getAccelValues());
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>(connectedPlayers);
        players.add(player);
        return players;
    }

    @Override
    public void onStop() {
        if(gameRenderer !=null){
            gameRenderer.onStop();
        }
    }

    @Override
    public void onDestroy() {
        if(gameRenderer !=null){
            gameRenderer.onDestroy();
        }
    }

    public GamePacketFromServer createServerPacket(){
        Set<Tile> tilesCopy;
        if(coloredTiles!=null) {
            tilesCopy = new HashSet<>(coloredTiles);
        }else{
            tilesCopy = new HashSet<>();
        }
        List<PlayerLite> playersLite = new ArrayList<>();

        synchronized (connectedPlayers) {
            for (ConnectedPlayer cp : connectedPlayers) {
                playersLite.add(new PlayerLite(cp));
            }
        }

        synchronized (player) {
            playersLite.add(new PlayerLite(player));
        }

        return new GamePacketFromServer(playersLite,tilesCopy,remainingTime);
    }

    public void playerWantsToConnect(InetAddress address){

        for(ConnectedPlayer cc : connectedPlayers){
            if(cc.getAddress().toString().equals(address.toString())){
                return;
            }
        }

        int UID = connectedPlayers.size();
        RGBColor color = new RGBColor(50,50,200);
        connectedPlayers.add(new ConnectedPlayer(new Location(50,50),color,UID,address));
        getActivity().getWaitRoomLog().setText("Waiting for players: "+(connectedPlayers.size()+1)+"/"+requiredPlayers);

        if(connectedPlayers.size()+1!=requiredPlayers) {
            return;
        }

        //TODO nastavit farby a lokacie hracom na zaciatku
        player = new Player(new Location(50,50),new RGBColor(0,0,0),0);

        for(int i = 0;i<connectedPlayers.size();i++){
            ConnectedPlayer p = connectedPlayers.get(i);
            List<Player> enemies = new ArrayList<>();
            for(Player e : connectedPlayers){
                if(e!=p){
                    enemies.add(e);
                }
            }
            enemies.add(player);
            GameStartData gsd = new GameStartData(p,enemies, world);
            serverGateway.sendObject(p.getAddress(),gsd);
        }

        GameManager gm = this;

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {

                enginePhysics = new EnginePhysics(world, player, new ArrayList<>(connectedPlayers));
                gameRenderer = new GameRenderer(gm, world);

                gm.getActivity().runOnUiThread(() -> {
                    getActivity().setContentView(R.layout.game);

                    FrameLayout frame = getActivity().findViewById(R.id.gameFrame);
                    PFragment fragment = new PFragment(gameRenderer);
                    fragment.setView(frame, getActivity());
                });
                serverGateway.setReadyToStart(true);
            }
        }, 2000);
    }

    public void onGamePacketReceived(GamePacketFromClient gpc){

        Player p = connectedPlayers.get(gpc.getClientID());
        p.setLocation(gpc.getPlayer().getLocation());
        p.setVelocity(gpc.getPlayer().getVelocity());

    }

    public int getRequiredPlayers() {
        return requiredPlayers;
    }

    public World getWorld(){
        return world;
    }

    public List<ConnectedPlayer> getConnectedPlayers() {
        return connectedPlayers;
    }
}
