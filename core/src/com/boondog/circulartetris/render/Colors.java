package com.boondog.circulartetris.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class Colors {
	public Array<Color> colors = new Array<Color>();
	
	public Colors() {
		colors.add(Color.RED);
		colors.add(Color.GREEN);
		colors.add(Color.BLUE);
		colors.add(Color.CYAN);
		colors.add(Color.MAGENTA);
		colors.add(Color.MAROON);
	}

	public int getN() {
		return colors.size;
	}

	public Color get(int colNum) {
		return colors.get(colNum % colors.size);
	}
}
