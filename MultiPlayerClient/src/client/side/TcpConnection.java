package client.side;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import javax.xml.bind.JAXBException;

import client.side.models.Box;
import client.side.models.CharacterObj;
import client.side.models.ServerMessage;


class TcpConnection {

	private static final int GET_ID = 0;
	private static final int GET_MAP = 1;
	private static final int SEND_MAIN_CHARACTER = 2;
	private static final int GET_ID_IP_PORT = 3;
	private static final int REMOVE_CHARACTER = 4;
	
	private final int SERVER_PORT_TCP;
	
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
			ServerMessage sm = new ServerMessage(GET_ID);
			String data = Helper.marshall(sm);
			oos.writeObject(data);
			
			return ois.readLong();
		} catch (IOException | JAXBException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/** Downloads map from server **/
	List<Box> getMapDetails() {
		
		try {
			ServerMessage sm = new ServerMessage(GET_MAP);
			String data = Helper.marshall(sm);
			oos.writeObject(data);
			
			String response = (String) ois.readObject();
			return Helper.unmarshall(response);
			
		} catch (IOException | ClassNotFoundException | JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** Sends data about the main character to server. Velocity, etc. */
	void sendUpdatedVersion(CharacterObj character) {
		try {
			ServerMessage sm = new ServerMessage(SEND_MAIN_CHARACTER);
			sm.setCharacterData(character);
			String data = Helper.marshall(sm);
			oos.writeObject(data);
			oos.reset();
		} catch (IOException | JAXBException e) {
			e.printStackTrace();
		}
	}
	
	/** Sends IP and port of Udp connection **/
	void sendIpIdPort(int port) {
		
		try {
			ServerMessage sm = new ServerMessage(GET_ID_IP_PORT);
			sm.setPort(port);
			String data = Helper.marshall(sm);
			oos.writeObject(data);
		} catch (IOException | JAXBException e) {
			e.printStackTrace();
		}
	}
	
	/** Sends id of player to the server to inform that a player has left the game **/
	void removeCharacter(long id) {
		
		try {
			ServerMessage sm = new ServerMessage(REMOVE_CHARACTER);
			sm.setId(id);
			String data = Helper.marshall(sm);
			oos.writeObject(data);
			//oos.reset();
		} catch (IOException | JAXBException e) {
			e.printStackTrace();
		}
	}

}
