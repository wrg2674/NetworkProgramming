import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class DoubleBufferingTest2 extends JFrame{
	
	CarCanvas carCanvas;
	
	public DoubleBufferingTest2() {
		super("Using Double Buffering");
		
		buildGUI();
		this.setSize(1000, 500);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		carCanvas.start();
	}
	private void buildGUI() {
		JButton b_change = new JButton("전환");
		b_change.setBounds(100,400,200,50);
		b_change.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				carCanvas.useDoubleBuffering = !carCanvas.useDoubleBuffering;
				
			}
		});
		carCanvas = new CarCanvas();
		this.add(carCanvas);
		this.add(b_change, BorderLayout.SOUTH);
		carCanvas.requestFocus();
	}
	class CarCanvas extends JComponent{
		private Image img;
		private boolean useDoubleBuffering= false;
		private Image bufferImage;
		private Graphics bufferGraphics;
		private int speed_x, speed_y;
		private int x, y;
		
		private Thread thread;
		
		CarCanvas(){
			loadImage();
			this.setFocusable(true);
			
			this.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					switch(e.getKeyChar()) {
					case 'g':
					case 'G':
						if(thread == null) {
							start();
						}
						break;
					case 's':
					case 'S':
						thread = null;
						break;
					}
					
				}
			});
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					Component c = (Component)e.getSource();
					c.setFocusable(true);
					c.requestFocus();
				}
			});
		}
		
		private void loadImage() {
			img = Toolkit.getDefaultToolkit().getImage("car.png");
			
			MediaTracker tracker = new MediaTracker(this);
			tracker.addImage(img,  0);
			try {
				tracker.waitForAll();
			}catch(InterruptedException e) {
				
			}
			init();
		}
		private void init() {
			x=0;
			y=20;
			speed_x=3;
			speed_y=0;
		}
		@Override
		public void paint(Graphics g) {
			int w=this.getWidth();
			int h= this.getHeight();
			if(useDoubleBuffering) {
				if(bufferGraphics == null) {
					bufferImage=createImage(w,h);
					bufferGraphics = bufferImage.getGraphics();
				}
				super.paint(bufferGraphics);
				drawACar(bufferGraphics, w, h);
				g.drawImage(bufferImage, 0, 0, this);
			}
			else {
				super.paint(g);
				drawACar(g,w,h);
			}
		}
		private void drawACar(Graphics g, int w, int h) {
			g.clearRect(0, 0, w, h);
			update();
			g.drawImage(img, x, h, this);
			if(useDoubleBuffering) {
				g.drawString("Use Double-Buffer", 50, 50);
			}
			else {
				g.drawString("Not Use Double-Buffering", 50, 50);
			}
		}
		private void update() {
			x += speed_x;
			y += speed_y;
			if(x>=getWidth()) {
				init();
			}
		}
		private void start() {
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					while(thread == Thread.currentThread()) {
						repaint();
						try {
							Thread.sleep(10);
						}catch(InterruptedException e) {
							
						}
					}
				}
			});
			thread.start();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new DoubleBufferingTest2();
	}

}
