

import java.awt.BorderLayout;
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class P2PFreeTalk {
	private JFrame frame;
	private JTextField text;
	private JTextArea textArea;
	private JLabel label;
	private JButton b_send;
	private JButton b_exit;
	private JButton b_disconnect;
	private JButton b_connect;
	private String serverAddress;
	private JTextField t_userID;
	JTextField t_serverAddress;
	JTextField t_portNumber;
	private int serverPort;
	private Socket clientSocket;
	private Writer out;
	private Reader in;
	private Thread receiveThread =null;
	
	public P2PFreeTalk(String serverAddress, int serverPort) {
		frame = new JFrame("P2P Free  Talk");
		
		buildGUI();
		frame.setBounds(100, 200, 200, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		this.serverPort = serverPort;
		this.serverAddress = serverAddress;
	}

	private void buildGUI() {
		frame.setLayout(new BorderLayout());
		frame.add(createDisplayPanel());
		frame.add(createInfoPanel());
		frame.add(createSouthPanel(), BorderLayout.SOUTH);

	}
	private JPanel createInfoPanel() {
		
		InetAddress local=null;
		try {
			local = InetAddress.getLocalHost();
		}
		catch(IOException e) {
			System.err.println("주소 가져오기 실패");
		}
		String addr = local.getHostAddress();
		String[] part = addr.split("\\.");
		
		JPanel panel = new JPanel(new BorderLayout());
		JLabel l_userID = new JLabel("아이디: ");
		t_userID = new JTextField();
		t_userID.setText("guest"+part[3]);
		JLabel l_serverAddress = new JLabel("서버주소: ");
		t_serverAddress = new JTextField();
		t_serverAddress.setText(serverAddress);
		JLabel l_portNumber = new JLabel("포트번호: ");
		t_portNumber = new JTextField();
		t_portNumber.setText(""+serverPort);
		
		panel.add(l_userID);
		panel.add(t_userID);
		panel.add(l_serverAddress);
		panel.add(t_serverAddress);
		panel.add(l_portNumber);
		panel.add(t_portNumber);
		
		return panel;
		
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
		text.setEnabled(false);
		text.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				sendMessage();
				//receiveMessage();
			}
			
		});
		b_send = new JButton("보내기");
		b_send.setEnabled(false);
		b_send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				sendMessage();
				//receiveMessage();
			}
			
		});
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(text, BorderLayout.CENTER);
		panel.add(b_send, BorderLayout.EAST);
		return panel;
	}

	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 3));
		b_connect = new JButton("접속하기");
		b_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				serverAddress = t_serverAddress.getText();
				serverPort=Integer.parseInt(t_portNumber.getText());
				
				connectToServer();
				sendUserID();
				b_connect.setEnabled(false);
				b_disconnect.setEnabled(true);
				b_send.setEnabled(true);
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
				b_send.setEnabled(false);
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
	private void sendUserID() {
		try {
			out.write("/uid: "+t_userID.getText()+"\n");
		}
		catch(IOException e) {
			System.err.println("유저 아이디 전송 실패");
		}
	}
	private void connectToServer() {
		try {
			clientSocket = new Socket();
			SocketAddress sa = new InetSocketAddress(serverAddress, serverPort);
			clientSocket.connect(sa, 3000);
			out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));

			receiveThread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(receiveThread == Thread.currentThread()) {
						receiveMessage();
					}
				}
				
			});
			receiveThread.start();
		}catch(IOException e) {
			textArea.setText("서버 접속 실패 : " + e.getMessage());
			b_connect.setEnabled(true);
			b_disconnect.setEnabled(false);
			b_send.setEnabled(false);
			text.setEnabled(false);
		}
	}
	private void disconnect() {
		try {
			receiveThread=null;
			out.close();
			clientSocket.close();
		}catch(IOException e) {
			System.err.println("소켓 닫기 실패");
			System.exit(-1);
		}
	}
	private void sendMessage() {
		try {
			String str = text.getText();
			out.write(str+"\n");
			out.flush();
			String myMsg = ((BufferedReader)in).readLine();
			textArea.append(myMsg);
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
		String inMsg;
		try {
			while((inMsg = ((BufferedReader)in).readLine()) != null) {
				printDisplay(inMsg);
			}
			printDisplay("서버와의 연결을 종료합니다.");
			System.exit(0);
			
		}catch(IOException e) {
			System.err.println("클라이언트 일반 수신 오류 > " + e.getMessage());
		}
		
	}
	private void printDisplay(String msg) {
		textArea.append(msg+"\n");
		//커서를 textArea의 가장 끝으로 보내는 코드
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort=54321;
		new P2PFreeTalk(serverAddress, serverPort);
	}
}
