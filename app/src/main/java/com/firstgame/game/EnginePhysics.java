package com.firstgame.game;

import com.firstgame.game.math.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class EnginePhysics {

	private final float FRICTION = 1000;

	private final int BLOCK_CHECK_OFFSET = 2;
	
	// How precisely will be collision with edges calculated, its same over 20, where int overflow
	private final int COLLISION_PRECISION = 2;

	private World world;
	private Player player;
	private LinkedHashSet<Tile> updatedTiles;

	public EnginePhysics(World world, Player player, LinkedHashSet<Tile> updatedTiles) {
		this.world = world;
		this.player = player;
		this.updatedTiles = updatedTiles;
	}

	public void loop(float deltaTime, float[] accelValues, List<PlayerLite> entities, boolean serverSide){

		synchronized (player) {
			if (Math.abs(player.getMovement().getX()) < 200) {
				player.getMovement().addX(accelValues[1] * 500 * deltaTime);
			}
			if (Math.abs(player.getMovement().getY()) < 200) {
				player.getMovement().addY(accelValues[0] * 500 * deltaTime);
			}

			applyPhysicsToEntity(player, deltaTime);
		}
		synchronized (entities) {
			for (PlayerLite e : entities) {
				applyPhysicsToEntity(e, deltaTime);
			}
		}

		if(serverSide) {

			List<Tile> collidedTilesFromEntities = new ArrayList<>();
			List<Tile> common = new ArrayList<>();

			synchronized (entities) {
				for (PlayerLite e : entities) {
					List<Tile> l = getCollidedTiles(e);
					collidedTilesFromEntities.addAll(l);
					common.addAll(l);
					common.retainAll(l);
					for(Tile t : l){
						t.setColoredCode(ColorCode.get(e.getUniqueID()));
					}
				}
			}

			List<Tile> collidedTilesFromPlayer = getCollidedTiles(player);
			common.retainAll(collidedTilesFromPlayer);

			for (Tile t : common) {
				collidedTilesFromPlayer.remove(t);
				collidedTilesFromEntities.remove(t);
				t.setColoredCode(ColorCode.COMMON);
			}

			for (Tile t : collidedTilesFromPlayer) {
				if (!t.isSolid()) {
					t.setColoredCode(ColorCode.PLAYER_4);
				}
			}

            synchronized (updatedTiles) {
                updatedTiles.addAll(collidedTilesFromPlayer);
                updatedTiles.addAll(collidedTilesFromEntities);
                updatedTiles.addAll(common);
            }
		}else{
            synchronized (updatedTiles) {
                int size = updatedTiles.size();
                Iterator<?> it = updatedTiles.iterator();
                for (int i = 0; i < size; i++) {
                    Tile t = (Tile) it.next();
                    it.remove();
                    world.getTileMap()[t.getPosition().getY()][t.getPosition().getX()].setColoredCode(t.getColoredCode());
                }
            }
		}
	}

	public void applyPhysicsToEntity(PlayerLite playerLite, float deltaTime) {

		applyFriction(playerLite, deltaTime);

		float absMovementX = Math.abs(playerLite.getMovement().getX());
		float absMovementY = Math.abs(playerLite.getMovement().getY());

		int slowCX = (int) (absMovementX*deltaTime/Math.min(world.getTileSize(), playerLite.getSizeX()))+1;
		int slowCY = (int) (absMovementY*deltaTime/Math.min(world.getTileSize(), playerLite.getSizeY()))+1;

		for(int i = 0;i<slowCX;i++) {

			applyMovementX(playerLite, slowCX, deltaTime);
			boolean collision = isColliding(playerLite);

			if(!collision) {
				continue;
			}
			playerLite.setLocation(playerLite.getOldLocation().clone());

			int depth = 0;
			int slowCoef = slowCX*2;

			while(depth!=COLLISION_PRECISION) {
				applyMovementX(playerLite, slowCoef, deltaTime);
				collision = isColliding(playerLite);
				if(collision) {
					playerLite.setLocation(playerLite.getOldLocation().clone());
				}
				try {
					slowCoef = Math.multiplyExact(slowCoef, 2);
				}catch(ArithmeticException e) {
					break;
				}
				depth++;
			}
			playerLite.getMovement().setX(0);
			break;
		}

		for(int i = 0;i<slowCY;i++) {

			applyMovementY(playerLite, slowCY, deltaTime);
			boolean collision = isColliding(playerLite);

			if(!collision) {
				continue;
			}
			playerLite.setLocation(playerLite.getOldLocation().clone());

			int depth = 0;
			int slowCoef = slowCY*2;

			while(depth!=COLLISION_PRECISION) {
				applyMovementY(playerLite, slowCoef, deltaTime);
				collision = isColliding(playerLite);
				if(collision) {
					playerLite.setLocation(playerLite.getOldLocation().clone());
				}
				try {
					slowCoef = Math.multiplyExact(slowCoef, 2);
				}catch(ArithmeticException e) {
					break;
				}
				depth++;
			}
			playerLite.getMovement().setY(0);
			break;
		}
	}
	
	private void applyMovementX(PlayerLite playerLite, int slowCoef, float deltaTime) {
		float x = playerLite.getLocation().getX()+(playerLite.getMovement().getX()*deltaTime/slowCoef);
		playerLite.setOldLocation(playerLite.getLocation().clone());
		playerLite.getLocation().setX(x);
	}
	
	private void applyMovementY(PlayerLite playerLite, int slowCoef, float deltaTime) {
		float y = playerLite.getLocation().getY()+(playerLite.getMovement().getY()*deltaTime/slowCoef);
		playerLite.setOldLocation(playerLite.getLocation().clone());
		playerLite.getLocation().setY(y);
	}
	
	private void applyFriction(PlayerLite playerLite, float deltaTime) {
		
		if(playerLite.isIgnoreNextFriction()) {
			playerLite.setIgnoreNextFriction(false);
			return;
		}
		
		float realFriction = FRICTION*deltaTime;
		
		if(Math.abs(playerLite.getMovement().getX())-realFriction<0) {
			playerLite.getMovement().setX(0);
		}else{
			if(playerLite.getMovement().getX()>0){
				playerLite.getMovement().addX(-realFriction);
			}else{
				playerLite.getMovement().addX(realFriction);
			}

		}

		if(Math.abs(playerLite.getMovement().getY())-realFriction<0) {
			playerLite.getMovement().setY(0);
		}else{
			if(playerLite.getMovement().getY()>0){
				playerLite.getMovement().addY(-realFriction);
			}else{
				playerLite.getMovement().addY(realFriction);
			}
		}
	}

	private List<Tile> getCollidedTiles(PlayerLite playerLite){

		List<Tile> collidedTiles = new ArrayList<Tile>();

		float eX = playerLite.getLocation().getX();
		float eY = playerLite.getLocation().getY();

		int bSX = (int) (eX/world.getTileSize());
		int bSY = (int) (eY/world.getTileSize());

		int blocksRight = (playerLite.getSizeX()/world.getTileSize())+BLOCK_CHECK_OFFSET;
		int blocksDown = (playerLite.getSizeY()/world.getTileSize())+BLOCK_CHECK_OFFSET;

		if(bSY<0||bSY>=world.getTileMap().length||bSX<0||bSX>=world.getTileMap()[0].length){
			return Arrays.asList();
		}

		for(int i = 0;i<blocksDown;i++) {
			for (int j = 0; j < blocksRight; j++) {
				if(world.getYTiles()<=bSY+i||world.getXTiles()<=bSX+j){
					continue;
				}
				Tile t = world.getTileMap()[bSY + i][bSX + j];

				boolean collide = isColliding(playerLite, t);

				if (collide) {
					collidedTiles.add(t);
				}
			}
		}
		return collidedTiles;
	}

	private boolean isColliding(PlayerLite playerLite) {

		float eX = playerLite.getLocation().getX();
		float eY = playerLite.getLocation().getY();

		if(eX<world.getBorderLeft()||eX+ playerLite.getSizeX()>world.getBorderRight()) {
			return true;
		}

		if(eY<world.getBorderUp()||eY+ playerLite.getSizeY()>world.getBorderDown()) {
			return true;
		}

		int bSX = (int) (eX/world.getTileSize());
		int bSY = (int) (eY/world.getTileSize());

		int blocksRight = (playerLite.getSizeX()/world.getTileSize())+BLOCK_CHECK_OFFSET;
		int blocksDown = (playerLite.getSizeY()/world.getTileSize())+BLOCK_CHECK_OFFSET;

		for(int i = 0;i<blocksDown;i++) {
			for (int j = 0; j < blocksRight; j++) {
				if(world.getYTiles()<=bSY+i||world.getXTiles()<=bSX+j){
					continue;
				}
				Tile t = world.getTileMap()[bSY + i][bSX + j];

				boolean collide = isColliding(playerLite, t);

				if (collide&&t.isSolid()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isColliding(PlayerLite playerLite, Tile tile) {
		
		int bX = (int) tile.getLocation().getX();
		int bY = (int) tile.getLocation().getY();
		
		float eX = playerLite.getLocation().getX();
		float eY = playerLite.getLocation().getY();
		
		Location[] points = new Location[4];
		
		points[0] = tile.getLocation();
		points[1] = new Location(bX+world.getTileSize(), bY);
		points[2] = new Location(bX+world.getTileSize(), bY+world.getTileSize());
		points[3] = new Location(bX, bY+world.getTileSize());

		for (Location loc : points) {
			boolean collision = isColliding(playerLite, loc);
			if (collision) {
				return true;
			}
		}
		
		int eSW = playerLite.getSizeX();
		int eSH = playerLite.getSizeY();
		
		if(points[0].getX()<=eX&&points[1].getX()>=eX+eSW&&points[0].getY()>=eY&&points[3].getY()<=eY+eSH) {
			return true;
		}
		if(points[0].getY()<=eY&&points[3].getY()>=eY+eSH&&points[0].getX()>=eX&&points[1].getX()<=eX+eSW) {
			return true;
		}
		
		points[0] = playerLite.getLocation();
		points[1] = new Location(eX+ playerLite.getSizeX(), eY);
		points[2] = new Location(eX+ playerLite.getSizeX(), eY+ playerLite.getSizeY());
		points[3] = new Location(eX, eY+ playerLite.getSizeY());

		for (Location loc : points) {
			boolean collision = isColliding(tile, loc);
			if (collision) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isColliding(PlayerLite playerLite, Location loc) {
		
		Location eLoc = playerLite.getLocation();
		
		if(loc.getX()>eLoc.getX()&&loc.getX()<eLoc.getX()+ playerLite.getSizeX()) {
			return loc.getY() > eLoc.getY() && loc.getY() < eLoc.getY() + playerLite.getSizeY();
		}
		return false;
	}
	
	private boolean isColliding(Tile tile, Location loc) {
		
		Location bLoc = tile.getLocation();

		if(loc.getX()>bLoc.getX()&&loc.getX()<bLoc.getX()+world.getTileSize()) {
			return loc.getY() > bLoc.getY() && loc.getY() < bLoc.getY() + world.getTileSize();
		}
		return false;
	}
	
}
