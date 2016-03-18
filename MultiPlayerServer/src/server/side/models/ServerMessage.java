package server.side.models;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerMessage implements Serializable{
	
	public CharacterObj characterData;
	
	public int messageType;
	public long id;
	public int port;
	
	public ServerMessage(){}
	
	public ServerMessage(int msgType){
		messageType = msgType;
	}
	
	public void setCharacterData(CharacterObj data){
		characterData = data;
	}
	
}
