package com.firstgame.game;

import com.firstgame.game.math.RGBColor;

import processing.core.PApplet;

public class GameRenderer extends PApplet {

    private World world;
    private GameManager gameManager;
    private long lastTimeStamp;

    public GameRenderer(GameManager manager, World world){
        this.world = world;
        this.gameManager = manager;
        lastTimeStamp = System.currentTimeMillis();
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

}
