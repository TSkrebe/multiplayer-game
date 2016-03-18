package client.side.models;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerMessage{
	
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
	
	/*	        Unmarshaller unmarshaller = jc.createUnmarshaller();
	        File xml = new File("src/forum13159089/input.xml");
	        Transition transition = (Transition) unmarshaller.unmarshal(xml);
*/

	public void setPort(int port) {
		this.port = port;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
