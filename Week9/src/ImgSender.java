
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class ImgSender {
	private JFrame frame;
	private JTextField t_input;
	// private JTextArea t_display;
	private JTextPane t_display;
	private JButton b_send;
	private JButton b_exit;
	private JButton b_disconnect;
	private JButton b_connect;
	private JButton b_select;
	private DefaultStyledDocument document;

	private String serverAddress;
	private int serverPort;
	private Socket clientSocket;
	// private Writer out;
	// private Reader in;
	private OutputStream out;

	public ImgSender(String serverAddress, int serverPort) {
		frame = new JFrame("Image Sender");

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
		// t_display = new JTextArea();
		document = new DefaultStyledDocument();
		t_display = new JTextPane(document);
		t_display.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(t_display);
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
		t_input = new JTextField(20);
		t_input.setEnabled(false);

		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// sendMessage();
				// receiveMessage();
				sendImage();
			}

		};
		t_input.addActionListener(listener);
		b_send = new JButton("보내기");
		b_send.setEnabled(false);
		b_send.addActionListener(listener);

		b_select = new JButton("선택하기");
		b_select.addActionListener(new ActionListener() {
			JFileChooser chooser = new JFileChooser();

			@Override
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF & PNG Images", "jpg", "gif",
						"png");
				chooser.setFileFilter(filter);

				int ret = chooser.showOpenDialog(ImgSender.this);
				if (ret != JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(ImgSender.this, "파일을 선택하지 않았습니다", "경고", JOptionPane.WARNING_MESSAGE);
					return;
				}
				t_input.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(t_input, BorderLayout.CENTER);
		// panel.add(b_send, BorderLayout.EAST);
		JPanel p_button = new JPanel(new GridLayout(1, 0));
		p_button.add(b_select);
		p_button.add(b_send);
		panel.add(p_button, BorderLayout.EAST);

		t_input.setEnabled(false);
		b_select.setEnabled(false);
		b_send.setEnabled(false);
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
				b_select.setEnabled(true);
				t_input.setEnabled(true);
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
				b_select.setEnabled(false);
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

	private void connectToServer() {
		try {
			clientSocket = new Socket(serverAddress, serverPort);
			out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			// out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(),
			// "UTF-8"), true);
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

	/*
	 * private void sendMessage() {
	 * 
	 * try { String str = text.getText();
	 * 
	 * out.write(str+"\n"); out.flush();
	 * 
	 * ((PrintWriter)out).println(str); textArea.append("나: "+str+"\n");
	 * text.setText(""); }catch(IOException e) { System.err.println("데이터 쓰기 실패");
	 * System.exit(-1); }
	 * 
	 * String str = t_input.getText(); if(str.isEmpty()) { return; } try {
	 * ((DataOutputStream)out).writeUTF(str+"\n"); out.flush();
	 * t_display.append("나: "+str+"\n"); }catch(IOException e) {
	 * System.err.println("클라이언트 일반 전송 오류 >> "+e.getMessage()); System.exit(-1); }
	 * 
	 * t_input.setText(""); }
	 */
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

	private void sendImage() {
		String filename = t_input.getText().strip();
		if (filename.isEmpty()) {
			return;
		}
		File file = new File(filename);
		if (!file.exists()) {
			printDisplay(">> 파일이 존재하지 않습니다 : " + filename + "\n");
			return;
		}
		// ((PrintWriter)out).println(filename);

		// BufferedReader br=null;
		BufferedInputStream bis = null;
		try {
			((DataOutputStream) out).writeUTF(file.getName());
			((DataOutputStream)out).writeLong(file.length());
			// br=new BufferedReader(new InputStreamReader(new FileInputStream(file),
			// "UTF-8"));
			bis = new BufferedInputStream(new FileInputStream(file));
			String line;
			/*
			 * while((line=br.readLine())!=null) { ((PrintWriter)out).println(line); }
			 */
			byte[] buffer = new byte[1024];
			int nRead;
			while ((nRead = bis.read(buffer)) != -1) {
				out.write(buffer, 0, nRead);
			}
			//out.close();
			out.flush();
			printDisplay(">> 전송을 완료했습니다 : " + filename);
			t_input.setText("");
			
			ImageIcon icon = new ImageIcon(filename);
			printDisplay(icon);
		} catch (FileNotFoundException e) {
			// textArea.append(">> 파일이 존재하지 않습니다 : "+e.getMessage()+"\n");
			printDisplay(">> 파일이 존재하지 않습니다 : " + e.getMessage());
			return;
		} catch (IOException e) {
			// textArea.append(">> 파일을 읽을 수 없습니다 : "+e.getMessage()+"\n");
			printDisplay(">> 파일을 읽을 수 없습니다 : " + e.getMessage());
			return;
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				printDisplay(">> 파일을 닫을 수 없습니다" + e.getMessage());
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
		new ImgSender("localhost", 54321);
	}
}
