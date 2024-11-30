import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JComponent;

public class GameScreen extends JComponent {
	public static final int FRAME_WIDTH = 500;
	public static final int FRAME_HEIGHT = 500;
	
	private Unit player1;
	private Unit player2;
	
	private Thread moveThread;
	
	Random random = new Random();
	CatchMe app;
	
	public GameScreen(CatchMe app) {
		this.app = app;
		this.setFocusable(true);
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(player1 == null) {
					return;
				}
				int key = e.getKeyCode();
				switch(key) {
				case KeyEvent.VK_RIGHT:
					player1.startMovingRight();
					app.sendDirection(ChatMsg.MOVE_RIGHT, player1.getX(), player1.getY());
					break;
				case KeyEvent.VK_LEFT:
					player1.startMovingLeft();
					app.sendDirection(ChatMsg.MOVE_LEFT, player1.getX(), player1.getY());
					break;
				case KeyEvent.VK_UP:
					player1.startMovingUp();
					app.sendDirection(ChatMsg.MOVE_UP, player1.getX(), player1.getY());
					break;
				case KeyEvent.VK_DOWN:
					player1.startMovingDown();
					app.sendDirection(ChatMsg.MOVE_DOWN, player1.getX(), player1.getY());
					break;
				case KeyEvent.VK_SPACE:
					player1.stopMoving();
					app.sendDirection(ChatMsg.MOVE_STOP, player1.getX(), player1.getY());
				}
			}
		});
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(player1 != null) {
			player1.draw(g);
		}
		if(player2 != null) {
			player2.draw(g);
		}
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
	public void addPlayer(int id) {
		int initialX = random.nextInt(FRAME_WIDTH- Unit.UNIT_SIZE);
		int initialY = random.nextInt(FRAME_HEIGHT-Unit.UNIT_SIZE);
		
		if(id == 1) {
			player1 = new Unit(Color.BLUE, initialX, initialY);
		}
		else {
			player2 = new Unit(Color.RED, initialX, initialY);
		}
		repaint();
	}
	public void removePlayer(int id) {
		if(id == 1) {
			player1 = null;
		}
		else {
			player2 = null;
		}
	}
	public Unit getPlayer(int id) {
		if(id == 1) {
			return player1;
		}
		else {
			return player2;
		}
	}
	public void moveUnits() {
		
	}
	public void stop() {
		moveThread = null;
	}
	public boolean isReady() {
		return player1 != null && player2 != null;
	}
}
