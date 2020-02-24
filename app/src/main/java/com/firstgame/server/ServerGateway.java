package com.firstgame.server;

import com.firstgame.game.Enemy;
import com.firstgame.game.PlayerLite;
import com.firstgame.game.Player;
import com.firstgame.game.Tile;
import com.firstgame.game.World;
import com.firstgame.game.math.Location;
import com.firstgame.game.math.RGBColor;
import com.firstgame.menu.MainActivity;
import com.firstgame.server.packets.EndPacket;
import com.firstgame.server.packets.GamePacketFromClient;
import com.firstgame.server.packets.GamePacketFromServer;
import com.firstgame.server.packets.HandshakePacketFromClient;
import com.firstgame.server.packets.HandshakePacketFromServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ServerGateway implements Connectable, Runnable {

    private boolean readyToStart;
    private boolean gameEnded;
    private Player player;
    final private LinkedHashSet<Tile> updatedTiles;
    private UDPReceiver udpReceiver;
    private int requiredPlayers;
    private List<ConnectedClient> clients;
    private List<PlayerLite> enemies;
    private double secondsToEnd;
    private World world;
    private MainActivity activity;

    public ServerGateway(MainActivity activity){
        this.activity = activity;
        secondsToEnd = 0;
        gameEnded = false;
        this.requiredPlayers = requiredPlayers;
        readyToStart = false;
        this.player = player;
        this.updatedTiles = updatedTiles;
        clients = new ArrayList<>();
        enemies = new ArrayList<>();
        this.world = world;

        try {
            udpReceiver = new UDPReceiver(this, InetAddress.getByName("0.0.0.0"), DEFAULT_PORT);
            Thread t = new Thread(udpReceiver);
            t.start();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void updateSeconds(double secondsToEnd){
        this.secondsToEnd = secondsToEnd;
        if(secondsToEnd<=0){
            EndPacket ep = new EndPacket(new ArrayList<ConnectedClient>());
            for(ConnectedClient cc : clients){
                sendObject(cc.getAddress(), ep);
            }
            gameEnded = true;
            terminate();
            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameActivity.onGameEnd();
                }
            });
        }
    }

    @Override
    public boolean isReadyToStart() {
        return readyToStart;
    }

    @Override
    public void onObjectReceive(Object o, DatagramPacket receivedPacket) {

        if(o instanceof HandshakePacketFromClient){
            InetAddress clientAddress = receivedPacket.getAddress();
            addNewClient(clientAddress);
            System.out.println("Successful handshake on adress: "+receivedPacket.getAddress());
            sendObject(clientAddress, new HandshakePacketFromServer(clients.size()-1, world));

            if(clients.size()==requiredPlayers-1&&!readyToStart){
                for(ConnectedClient cc : clients){
                    sendObject(cc.getAddress(), new ConfirmationPacketServer());
                }
                gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameActivity.changeToGameLayout();
                    }
                });
                readyToStart = true;
            }
        }else if(o instanceof GamePacketFromClient){

            GamePacketFromClient gpc = (GamePacketFromClient) o;

            PlayerLite e = gpc.getPlayer();
            ConnectedClient cc = clients.get(gpc.getClientID());
            final PlayerLite cp = cc.getPlayerLite();

            synchronized (cp) {
                cp.setLocation(e.getLocation());
                cp.setMovement(e.getMovement());
            }
        }
    }

    private boolean addNewClient(InetAddress address){
        for(ConnectedClient cc : clients){
            if(cc.getAddress().toString().equals(address.toString())){
                return false;
            }
        }
        Enemy e = new Enemy(new Location(-500, -500), 40, 40, new RGBColor((short)0,(short)200,(short)100),clients.size()+1);
        clients.add(new ConnectedClient(e, address));
        enemies.add(e);
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameActivity.addLineToStatus("New client connected.");
            }
        });
        return true;
    }

    public void terminate(){
        if(udpReceiver!=null) {
            udpReceiver.terminate();
        }
    }

    @Override
    public void run() {

        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameActivity.clearStatus();
                gameActivity.addLineToStatus("ServerGateway started, waiting for players...");
                gameActivity.addLineToStatus("Running on " + GameActivity.getLocalIPAdress());
            }
        });

        System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets! ");

        try {

            while(true){

                if(!readyToStart){
                    Thread.sleep(100);
                    continue;
                }

                if(gameEnded){
                    return;
                }

                LinkedHashSet<Tile> lhs;
                synchronized (updatedTiles) {
                    lhs = new LinkedHashSet<>(updatedTiles);
                    updatedTiles.clear();
                }

                for(ConnectedClient cc : clients){
                    List<PlayerLite> entities = new ArrayList<>();

                    for (ConnectedClient cc2 : clients) {
                        if (!cc.equals(cc2)) {
                            entities.add(cc2.getPlayerLite());
                        }
                    }
                    entities.add(player);
                    GamePacketFromServer gps = new GamePacketFromServer(entities, lhs, secondsToEnd);
                    sendObject(cc.getAddress(), gps);
                }

                Thread.sleep(50);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private synchronized void sendObject(InetAddress address, Object o) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            oos.writeObject(o);
            byte[] data = baos.toByteArray();

            DatagramPacket packet = new DatagramPacket(data, data.length, address, DEFAULT_PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<PlayerLite> getEnemies(){
        return enemies;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }
}
