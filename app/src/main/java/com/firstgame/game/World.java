package com.firstgame.game;

import com.firstgame.game.math.Location;
import com.firstgame.game.math.TilePosition;

import java.io.Serializable;

public class World implements Serializable {

    private Tile[][] tileMap;
    private int tileSize;
    private int borderRight;
    private int borderDown;
    private int borderLeft;
    private int borderUp;

    public World(int xTiles, int yTiles){
        tileMap = new Tile[yTiles][xTiles];
        this.tileSize = 30;

        for(int i = 0;i<yTiles;i++) {
            for (int j = 0; j<xTiles; j++) {
                int x = j*tileSize;
                int y = i*tileSize;
                if(x<=150&&y<=150) {
                    tileMap[i][j] = new Tile(false, new Location(x, y), new TilePosition(j, i));
                }else{
                    if(Math.random()<=0.06){
                        tileMap[i][j] = new Tile(true, new Location(x, y), new TilePosition(j, i));
                    }else{
                        tileMap[i][j] = new Tile(false, new Location(x, y), new TilePosition(j, i));
                    }
                }
            }
        }

        borderRight = tileSize*xTiles;
        borderDown = tileSize*yTiles;
        borderLeft = 0;
        borderUp = 0;
    }

    public Tile[][] getTileMap() {
        return tileMap;
    }

    public int getXTiles(){
        return tileMap[0].length;
    }

    public int getYTiles(){
        return tileMap.length;
    }

    public int getTileSize(){
        return tileSize;
    }

    public int getBorderRight() {
        return borderRight;
    }

    public int getBorderDown() {
        return borderDown;
    }

    public int getBorderLeft() {
        return borderLeft;
    }

    public int getBorderUp() {
        return borderUp;
    }
}
