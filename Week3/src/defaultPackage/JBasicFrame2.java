package defaultPackage;

import javax.swing.JFrame;

public class JBasicFrame2 extends JFrame {
	public JBasicFrame2() {
		super("Frame Test2");
		buildGUI();
		this.setBounds(100,200,200,300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	private void buildGUI() {
		
	}
}
