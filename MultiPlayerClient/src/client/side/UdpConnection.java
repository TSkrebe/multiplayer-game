package client.side;

import game.library.Box;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.List;

/**
* This class establishes UDP connection with server and receives data about
* the game state
*/
class UdpConnection implements Runnable {
	
		private Main main;
		
		private byte[] buffer = new byte[1024 * 3];
		
		private DatagramSocket datagramSocket;
		
		private TcpConnection tcpConnection;
		
		//set udp port you want get game-play though. Make sure
		//router port forwards it.
		//private final int UDP_PORT;

		private final int UDP_PORT;

		UdpConnection(Main main, TcpConnection tcpConnection, int client_port_udp) {
			
			this.main = main;
			this.tcpConnection = tcpConnection;
			UDP_PORT = client_port_udp;
		}

		/** Listens to server, reads sent data and passes it to main class */
		@Override
		public void run() {

			try {
				if (UDP_PORT < 0 || UDP_PORT > 65535){
					datagramSocket = new DatagramSocket();
					System.err.append(UDP_PORT + "port is not possible. Random port assigned");
				}
				else{
					datagramSocket = new DatagramSocket(UDP_PORT);
				}
				// send info about udp to server
				tcpConnection.sendIpIdPort(datagramSocket.getLocalPort());

				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				while (true) {
					datagramSocket.receive(packet);
					ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
					ObjectInputStream ois = new ObjectInputStream(bais);
					List<Box> objects = (List<Box>) ois.readObject();
					main.updateListOfObjects(objects);
					packet.setData(buffer);
					packet.setLength(buffer.length);
				}

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

		}

}
