package com.firstgame.game;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public interface Connectable {

    int DEFAULT_PORT = 1414;

    void onObjectReceive(Object o, DatagramPacket packet);

    default void sendObject(InetAddress address, Object o) {
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

}
