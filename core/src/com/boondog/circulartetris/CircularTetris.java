package com.boondog.circulartetris;

import com.badlogic.gdx.Game;
import com.boondog.circulartetris.screens.GameScreen;


public class CircularTetris extends Game {
	public static final int worldWidth = 920, worldHeight = 720;
	public static final int segments = 9;
	public static final int blockWidth = 360/segments;
	
	
	@Override
	public void create() {
		setScreen(new GameScreen(this));
	}
}
