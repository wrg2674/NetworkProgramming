import javax.swing.JFrame;

public class GamePrototype extends JFrame {
	private GameScreen gameScreen;
	
	public GamePrototype() {
		this.setTitle("2인용 게임 준비");
		buildGUI();
		this.setSize(500,500);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		gameScreen.start();
	}
	private void buildGUI() {
		gameScreen = new GameScreen();
		this.add(gameScreen);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GamePrototype();
	}

}
