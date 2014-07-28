package com.boondog.circulartetris.model;

public class Block {
	// Universal size of all blocks
	public static final float blockHeight = 40, blockWidth = 360/12;
	
	// Position of the block
	float rotation, position;
	
	public Block(float rotation, float position) {
		this.rotation = rotation;
		this.position = position;
	}
	
	
	public float getRotation() {
		return rotation;
	}
	
	public float getPosition() {
		return position;
	}
	
	public void rotate(float degrees) {
		rotation += degrees;
	}
	
}
