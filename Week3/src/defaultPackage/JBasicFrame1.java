package defaultPackage;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

public class JBasicFrame1 {
	private JFrame frame;
	public JBasicFrame1() {
		frame = new JFrame("Frame Test1");
		//frame.setTitle("Frame Test1");
		
		/*
		 * frame.setSize(200,300); 
		 * frame.setLocation(100,200);
		 */
		buildGUI();
		frame.setBounds(100,200,200,300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	private void buildGUI() {
		//frame.setLayout(new BorderLayout());
		
		JButton btn;
		btn = new JButton("1");
		frame.add(btn, BorderLayout.NORTH);
		
		btn = new JButton("2");
		frame.add(btn, BorderLayout.WEST);
		
		btn = new JButton("3");
		frame.add(btn, BorderLayout.SOUTH);
		
		btn = new JButton("4");
		frame.add(btn, BorderLayout.EAST);
		
		btn = new JButton("5");
		frame.add(btn);
	}
}
