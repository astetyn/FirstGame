package com.firstgame.game;

import com.firstgame.game.math.Location;
import com.firstgame.game.math.RGBColor;
import com.firstgame.game.math.Vector;

import java.io.Serializable;

public abstract class PlayerLite implements Serializable {

	private Location location;
	private Location oldLocation;
	private int sizeX;
	private int sizeY;
	private Vector movement;
	private boolean ignoreNextFriction;
	private RGBColor rgbColor;
	private int uniqueID;
	
	public PlayerLite(Location location, int sizeX, int sizeY, RGBColor color, int uniqueID) {
		this.location = location;
		this.oldLocation = location;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.movement = new Vector(0, 0);
		this.ignoreNextFriction = false;
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

	public Vector getMovement() {
		return movement;
	}

	public void setMovement(Vector movement){
		this.movement = movement;
	}

	public boolean isIgnoreNextFriction() {
		return ignoreNextFriction;
	}

	public void setIgnoreNextFriction(boolean ignoreNextFriction) {
		this.ignoreNextFriction = ignoreNextFriction;
	}

	public RGBColor getRgbColor() {
		return rgbColor;
	}

	public int getUniqueID() {
		return uniqueID;
	}
}
