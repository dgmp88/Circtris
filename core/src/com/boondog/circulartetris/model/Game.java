package com.boondog.circulartetris.model;

import com.badlogic.gdx.utils.Array;
import com.boondog.circulartetris.CircularTetris;

public class Game {
	Array<Block> center = new Array<Block>();
	Array<Block> moving = new Array<Block>();
	
	public Game() {
		addCenterStartBlocks();
	}

	private void addCenterStartBlocks() {
		for (int i = 0; i < CircularTetris.segments; i ++) {
			Block b = new Block(i*(360/CircularTetris.segments),0);
			center.add(b);
		}
	}

	public Array<Block> getCenter() {
		return center;
	}
	
	public void rotateCenterLeft() {
		MultiBlock.rotateLeft(center);
	}
	
	public void rotateCenterRight() {
		MultiBlock.rotateRight(center);
	}
	
}
