package client.side;

import game.library.Box;
import game.library.CharacterControlData;
import game.library.ServerMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;


class TcpConnection {

	private static final int GET_ID = 0;
	private static final int GET_MAP = 1;
	private static final int SEND_MAIN_CHARACTER = 2;
	private static final int GET_ID_IP_PORT = 3;
	private static final int REMOVE_CHARACTER = 4;
	
	private final int SERVER_PORT_TCP;
	
	//ip of server side
	private final String SERVER_IP;

	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	private Socket socket;

	TcpConnection(Main main, String ip, int port) {
		
		SERVER_PORT_TCP = port;
		SERVER_IP = ip;
		try {
			socket = new Socket(SERVER_IP, SERVER_PORT_TCP);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Gets unique ID for player **/
	long getIdFromServer() {
		
		try {
			oos.writeObject(new ServerMessage(GET_ID));
			return ois.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/** Downloads map from server **/
	List<Box> getMapDetails() {
		
		try {
			oos.writeObject(new ServerMessage(GET_MAP));
			return (List<Box>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** Sends data about the main character to server. Velocity, etc. */
	void sendUpdatedVersion(CharacterControlData data) {
		try {
			ServerMessage msg = new ServerMessage(SEND_MAIN_CHARACTER);
			msg.setCharacterData(data);
			oos.writeObject(msg);
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Sends Ip and port of Udp connection **/
	void sendIpIdPort(int port) {
		
		try {
			ServerMessage msg = new ServerMessage(GET_ID_IP_PORT);
			msg.port = port;
			oos.writeObject(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Sends id of player to server to inform that player has left the game **/
	void removeCharacter(long id) {
		
		try {
			ServerMessage msg = new ServerMessage(REMOVE_CHARACTER);
			msg.id = id;
			oos.writeObject(msg);
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
