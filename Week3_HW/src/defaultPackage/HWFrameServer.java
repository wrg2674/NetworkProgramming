package defaultPackage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class HWFrameServer {
	JFrame frame;
	JTextArea textarea;
	JButton btn;
	public HWFrameServer() {
		frame = new JFrame("Frame");

		buildGUI();
		frame.setBounds(100, 200, 200, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	private void buildGUI() {
		frame.setLayout(new BorderLayout());
		frame.add(createDisplayPanel());
		frame.add(createControlPanel(), BorderLayout.SOUTH);
	}
	private JPanel createDisplayPanel() {
		textarea = new JTextArea("서버가 시작되었습니다.");
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(textarea);
		return panel;
		
	}
	private JPanel createControlPanel() {
		btn = new JButton("종료");
		JPanel panel = new JPanel(new BorderLayout());
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
