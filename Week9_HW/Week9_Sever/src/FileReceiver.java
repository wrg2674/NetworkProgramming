

import java.awt.BorderLayout;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class FileReceiver {
	JFrame frame;
	JTextArea textarea;
	JButton btn;
	int port;
	ServerSocket serverSocket;
	public FileReceiver(int port) {
		frame = new JFrame("File Receiver");

		buildGUI();
		frame.setBounds(100, 200, 200, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		this.port =port;
		startServer();
	}
	private void buildGUI() {
		frame.setLayout(new BorderLayout());
		frame.add(createDisplayPanel());
		frame.add(createControlPanel(), BorderLayout.SOUTH);
	}
	private JPanel createDisplayPanel() {
		textarea = new JTextArea("서버가 시작되었습니다.\n");
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
				textarea.append("클라이언트가 연결되었습니다.\n");
				ClientHandler cHandler = new ClientHandler(clientSocket);
				cHandler.start();
			}
		}catch(IOException e) {
			
			System.err.println("서버 접속 오류");
			//System.exit(-1);
		}
	}
	private void printDisplay(String msg) {
		textarea.append(msg+"\n");
		//커서를 textArea의 가장 끝으로 보내는 코드
		textarea.setCaretPosition(textarea.getDocument().getLength());
	}
	
	private class ClientHandler extends Thread{
		private Socket clientSocket;
		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		private void receiveFile(Socket clientSocket) {
			//BufferedReader in;
			try {
				//in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
				//BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
				DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
				//String fileName = in.readLine();
				String fileName = in.readUTF();
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
				while((nRead=in.read(buffer))!=-1) {
					bos.write(buffer, 0, nRead);
				}
				bos.close();
				printDisplay("수신을 완료했습니다 : "+file.getName());
				printDisplay("클라이언트가 연결을 종료했습니다");
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
			receiveFile(clientSocket);
		}
		
	}
	public static void main(String[] args) {
		new FileReceiver(54321);
	}

}
