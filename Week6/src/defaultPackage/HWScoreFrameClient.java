package defaultPackage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class HWScoreFrameClient {
	private JFrame frame;
	private JTextField text;
	private JTextArea textArea;
	private JLabel label;
	private JButton b_send;
	private JButton b_exit;
	private JButton b_disconnect;
	private JButton b_connect;
	private JLabel l_grade;
	private String serverAddress;
	private int serverPort;
	private Socket clientSocket;
	private Writer out;
	private Reader in;
	
	public HWScoreFrameClient() {
		frame = new JFrame("EchoClientFrame");
		
		buildGUI();
		frame.setBounds(100, 200, 200, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		serverPort = 1234;
		serverAddress = "localhost";
	}

	private void buildGUI() {
		frame.setLayout(new BorderLayout());
		frame.add(createInputPanel());
		frame.add(createControlPanel(), BorderLayout.SOUTH);

	}

	private JPanel createInputPanel() {
		text = new JTextField(10);
		text.setEnabled(false);
		text.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				sendMessage();
				receiveMessage();
			}
			
		});
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		l_grade = new JLabel("학점 확인");
		panel.add(new JLabel("이름 : "));
		panel.add(text);
		panel.add(l_grade);
		return panel;
	}

	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 3));
		b_connect = new JButton("접속하기");
		b_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				connectToServer();
				b_connect.setEnabled(false);
				b_disconnect.setEnabled(true);
				//b_send.setEnabled(true);
				text.setEnabled(true);
			}
			
		});
		panel.add(b_connect);
		b_disconnect = new JButton("접속 끊기");
		b_disconnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				disconnect();
				b_connect.setEnabled(true);
				b_disconnect.setEnabled(false);
				//b_send.setEnabled(false);
				text.setEnabled(false);
			}
			
		});
		b_disconnect.setEnabled(false);
		panel.add(b_disconnect);
		b_exit = new JButton("종료하기");
		b_exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(-1);
			}
			
		});
		panel.add(b_exit);
		return panel;
	}
	private void connectToServer() {
		try {
			clientSocket = new Socket(serverAddress, serverPort);
			out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
		}catch(IOException e) {
			System.err.println("소켓 생성 실패");
			System.exit(-1);
		}
	}
	private void disconnect() {
		try {
			out.close();
			clientSocket.close();
		}catch(IOException e) {
			System.err.println("소켓 닫기 실패");
			System.exit(-1);
		}
	}
	private void sendMessage() {
		try {
			String str = text.getText().trim();
			out.write(str+"\n");
			out.flush();
			//textArea.append("나: "+str+"\n");
			text.setText("");
		}catch(IOException e) {
			System.err.println("데이터 쓰기 실패");
			System.exit(-1);
		}catch(NumberFormatException e) {
			System.err.println("정수 변환 실패");
			System.exit(-1);
		}
	}
	private void receiveMessage() {
		try {
			String grade = ((BufferedReader)in).readLine();
			l_grade.setText("학점 : "+grade);
		}catch(IOException e) {
			System.err.println("클라이언트 일반 수신 오류 > " + e.getMessage());
		}
		
	}
	public static void main(String arg[]) {
		new HWScoreFrameClient();
	}
}
