import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TwoPlayerGame extends JFrame {
	private GameScreen gameScreen;
	
	public TwoPlayerGame() {
		this.setTitle("독립형 2인용 게임");
		buildGUI();
		this.setSize(GameScreen.FRAME_WIDTH,GameScreen.FRAME_HEIGHT+50);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
	}
	private void buildGUI() {
		gameScreen = new GameScreen();
		this.add(gameScreen);
		this.add(createButtonPanel(), BorderLayout.SOUTH);
	}
	private JPanel createButtonPanel() {
		JPanel p = new JPanel(new GridLayout(1, 0));
		JButton b_start = new JButton("시작");
		JButton b_end = new JButton("끝");
		
		p.add(b_start);
		p.add(b_end);
		
		b_start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gameScreen.start();
				gameScreen.requestFocus();
			}
		});
		b_end.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gameScreen.stop();
			}
		});
		return p;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TwoPlayerGame();
	}

}
