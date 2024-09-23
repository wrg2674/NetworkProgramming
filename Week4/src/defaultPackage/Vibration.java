package defaultPackage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Vibration {
	JFrame frame;
	JButton btn;
	Thread thread;
	private Vibration() {
		frame = new JFrame("Vibration");
		buildGUI();
		frame.setSize(200,200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	private void buildGUI() {
		btn = new JButton("진동시작");
		frame.add(btn);
		
		btn.addActionListener(handler);
	}
	/*
	private ActionListener handler = new ActionListener() {
		private boolean cont = true;
		private int offset = 10;
		@Override
		public void actionPerformed(ActionEvent e) {
			String s = e.getActionCommand();
			if(s.equals("진동시작")) {
			
				//윈도우를 바꾸는 것은 update 이벤트를 발생시켜서 이 이벤트가 처리되어야 화면이 바뀜
				//하지만 이벤트는 메세지 큐에 들어가서 순서대로 처리되기 때문에 현재 처리 중인 Action 이벤트가 우선임
				//while 반복문이 무한루프면 버튼의 글씨도 안바뀌고 이 무한루프를 벗어날 방법이 없음
				//따라서 병렬처리가 필요함
				
				btn.setText("진동끝");
				while(cont) {
					frame.setLocation(frame.getLocation().x + offset, frame.getLocation().y);
					offset = -offset;
					try {
						Thread.sleep(100);
						
					}catch(InterruptedException e1) {
						
					}
				}
			}
			else {
				btn.setText("진동시작");
				cont=false;
			}
			
		}
	};
	*/
	private ActionListener handler = new ActionListener() {
		private boolean cont = true;

		@Override
		public void actionPerformed(ActionEvent e) {
			String s = e.getActionCommand();
			if(s.equals("진동시작")) {
			
				btn.setText("진동끝");
				//thread = new VibrationThread();
				//thread = new Thread(new VibrationRunnable());
				thread = new Thread(new Runnable() {
					private int offset = 10;
					@Override
					public void run() {
						while(thread == Thread.currentThread()) {
							frame.setLocation(frame.getLocation().x + offset, frame.getLocation().y);
							offset = -offset;
							try {
								Thread.sleep(100);
								
							}catch(InterruptedException e1) {
								
							}
						}
					}
				});
				thread.start();
			}
			else {
				btn.setText("진동시작");
				thread = null;
			}
			
		}
	};
	class VibrationThread extends Thread{
		private int offset = 10;
		@Override
		public void run() {
			while(thread == Thread.currentThread()) {
				frame.setLocation(frame.getLocation().x + offset, frame.getLocation().y);
				offset = -offset;
				try {
					Thread.sleep(100);
					
				}catch(InterruptedException e1) {
					
				}
			}
		}
		
	}
	class VibrationRunnable implements Runnable{
		private int offset = 10;
		@Override
		public void run() {
			while(thread == Thread.currentThread()) {
				frame.setLocation(frame.getLocation().x + offset, frame.getLocation().y);
				offset = -offset;
				try {
					Thread.sleep(100);
					
				}catch(InterruptedException e1) {
					
				}
			}
		}
	}
	public static void main(String[] args) {
		new Vibration();
	}
}
