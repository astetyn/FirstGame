package com.firstgame.game.math;

import java.io.Serializable;

public class Vector implements Serializable {

	private float x;
	private float y;
	
	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void addX(float value) {
		this.x += value;
	}
	
	public void addY(float value) {
		this.y += value;
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
    public String toString() {
		return ("x="+this.x+" y="+this.y);
	}
	
}
