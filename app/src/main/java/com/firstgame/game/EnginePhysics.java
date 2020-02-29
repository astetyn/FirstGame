package com.firstgame.game;

import com.firstgame.game.math.Location;
import com.firstgame.game.server.PlayerLite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EnginePhysics {

	private final float FRICTION = 1000;

	private final int BLOCK_CHECK_OFFSET = 2;
	
	// How precisely will be collision with edges calculated, its same over 20, where int overflow
	private final int COLLISION_PRECISION = 2;

	private World world;
	private Player player;
	private List<Player> enemies;

	public EnginePhysics(World world, Player player, List<Player> enemies) {
		this.world = world;
		this.player = player;
		this.enemies = enemies;
	}

	public Set<Tile> loop(float deltaTime, float[] accelValues){

		synchronized (player) {
			if (Math.abs(player.getVelocity().getX()) < 200) {
				player.getVelocity().addX(accelValues[1] * 500 * deltaTime);
			}
			if (Math.abs(player.getVelocity().getY()) < 200) {
				player.getVelocity().addY(accelValues[0] * 500 * deltaTime);
			}

			applyPhysicsToEntity(player, deltaTime);
		}
		synchronized (enemies) {
			for (Player e : enemies) {
				applyPhysicsToEntity(e, deltaTime);
			}
		}

		//Calculate all tiles which collide with players
		Set<Tile> collidedTilesFromEntities = new HashSet<>();

		collidedTilesFromEntities.addAll(getCollidedTiles(player));

		for(Player p : enemies){
			collidedTilesFromEntities.addAll(getCollidedTiles(p));
		}

		//Return these tiles
		return collidedTilesFromEntities;
	}

	public void applyPhysicsToEntity(Entity entity, float deltaTime) {

		applyFriction(entity, deltaTime);

		float absMovementX = Math.abs(entity.getVelocity().getX());
		float absMovementY = Math.abs(entity.getVelocity().getY());

		int slowCX = (int) (absMovementX*deltaTime/Math.min(world.getTileSize(), entity.getSizeX()))+1;
		int slowCY = (int) (absMovementY*deltaTime/Math.min(world.getTileSize(), entity.getSizeY()))+1;

		for(int i = 0;i<slowCX;i++) {

			applyMovementX(entity, slowCX, deltaTime);
			boolean collision = isColliding(entity);

			if(!collision) {
				continue;
			}
			entity.setLocation(entity.getOldLocation().clone());

			int depth = 0;
			int slowCoef = slowCX*2;

			while(depth!=COLLISION_PRECISION) {
				applyMovementX(entity, slowCoef, deltaTime);
				collision = isColliding(entity);
				if(collision) {
					entity.setLocation(entity.getOldLocation().clone());
				}
				try {
					slowCoef = Math.multiplyExact(slowCoef, 2);
				}catch(ArithmeticException e) {
					break;
				}
				depth++;
			}
			entity.getVelocity().setX(0);
			break;
		}

		for(int i = 0;i<slowCY;i++) {

			applyMovementY(entity, slowCY, deltaTime);
			boolean collision = isColliding(entity);

			if(!collision) {
				continue;
			}
			entity.setLocation(entity.getOldLocation().clone());

			int depth = 0;
			int slowCoef = slowCY*2;

			while(depth!=COLLISION_PRECISION) {
				applyMovementY(entity, slowCoef, deltaTime);
				collision = isColliding(entity);
				if(collision) {
					entity.setLocation(entity.getOldLocation().clone());
				}
				try {
					slowCoef = Math.multiplyExact(slowCoef, 2);
				}catch(ArithmeticException e) {
					break;
				}
				depth++;
			}
			entity.getVelocity().setY(0);
			break;
		}
	}
	
	private void applyMovementX(Entity entity, int slowCoef, float deltaTime) {
		float x = entity.getLocation().getX()+(entity.getVelocity().getX()*deltaTime/slowCoef);
		entity.setOldLocation(entity.getLocation().clone());
		entity.getLocation().setX(x);
	}
	
	private void applyMovementY(Entity entity, int slowCoef, float deltaTime) {
		float y = entity.getLocation().getY()+(entity.getVelocity().getY()*deltaTime/slowCoef);
		entity.setOldLocation(entity.getLocation().clone());
		entity.getLocation().setY(y);
	}
	
	private void applyFriction(Entity entity, float deltaTime) {
		
		float realFriction = FRICTION*deltaTime;
		
		if(Math.abs(entity.getVelocity().getX())-realFriction<0) {
			entity.getVelocity().setX(0);
		}else{
			if(entity.getVelocity().getX()>0){
				entity.getVelocity().addX(-realFriction);
			}else{
				entity.getVelocity().addX(realFriction);
			}

		}

		if(Math.abs(entity.getVelocity().getY())-realFriction<0) {
			entity.getVelocity().setY(0);
		}else{
			if(entity.getVelocity().getY()>0){
				entity.getVelocity().addY(-realFriction);
			}else{
				entity.getVelocity().addY(realFriction);
			}
		}
	}

	private List<Tile> getCollidedTiles(Entity entity){

		List<Tile> collidedTiles = new ArrayList<Tile>();

		float eX = entity.getLocation().getX();
		float eY = entity.getLocation().getY();

		int bSX = (int) (eX/world.getTileSize());
		int bSY = (int) (eY/world.getTileSize());

		int blocksRight = (entity.getSizeX()/world.getTileSize())+BLOCK_CHECK_OFFSET;
		int blocksDown = (entity.getSizeY()/world.getTileSize())+BLOCK_CHECK_OFFSET;

		if(bSY<0||bSY>=world.getTileMap().length||bSX<0||bSX>=world.getTileMap()[0].length){
			return Arrays.asList();
		}

		for(int i = 0;i<blocksDown;i++) {
			for (int j = 0; j < blocksRight; j++) {
				if(world.getYTiles()<=bSY+i||world.getXTiles()<=bSX+j){
					continue;
				}
				Tile t = world.getTileMap()[bSY + i][bSX + j];

				boolean collide = isColliding(entity, t);

				if (collide) {
					collidedTiles.add(t);
				}
			}
		}
		return collidedTiles;
	}

	private boolean isColliding(Entity entity) {

		float eX = entity.getLocation().getX();
		float eY = entity.getLocation().getY();

		if(eX<world.getBorderLeft()||eX+ entity.getSizeX()>world.getBorderRight()) {
			return true;
		}

		if(eY<world.getBorderUp()||eY+ entity.getSizeY()>world.getBorderDown()) {
			return true;
		}

		int bSX = (int) (eX/world.getTileSize());
		int bSY = (int) (eY/world.getTileSize());

		int blocksRight = (entity.getSizeX()/world.getTileSize())+BLOCK_CHECK_OFFSET;
		int blocksDown = (entity.getSizeY()/world.getTileSize())+BLOCK_CHECK_OFFSET;

		for(int i = 0;i<blocksDown;i++) {
			for (int j = 0; j < blocksRight; j++) {
				if(world.getYTiles()<=bSY+i||world.getXTiles()<=bSX+j){
					continue;
				}
				Tile t = world.getTileMap()[bSY + i][bSX + j];

				boolean collide = isColliding(entity, t);

				if (collide&&t.isSolid()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isColliding(Entity entity, Tile tile) {
		
		int bX = (int) tile.getLocation().getX();
		int bY = (int) tile.getLocation().getY();
		
		float eX = entity.getLocation().getX();
		float eY = entity.getLocation().getY();
		
		Location[] points = new Location[4];
		
		points[0] = tile.getLocation();
		points[1] = new Location(bX+world.getTileSize(), bY);
		points[2] = new Location(bX+world.getTileSize(), bY+world.getTileSize());
		points[3] = new Location(bX, bY+world.getTileSize());

		for (Location loc : points) {
			boolean collision = isColliding(entity, loc);
			if (collision) {
				return true;
			}
		}
		
		int eSW = entity.getSizeX();
		int eSH = entity.getSizeY();
		
		if(points[0].getX()<=eX&&points[1].getX()>=eX+eSW&&points[0].getY()>=eY&&points[3].getY()<=eY+eSH) {
			return true;
		}
		if(points[0].getY()<=eY&&points[3].getY()>=eY+eSH&&points[0].getX()>=eX&&points[1].getX()<=eX+eSW) {
			return true;
		}
		
		points[0] = entity.getLocation();
		points[1] = new Location(eX+ entity.getSizeX(), eY);
		points[2] = new Location(eX+ entity.getSizeX(), eY+ entity.getSizeY());
		points[3] = new Location(eX, eY+ entity.getSizeY());

		for (Location loc : points) {
			boolean collision = isColliding(tile, loc);
			if (collision) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isColliding(Entity entity, Location loc) {
		
		Location eLoc = entity.getLocation();
		
		if(loc.getX()>eLoc.getX()&&loc.getX()<eLoc.getX()+ entity.getSizeX()) {
			return loc.getY() > eLoc.getY() && loc.getY() < eLoc.getY() + entity.getSizeY();
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
