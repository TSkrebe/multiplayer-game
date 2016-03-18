package server.side;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.xml.bind.JAXBException;

import server.side.models.ServerMessage;

/**
 * This class establishes TCP connection and listens to client side
 * for tasks to do.
 */
class TcpConnection implements Runnable{
	
	private static final int GET_ID = 0;
	private static final int GET_MAP = 1;
	private static final int SEND_MAIN_CHARACTER = 2;
	private static final int GET_ID_IP_PORT = 3;
	private static final int REMOVE_CHARACTER = 4;

	private Main main;
	private Socket socket;
	
	TcpConnection(Main main, Socket socket) {
		this.main = main;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		
		try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
			
			while(true){
				String msg = (String)ois.readObject();
				ServerMessage sm;
				try {
					sm = Helper.unmarshall(msg);
				} catch (JAXBException e) {
					e.printStackTrace();
					continue;
				}
				switch(sm.messageType){
					case GET_ID:
						oos.writeLong(main.getId());
						break;
					case GET_MAP:
						try {
							String data = Helper.marshall(main.getMap());
							oos.writeObject(data);
						} catch (JAXBException e) {
							e.printStackTrace();
						}
						break;
					case SEND_MAIN_CHARACTER:
						main.includeCharacter(sm.characterData);
						break;
					case GET_ID_IP_PORT: 
						String ipString = socket.getInetAddress().getHostName();
						InetAddress clientIp = InetAddress.getByName(ipString);
						System.err.println(ipString + " " + clientIp);
						main.addressBook(clientIp, sm.port);
						break;
					case REMOVE_CHARACTER:
						main.removeCharacter(sm.id);
						break;
					default:
						break;
				}
				oos.flush();
				
			}
		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
			System.out.println("Player leaves");
		}
	}

}
