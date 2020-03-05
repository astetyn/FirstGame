package com.firstgame.game.client;

import android.widget.FrameLayout;

import com.firstgame.R;
import com.firstgame.game.EnginePhysics;
import com.firstgame.game.GameRenderer;
import com.firstgame.game.GameManager;
import com.firstgame.game.Player;
import com.firstgame.game.Tile;
import com.firstgame.game.World;
import com.firstgame.menu.MainActivity;
import com.firstgame.game.server.PlayerLite;
import com.firstgame.packets.GamePacketFromClient;
import com.firstgame.packets.GamePacketFromServer;
import com.firstgame.packets.GameStartData;

import java.util.ArrayList;
import java.util.List;

import processing.android.PFragment;

public class ClientGameManager extends GameManager {

    private ClientGateway clientGateway;
    private GameRenderer gameRenderer;
    private World world;
    private int playersToStart;
    private Player player;
    private List<Player> enemies;
    private EnginePhysics enginePhysics;
    private boolean startDataCame;

    public ClientGameManager(MainActivity activity){
        super(activity);
        activity.getWaitRoomLog().setText("Connecting to server.");
        startDataCame = false;

        clientGateway = new ClientGateway(this);
        Thread t = new Thread(clientGateway);
        t.start();

    }

    @Override
    public void onLogicLoop(float timePassed) {
        enginePhysics.loop(timePassed,super.getAccelValues());
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>(enemies);
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

    public GamePacketFromClient createClientPacket(){
        PlayerLite playerLite;
        synchronized (player) {
            playerLite = new PlayerLite(player);
        }
        return new GamePacketFromClient(playerLite, player.getUniqueID());
    }

    public void onServerConnection(int playersConnected, int playersToStart){

        this.playersToStart = playersToStart;
        updatePlayersConnected(playersConnected);

    }

    public void onGameStart(GameStartData gameStartData){

        player = gameStartData.getPlayer();
        world = gameStartData.getWorld();
        enemies = gameStartData.getEnemies();

        enginePhysics = new EnginePhysics(world, player, enemies);
        gameRenderer = new GameRenderer(this, world);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().setContentView(R.layout.game);

                FrameLayout frame = getActivity().findViewById(R.id.gameFrame);
                PFragment fragment = new PFragment(gameRenderer);
                fragment.setView(frame, getActivity());
            }
        });
        startDataCame = true;
    }

    public void onGamePacketReceived(GamePacketFromServer gamePacketFromServer){

        if(!startDataCame){
            return;
        }

        int i = 0;
        boolean b = false;

        for (PlayerLite pl : gamePacketFromServer.getPlayers()) {

            if(player.getUniqueID()==i){
                i++;
                b = true;
                continue;
            }
            Player enemy;

            if(b){
                enemy = enemies.get(i-1);
            }else{
                enemy = enemies.get(i);
            }
            enemy.setLocation(pl.getLocation());
            enemy.setVelocity(pl.getVelocity());
            i++;
        }
        for(Tile t : gamePacketFromServer.getActiveTiles()){
            world.getTileMap()[t.getPosition().getX()][t.getPosition().getY()] = t;
        }
    }

    public void updatePlayersConnected(int playersConnected){
        getActivity().runOnUiThread(() -> getActivity().getWaitRoomLog().setText("Connected. Waiting for players: "+(playersConnected+1)+"/"+playersToStart));
    }
}
