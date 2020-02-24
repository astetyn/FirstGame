package com.firstgame.game.math;

import java.io.Serializable;

public class TilePosition implements Serializable {

	private int x;
	private int y;
	
	public TilePosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
    public String toString() {
		return ("x="+this.x+" y="+this.y);
	}
	
}
