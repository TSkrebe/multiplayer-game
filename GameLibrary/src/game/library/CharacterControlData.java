package game.library;

import java.io.Serializable;
import java.util.List;

public class CharacterControlData implements Serializable{
	
	public int xVel;
	public int yVel; 
	
	public long id;
	
	public List<BulletInfo> newBullets;
	
	public CharacterControlData(int xVel, int yVel, long id) {
		
		this.xVel = xVel;
		this.yVel = yVel;
		this.id = id;
	}

}
