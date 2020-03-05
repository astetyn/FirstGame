package com.firstgame.game;

import com.firstgame.game.math.Location;
import com.firstgame.game.math.RGBColor;
import com.firstgame.game.math.Vector;

import java.io.Serializable;

public abstract class Entity implements Serializable {

	private Location location;
	private Location oldLocation;
	private int sizeX;
	private int sizeY;
	private Vector velocity;
	private RGBColor rgbColor;
	private int uniqueID;
	
	public Entity(Location location, int sizeX, int sizeY, RGBColor color, int uniqueID) {
		this.location = location;
		this.oldLocation = location;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.velocity = new Vector(0, 0);
		this.rgbColor = color;
		this.uniqueID = uniqueID;
	}

	public Location getLocation() {
		return this.location;
	}
	
	public void setLocation(Location loc) {
		this.location = loc;
	}

	public Location getOldLocation() {
		return oldLocation;
	}

	public void setOldLocation(Location oldLocation) {
		this.oldLocation = oldLocation;
	}
	
	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector velocity){
		this.velocity = velocity;
	}

	public RGBColor getRgbColor() {
		return rgbColor;
	}

	public int getUniqueID() {
		return uniqueID;
	}
}
