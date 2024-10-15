

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class P2PChatServer {
	private JFrame frame;
	private JTextField text;
	private JTextArea textArea;
	private JLabel label;
	private JButton b_send;
	private JButton b_exit;
	private JButton b_disconnect;
	private JButton b_connect;
	int port;
	ServerSocket serverSocket;
	private Writer out;
	//private Reader in;
	private Thread acceptThread = null;
	
	public P2PChatServer() {
		frame = new JFrame("P2P ChatServer");
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
				// receiveMessage();
			}
			
		});
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(text, BorderLayout.CENTER);
		panel.add(b_send, BorderLayout.EAST);
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
	private void startServer() {
		Socket clientSocket = null;
		printDisplay("서버가 시작되었습니다.");
		try {
			serverSocket = new ServerSocket(port);
			while(acceptThread == Thread.currentThread()) {
				clientSocket = serverSocket.accept();
				textArea.append("클라이언트가 연결되었습니다.\n");
				ClientHandler cHandler = new ClientHandler(clientSocket);
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
		textArea.append(msg+"\n");
		//커서를 textArea의 가장 끝으로 보내는 코드
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	private void sendMessage() {
		try {
			String str = text.getText();
			out.write(str+"\n");
			out.flush();
			textArea.append("나: "+str+"\n");
			text.setText("");
		}catch(IOException e) {
			System.err.println("데이터 쓰기 실패");
			System.exit(-1);
		}catch(NumberFormatException e) {
			System.err.println("정수 변환 실패");
			System.exit(-1);
		}
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
		private Socket clientSocket;
		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
			b_send.setEnabled(true);
			text.setEnabled(true);
			
		}
		@Override
		public void run() {
			receiveMessages(clientSocket);
		}
		private void receiveMessages(Socket clientSocket) {
			BufferedReader in;
			try {
				in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
				out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
				String msg;
				while((msg = in.readLine()) != null) {
					String message = "클라이언트 메세지: "+msg;
//					out.write("'"+msg+"'...echo\n");
//					out.flush();
					printDisplay(message);
				}
				printDisplay("클라이언트가 연결을 종료했습니다.");
			}catch(IOException e) {
				System.err.println("인풋 스트림 생성 오류");
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
	}
	public static void main(String args[]) {
		new P2PChatServer();
	}

}
