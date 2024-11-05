

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class MultiImageSender {
	private JFrame frame;
	private JTextField t_input;
	private JTextPane t_display;
	private JLabel label;
	private JButton b_send;
	private JButton b_exit;
	private JButton b_disconnect;
	private JButton b_connect;
	private JButton b_select;
	private String serverAddress;
	private JTextField t_userID;
	private DefaultStyledDocument document;
	
	private JTextField t_serverAddress;
	private JTextField t_portNumber;
	private int serverPort;
	private Socket clientSocket;
	private OutputStream out;
	private InputStream in;
	private Thread receiveThread =null;
	
	public MultiImageSender(String serverAddress, int serverPort) {
		frame = new JFrame("Multi Talk");
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
		document = new DefaultStyledDocument();
		t_display = new JTextPane(document);
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
		b_select = new JButton("선택하기");
		b_select.addActionListener(new ActionListener() {
			JFileChooser chooser = new JFileChooser();

			@Override
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF & PNG Images", "jpg", "gif",
						"png");
				chooser.setFileFilter(filter);

				int ret = chooser.showOpenDialog(frame);
				if (ret != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(frame, "파일을 선택하지 않았습니다", "경고", JOptionPane.WARNING_MESSAGE);
					return;
				}
				t_input.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});
		JPanel panel = new JPanel(new GridLayout(1,3));
		panel.add(t_input);
		panel.add(b_select);
		panel.add(b_send);
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
	private void sendUserID() {
		try {
			((DataOutputStream)out).writeUTF("/uid: "+t_userID.getText()+"\n");
			out.flush();
		}
		catch(IOException e) {
			System.err.println("유저 아이디 전송 실패");
		}
	}
	private void connectToServer() {
		try {
			clientSocket = new Socket();
			SocketAddress sa = new InetSocketAddress(serverAddress, serverPort);
			clientSocket.connect(sa, 3000);
			out = new BufferedOutputStream(clientSocket.getOutputStream());
			in = new BufferedInputStream(clientSocket.getInputStream());
			sendUserID();
			ClientHandler cHandler = new ClientHandler(clientSocket);
			cHandler.start();
		}catch(IOException e) {
			t_display.setText("서버 접속 실패 : " + e.getMessage());
			b_connect.setEnabled(true);
			b_disconnect.setEnabled(false);
			b_send.setEnabled(false);
			t_input.setEnabled(false);
		}
	}
	private void disconnect() {
		try {
			receiveThread=null;
			out.close();
			clientSocket.close();
		}catch(IOException e) {
			System.err.println("소켓 닫기 실패");
			System.exit(-1);
		}
	}
	private void sendMessage() {
		try {
			String str = t_input.getText();
			((DataOutputStream)out).writeUTF(str+"\n");
			out.flush();
			//String myMsg = ((BufferedReader)in).readLine();
			//textArea.append(myMsg);
			t_input.setText("");
		}catch(IOException e) {
			System.err.println("데이터 쓰기 실패");
			System.exit(-1);
		}catch(NumberFormatException e) {
			System.err.println("정수 변환 실패");
			System.exit(-1);
		}
	}
	private void sendImage() {
		String filename = t_input.getText().strip();
		if(filename.isEmpty()) {
			return ;
		}
		File file = new File(filename);
		if(!file.exists()) {
			printDisplay(">> 파일이 존재하지 않습니다 : "+filename+"\n");
			return;
		}
		BufferedInputStream bis = null;
		try {
			((DataOutputStream)out).writeUTF(file.getName());
			((DataOutputStream)out).writeLong(file.length());
			bis = new BufferedInputStream(new FileInputStream(file));
		
			String line;
			byte[] buffer = new byte[1024];
			int nRead;
			while((nRead = bis.read(buffer))!=-1) {
				out.write(buffer, 0, nRead);
			}
			out.flush();
			printDisplay(">> 전송을 완료했습니다 : "+filename);
			t_input.setText("");
			ImageIcon icon = new ImageIcon(filename);
			printDisplay(icon);
		}catch(FileNotFoundException e) {
			printDisplay(">> 파일이 존재하지 않습니다 : "+e.getMessage());
			return;
		}catch(IOException e) {
			printDisplay(">> 파일을 읽을 수 없습니다 : "+e.getMessage());
			return;
		}finally {
			try {
				if(bis != null) {
					bis.close();
				}
			}catch(IOException e) {
				printDisplay(">> 파일을 닫을 수 없습니다 : "+e.getMessage());
				return;
			}
		}
		
	}
	private class ClientHandler extends Thread{
		private Socket clientSocket;
		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		private void receiveImage(Socket clientSocket) {
			try {
				DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
				while(true) {
					String filename = in.readUTF();
					long size = in.readLong();
					File file = new File(filename);
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
					byte[] buffer = new byte[1024];
					int nRead;
					while(size > 0) {
						nRead = in.read(buffer);
						size -= nRead;
						bos.write(buffer, 0, nRead);
					}
					bos.close();
					printDisplay("수신 완료 했습니다 : "+file.getName());
					ImageIcon icon=new ImageIcon(file.getName());
					printDisplay(icon);
				}
			}catch(IOException e) {
				printDisplay("인풋 스트림 생성 오류 : "+e.getMessage());
			}
			
		}
		@Override
		public void run() {
			//receiveFile(clientSocket);
			receiveImage(clientSocket);
		}
		
	}
	
	private void printDisplay(String msg) {
		/*
		 * t_display.append(msg+"\n"); //커서를 textArea의 가장 끝으로 보내는 코드
		 * t_display.setCaretPosition(t_display.getDocument().getLength());
		 */
		int len = t_display.getDocument().getLength();
		try {
			document.insertString(len, msg + "\n", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		t_display.setCaretPosition(len);
	}

	private void printDisplay(ImageIcon icon) {
		t_display.setCaretPosition(t_display.getDocument().getLength());
		if (icon.getIconWidth() > 400) {
			Image img = icon.getImage();
			Image changeImg = img.getScaledInstance(400, -1, Image.SCALE_SMOOTH);
			icon = new ImageIcon(changeImg);
		}
		t_display.insertIcon(icon);
		printDisplay("");
		t_input.setText("");
	}
	public static void main(String[] args) {
		String serverAddress = "localhost";
		int serverPort=54321;
		new MultiImageSender(serverAddress, serverPort);
	}
}
