package com.boondog.circulartetris.render;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;


/**
 * This draws anti-aliased lines using the shader.
 * 
 * It's heavily based on the tutorial at the location below
 * 
 * @author george, most of the heavy lifting by mattdesl
 * Derived from
 * @see https://github.com/mattdesl/lwjgl-basics/wiki/LibGDX-Meshes-Lesson-1
 * @see https://gist.github.com/mattdesl/5793041
 */
public class MyShapeRenderer implements Disposable {
	Mesh mesh;
	Camera cam;
	ShaderProgram shader;
	Vector2 c;
	float deg2rad = (float) (Math.PI/180);
	
	enum LineType {
		top,
		bottom,
		center
	}
	
	
	//Position attribute - (x, y) 
	public static final int POSITION_COMPONENTS = 2;
	
	//Color attribute - (r, g, b, a)
	public static final int COLOR_COMPONENTS = 4;
	
	//Total number of components for all attributes
	public static final int NUM_COMPONENTS = POSITION_COMPONENTS + COLOR_COMPONENTS;
		
	//The maximum number of vertices our mesh will hold
	public static int MAX_VERTS = 5000; // We define the first square with 4 points, then 2 each for the next two
	
	public static int RENDER_TYPE;
	
	
	//The array which holds all the data, interleaved like so:
	//    x, y, r, g, b, a
	//    x, y, r, g, b, a, 
	//    x, y, r, g, b, a, 
	//    x, y, r, g, b, a, 
	//    ... etc ...
	private float[] verts = new float[MAX_VERTS];
	
	//The index position
	private int idx = 0;

	
	public MyShapeRenderer () {
		mesh = new Mesh(true, MAX_VERTS, 0, 
				new VertexAttribute(Usage.Position, POSITION_COMPONENTS, "a_position"),
				new VertexAttribute(Usage.Color, COLOR_COMPONENTS, "a_color"));
		shader = createMeshShader();
	}
	
	
	public void setCamera(Camera cam) {
		this.cam = cam;
	}
	
	
	
	protected static ShaderProgram createMeshShader() {
		
		final String VERT_SHADER =  
				"attribute vec2 a_position;\n" +
				"attribute vec4 a_color;\n" +			
				"uniform mat4 u_projTrans;\n" + 
				"varying vec4 vColor;\n" +			
				"void main() {\n" +  
				"	vColor = a_color;\n" +
				"	gl_Position =  u_projTrans * vec4(a_position.xy, 0.0, 1.0);\n" +
				"}";
		
		final String FRAG_SHADER = 
	            "#ifdef GL_ES\n" +
	            "precision mediump float;\n" +
	            "#endif\n" +
				"varying vec4 vColor;\n" + 			
				"void main() {\n" +  
				"	gl_FragColor = vColor;\n" + 
				"}";
		
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(VERT_SHADER, FRAG_SHADER);
		String log = shader.getLog();
		if (!shader.isCompiled())
			throw new GdxRuntimeException(log);		
		if (log!=null && log.length()!=0)
			System.out.println("Shader Log: "+log);
		return shader;
	}

	void flush() {
		//if we've already flushed
		if (idx==0)
			return;
		
		//sends our vertex data to the mesh
		mesh.setVertices(verts);
		
		//no need for depth...
		Gdx.gl.glDepthMask(false);
		
		//enable blending, for alpha
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		//number of vertices we need to render
		int vertexCount = (idx/NUM_COMPONENTS);
		
		cam.update();
		
		//start the shader before setting any uniforms
		shader.begin();
		
		//update the projection matrix so our triangles are rendered in 2D
		shader.setUniformMatrix("u_projTrans", cam.combined);
				
		//render the mesh
		mesh.render(shader, RENDER_TYPE, 0, vertexCount);
		
		shader.end();
		
		//re-enable depth to reset states to their default
		Gdx.gl.glDepthMask(true);
		
		//reset index to zero
		idx = 0;
		
		//reset verts
		Arrays.fill(verts, 0);
	}
	
	/** 
	 * Draw lines from points[0] -> points[1], points[1] -> points[2] ... points[n-1] -> points[n]
	 * 
	 * @param points
	 * @param width
	 * @param color
	 */
	public void drawLines(Array<Vector2> points, float width, Color color) {
		float feather = 0.3f * width;
		width = width - (feather);
		for (int i = 0; i < points.size - 1; i ++) {
			drawFullLine(points.get(i), points.get(i+1),width,feather,color);
		}
		flush();
	}
	
	public void drawLine(Vector2 a, Vector2 b, float width, Color color) {
		float feather = 0.3f * width;
		width = width - (feather);
		drawFullLine(a, b,width,feather,color);
		flush();
	}
	
	
	public void drawLines(Array<Vector2> points, float width, float feather, Color color) {
		for (int i = 0; i < points.size - 1; i ++) {
			drawFullLine(points.get(i), points.get(i+1),width,feather,color);
		}
		flush();
	}
	
	public void drawLine(Vector2 a, Vector2 b, float width, float feather, Color color) {
		drawFullLine(a, b,width,feather,color);
		flush();
	}
	
	private void drawFullLine(Vector2 a, Vector2 b, float width, float feather, Color color) {
		Vector2 norm,feath;
		Vector2 bL, bR, tL, tR;
		
		
		RENDER_TYPE = GL20.GL_TRIANGLE_STRIP;

		// Faded color
		Color fadedCol = color.cpy();
		fadedCol.a = 0;
		
		
		// Calculate the normal that defines the center rectangle
		norm = b.cpy();
		norm.sub(a);
		norm.rotate(90);
		norm.nor(); // Set to 1
		norm.scl(width/2); // Scale to line width

		// Calculate the normal that defines feathering
		feath = norm.cpy().nor();
		feath.scl(feather/2);

		// ORDER OF RENDERING
		
		
		// 1                    2
				//top feather
		// 3                    4
	
				// center
		
		// 5                    6
				//bottom feather
		// 7                    8
		
		
		// Top feather 
		tL = a.cpy().add(norm).add(feath);
		tR = b.cpy().add(norm).add(feath);
		bL = a.cpy().add(norm);
		bR = b.cpy().add(norm);
		
		// Draw it
		// The first triangle  (1-2-3)
		putVertex(tL,fadedCol);
		putVertex(tR,fadedCol);
		putVertex(bL, color);
		
		// The second triangle (2-3-4);
		putVertex(bR, color);

		// Center line 
		bL = a.cpy().sub(norm);
		bR = b.cpy().sub(norm);
		
		// Draw it
		putVertex(bL,color);// 3-4-5
		putVertex(bR,color);

		// Bottom feather 
		bL = a.cpy().sub(norm).sub(feath);
		bR = b.cpy().sub(norm).sub(feath);
		
		// Draw it
		putVertex(bL,fadedCol);
		putVertex(bR,fadedCol);
	}
	
	
	public void drawTriangle(Vector2 a, Vector2 b, Vector2 c, Color color) {
		drawTriangle(a,b,c,color,color,color);
	}
	
	public void drawTriangle(Vector2 a, Vector2 b, Vector2 c, Color c1,
			Color c2, Color c3) {
		RENDER_TYPE = GL20.GL_TRIANGLES;

		// Draw it
		// The first triangle  (1-2-3)
		putVertex(a,c1);
		putVertex(b,c2);
		putVertex(c,c3);
		
		flush();
	}
	
	public void drawArc(Vector2 cent, float fromDeg, float toDeg, float rad, float thick, Color col, int seg) {
		RENDER_TYPE = GL20.GL_TRIANGLE_STRIP;
		float x,y, d, j =0;
		// Make it even, makes sure it works properly
		if (seg % 2 != 0) {
			seg ++;
		}
		
		for (int i = 0; i < (seg+2); i++) {
			d = ((toDeg - fromDeg) / (seg/2f)) * j;
			d += fromDeg;
			d *= deg2rad;
			if (i % 2 ==0) { // go low 
				x = (float) ((rad)*Math.cos(d) + cent.x); 
				y = (float) ((rad)*Math.sin(d) + cent.y);
				putVertex(x,y,col);
			} else {
				x = (float) ((rad+thick)*Math.cos(d) + cent.x); 
				y = (float) ((rad+thick)*Math.sin(d) + cent.y);
				putVertex(x,y,col);
				j++;
			}
		}
		flush();
	}
	
	public void drawTriangleFeathered(Vector2 a, Vector2 b, Vector2 c, Color ca, Color cb, Color cc, float feather) {
		System.out.println("Incomplete function, doesn't work.");
		System.exit(1);
		// a, b, c = outer points 
		// ai, bi, ci = inner points
		RENDER_TYPE = GL20.GL_TRIANGLE_STRIP;
		
		Vector2 center; 
		center = a.cpy().add(b);
		center.add(c);
		center.scl(1/3f);

		
		Vector2 ai = a.cpy(),bi = b.cpy(),ci = c.cpy();
		ai.sub((a.cpy().sub(center)).scl(feather));
		
		bi.sub((b.cpy().sub(center)).scl(feather));
		ci.sub((c.cpy().sub(center)).scl(feather));
		
		
		Color fca = ca.cpy(), fcb = cb.cpy(), fcc = cc.cpy();
		fca.a = 0;
		fcb.a = 0;
		fcc.a = 0;
		
		Color tmp1 = Color.WHITE.cpy();
		Color tmp2 = Color.WHITE.cpy();
		tmp1.a = 0.2f;
		tmp2.a = 0.4f;
		putVertex(a,tmp1);
		putVertex(b,tmp1);
		putVertex(c,tmp1);
		flush();
		putVertex(ai,tmp2);
		putVertex(bi,tmp2);
		putVertex(ci,tmp2);
		flush();
		// Draw the inner triangle
	//	putVertex(a, fca);
		putVertex(b, fcb);
		putVertex(c, fcc);
		
		putVertex(ci,cc);
		putVertex(a,fca);		
		putVertex(ai,ca);
		// Now draw the outer bit 
	/*	putVertex(c, fcc);
		putVertex(ai, ca);
		putVertex(a, fca);
		putVertex(bi, cb);
		putVertex(b, fcb);
		putVertex(c, fcc);
*/
		flush();

	}
	
	public void drawTriangleFeathered(Vector2 a, Vector2 b, Vector2 c, Color col, float feather) {
		// a, b, c = outer points 
		// ai, bi, ci = inner points
		RENDER_TYPE = GL20.GL_TRIANGLE_STRIP;
		
		Vector2 center; 
		center = a.cpy().add(b);
		center.add(c);
		center.scl(1/3f);

		
		Vector2 ai = a.cpy(),bi = b.cpy(),ci = c.cpy();
		ai.sub((a.cpy().sub(center)).scl(feather));
		
		bi.sub((b.cpy().sub(center)).scl(feather));
		ci.sub((c.cpy().sub(center)).scl(feather));
		
		
		Color fc = col.cpy();
		fc.a = 0;

		// Draw the inner triangle
		putVertex(ai, col);
		putVertex(bi, col);
		putVertex(ci, col);
		
		
		// Jump around a bit
		putVertex(c,fc);
		putVertex(ai,col);
		putVertex(a,fc);
		putVertex(bi,col);
		putVertex(b,fc);
		putVertex(c, fc);

		flush();

	}
	
	
	private void putVertex(Vector2 v1, 
			Color color) {
		if (idx>verts.length - 6) {
			flush();
		}

		// v1
		verts[idx++] = v1.x;
		verts[idx++] = v1.y;
		verts[idx++] = color.r; 	//Color(r, g, b, a)
		verts[idx++] = color.g;
		verts[idx++] = color.b;
		verts[idx++] = color.a;
			
	}
	
	private void putVertex(float x, float y, 
			Color color) {
		if (idx>verts.length - 6) {
			flush();
		}

		// v1
		verts[idx++] = x;
		verts[idx++] = y;
		verts[idx++] = color.r; 	//Color(r, g, b, a)
		verts[idx++] = color.g;
		verts[idx++] = color.b;
		verts[idx++] = color.a;
			
	}
	
	@Override
	public void dispose() {
		mesh.dispose();
		shader.dispose();
	}



	public void emptyRect(float x, float y, float w, float h, Color color, float lineWidth) {
		// Draw a rectangle.
		
		// NOTE: the diagram below accurately depicts edging on the lines to stop odd looking rectangles
		// side lines are shorter than they should be, top/bottoms are longer
		///  h	----- 1 -----
		/// 	|			|
		/// 	2			3		
		/// 	|			|
		///  xy ---- 4 -----w
		
		drawLine(new Vector2(x-lineWidth/2,y+h), new Vector2(x+w+lineWidth/2,y+h), lineWidth, color);
		drawLine(new Vector2(x,y), new Vector2(x,y+h), lineWidth, color);
		drawLine(new Vector2(x+w,y), new Vector2(x+w,y+h), lineWidth, color);
		drawLine(new Vector2(x-lineWidth/2,y), new Vector2(x+w+lineWidth/2,y), lineWidth, color);
	}



	public void fillRect(float x0, float y0, float x1, float y1,
			Color bottomLeft,
			Color bottomRight,
			Color topLeft, 
			Color topRight
			) {		
		RENDER_TYPE = GL20.GL_TRIANGLE_STRIP;
		if (idx > 0) {
			flush();
		}
		putVertex(x0,y0,bottomLeft);
		putVertex(x0+x1,y0,bottomRight);
		putVertex(x0,y0+y1,topLeft);
		putVertex(x0+x1,y0+y1,topRight);
		flush();
	}

	public void fillRect(float x0, float y0, float x1, float y1,
			Color color
			) {		
		RENDER_TYPE = GL20.GL_TRIANGLE_STRIP;
		if (idx > 0) {
			flush();
		}
		putVertex(x0,y0,color);
		putVertex(x0+x1,y0,color);
		putVertex(x0,y0+y1,color);
		putVertex(x0+x1,y0+y1,color);
		flush();
	}

	

	public void drawCircle(float cX, float cY, float rad, Color col,int seg) {
		if (idx > 0) {
			flush();
		}
		RENDER_TYPE = GL20.GL_TRIANGLE_FAN;
		if (seg < 3){
			seg = 3;
		}

		// Initial vertex at center
		putVertex(cX,cY,col);

		float radPos, x, y;
		for (int i = 0; i < seg+1; i ++) {
			radPos = (float) ((Math.PI * 2) * (1f*i/seg));
			
			x = (float) (rad*Math.cos(radPos) + cX); 
			y = (float) (rad*Math.sin(radPos) + cY);
			putVertex(x, y, col);
		}
		
		flush();
	}
	
	public void drawCircle(float cX, float cY, float rad, Color col, float fadeFrom,int seg) {
		float innerRad = rad * fadeFrom;
		if (seg < 3){
			seg = 3;
		}
		drawCircle(cX,cY,innerRad,col,seg);
		RENDER_TYPE = GL20.GL_TRIANGLE_STRIP;
		Color fadedCol = col.cpy();
		fadedCol.a = 0;
		float radPos, x, y;
		for (int i = 0; i < seg+1; i ++) {
			radPos = (float) ((Math.PI * 2) * (1f*i/seg));
			
			x = (float) (rad*Math.cos(radPos) + cX); 
			y = (float) (rad*Math.sin(radPos) + cY);
			putVertex(x, y, fadedCol);
						
			x = (float) (innerRad*Math.cos(radPos) + cX); 
			y = (float) (innerRad*Math.sin(radPos) + cY);
			putVertex(x, y, col);
			
		}
		
		flush();
	}
	

}