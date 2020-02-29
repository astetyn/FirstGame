package com.firstgame.game.client;

import android.widget.FrameLayout;

import com.firstgame.R;
import com.firstgame.game.EnginePhysics;
import com.firstgame.game.Game;
import com.firstgame.game.GameManager;
import com.firstgame.game.Player;
import com.firstgame.game.World;
import com.firstgame.game.math.Location;
import com.firstgame.game.math.RGBColor;
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
    private Game game;
    private World world;
    private int playersToStart;
    private Player player;
    private List<Player> enemies;
    private EnginePhysics enginePhysics;

    public ClientGameManager(MainActivity activity){
        super(activity);
        activity.getWaitRoomLog().setText("Connecting to server.");

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

    public void onServerConnection(int playersConnected, int playersToStart){

        this.playersToStart = playersToStart;
        updatePlayersConnected(playersConnected);

    }

    public void onGameStart(GameStartData gameStartData){

        player = gameStartData.getPlayers().get(gameStartData.getUID());
        world = gameStartData.getWorld();

        enemies = gameStartData.getPlayers();
        enemies.remove(player);

        System.out.println("11111"+player);

        enginePhysics = new EnginePhysics(world, player, enemies);

        game = new Game(this, world, player);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().setContentView(R.layout.game);

                FrameLayout frame = getActivity().findViewById(R.id.gameFrame);
                PFragment fragment = new PFragment(game);
                fragment.setView(frame, getActivity());
            }
        });
    }

    public void onGamePacketReceived(GamePacketFromServer gamePacketFromServer){ //unsynchronized

        //process packet every time when packet comes

        GamePacketFromClient gpc = new GamePacketFromClient(new PlayerLite(player),player.getUniqueID());
        clientGateway.setGamePacketToBeSent(gpc);
    }

    public void updatePlayersConnected(int playersConnected){
        getActivity().runOnUiThread(() -> getActivity().getWaitRoomLog().setText("Connected. Waiting for players: "+playersConnected+1+"/"+playersToStart));
    }
}
