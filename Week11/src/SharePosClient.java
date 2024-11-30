import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SharePosClient extends JFrame{
	private JTextField t_userID, t_hostAddr, t_portNum;
	private JButton b_connect, b_disconnect, b_exit;
	private String serverAddress;
	private int serverPort;
	private String uid;
	private Socket socket;
	private ObjectOutputStream out;
	private BufferedOutputStream bos;
	private Thread receiveThread = null;
	private final int FLYING_UNIT=10;
	private JLabel unit1 = new JLabel("*");
	private JLabel unit2 = new JLabel("@");
	
	public SharePosClient(String serverAddress, int port) {
		super("Share Position");
		this.serverAddress = serverAddress;
		this.serverPort = port;
		
		buildGUI();
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void buildGUI() {
		add(createDisplayPanel(), BorderLayout.CENTER);
		JPanel p_input=new JPanel(new GridLayout(0,1));
		p_input.add(createInfoPanel());
		p_input.add(createControlPanel());
		add(p_input, BorderLayout.SOUTH);
	}
	private JPanel createDisplayPanel() {
		JPanel p = new JPanel(null);
		unit1.setSize(100, 20);
		unit1.setLocation(50, 50);
		
		unit2.setSize(100,20);
		unit2.setLocation(50,50);
		unit2.setVisible(false);
		
		p.add(unit1);
		p.add(unit2);
		
		p.setFocusable(true);
		p.requestFocus();
		
		p.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				switch(keyCode) {
				case KeyEvent.VK_UP:
					unit1.setLocation(unit1.getX(), unit1.getY()-FLYING_UNIT);
					sendPos(unit1.getX(), unit1.getY()-FLYING_UNIT);
					break;
				case KeyEvent.VK_DOWN:
					unit1.setLocation(unit1.getX(), unit1.getY()+FLYING_UNIT);
					sendPos(unit1.getX(), unit1.getY()+FLYING_UNIT);
					break;
				case KeyEvent.VK_LEFT:
					unit1.setLocation(unit1.getX()-FLYING_UNIT, unit1.getY());
					sendPos(unit1.getX()-FLYING_UNIT, unit1.getY());
					break;
				case KeyEvent.VK_RIGHT:
					unit1.setLocation(unit1.getX()+FLYING_UNIT, unit1.getY());
					sendPos(unit1.getX()+FLYING_UNIT, unit1.getY());
					break;
				}
			}
		});
		p.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Component c = (Component)e.getSource();
				c.setFocusable(true);
				c.requestFocus();
			}
		});
		return p;
		
	}
	private JPanel createInfoPanel() {
		JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel id = new JLabel("아이디 : ");
		JLabel serverAddr = new JLabel("서버 주소 : ");
		JLabel portNum = new JLabel("포트 번호 : ");
		t_userID =new JTextField(7);
		t_hostAddr = new JTextField(12);
		t_portNum = new JTextField(5);
		
		t_userID.setText("guest"+getLocalAddr().split("\\.")[3]);
		t_hostAddr.setText(this.serverAddress);
		t_portNum.setText(String.valueOf(this.serverPort));
		t_portNum.setHorizontalAlignment(JTextField.CENTER);
		
		infoPanel.add(id);
		infoPanel.add(t_userID);
		infoPanel.add(serverAddr);
		infoPanel.add(t_hostAddr);
		infoPanel.add(portNum);
		infoPanel.add(t_portNum);
		
		return infoPanel;
	}
	private JPanel createControlPanel() {
		JPanel panel3 = new JPanel(new GridLayout(0, 3));
		b_connect = new JButton("접속하기");
		b_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SharePosClient.this.serverAddress = t_hostAddr.getText();
				SharePosClient.this.serverPort = Integer.parseInt(t_portNum.getText());
				
				try {
					connectToServer();
					sendUserID();
				}catch(UnknownHostException e1) {
					printDisplay("서버 주소와 포트번호를 확인하세요: "+e1.getMessage());
					return;
				}catch(IOException e1) {
					printDisplay("서버와의 연결 오류 : "+e1.getMessage());
					return;
				}
				b_connect.setEnabled(false);
				b_disconnect.setEnabled(true);
				b_exit.setEnabled(false);
				
				t_userID.setEditable(false);
				t_hostAddr.setEditable(false);
				t_portNum.setEditable(false);
				unit1.setText(uid);
			}
			
		});
		b_disconnect = new JButton("접속 끊기");
		b_disconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				disconnect();
				b_disconnect.setEnabled(false);
				b_connect.setEnabled(true);
				b_exit.setEnabled(true);
			}
		});
		b_exit = new JButton("종료하기");
		b_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panel3.add(b_connect);
		panel3.add(b_disconnect);
		panel3.add(b_exit);
		b_disconnect.setEnabled(false);
		
		return panel3;
	}
	private String getLocalAddr() {
		InetAddress local = null;
		String addr = "";
		try {
			local =InetAddress.getLocalHost();
			addr = local.getHostAddress();
			System.out.println(addr);
		}catch(UnknownHostException e) {
			e.printStackTrace();
		}
		return addr;
	}
	private void connectToServer() throws UnknownHostException, IOException{
		socket = new Socket();
		SocketAddress sa = new InetSocketAddress(serverAddress, serverPort);
		socket.connect(sa, 3000);
		
		out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		receiveThread = new Thread(new Runnable() {
			private ObjectInputStream in;
			private void receiveMessage() {
				try {
					ChatMsg inMsg=(ChatMsg)in.readObject();
					if(inMsg==null) {
						disconnect();
						printDisplay("서버 연결 끊김");
						return;
					}
					switch(inMsg.mode) {
					case ChatMsg.MODE_LOGIN:
						if(!inMsg.userID.equals(t_userID.getText())) {
							unit2.setVisible(true);
							unit2.setText(inMsg.userID);
						}
						break;
					case ChatMsg.MODE_TX_POS:
						Point p=(Point)inMsg.object;
						if(!inMsg.userID.equals(t_userID.getText())) {
							unit2.setLocation(p);
						}
						break;
					}
				}catch(IOException e) {
					printDisplay("연결을 종료했습니다.");
				}catch(ClassNotFoundException e) {
					printDisplay("잘못된 객체가 전달되었습니다.");
				}
			}
			@Override
			public void run() {
				try {
					in=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
				}catch(IOException e) {
					printDisplay("입력 스트림이 열리지 않음");
				}
				while(receiveThread == Thread.currentThread()) {
					receiveMessage();
				}
			}
		});
		receiveThread.start();
	}
	private void disconnect() {
		send(new ChatMsg(uid, ChatMsg.MODE_LOGOUT));
		try {
			receiveThread = null;
			socket.close();
		}catch(IOException e) {
			System.err.println("클라이언트 닫기 오류 >" +e.getMessage());
			System.exit(-1);
		}
	}
	private void printDisplay(String msg) {
		System.out.println(msg);
	}
	private void send(ChatMsg msg) {
		try {
			out.writeObject(msg);
			out.flush();
		}catch(IOException e) {
			System.err.println("클라이언트 일반 전송 오류 > "+e.getMessage());
		}
	}
	private void sendUserID() {
		uid=t_userID.getText();
		send(new ChatMsg(uid, ChatMsg.MODE_LOGIN));
	}
	private void sendPos(int x, int y){
		send(new ChatMsg(uid, ChatMsg.MODE_TX_POS, new Point(x,y)));
	}
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort = 54321;
		SharePosClient c = new SharePosClient(serverAddress, serverPort);

	}
}