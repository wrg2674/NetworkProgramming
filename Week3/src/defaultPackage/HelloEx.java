package defaultPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class HelloEx{
	private JFrame frame;
	private JTextField text;
	private JLabel label;
	public HelloEx() {
		frame = new JFrame("HelloEx");
		buildGUI();
		frame.setSize(200,80);
		frame.setLocation(500,300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	private void buildGUI() {
		label = new JLabel("Hello World");
		frame.add(createInputPanel(), BorderLayout.CENTER);
		
		frame.add(label, BorderLayout.SOUTH);
	}
	private JPanel createInputPanel() {
		text=new JTextField(10);
		JButton button = new JButton("확인");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String name = text.getText();
				label.setText("Hello,"+name);
				text.setText("");
			}
			
		});
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(text);
		panel.add(button);
		panel.setBackground(Color.ORANGE);
		return panel;
	}
	private ActionListener handler = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String name = text.getText();
			label.setText("Hello,"+name);
			text.setText("");
		}
		
	};
	class ActionEventHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String name = text.getText();
			label.setText("Hello,"+name);
			text.setText("");
		}
		
	}
	
}