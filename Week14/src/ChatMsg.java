import java.io.Serializable;

import javax.swing.ImageIcon;

public class ChatMsg implements Serializable{
	public final static int MODE_LOGIN = 0x1;
	public final static int MODE_LOGOUT = 0x2;
	public final static int MODE_TX_STRING = 0x10;
	public final static int MODE_TX_FILE = 0x20;
	public final static int MODE_TX_IMAGE = 0x40;
	public final static int MODE_TX_POS = 0x100;
	public final static int MOVE_STOP = 0x200;
	public final static int MOVE_LEFT = 0x201;
	public final static int MOVE_RIGHT = 0x202;
	public final static int MOVE_UP = 0x204;
	public final static int MOVE_DOWN = 0x208;
	
	String userID;
	int mode;
	String message;
	Object object;
	long size;
	
	public ChatMsg(String userID, int code, String message, Object object, long size) {
		this.userID = userID;
		this.mode = code;
		this.message = message;
		this.object = object;
		this.size = size;
		
	}
	public ChatMsg(String userID, int code, String message, Object object) {
		this(userID, code, message, object, 0);
	}
	public ChatMsg(String userID, int code) {
		this(userID, code,null,null);
	}
	public ChatMsg(String userID, int code, String message) {
		this(userID, code, message, null);
	}
	public ChatMsg(String userID, int code, Object object) {
		this(userID, code, null, object);
	}
	public ChatMsg(String userID, int code, String filename, long size) {
		this(userID, code, filename, null, size);
	}
}
