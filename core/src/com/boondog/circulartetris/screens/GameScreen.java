package com.boondog.circulartetris.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.boondog.circulartetris.CircularTetris;
import com.boondog.circulartetris.controls.Controller;
import com.boondog.circulartetris.model.Block;
import com.boondog.circulartetris.model.Game;
import com.boondog.circulartetris.render.MyShapeRenderer;

public class GameScreen implements Screen {
	CircularTetris app;
	OrthographicCamera cam;
	MyShapeRenderer rend;
	Vector2 center = new Vector2(0,0);
	Game game = new Game();
	Controller control = new Controller(game);
	
	public GameScreen(CircularTetris app) {
		this.app = app;
		cam = new OrthographicCamera(CircularTetris.worldWidth, CircularTetris.worldHeight);
		rend = new MyShapeRenderer();
		rend.setCamera(cam);
		Gdx.input.setInputProcessor(control);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		for (Block b : game.getCenter()) {
			rend.drawArc(center, b.getRotation() - CircularTetris.blockWidth/2, b.getRotation() + CircularTetris.blockWidth/2, b.getPosition(), Block.blockHeight, b.getColor(), 30);		
		}

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
