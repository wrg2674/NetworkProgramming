import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Unit {
	public static final int UNIT_SIZE = 50;
	public static final int MOVE_DISTANCE = 5;
	
	private Color color;
	private int x;
	private int y;
	
	private int xDirection;
	private int yDirection;
	
	
	public Unit(Color color, int x, int y) {
		this.color = color;
		this.x=x;
		this.y = y;
		this.xDirection=0;
		this.yDirection=0;
		
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getXDirection() {
		return xDirection;
	}
	public int getYDirection() {
		return yDirection;
	}
	public void setXY(int x, int y) {
		this.x=x;
		this.y=y;
	}
	public void setXY(Point p) {
		this.x=p.x;
		this.y=p.y;
	}
	public void startMovingLeft() {
		xDirection = -1;
	}
	public void startMovingRight() {
		xDirection = 1;
	}
	public void startMovingUp() {
		yDirection = -1;
	}
	public void startMovingDown() {
		yDirection = 1;
	}
	public void stopMoving() {
		xDirection =0;
		yDirection =0;
	}
	public void move() {
		x += xDirection * MOVE_DISTANCE;
		y += yDirection * MOVE_DISTANCE;
		
		if(x<=0|| x+UNIT_SIZE >= GameScreen.FRAME_WIDTH) {
			xDirection = -xDirection;
		}
		if(y<=0||y+UNIT_SIZE >= GameScreen.FRAME_HEIGHT) {
			yDirection = -yDirection;
		}
	}
	public void moveBack() {
		x -= xDirection * MOVE_DISTANCE;
		y -= yDirection*MOVE_DISTANCE;
	}
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(x,  y, UNIT_SIZE, UNIT_SIZE);
	}
}
