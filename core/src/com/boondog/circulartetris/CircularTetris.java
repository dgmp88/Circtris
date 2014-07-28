package com.boondog.circulartetris;

import com.badlogic.gdx.Game;
import com.boondog.circulartetris.screens.GameScreen;


public class CircularTetris extends Game {
	public static final int worldWidth = 920, worldHeight = 720;
	
	@Override
	public void create() {
		setScreen(new GameScreen(this));
	}
}
