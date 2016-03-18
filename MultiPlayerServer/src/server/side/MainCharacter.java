package server.side;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import server.side.models.Box;
import server.side.models.Bullet;
import server.side.models.CharacterObj;

public class MainCharacter{

	private static final int MAP_WIDTH = 1500;
	private static final int MAP_HEIGTH = 900;
	
	private float r;
	private float g;
	private float b;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	//Thread safe list because bullets can be updated while 
	//iterating them which would resolve in an error.
	private List<ServerBullet> bullets;
	
	private long id;

	private int xVel;
	private int yVel;
	
	private int xp;				
	
	/** Constructor for new player
	 * 
	 * @param data This is data that we get from client side and put on the server side
	 * to simulate game.
	 */
	MainCharacter(CharacterObj data) {
		
		x = 0;
		y = 0;
		
		Random randomFloat = new Random(System.currentTimeMillis());
		r = randomFloat.nextFloat();
		b = randomFloat.nextFloat();
		g = randomFloat.nextFloat();

		xVel = data.xVel;
		yVel = data.yVel;
		
		width = 50;
		height = 50;
		
		id = data.id;
		
		bullets = Collections.synchronizedList(new ArrayList<ServerBullet>());
			
		xp = 100;
		
		addBullets(data.newBullets);
	}
	
	/**
	 * If player already exists we need only to update velocity and add new bullets.
	 * @param data This is data that we get from client side and put on the server side
	 * to simulate game.
	 */
	
	void updateState(CharacterObj data) {
		
		xVel = data.xVel;
		yVel = data.yVel;
		addBullets(data.newBullets);
	}	

	/**
	 * Function to add bullets that we get from client side to corresponding character.
	 * @param newBullets New bullets.
	 */
	
	private void addBullets(List<Bullet> newBullets){
		if (newBullets == null)	return;
		
		for (Bullet sb : newBullets){
			bullets.add(new ServerBullet(sb.x, sb.y, sb.k, sb.c, sb.pn, r, g, b));
		}
	}
	
	/**
	 * This function updates character's and bullets' positions.
	 * Deletes bullet if there is a collision, changes XPs of enemies.
	 * @param tiles 	Simple obstacles.
	 * @param fullCharacters	All players on-line.
	 * @return Returns main character and bullets in a Box class form. Box class contains
	 * only information necessary to render objects on the client side.
	 */
	
	List<Box> update(List<Box> tiles, Vector<MainCharacter> fullCharacters) {
		
		List<Box> boxes = new ArrayList<Box>();
		
		//updating bullets
		synchronized (bullets) {
			
			Iterator<ServerBullet> itr = bullets.listIterator();
			while (itr.hasNext()) {
				
				ServerBullet bullet = itr.next();
				if (bullet.update(tiles, fullCharacters, id)) {
					itr.remove();
				}
				else{
					boxes.add(new Box(bullet.x, bullet.y, bullet.width, bullet.height, 
							bullet.r, bullet.g, bullet.b, -1L, -1));
				}
			}
		}
		//updating character
		x += xVel;
		if (x < 0 || x + width > MAP_WIDTH) {
			x -= xVel;
		}
		y += yVel;
		if (y < 0 || y + height > MAP_HEIGTH) {
			y -= yVel;
		}
		
		//checking collision with obstacles
		for (Box obs : tiles) {
			if (collision(x, y, x + width, y + height,
					obs.x, obs.y, obs.x + obs.w, obs.y + obs.h)){
				x -= xVel;
				y -= yVel;
			}
		}
		
		//if xp is below 1 we reset player to its initial position
		if (xp < 1){
			x = y = 0;
			xp = 100;
		}
		
		boxes.add(new Box(x, y, width, height, r, g, b, id, xp));
		return boxes;
	}

	public long getID(){
		return id;
	}
	/**
	 * Function to check if two square objects collide.
	 */
	private boolean collision(float f, float h, float i, float j, float left,
			float top, float right, float buttom) {
		return f < right && i > left && h < buttom && j > top;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public float getR(){
		return r;
	}
	
	public float getG(){
		return g;
	}
	
	public float getB(){
		return b;
	}
	
	/**
	 * ServerBullet class represents bullets of main character
	 */
	private class ServerBullet {

		private float d, 	// distance between old and new bullet position
				direc; 		// y=kx+c going up or down
		private float k, c, x, y; 	// y=kx+c
		private int width, height;
		private float r, g,b;
		
		public ServerBullet(float x, float y, float k, float c, float direc, float r, float g, float b){
			
			this.x = x;
			this.y = y;
			this.c = c;
			this.k = k;
			this.direc = direc;
			this.r = r;
			this.g = g;
			this.b = b;
			
			d = 8f;
			width = height = 10;
		}
		
		/**
		 * Updates bullets state.
		 * @param obstacles		Simple square obstacles .
		 * @param fullCharacters	All characters.
		 * @param id	Id of this character so we don't check collision with itself.
		 * @return If there was a collision returns true otherwise false.
		 */
		
		private boolean update(List<Box> obstacles,Vector<MainCharacter> fullCharacters, long id) {
			
			//collision with tiles
			for (Box obs : obstacles) {
				if (collision(x, y, x + width, y + height,
						obs.x, obs.y, obs.x + obs.w, obs.y + obs.h)) {
					return true;
				}
			}
			//collision with enemies
			for (MainCharacter mc : fullCharacters){
				if (mc.id == id)
					continue;
				if (collision(x, y, x + width, y + height,
						mc.x, mc.y, mc.x + mc.width, mc.y + mc.height)){
					mc.xp -= 30;
					return true;
				}
			}

			//collision with map
			if (x < 0 || x > MAP_WIDTH || y < 0 || y > MAP_HEIGTH) {
				return true;
			}

			/* 
			 * Super cool formula to find next x that is d (distance) away from
			 * starting point.
			 * Used distance formula and wolfram alpha to express next x position 
			 */
			x = (float) (-c * k + x + k * y - direc
					* Math.sqrt(-c * c + d * d + d * d * k * k - 2 * c * k * x
							- k * k * x * x + 2 * c * y + 2 * k * x * y - y * y))
					/ (1 + k * k);
			y = k * x + c;
			
			return false;
		}

	}

}
