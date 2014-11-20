package game.library;

import java.io.Serializable;

public class ServerMessage implements Serializable{
	
	public CharacterControlData characterData;
	
	public int messageType;

	public int port;
	
	public ServerMessage(int msgType){
		messageType = msgType;
	}
	
	public void setCharacterData(CharacterControlData data){
		characterData = data;
	}
}
