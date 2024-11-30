 

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
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
import javax.swing.SwingUtilities;

public class SharePosServer {
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
	
	public SharePosServer(int port) {
		frame = new JFrame("SharePos Server");
		buildGUI();
		frame.setSize(400, 300);
		frame.setLocation(400, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		this.port = port;
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
				b_exit.setEnabled(false);
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
				b_exit.setEnabled(true);
				
			}
			
		});
		b_disconnect.setEnabled(false);
		panel.add(b_disconnect);
		b_exit = new JButton("종료하기");
		b_exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(serverSocket != null) {
						serverSocket.close();
					}
				}catch(IOException e1) {
					System.err.println("서버 닫기 오류 > "+e1.getMessage());
				}
				System.exit(-1);
			}
			
		});
		panel.add(b_exit);
		return panel;
	}
	private void startServer() {
		Socket clientSocket = null;

		
		try {
			serverSocket = new ServerSocket(port);
			printDisplay("서버가 시작되었습니다."+getLocalAddr());
			System.out.println("서버가 시작되었습니다");
			while(acceptThread == Thread.currentThread()) {
				clientSocket = serverSocket.accept();
				String cAddr = clientSocket.getInetAddress().getHostAddress();
				t_display.append("클라이언트가 연결되었습니다 : "+cAddr+"\n");
				System.out.println("클라이언트가 연결되었습니다.\n");
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
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				t_display.append(msg+"\n");
				t_display.setCaretPosition(t_display.getDocument().getLength());
			}
		});
	}
	private String getLocalAddr() {
		InetAddress local = null;
		String addr = "";
		try {
			local = InetAddress.getLocalHost();
			addr = local.getHostAddress();
			System.out.println(addr);
		}catch(UnknownHostException e) {
			e.printStackTrace();
		}
		return addr;
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
				
				ChatMsg msg;
				while((msg = (ChatMsg)in.readObject()) != null) {
					if(msg.mode == ChatMsg.MODE_LOGIN) {
						uid = msg.userID;
						broadcasting(msg);
						for(ClientHandler c:users) {
							String others = c.uid;
							if(!uid.equals(others)) {
								ChatMsg existingUserMsg = new ChatMsg(c.uid, ChatMsg.MODE_LOGIN);
								send(existingUserMsg);
							}
						}
						printDisplay("새 참가자 : "+uid);
						printDisplay("현재 참가자 수 : "+users.size());
					}
					else if(msg.mode == ChatMsg.MODE_LOGOUT) {
						break;
					}
					else if(msg.mode == ChatMsg.MODE_TX_POS) {
						Point p=(Point)msg.object;
						printDisplay(uid +"("+p.x+", "+p.y+")");
						broadcasting(msg);
					}
				}
				
				users.removeElement(this);
				printDisplay(uid+" 퇴장. 현재 참가자 수 : "+users.size());
			}catch(IOException e) {
				users.removeElement(this);
				printDisplay(uid+"연결 끊김. 현재 참가자 수 : "+users.size());
			}catch(ClassNotFoundException e){
				e.printStackTrace();
			}finally {
				try {
					serverSocket.close();
				}catch(IOException ie) {
					System.err.println("서버 소켓 닫기 실패");
					System.exit(-1);
				}
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
		int port = 54321;
		new SharePosServer(port);
	}

}
