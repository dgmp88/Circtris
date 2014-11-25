package com.boondog.circulartetris.model;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.boondog.circulartetris.CircularTetris;
import com.boondog.circulartetris.render.Colors;

public class Block {
	// Universal size of all blocks
	public static final float blockHeight = 40;
	public static Random rand = new Random();
	public static Colors colors = new Colors();
	public static int colNum = 0;
	
	// Position of the block
	float rotation, position;
	
	Color color;
	
	public Block(float rotation, float position) {
		this.rotation = rotation;
		this.position = position;
		color = colors.get(colNum);
		colNum++;
	}
	
	
	public float getRotation() {
		return rotation;
	}
	
	public float getPosition() {
		return position;
	}
	
	public void rotateLeft() {
		rotation += CircularTetris.blockWidth;
	}

	public void rotateRight() {
		rotation -= CircularTetris.blockWidth;		
	}
	
	public Color getColor() {
		return color;
	}



	
}
