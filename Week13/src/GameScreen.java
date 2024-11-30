import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JComponent;

public class GameScreen extends JComponent {
	public static final int FRAME_WIDTH =500;
	public static final int FRAME_HEIGHT=500;
	
	private Unit player1;
	private Unit player2;
	
	private Thread moveThread;
	
	Random random = new Random();
	
	public GameScreen() {
		this.addPlayer(1);
		this.addPlayer(2);
		this.setFocusable(true);
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int key =e.getKeyCode();
				switch(key) {
				case KeyEvent.VK_RIGHT:
					player1.startMovingRight();
					break;
				case KeyEvent.VK_LEFT:
					player1.startMovingLeft();
					break;
				case KeyEvent.VK_UP:
					player1.startMovingUp();
					break;
				case KeyEvent.VK_DOWN:
					player1.startMovingDown();
					break;
				case KeyEvent.VK_SPACE:
					player1.stopMoving();
					break;
				case KeyEvent.VK_D:
					player2.startMovingRight();
					break;
				case KeyEvent.VK_A:
					player2.startMovingLeft();
					break;
				case KeyEvent.VK_W:
					player2.startMovingUp();
					break;
				case KeyEvent.VK_S:
					player2.startMovingDown();
					break;
				case KeyEvent.VK_Q:
					player2.stopMoving();
					break;
				}
			}
		});
	}
	public void addPlayer(int id) {
		int initialX = random.nextInt(FRAME_WIDTH - Unit.UNIT_SIZE);
		int initialY = random.nextInt(FRAME_HEIGHT - Unit.UNIT_SIZE);
		
		if(id == 1) {
			player1 = new Unit(Color.BLUE, initialX, initialY);
		}
		else {
			player2 = new Unit(Color.RED, initialX, initialY);
		}
		repaint();
	}
	public void moveUnits() {
		player1.move();
		player2.move();
		
		if(checkCollision(player1, player2)) {
			player1.stopMoving();
			player2.stopMoving();
		}
		repaint();
	}
	private boolean checkCollision(Unit unit1, Unit unit2) {
		int x1=unit1.getX();
		int y1 = unit1.getY();
		int x2=unit2.getX();
		int y2=unit2.getY();
		boolean collisionX = (x1 < x2 + Unit.UNIT_SIZE && x1+Unit.UNIT_SIZE > x2);
		boolean collisionY = (y1 < y2 + Unit.UNIT_SIZE && y1+Unit.UNIT_SIZE > y2);
		
		if(collisionX && collisionY) {
			unit1.moveBack();
			unit2.moveBack();
			
			return true;
		}
		return false;
		
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		player1.draw(g);
		player2.draw(g);
	}
	public void start() {
		moveThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(moveThread == Thread.currentThread()) {
					moveUnits();
					try {
						Thread.sleep(10);
					}catch(InterruptedException e) {
						
					}
				}
			}
		});
		moveThread.start();
		this.requestFocus();
	}
	public void stop() {

		moveThread = null;
	}
}
