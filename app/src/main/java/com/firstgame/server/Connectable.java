package com.firstgame.server;

import java.net.DatagramPacket;

public interface Connectable {

    int DEFAULT_PORT = 1414;

    void onObjectReceive(Object o, DatagramPacket packet);

}
