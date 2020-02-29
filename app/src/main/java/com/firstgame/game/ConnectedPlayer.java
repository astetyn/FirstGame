package com.firstgame.game;

import com.firstgame.game.math.Location;
import com.firstgame.game.math.RGBColor;
import com.firstgame.game.server.PlayerLite;

import java.io.Serializable;
import java.net.InetAddress;

public class ConnectedPlayer extends Player implements Serializable {

    private InetAddress address;

    public ConnectedPlayer(Location loc, RGBColor color, int UID, InetAddress address) {
        super(loc, color, UID);
        this.address = address;
    }

    public InetAddress getAddress() {
        return address;
    }
}
