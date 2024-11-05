

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

public class ObjTalk {
	private JFrame frame;
	private JTextField t_input;
	private JTextArea t_display;
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
	private ObjectOutputStream out;
	private Thread receiveThread =null;
	private String uid;
	
	public ObjTalk(String serverAddress, int serverPort) {
		frame = new JFrame("Object Talk");
		this.serverPort = serverPort;
		this.serverAddress = serverAddress;
		buildGUI();
		frame.setBounds(100, 200, 500, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		
		
	}

	private void buildGUI() {
		frame.setLayout(new BorderLayout());
		frame.add(createDisplayPanel());
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
		
		JPanel panel = new JPanel(new FlowLayout());
		JLabel l_userID = new JLabel("아이디: ");
		t_userID = new JTextField(8);
		t_userID.setText("guest"+part[3]);
		JLabel l_serverAddress = new JLabel("서버주소: ");
		t_serverAddress = new JTextField(10);
		t_serverAddress.setText(serverAddress);
		JLabel l_portNumber = new JLabel("포트번호: ");
		t_portNumber = new JTextField(5);
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
		t_display = new JTextArea();
		t_display.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(t_display);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(scrollPane);
		return panel;
	}
	private JPanel createSouthPanel() {
		JPanel panel = new JPanel(new GridLayout(3,1));
		panel.add(createInputPanel());
		panel.add(createInfoPanel());
		panel.add(createControlPanel());
		return panel;
		
	}

	private JPanel createInputPanel() {
		t_input = new JTextField(20);
		t_input.setEnabled(false);
		t_input.addActionListener(new ActionListener() {

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
		panel.add(t_input, BorderLayout.CENTER);
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
				b_connect.setEnabled(false);
				b_disconnect.setEnabled(true);
				b_send.setEnabled(true);
				t_input.setEnabled(true);
				connectToServer();
				
				
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
				t_input.setEnabled(false);
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
			clientSocket = new Socket();
			SocketAddress sa = new InetSocketAddress(serverAddress, serverPort);
			clientSocket.connect(sa, 3000);
			out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			
			sendUserID();
			receiveThread = new Thread(new Runnable() {
				private ObjectInputStream in;
				
				private void receiveMessage() {
					
					try {
						ChatMsg inMsg = (ChatMsg)in.readObject();
						if(inMsg == null) {
							disconnect();
							printDisplay("서버 연결 끊김");
							return;
						}
						switch(inMsg.mode) {
						case ChatMsg.MODE_TX_STRING:
							printDisplay(inMsg.userID+" : "+inMsg.message);
							break;
						}
					}catch(IOException e) {
						System.err.println("클라이언트 일반 수신 오류 > " + e.getMessage());
					}catch(ClassNotFoundException e) {
						printDisplay("잘못된 객체가 전달되었습니다.");
					}
					
				}
				
				@Override
				public void run() {
					try {
						in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
					}catch(IOException e) {
						printDisplay("입력 스트림이 열리지 않음");
					}
					// TODO Auto-generated method stub
					while(receiveThread == Thread.currentThread()) {
						receiveMessage();
					}
				}
				
			});
			receiveThread.start();
		}catch(IOException e) {
			t_display.setText("서버 접속 실패 : " + e.getMessage());
			b_connect.setEnabled(true);
			b_disconnect.setEnabled(false);
			b_send.setEnabled(false);
			t_input.setEnabled(false);
		}
	}
	private void disconnect() {
		send(new ChatMsg(uid, ChatMsg.MODE_LOGOUT));
		try {
			receiveThread = null;
			clientSocket.close();
		}catch(IOException e) {
			System.err.println("클라이언트 닫기 오류 > "+e.getMessage());
			System.exit(-1);
		}
	}
	private void send(ChatMsg msg) {
		try {
			out.writeObject(msg);
			out.flush();
		}catch(IOException e) {
			System.err.println("클라이언트 일반 전송 오류 > "+e.getMessage());
		}
	}
	private void sendMessage() {
		String message = t_input.getText();
		if(message.isEmpty()) return;
		send(new ChatMsg(uid, ChatMsg.MODE_TX_STRING, message));
		t_input.setText("");
	}
	private void sendUserID() {
		uid=t_userID.getText();
		send(new ChatMsg(uid, ChatMsg.MODE_LOGIN));
	}
	
	private void printDisplay(String msg) {
		t_display.append(msg+"\n");
		//커서를 textArea의 가장 끝으로 보내는 코드
		t_display.setCaretPosition(t_display.getDocument().getLength());
	}
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort=54321;
		new ObjTalk(serverAddress, serverPort);
	}
}
