package com.firstgame.game;

import com.firstgame.game.math.Location;
import com.firstgame.game.math.RGBColor;
import com.firstgame.game.server.PlayerLite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Player extends Entity {

    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGTH = 50;

    public Player(Location location, RGBColor color, int UID) {
        super(location, PLAYER_WIDTH, PLAYER_HEIGTH, color, UID);
    }

    public Player deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Player) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
