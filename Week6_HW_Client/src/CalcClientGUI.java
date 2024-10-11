// 1971135 정구현
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CalcClientGUI {
	private JFrame frame;
	private JTextField num1;
	private JTextField num2;
	private JTextField operator;
	private JTextField result;
	private JLabel label;
	private JButton b_send;
	private JButton b_exit;
	private JButton b_disconnect;
	private JButton b_connect;
	private String serverAddress;
	private int serverPort;
	private Socket clientSocket;
	private ObjectOutputStream out;
	private DataInputStream in;
	
	public CalcClientGUI() {
		frame = new JFrame("CalcClient GUI");
		
		buildGUI();
		frame.setBounds(100, 200, 600, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		serverPort = 1234;
		serverAddress = "localhost";
	}

	private void buildGUI() {
		frame.setLayout(new BorderLayout());
		frame.add(createInputPanel());
		frame.add(createSouthPanel(), BorderLayout.SOUTH);

	}

	private JPanel createInputPanel() {
		JPanel panel = new JPanel();
		JLabel equal = new JLabel("=");
		num1 = new JTextField(10);
		num2 = new JTextField(10);
		operator = new JTextField(3);
		result = new JTextField(10);
		b_send = new JButton("계산");
		b_send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				sendMessage();
				receiveMessage();
			}
			
		});
		result.setEditable(false);
		num1.setEnabled(false);
		num2.setEnabled(false);
		operator.setEnabled(false);
		b_send.setEnabled(false);
		
		
		panel.add(num1);
		panel.add(operator);
		panel.add(num2);
		panel.add(equal);
		panel.add(result);
		panel.add(b_send);
		return panel;
	}
	private JPanel createSouthPanel() {
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.add(createControlPanel());
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
				b_send.setEnabled(true);
				num1.setEnabled(true);
				num2.setEnabled(true);
				operator.setEnabled(true);
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
				b_send.setEnabled(false);
				num1.setEnabled(false);
				num2.setEnabled(false);
				operator.setEnabled(false);
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
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new DataInputStream(clientSocket.getInputStream());
			
		}catch(IOException e) {
			System.err.println("소켓 생성 실패");
			System.exit(-1);
		}
	}
	private void disconnect() {
		try {
			out.close();
			in.close();
			clientSocket.close();
		}catch(IOException e) {
			System.err.println("소켓 닫기 실패");
			System.exit(-1);
		}
	}
	private void sendMessage() {
		
		 try { 
			 CalcExpr data = new CalcExpr(Double.parseDouble(num1.getText()), operator.getText().charAt(0), Double.parseDouble(num2.getText()));
			 out.writeObject(data);
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
			double msg = in.readDouble();
			String str = String.format("%.2f", msg);
			result.setText(str);
		}catch(IOException e) {
			System.err.println("클라이언트 일반 수신 오류 > " + e.getMessage());
		}
		
	}
	public static void main(String args[]) {
		new CalcClientGUI();
	}
}
