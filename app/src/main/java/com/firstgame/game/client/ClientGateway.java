package com.firstgame.game.client;

import com.firstgame.game.Connectable;
import com.firstgame.game.server.UDPReceiver;
import com.firstgame.packets.EndPacket;
import com.firstgame.packets.GamePacketFromClient;
import com.firstgame.packets.GamePacketFromServer;
import com.firstgame.packets.GameStartData;
import com.firstgame.packets.HandshakePacketFromClient;
import com.firstgame.packets.HandshakePacketFromServer;
import com.firstgame.packets.UpdateWaitingPacketFromServer;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class ClientGateway implements Connectable, Runnable {

    private ClientGameManager clientManager;
    private InetAddress serverAddress;
    private UDPReceiver udpReceiver;
    private boolean connected;
    private GamePacketFromClient gamePacketFromClient;

    public ClientGateway(ClientGameManager clientManager){
        connected = false;
        this.clientManager = clientManager;
        udpReceiver = new UDPReceiver(this, DEFAULT_PORT);
        Thread t = new Thread(udpReceiver);
        t.start();
    }

    @Override
    public void run() {
        try {

            while(!connected){
                makeHandshakeWithServer();
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onObjectReceive(Object o, DatagramPacket receivedPacket) {

        if(o instanceof HandshakePacketFromServer) {
            System.out.println("//Received HS packet");
            if(connected){
                return;
            }
            HandshakePacketFromServer hp = (HandshakePacketFromServer) o;
            System.out.println("Successful handshake on adress: " + receivedPacket.getAddress());
            serverAddress = receivedPacket.getAddress();
            clientManager.onServerConnection(hp.getPlayersConnected(), hp.getPlayersToStart());
            connected = true;

        }else if(o instanceof UpdateWaitingPacketFromServer){
            System.out.println("//Received UW packet");
            UpdateWaitingPacketFromServer uwp = (UpdateWaitingPacketFromServer) o;
            clientManager.updatePlayersConnected(uwp.getPlayersConnected());

        }else if(o instanceof GameStartData){
            System.out.println("//Received GSD packet");
            GameStartData gsd = (GameStartData) o;
            clientManager.onGameStart(gsd);

        }else if(o instanceof GamePacketFromServer){
            System.out.println("//Received GPS packet");
            clientManager.onGamePacketReceived((GamePacketFromServer) o);

            GamePacketFromClient gpc;
            synchronized (gamePacketFromClient) {
                gpc = new GamePacketFromClient(gamePacketFromClient);
            }
            sendObject(serverAddress,gpc);

        }else if(o instanceof EndPacket){
            terminate();
        }

    }

    public void setGamePacketToBeSent(GamePacketFromClient gpc){
        synchronized (gamePacketFromClient) {
            gamePacketFromClient = gpc;
        }
    }

    private void makeHandshakeWithServer() throws Exception {

        DatagramSocket sendSocket = new DatagramSocket();
        sendSocket.setBroadcast(true);

        HandshakePacketFromClient hp = new HandshakePacketFromClient();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(hp);

        byte[] requestBuff = baos.toByteArray();

        DatagramPacket sendPacket = new DatagramPacket(requestBuff, requestBuff.length, InetAddress.getByName("255.255.255.255"), DEFAULT_PORT);
        sendSocket.send(sendPacket);
        System.out.println(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");

        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                InetAddress broadcast = interfaceAddress.getBroadcast();
                if (broadcast == null) {
                    continue;
                }

                DatagramPacket sendPacket2 = new DatagramPacket(requestBuff, requestBuff.length, broadcast, DEFAULT_PORT);
                sendSocket.send(sendPacket2);

                System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
            }
        }
        System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");
        sendSocket.close();
    }

    public void terminate(){
        if(udpReceiver!=null) {
            udpReceiver.terminate();
        }
    }
}
