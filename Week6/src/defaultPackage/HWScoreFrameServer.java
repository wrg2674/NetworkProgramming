package defaultPackage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class HWScoreFrameServer {
	JFrame frame;
	JTextArea textarea;
	JButton btn;
	int port;
	ServerSocket serverSocket;
	private ScoreManager sMgr=null;
	
	public HWScoreFrameServer(int port) {
		frame = new JFrame("ScoreFrameServer");

		buildGUI();
		frame.setBounds(100, 200, 200, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		sMgr=new ScoreManager("score.txt");
		this.port = port;
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
			System.exit(-1);
		}
	}
	private void printDisplay(String msg) {
		textarea.append(msg+"\n");
		//커서를 textArea의 가장 끝으로 보내는 코드
		textarea.setCaretPosition(textarea.getDocument().getLength());
	}
	private void receiveMessages(Socket clientSocket) {
		BufferedReader in;
		try {
			in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
			String name;
			while((name = in.readLine()) != null) {
				String grade = sMgr.get(name);
				if(grade==null) {
					grade = "성적 정보 없음";
				}
				String message = name + " : "+grade+"("+clientSocket.getInetAddress().getHostAddress()+")";
				printDisplay(message);
				((BufferedWriter)out).write(grade+"\n");
				out.flush();
			}
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
	private class ClientHandler extends Thread{
		private Socket clientSocket;
		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		@Override
		public void run() {
			receiveMessages(clientSocket);
		}
		
	}
	private class ScoreManager{
		private HashMap<String, String> map = new HashMap<String, String>();
		ScoreManager(String fileName){
			try {
				Scanner reader=new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8")));
				
				while(reader.hasNext()) {
					String name = reader.next();
					String grade = reader.next();
					
					map.put(name, grade);
				}
				reader.close();
				printDisplay("데이터 파일 불러오기 성공");
			}catch(FileNotFoundException e) {
				System.err.println("데이터 파일이 없습니다 : "+e.getMessage());
				System.exit(-1);
			}catch(UnsupportedEncodingException e) {
				System.err.println("파일 인코딩 형식을 확인하세요 : "+e.getMessage());
				System.exit(-1);
			}
			
		}
		String get(String name) {
			return map.get(name);
		}
	}
	public static void main(String arg[]) {
		new HWScoreFrameServer(1234);
	}

}
