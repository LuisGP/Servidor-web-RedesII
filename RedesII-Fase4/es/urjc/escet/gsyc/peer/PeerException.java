package es.urjc.escet.gsyc.peer;

public class PeerException extends Exception{
	private String msg_error;
	
	public void setMensajeDeError(String string) {
		// TODO Auto-generated method stub
		msg_error = string;
	}
	
	public String getMensajeDeError(){
		return msg_error;
	}
}
