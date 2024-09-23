package defaultPackage;

import java.io.IOException;
import java.net.ServerSocket;

public class PortTest {

	public static void main(String[] args) {
		System.out.println("포트 스캔 시작");

		ServerSocket ss = null;
		for (int i = 0; i < 65535; i++) {
			try {
				//ServerSocket 생성자
				// 매개변수 : 포트번호
				// 특정 포트에서 클라이언트의 연결을 기다리는 서버 소켓을 생성
				ss = new ServerSocket(i);
				ss.close();
			}
			catch(IOException e) {
				System.out.println(i+"번 TCP 포트 사용중");
			}
		}

		System.out.println("포트 스캔 끝");
	}

}
