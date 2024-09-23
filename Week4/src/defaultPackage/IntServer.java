package defaultPackage;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class IntServer {
	int port;

	public IntServer(int port) {
		this.port = port;
	}

	public void startServer() {
		Socket clientSocket = null;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("서버가 시작되었습니다.");

			while (true) {
				//클라이언트의 접속을 대기하다가 접속 요청이 있으면 소켓 객체 반환
				//이 소켓은 클라이언트와 통신하는데 사용
				//IOException 처리 필수
				clientSocket = serverSocket.accept();
				System.out.println("클라이언트가 연결되었습니다.");

				showMessages(clientSocket);
			}
		}catch(IOException e) {
			System.err.println("서버 오류> "+e.getMessage());
			System.exit(-1);
		}
		finally {
			try {
				if(clientSocket != null) {
					clientSocket.close();
				}
			}catch(IOException e) { 
				System.err.println("서버 닫기 오류> "+e.getMessage());
				System.exit(-1);
			}
		}
		
	}

	private void showMessages(Socket cs) {
			InputStream in;
			try {
				//IOException 처리 필수
				in= cs.getInputStream();

				int message;
				//IOException 처리 필수
				while ((message = in.read()) != 0) {
					System.out.println("클라이언트 메시지: " + message);
				}
				System.out.println("클라이언트가 연결을 종료했습니다.");
			}catch(IOException e) {
				System.err.println("서버 읽기 오류> "+e.getMessage());
				System.exit(-1);
			}
			finally {
				try {
					cs.close();
				}catch(IOException e) {
					System.err.println("서버 닫기 오류> "+e.getMessage());
					System.exit(-1);
				}
				
			}
			

			
	}

	public static void main(String[] args) {
		int port = 54321;

		IntServer server = new IntServer(port);
		server.startServer();
	}
}
