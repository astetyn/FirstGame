package com.firstgame.game.server;

import com.firstgame.game.ConnectedPlayer;
import com.firstgame.game.math.RGBColor;
import com.firstgame.packets.GamePacketFromClient;
import com.firstgame.packets.GamePacketFromServer;
import com.firstgame.packets.HandshakePacketFromClient;
import com.firstgame.packets.HandshakePacketFromServer;
import com.firstgame.game.Connectable;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerGateway implements Connectable, Runnable {

    private UDPReceiver udpReceiver;
    private ServerGameManager serverManager;
    private GamePacketFromServer gps;
    boolean readyToStart;

    public ServerGateway(ServerGameManager serverManager){
        this.serverManager = serverManager;
        readyToStart = false;

        try {
            udpReceiver = new UDPReceiver(this, InetAddress.getByName("0.0.0.0"), DEFAULT_PORT);
            Thread t = new Thread(udpReceiver);
            t.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets! ");

        try {

            while(true){

                if(!readyToStart){
                    Thread.sleep(100);
                    continue;
                }

                GamePacketFromServer gps2;
                synchronized (gps) {
                    gps2 = new GamePacketFromServer(gps);
                }

                for(ConnectedPlayer cc : serverManager.getConnectedPlayers()){
                    sendObject(cc.getAddress(), gps2);
                }

                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setGamePacketToBeSent(GamePacketFromServer gps2){
        if(gps!=null) {
            synchronized (gps) {
                gps = gps2;
            }
        }else{
            gps = gps2;
        }
    }

    @Override
    public void onObjectReceive(Object o, DatagramPacket receivedPacket) {

        if(o instanceof HandshakePacketFromClient){

            System.out.println("Successful handshake on adress: "+receivedPacket.getAddress());

            InetAddress clientAddress = receivedPacket.getAddress();

            int pc = serverManager.getConnectedPlayers().size();
            int rp = serverManager.getRequiredPlayers();
            sendObject(clientAddress, new HandshakePacketFromServer(pc, rp));

            serverManager.playerWantsToConnect(receivedPacket.getAddress());
        }else if(o instanceof GamePacketFromClient){

            GamePacketFromClient gpc = (GamePacketFromClient) o;
            serverManager.onGamePacketReceived(gpc);

        }
    }

    public void terminate(){
        if(udpReceiver!=null) {
            udpReceiver.terminate();
        }
    }

    public void setReadyToStart(boolean b){
        readyToStart = b;
    }
}
