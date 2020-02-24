package com.firstgame.game;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.firstgame.game.math.Location;
import com.firstgame.game.math.RGBColor;
import com.firstgame.menu.MainActivity;
import com.firstgame.server.ClientGateway;
import com.firstgame.server.ServerGateway;

import java.util.LinkedHashSet;
import java.util.List;

import processing.core.PApplet;

public class Game extends PApplet {

    private final double INIT_GAME_SECONDS = 100;

    private World world;
    private Player player;
    private EnginePhysics enginePhysics;

    private ServerGateway serverGateway;
    private ClientGateway clientGateway;

    private boolean connected = false;

    private SensorManager manager;
    private Sensor sensor;
    private AccelerometerListener listener;

    private long lastTimeStamp = System.currentTimeMillis();
    private float[] accelValues = new float[3];
    private LinkedHashSet<Tile> updatedTiles = new LinkedHashSet<>();
    private double secondsToEnd;
    private MainActivity activity;

    public Game(MainActivity activity, World world){
        this.activity = activity;
        this.world = world;
    }

    public void settings(){
        fullScreen(P2D);
    }

    public void setup() {

        orientation(LANDSCAPE);
        frameRate(60);

        manager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new AccelerometerListener();
        manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void draw() {

        if(!connected){
            if(serverGateway !=null){
                connected = serverGateway.isReadyToStart();
                enginePhysics = new EnginePhysics(world,player,updatedTiles);
            }else if(clientGateway !=null){
                connected = clientGateway.isReadyToStart();
                if(connected){
                    world = clientGateway.getWorld();
                    enginePhysics = new EnginePhysics(world,player,updatedTiles);
                }
            }
            return;
        }

        if(serverGateway !=null){
            if(serverGateway.isGameEnded()){
                return;
            }
        }else{
            if(clientGateway.isGameEnded()){
                return;
            }
        }

        float factor = (float)displayHeight/(world.getYTiles()*world.getTileSize());

        scale(factor);

        //Logic
        float deltaTime =  (float)(System.currentTimeMillis()-lastTimeStamp)/1000;
        lastTimeStamp = System.currentTimeMillis();

        if(serverGateway !=null){
            secondsToEnd -= deltaTime;
            serverGateway.updateSeconds(secondsToEnd);
        }else{
            secondsToEnd = clientGateway.getSecondsToEnd();
        }

        if(serverGateway !=null){
            enginePhysics.loop(deltaTime, accelValues, serverGateway.getEnemies(),true);
        }else{
            enginePhysics.loop(deltaTime, accelValues, clientGateway.getEnemies(),false);
        }

        //Render
        background(255,255,255);

        int client1S = 0;
        int client2S = 0;
        int client3S = 0;
        int client4S = 0;

        for(int i = 0;i<world.getYTiles();i++){
            for(int j = 0;j<world.getXTiles();j++){
                Tile tile = world.getTileMap()[i][j];

                RGBColor rc = null;

                switch (tile.getColoredCode()){
                    case DEFAULT: rc = new RGBColor((short)255,(short)255,(short)255); break;
                    case COMMON: rc = new RGBColor((short)245, (short)200, (short)66); break;
                    case SOLID: rc = new RGBColor((short)0, (short)0, (short)0); break;
                    case PLAYER_1:
                        rc = new RGBColor((short)255, (short)30, (short)30);
                        client1S++;
                        break;
                    case PLAYER_2:
                        rc = new RGBColor((short)30, (short)255, (short)30);
                        client2S++;
                        break;
                    case PLAYER_3:
                        rc = new RGBColor((short)30, (short)30, (short)255);
                        client3S++;
                        break;
                    case PLAYER_4:
                        rc = new RGBColor((short)220, (short)200, (short)100);
                        client4S++;
                        break;
                }

                fill(rc.getR(),rc.getG(),rc.getB());
                square(j*world.getTileSize(),i*world.getTileSize(),world.getTileSize());
            }
        }

        if(serverGateway !=null) {
            List<PlayerLite> entities = serverGateway.getEnemies();
            for(PlayerLite e : entities){
                Location loc = e.getLocation();
                fill(0,255,0);
                rect(loc.getX(),loc.getY(),e.getSizeX(),e.getSizeY());
            }
        }else{
            List<PlayerLite> entities = clientGateway.getEnemies();
            for(PlayerLite e : entities){
                Location loc = e.getLocation();
                fill(0,255,0);
                rect(loc.getX(),loc.getY(),e.getSizeX(),e.getSizeY());
            }
        }
        float pX = player.getLocation().getX();
        float pY = player.getLocation().getY();
        fill(255,0,0);
        rect(pX,pY,player.getSizeX(),player.getSizeY());

        final int s1 = client1S;
        final int s2 = client2S;
        final int s3 = client3S;
        final int s4 = client4S;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.updateTable((int)secondsToEnd,s1,s2,s3,s4);
            }
        });

    }

    public ServerGateway getServerGateway() {
        return serverGateway;
    }

    public void setServerGateway(ServerGateway serverGateway) {
        this.serverGateway = serverGateway;
    }

    public void setClientGateway(ClientGateway clientGateway) {
        this.clientGateway = clientGateway;
    }

    public LinkedHashSet<Tile> getUpdatedTiles() {
        return updatedTiles;
    }

    class AccelerometerListener implements SensorEventListener {
        public void onSensorChanged(SensorEvent event) {
            accelValues[0] = event.values[0];
            accelValues[1] = event.values[1];
            accelValues[2] = event.values[2];
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    public void onResume() {
        super.onResume();
        if (manager != null) {
            manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onPause() {
        super.onPause();
        if (manager != null) {
            manager.unregisterListener(listener);
        }
    }

    public Player getPlayer(){
        return player;
    }

    public World getWorld(){
        return world;
    }
}
