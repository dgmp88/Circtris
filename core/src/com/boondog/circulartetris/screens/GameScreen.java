package com.boondog.circulartetris.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.boondog.circulartetris.CircularTetris;
import com.boondog.circulartetris.model.Block;
import com.boondog.circulartetris.render.MyShapeRenderer;

public class GameScreen implements Screen {
	CircularTetris app;
	OrthographicCamera cam;
	MyShapeRenderer rend;
	Vector2 center = new Vector2(0,0);
	
	Block b1;

	
	public GameScreen(CircularTetris app) {
		this.app = app;
		cam = new OrthographicCamera(app.worldWidth, app.worldHeight);
		rend = new MyShapeRenderer();
		rend.setCamera(cam);
		b1 = new Block(0,300);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		b1.rotate(1);
		rend.drawArc(center, b1.getRotation() - Block.blockWidth/2, b1.getRotation() + Block.blockWidth/2, b1.getPosition(), Block.blockHeight, Color.RED, 30);

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
