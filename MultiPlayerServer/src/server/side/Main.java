package server.side;


import server.side.Helper.WrapperList;
import server.side.models.Box;
import server.side.models.CharacterObj;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.JAXBException;


public class Main {

	private static final String TILES_FILE = "/gameInfo.txt";
	
	 // refreshing game state and sending data to clients every x ms
	private static final long RESHRESH_GAP = 30;

	private static int SERVER_PORT_TCP;
	
	private static long IDs = 0L;
	
	//thread safe array because while one thread is reading another
	//might add delete some entries
	private CopyOnWriteArrayList<IpPort> activeClients;
	private Vector<MainCharacter> fullCharacters;

	private WrapperList tiles;
	private WrapperList gamePlay;
	
	private UdpConnectionsSend udpSend;
	
	public static void main(String[] args) {
		
		if (args.length != 1)
			throw new IllegalArgumentException("Bad input");
		
		Main main = new Main(Integer.parseInt(args[0]));
		main.start();
	}
	
	public Main(int tcpPort){
		
		SERVER_PORT_TCP = tcpPort;
		activeClients = new CopyOnWriteArrayList<IpPort>();
		tiles = new WrapperList();
		gamePlay = new WrapperList();
		udpSend = new UdpConnectionsSend();
		fullCharacters = new Vector<MainCharacter>();
	}

	private void start(){
			
		gameStateRefresher();

		InputStream ff = Main.class.getResourceAsStream(TILES_FILE);
		try (Scanner fileReader = new Scanner(ff);
		ServerSocket serverSocket = new ServerSocket(SERVER_PORT_TCP)){

			while(fileReader.hasNext()){
				tiles.add(new Box(fileReader.nextInt(), fileReader.nextInt(),
						fileReader.nextInt(), fileReader.nextInt(), 1f,
						1f, 1f, -1L, -1));
			}
			
			Socket clientSocket;
			while((clientSocket = serverSocket.accept()) != null){
				new Thread(new TcpConnection(this, clientSocket)).start();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void gameStateRefresher(){
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				updateGamePlay();
				udpSend.sendGamePlay();
			}

			private void updateGamePlay() {
				gamePlay.clear();
				for (MainCharacter mc : fullCharacters){
					gamePlay.addAll(mc.update(tiles.realList, fullCharacters));
				}
			}
			
		},0, RESHRESH_GAP);
	}

	synchronized long getId(){
		return IDs++;
	}
	
	/**
	 * This function is called when new data about character arrives.
	 * If this is a new character we update its state otherwise
	 * we simply update velocity and etc.
	 * @param data that we get from client
	 */
	
	void includeCharacter(CharacterObj data){
		
		long specId = data.id;
		for (MainCharacter mc : fullCharacters){
			
			//if character already exists we just update its status
			if (specId == mc.getID()){
			 	mc.updateState(data);
				return ;
			}
		}
		//if it is new character then we add it to the list
		MainCharacter newMc = new MainCharacter(data);
		fullCharacters.add(newMc);
	}
	
	void removeCharacter(long id){
		
		Iterator<MainCharacter> i = fullCharacters.iterator();
		while(i.hasNext()){
			
			MainCharacter mc = i.next();
			if (mc.getID() == id){
				i.remove();
				return;
			}
		}
	}
	

	void addressBook(InetAddress address, int port){
		activeClients.add(new IpPort(address, port));
	}
	
	private static class IpPort{
		
		InetAddress address;
		int port;
		
		public IpPort(InetAddress address, int port){	
			this.address = address;
			this.port = port;
		}
	} 
	
	public WrapperList getMap(){
		return tiles;
	}
	
	private class UdpConnectionsSend{
		
		DatagramSocket gamePlaySocket;
		
		public UdpConnectionsSend() {
			
			try {
				gamePlaySocket = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		public void sendGamePlay() {
			
			try{
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(Helper.marshall(gamePlay));
				byte [] bytes = baos.toByteArray();
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
				
				for (IpPort dest : activeClients){
					packet.setAddress(dest.address);
					packet.setPort(dest.port);
					gamePlaySocket.send(packet);
					packet.setData(bytes);
					packet.setLength(bytes.length);
				}
				
			}catch (IOException | JAXBException e) {
			
			}
		}
	}
}