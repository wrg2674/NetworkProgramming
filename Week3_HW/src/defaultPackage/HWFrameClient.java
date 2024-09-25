package defaultPackage;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class HWFrameClient {
	private JFrame frame;
	private JTextField text;
	private JTextArea textArea;
	private JLabel label;

	public HWFrameClient() {
		frame = new JFrame("Frame");

		buildGUI();
		frame.setBounds(100, 200, 200, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private void buildGUI() {
		frame.setLayout(new BorderLayout());
		frame.add(createDisplayPanel());
		frame.add(createSouthPanel(), BorderLayout.SOUTH);

	}

	private JPanel createDisplayPanel() {
		textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(scrollPane);
		return panel;
	}
	private JPanel createSouthPanel() {
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.add(createInputPanel());
		panel.add(createControlPanel());
		return panel;
		
	}

	private JPanel createInputPanel() {
		text = new JTextField(20);
		text.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
			
		});
		JButton button = new JButton("보내기");
		button.setEnabled(false);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
			
		});
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(text, BorderLayout.CENTER);
		panel.add(button, BorderLayout.EAST);
		return panel;
	}

	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 3));
		JButton btn;
		btn = new JButton("접속하기");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
			
		});
		panel.add(btn);
		btn = new JButton("접속 끊기");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
			
		});
		btn.setEnabled(false);
		panel.add(btn);
		btn = new JButton("종료하기");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
			
		});
		panel.add(btn);
		return panel;
	}
}