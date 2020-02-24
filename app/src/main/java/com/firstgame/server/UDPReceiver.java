package com.firstgame.server;

import android.provider.ContactsContract;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPReceiver implements Runnable {

    private Connectable connectable;
    private DatagramSocket socket;
    private byte[] buff = new byte[32768];
    private int port;
    private InetAddress address;
    private boolean terminated;

    public UDPReceiver(Connectable connectable, int port) {
        this.connectable = connectable;
        this.port = port;
        this.terminated = false;
    }

    public UDPReceiver(Connectable connectable, InetAddress address, int port) {
        this.connectable = connectable;
        this.port = port;
        this.address = address;
        this.terminated = false;
    }

    @Override
    public void run() {

        try {
            if(address==null){
                socket = new DatagramSocket(port);
            }else{
                socket = new DatagramSocket(port, address);
            }

            socket.setBroadcast(true);

            while(true) {

                DatagramPacket receivedPacket = new DatagramPacket(buff, buff.length);
                socket.receive(receivedPacket);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buff));
                connectable.onObjectReceive(ois.readObject(), receivedPacket);

                if(terminated){
                    return;
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void terminate(){
        terminated = true;
        if(socket!=null) {
            socket.close();
        }
    }
}
