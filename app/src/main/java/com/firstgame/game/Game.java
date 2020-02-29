package com.firstgame.game;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.firstgame.game.math.Location;
import com.firstgame.game.math.RGBColor;
import com.firstgame.menu.MainActivity;

import java.util.LinkedHashSet;
import java.util.List;

import processing.core.PApplet;

public class Game extends PApplet {

    private World world;
    private Player player;

    private long lastTimeStamp = System.currentTimeMillis();
    private LinkedHashSet<Tile> updatedTiles = new LinkedHashSet<>();
    private double secondsToEnd;
    private GameManager gameManager;

    public Game(GameManager manager, World world, Player player){
        this.gameManager = manager;
        this.world = world;
        this.player = player;
    }

    public void settings(){
        fullScreen(P2D);
    }

    public void setup() {

        orientation(LANDSCAPE);
        frameRate(60);

    }

    public void draw() {

        float factor = (float)displayHeight/(world.getYTiles()*world.getTileSize());

        scale(factor);

        //Logic
        float deltaTime =  (float)(System.currentTimeMillis()-lastTimeStamp)/1000;
        lastTimeStamp = System.currentTimeMillis();

        gameManager.onLogicLoop(deltaTime);

        //Render
        background(255,255,255);

        //Render map
        for(int i = 0;i<world.getYTiles();i++){
            for(int j = 0;j<world.getXTiles();j++){
                Tile tile = world.getTileMap()[i][j];
                RGBColor rc = tile.getColor();
                fill(rc.getR(),rc.getG(),rc.getB());
                square(j*world.getTileSize(),i*world.getTileSize(),world.getTileSize());
            }
        }

        //Render entities
        for(Player p : gameManager.getPlayers()){
            RGBColor c = p.getRgbColor();
            fill(c.getR(),c.getG(),c.getB());
            rect(p.getLocation().getX(),p.getLocation().getY(),p.getSizeX(),p.getSizeY());
        }
    }

    public LinkedHashSet<Tile> getUpdatedTiles() {
        return updatedTiles;
    }

}
