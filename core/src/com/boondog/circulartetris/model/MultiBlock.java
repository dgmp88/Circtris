package com.boondog.circulartetris.model;

import com.badlogic.gdx.utils.Array;

public class MultiBlock {
	public static void rotateLeft(Array<Block> blocks) {
		for (Block b : blocks) {
			b.rotateLeft();
		}
	}
	
	public static void rotateRight(Array<Block> blocks) {
		for (Block b : blocks) {
			b.rotateRight();
		}
	}
}
