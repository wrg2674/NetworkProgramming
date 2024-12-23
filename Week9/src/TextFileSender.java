
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TextFileSender {
	private JFrame frame;
	private JTextField text;
	private JTextArea textArea;
	private JButton b_send;
	private JButton b_exit;
	private JButton b_disconnect;
	private JButton b_connect;
	private String serverAddress;
	private int serverPort;
	private Socket clientSocket;
	private Writer out;
	// private Reader in;

	public TextFileSender(String serverAddress, int serverPort) {
		frame = new JFrame("TextFile Sender");

		buildGUI();
		frame.setBounds(100, 200, 200, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		this.serverPort = serverPort;
		this.serverAddress = "localhost";
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
		JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.add(createInputPanel());
		panel.add(createControlPanel());
		return panel;

	}

	private JPanel createInputPanel() {
		text = new JTextField(20);
		text.setEnabled(false);

		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// sendMessage();
				// receiveMessage();
				sendFile();
			}

		};
		text.addActionListener(listener);
		b_send = new JButton("보내기");
		b_send.setEnabled(false);
		b_send.addActionListener(listener);
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
				connectToServer();
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

	private void connectToServer() {
		try {
			clientSocket = new Socket(serverAddress, serverPort);
			out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
			// in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),
			// "UTF-8"));
		} catch (IOException e) {
			System.err.println("소켓 생성 실패");
			System.exit(-1);
		}
	}

	private void disconnect() {
		try {
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("소켓 닫기 실패");
			System.exit(-1);
		}
	}

	private void sendMessage() {
		/*
		 * try { String str = text.getText();
		 * 
		 * out.write(str+"\n"); out.flush();
		 * 
		 * ((PrintWriter)out).println(str); textArea.append("나: "+str+"\n");
		 * text.setText(""); }catch(IOException e) { System.err.println("데이터 쓰기 실패");
		 * System.exit(-1); }
		 */
		String str = text.getText();
		((PrintWriter)out).println(str);
		textArea.append("나: "+str+"\n");
		text.setText("");
	}
	private void sendFile() {
		String filename = text.getText().strip();
		if(filename.isEmpty()) {
			return;
		}
		File file = new File(filename);
		if(!file.exists()) {
			textArea.append(">> 파일이 존재하지 않습니다 : "+filename+"\n");
			return;
		}
		((PrintWriter)out).println(filename);
		BufferedReader br=null;
		try {
			br=new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line;
			while((line=br.readLine())!=null) {
				((PrintWriter)out).println(line);
			}
			out.close();
			textArea.append(">> 전송을 완료했습니다 : "+filename+"\n");
			text.setText("");
		}catch(UnsupportedEncodingException e){
			textArea.append(">> 인코딩 형식을 알 수 없습니다 : "+e.getMessage()+"\n");
			return;
		}catch(FileNotFoundException e) {
			textArea.append(">> 파일이 존재하지 않습니다 : "+e.getMessage()+"\n");
			return;
		}catch(IOException e) {
			textArea.append(">> 파일을 읽을 수 없습니다 : "+e.getMessage()+"\n");
			return;
		}finally {
			try {
				if(br!=null) {
					br.close();
				}
			}catch(IOException e) {
				textArea.append(">> 파일을 닫을 수 없습니다"+e.getMessage());
				return;
			}
		}
		
	}
	/*
	 * private void receiveMessage() { try { String inMsg =
	 * ((BufferedReader)in).readLine(); textArea.append("서버:\t"+inMsg+"\n");
	 * }catch(IOException e) { System.err.println("클라이언트 일반 수신 오류 > " +
	 * e.getMessage()); }
	 * 
	 * }
	 */
	public static void main(String[] args) {
		new TextFileSender("localhost", 54321);
	}
}
