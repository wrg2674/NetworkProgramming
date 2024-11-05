

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class ImgReceiver {
	private JFrame frame;
	//JTextArea t_display;
	private JTextPane t_display;
	private DefaultStyledDocument document;
	private JButton btn;
	private int port;
	private ServerSocket serverSocket;
	
	private Thread acceptThread = null;
	public ImgReceiver(int port) {
		frame = new JFrame("Image Receiver");

		buildGUI();
		frame.setBounds(100, 200, 200, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		this.port =port;
		//startServer();
		acceptThread = new Thread(new Runnable() {
			@Override
			public void run() {
				startServer();
			}
		});
		acceptThread.start();
	}
	private void buildGUI() {
		frame.setLayout(new BorderLayout());
		frame.add(createDisplayPanel());
		frame.add(createControlPanel(), BorderLayout.SOUTH);
	}
	private JPanel createDisplayPanel() {
		//textarea = new JTextArea("서버가 시작되었습니다.\n");
		document = new DefaultStyledDocument();
		t_display = new JTextPane(document);
		t_display.setEditable(false);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(t_display);
		return panel;
		
	}
	private JPanel createControlPanel() {
		btn = new JButton("종료");
		JPanel panel = new JPanel(new BorderLayout());
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{
					serverSocket.close();
					System.exit(0);
				}catch(IOException ie) {
					System.err.println("서버 소켓 닫기 실패");
					System.exit(-1);
				}
				
			}
			
		});
		panel.add(btn);
		return panel;
	}
	private void startServer() {
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			while(true) {
				clientSocket = serverSocket.accept();
				//t_display.append("클라이언트가 연결되었습니다.\n");
				printDisplay("클라이언트가 연결되었습니다.\n");
				ClientHandler cHandler = new ClientHandler(clientSocket);
				cHandler.start();
			}
		}catch(IOException e) {
			
			System.err.println("서버 접속 오류");
			//System.exit(-1);
		}
	}
	private void printDisplay(String msg) {
		//t_display.append(msg+"\n");

		//커서를 textArea의 가장 끝으로 보내는 코드
		//t_display.setCaretPosition(t_display.getDocument().getLength());
		int len = t_display.getDocument().getLength();
		try {
			document.insertString(len, msg+"\n", null);
		}catch(BadLocationException e) {
			e.printStackTrace();
		}
		t_display.setCaretPosition(len);
	}
	private void printDisplay(ImageIcon icon) {
		t_display.setCaretPosition(t_display.getDocument().getLength());
		if(icon.getIconWidth() > 400) {
			Image img = icon.getImage();
			Image changeImg = img.getScaledInstance(400,-1, Image.SCALE_SMOOTH);
			icon = new ImageIcon(changeImg);
		}
		t_display.insertIcon(icon);
		printDisplay("");
	}
	
	private class ClientHandler extends Thread{
		private Socket clientSocket;
		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		private void receiveImage(Socket clientSocket) {
			//BufferedReader in;
			try {
				//in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
				//BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
				DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
				while(true) {
					//String fileName = in.readLine();
					String fileName = in.readUTF();
					long size = in.readLong();
					File file = new File(fileName);
					//PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"),true);
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
					/*
					 * String line; while((line = in.readLine()) != null) { String message =
					 * "클라이언트 메세지: "+line; //out.write("'"+msg+"'...echo\n"); //out.flush();
					 * //printDisplay(message); pw.println(line); }
					 */
					byte[] buffer = new byte[1024];
					int nRead;
					//while((nRead=in.read(buffer))!= -1) {
					while(size > 0) {
						nRead = in.read(buffer);
						size -= nRead;
						bos.write(buffer, 0, nRead);
					}
					bos.close();
					printDisplay("수신을 완료했습니다 : "+file.getName());
					printDisplay("클라이언트가 연결을 종료했습니다");
					ImageIcon icon = new ImageIcon(file.getName());
					printDisplay(icon);
				}
				
			}catch(IOException e) {
				printDisplay("인풋 스트림 생성 오류"+e.getMessage());
			}
			//finally {
			//	try {
			//		serverSocket.close();
			//		System.exit(0);
			//	}catch(IOException e) {
			//		System.err.println("서버 소켓 닫기 실패"+e.getMessage());
			//		System.exit(-1);
			//	}
			//}
			
		}
		@Override
		public void run() {
			//receiveFile(clientSocket);
			receiveImage(clientSocket);
		}
		
	}
	public static void main(String[] args) {
		new ImgReceiver(54321);
	}

}
