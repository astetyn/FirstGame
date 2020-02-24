package com.firstgame.game.math;

import java.io.Serializable;

public class Location implements Serializable {
	
	private float x;
	private float y;
	
	public Location(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}
	
	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	@Override
	public Location clone() {
		return new Location(this.x,this.y);
	}
	
	@Override
    public String toString() {
		return ("x="+this.x+" y="+this.y);
	}
	
}
