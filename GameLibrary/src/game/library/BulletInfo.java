package game.library;

import java.io.Serializable;

public class BulletInfo implements Serializable{
	
	public float x;
	public float y;
	public float k;
	public float c;
	public float pn;
	
	public BulletInfo(float x, float y, float k, float c, float pn){
		this.x = x;
		this.y = y;
		this.k = k;
		this.c = c;
		this.pn = pn;
	}
}
