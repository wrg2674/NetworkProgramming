// 1971135 정구현
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class CalcServerGUI {
	JFrame frame;
	JTextArea textarea;
	JButton btn;
	int port;
	ServerSocket serverSocket;
	public CalcServerGUI(int port) {
		frame = new JFrame("CalcServerGUI");

		buildGUI();
		frame.setBounds(100, 200, 200, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
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
			//System.err.println("서버 접속 오류");
			//System.exit(-1);
		}
	}
	private void printDisplay(CalcExpr obj, double result) {
		textarea.append(Double.toString(obj.operand1) +" "+obj.operator+" "+Double.toString(obj.operand2) +"="+result+"\n");
		//커서를 textArea의 가장 끝으로 보내는 코드
		textarea.setCaretPosition(textarea.getDocument().getLength());
	}
	private void receiveMessages(Socket clientSocket) {
		ObjectInputStream in;
		DataOutputStream out;
		try {
			in=new ObjectInputStream(clientSocket.getInputStream());
			out=new DataOutputStream(clientSocket.getOutputStream());
			Object msg;
			while(true) {
				try {
					msg = in.readObject();
					CalcExpr obj =(CalcExpr)msg;
					double result = calc(obj);
					out.writeDouble(result);
					printDisplay(obj, result);
				}catch(ClassNotFoundException e) {
					System.err.println("데이터 읽기 실패");
					System.exit(-1);
				}
				
			}
		}catch(IOException e) {
			textarea.append("클라이언트의 연결이 종료되었습니다.\n");
			//System.err.println("인풋 스트림 생성 오류");
			//System.exit(-1);
		}
	}
	private double calc(CalcExpr obj) {
		double result = 0;
		switch(obj.operator) {
		case '+':
			result = obj.operand1+obj.operand2;
			break;
		case '-':
			result = obj.operand1-obj.operand2;
			break;
		case '*':
			result = obj.operand1*obj.operand2;
			break;
		case '/':
			result = obj.operand1/obj.operand2;
			break;
		}
		return result;
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new CalcServerGUI(1234);
	}

}
