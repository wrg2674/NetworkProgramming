 

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class WithChatServer {
	private JFrame frame;
	private JTextArea t_display;
	private JLabel label;
	private JButton b_exit;
	private JButton b_disconnect;
	private JButton b_connect;
	int port;
	ServerSocket serverSocket;
	
	//private Reader in;
	private Thread acceptThread = null;
	private Vector<ClientHandler> users =new Vector<ClientHandler>();
	
	public WithChatServer() {
		frame = new JFrame("With ChatServer");
		port=54321;
		buildGUI();
		frame.setSize(400, 300);
		frame.setLocation(400, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		//startServer();
	}
	private void buildGUI() {
		frame.setLayout(new BorderLayout());
		frame.add(createDisplayPanel());
		frame.add(createSouthPanel(), BorderLayout.SOUTH);

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
		JPanel panel = new JPanel(new GridLayout(0,1));
		panel.add(createControlPanel());
		return panel;
		
	}
	private JPanel createControlPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 0));
		b_connect = new JButton("서버 시작");
		b_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//connectToServer();
				//startServer();
				acceptThread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						startServer();
					}
					
				});
				acceptThread.start();
				b_connect.setEnabled(false);
				b_disconnect.setEnabled(true);
				//b_send.setEnabled(true);
				//text.setEnabled(true);
			}
			
		});
		panel.add(b_connect);
		b_disconnect = new JButton("서버 종료");
		b_disconnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				disconnect();
				b_connect.setEnabled(true);
				b_disconnect.setEnabled(false);
				
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
	private void startServer() {
		Socket clientSocket = null;
		InetAddress localHost=null;
		try {
			localHost = InetAddress.getLocalHost();
		}catch(UnknownHostException e) {
			
		}
		
		printDisplay("서버가 시작되었습니다."+localHost.getHostAddress());
		try {
			serverSocket = new ServerSocket(port);
			while(acceptThread == Thread.currentThread()) {
				clientSocket = serverSocket.accept();
				//textArea.append("클라이언트가 연결되었습니다.\n");
				ClientHandler cHandler = new ClientHandler(clientSocket);
				users.add(cHandler);
				cHandler.start();
			}
			
		}catch(SocketException e) {
			//System.err.println("서버 소켓 종료 : " +e.getMessage());
			printDisplay("서버 소켓 종료");
		}catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(clientSocket != null) {
					clientSocket.close();
				}
				if(serverSocket != null) {
					serverSocket.close();
				}
			}catch(IOException e) {
				System.err.println("서버 닫기 오류 >" + e.getMessage());
				System.exit(-1);
			}
		}
	}
	private void printDisplay(String msg) {
		t_display.append(msg+"\n");
		//커서를 textArea의 가장 끝으로 보내는 코드
		t_display.setCaretPosition(t_display.getDocument().getLength());
	}
	
	private void disconnect() {
		try {
			acceptThread = null;
			//out.close();
			serverSocket.close();
		}catch(IOException e) {
			System.err.println("서버 소켓 닫기 오류 > "+e.getMessage());
			System.exit(-1);
		}
	}
	private class ClientHandler extends Thread{
		private ObjectOutputStream out;
		private Socket clientSocket;
		private String uid;
		
		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
			
		}
		@Override
		public void run() {
			receiveMessages(clientSocket);
		}
		private void receiveMessages(Socket clientSocket) {
			try {
				ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
				out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
				String message;
				ChatMsg msg;
				while((msg = (ChatMsg)in.readObject()) != null) {
					if(msg.mode == ChatMsg.MODE_LOGIN) {
						uid = msg.userID;
						printDisplay("새 참가자 : "+uid);
						printDisplay("현재 참가자 수 : "+users.size());
						continue;
					}
					else if(msg.mode == ChatMsg.MODE_LOGOUT) {
						break;
					}
					else if(msg.mode == ChatMsg.MODE_TX_STRING) {
						message = uid+" : "+msg.message;
						printDisplay(message);
						broadcasting(msg);
					}
					else if(msg.mode == ChatMsg.MODE_TX_IMAGE) {
						printDisplay(uid+" : "+msg.message);
						broadcasting(msg);
					}
				}
				printDisplay(uid+" 퇴장. 현재 참가자 수 : "+users.size());
				users.removeElement(this);
				
			}catch(IOException e) {
				System.err.println("인풋 스트림 생성 오류");
				System.exit(-1);
			}catch(ClassNotFoundException e){
				System.err.println("클래스를 찾을 수 없습니다.");
				System.exit(-1);
			}
			try {
				serverSocket.close();
				System.exit(0);
			}catch(IOException ie) {
				System.err.println("서버 소켓 닫기 실패");
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
		private void sendMessage(String msg) {
			send(new ChatMsg(uid, ChatMsg.MODE_TX_STRING, msg));
		}
		private void broadcasting(ChatMsg msg) {
			for(ClientHandler c:users) {
				c.send(msg);
			}
		}
	}
	public static void main(String args[]) {
		new WithChatServer();
	}

}
